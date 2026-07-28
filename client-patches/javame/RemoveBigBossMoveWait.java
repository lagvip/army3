import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.MethodInsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;
import jdk.internal.org.objectweb.asm.tree.VarInsnNode;

/** Loai toan bo hook mWait/mNotify cu, giu nguyen cac patch JAR khac. */
public final class RemoveBigBossMoveWait {

    private static final String BIG_BOSS = "chibikun/BigBoss.class";
    private static final String C_PLAYER = "chibikun/CPlayer.class";
    private static final String FIX = "chibikun/BigBossMoveWaitFix";
    private static final String FIX_CLASS = FIX + ".class";

    private RemoveBigBossMoveWait() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("Usage: RemoveBigBossMoveWait <jar>");
        }
        File jar = new File(args[0]).getCanonicalFile();
        byte[] bigBoss;
        byte[] cPlayer;
        JarFile input = new JarFile(jar);
        try {
            bigBoss = removeHooks(read(input, BIG_BOSS), "BigBoss");
            cPlayer = removeHooks(read(input, C_PLAYER), "CPlayer");
        } finally {
            input.close();
        }
        replaceEntries(jar, bigBoss, cPlayer);
        System.out.println("Removed legacy movement wait hooks: " + jar);
    }

    private static byte[] removeHooks(
            byte[] source,
            String className
    ) {
        ClassNode node = new ClassNode(Opcodes.ASM8);
        new ClassReader(source).accept(node, 0);
        int removed = 0;
        for (MethodNode method : node.methods) {
            if (!method.name.equals("update") || !method.desc.equals("()V")) {
                continue;
            }
            for (AbstractInsnNode insn = method.instructions.getFirst();
                    insn != null;) {
                AbstractInsnNode next = insn.getNext();
                if (insn instanceof MethodInsnNode) {
                    MethodInsnNode call = (MethodInsnNode) insn;
                    if (call.getOpcode() == Opcodes.INVOKESTATIC
                            && call.owner.equals(FIX)
                            && (call.name.equals("tick")
                                    || call.name.equals("completed"))) {
                        AbstractInsnNode argument = previousCodeInsn(insn);
                        if (!(argument instanceof VarInsnNode)
                                || argument.getOpcode() != Opcodes.ALOAD
                                || ((VarInsnNode) argument).var != 0) {
                            throw new IllegalStateException(
                                    "Unexpected BigBoss hook argument for " + call.name);
                        }
                        method.instructions.remove(argument);
                        method.instructions.remove(insn);
                        removed++;
                    }
                }
                insn = next;
            }
        }
        if (removed > 2) {
            throw new IllegalStateException(
                    "Unexpected legacy hook count in " + className
                    + ": " + removed);
        }
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);
        return writer.toByteArray();
    }

    private static AbstractInsnNode previousCodeInsn(AbstractInsnNode insn) {
        AbstractInsnNode previous = insn.getPrevious();
        while (previous != null && previous.getOpcode() < 0) {
            previous = previous.getPrevious();
        }
        return previous;
    }

    private static void replaceEntries(
            File jar,
            byte[] bigBoss,
            byte[] cPlayer
    )
            throws Exception {
        File temp = new File(jar.getParentFile(), jar.getName() + ".remove-wait.tmp");
        JarFile input = new JarFile(jar);
        try {
            JarOutputStream output = new JarOutputStream(new FileOutputStream(temp));
            try {
                Enumeration<JarEntry> entries = input.entries();
                while (entries.hasMoreElements()) {
                    JarEntry oldEntry = entries.nextElement();
                    JarEntry next = new JarEntry(oldEntry.getName());
                    next.setTime(oldEntry.getTime());
                    if (oldEntry.getName().equals(FIX_CLASS)) {
                        continue;
                    }
                    output.putNextEntry(next);
                    if (oldEntry.getName().equals(BIG_BOSS)) {
                        output.write(bigBoss);
                    } else if (oldEntry.getName().equals(C_PLAYER)) {
                        output.write(cPlayer);
                    } else if (!oldEntry.isDirectory()) {
                        output.write(read(input, oldEntry.getName()));
                    }
                    output.closeEntry();
                }
            } finally {
                output.close();
            }
        } finally {
            input.close();
        }
        Files.move(temp.toPath(), jar.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    private static byte[] read(JarFile jar, String name) throws Exception {
        JarEntry entry = jar.getJarEntry(name);
        if (entry == null) {
            throw new IllegalArgumentException("Missing jar entry: " + name);
        }
        InputStream stream = jar.getInputStream(entry);
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int count;
            while ((count = stream.read(buffer)) >= 0) {
                output.write(buffer, 0, count);
            }
            return output.toByteArray();
        } finally {
            stream.close();
        }
    }
}
