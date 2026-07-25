import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

/** Patcher lap lai an toan cho CMD 125 va hook ve IronManLaserVfx. */
public final class PatchIronManLaser {

    private static final String MESSAGE_HANDLER = "chibikun/MessageHandler";
    private static final String GAME_SCR = "chibikun/GameScr";
    private static final String VFX = "chibikun/IronManLaserVfx";

    private PatchIronManLaser() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException(
                    "Usage: PatchIronManLaser <jar> <IronManLaserVfx.class>");
        }
        File jar = new File(args[0]).getCanonicalFile();
        byte[] helper = rewriteHelperVersion(
                Files.readAllBytes(new File(args[1]).toPath()));
        patchJar(jar, helper);
        System.out.println("Patched Iron Man laser: " + jar);
    }

    private static void patchJar(File jar, byte[] helper) throws Exception {
        Map<String, byte[]> replacements = new HashMap<String, byte[]>();
        JarFile input = new JarFile(jar);
        try {
            replacements.put(
                    MESSAGE_HANDLER + ".class",
                    patchClass(read(input, MESSAGE_HANDLER + ".class"), true));
            replacements.put(
                    GAME_SCR + ".class",
                    patchClass(read(input, GAME_SCR + ".class"), false));
            replacements.put(VFX + ".class", helper);

            File temp = new File(jar.getParentFile(), jar.getName() + ".iron.tmp");
            JarOutputStream output = new JarOutputStream(new FileOutputStream(temp));
            try {
                Enumeration<JarEntry> entries = input.entries();
                while (entries.hasMoreElements()) {
                    JarEntry oldEntry = entries.nextElement();
                    String name = oldEntry.getName();
                    if (name.equals(VFX + ".class")) {
                        continue;
                    }
                    JarEntry next = new JarEntry(name);
                    next.setTime(oldEntry.getTime());
                    output.putNextEntry(next);
                    byte[] replacement = replacements.remove(name);
                    if (replacement != null) {
                        output.write(replacement);
                    } else if (!oldEntry.isDirectory()) {
                        output.write(read(input, name));
                    }
                    output.closeEntry();
                }
                for (Map.Entry<String, byte[]> extra : replacements.entrySet()) {
                    JarEntry next = new JarEntry(extra.getKey());
                    output.putNextEntry(next);
                    output.write(extra.getValue());
                    output.closeEntry();
                }
            } finally {
                output.close();
            }
            // Windows khong cho thay JAR khi JarFile van giu handle.
            input.close();
            Files.move(
                    temp.toPath(),
                    jar.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } finally {
            input.close();
        }
    }

    private static byte[] patchClass(byte[] source, final boolean messageHandler) {
        final boolean[] alreadyPatched = new boolean[1];
        ClassReader scan = new ClassReader(source);
        scan.accept(new ClassVisitor(Opcodes.ASM8) {
            @Override
            public MethodVisitor visitMethod(
                    int access,
                    String name,
                    String descriptor,
                    String signature,
                    String[] exceptions
            ) {
                MethodVisitor visitor = super.visitMethod(
                        access, name, descriptor, signature, exceptions);
                return new MethodVisitor(Opcodes.ASM8, visitor) {
                    @Override
                    public void visitMethodInsn(
                            int opcode,
                            String owner,
                            String methodName,
                            String methodDescriptor,
                            boolean isInterface
                    ) {
                        if (owner.equals(VFX)
                                && (methodName.equals("handle")
                                || methodName.equals("paint"))) {
                            alreadyPatched[0] = true;
                        }
                        super.visitMethodInsn(
                                opcode, owner, methodName,
                                methodDescriptor, isInterface);
                    }
                };
            }
        }, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        if (alreadyPatched[0]) {
            return source;
        }

        ClassReader reader = new ClassReader(source);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        reader.accept(new ClassVisitor(Opcodes.ASM8, writer) {
            @Override
            public MethodVisitor visitMethod(
                    int access,
                    String name,
                    String descriptor,
                    String signature,
                    String[] exceptions
            ) {
                MethodVisitor base = super.visitMethod(
                        access, name, descriptor, signature, exceptions);
                if (messageHandler
                        && name.equals("onMessage")
                        && descriptor.equals("(Lchibikun/Message;)V")) {
                    return new MethodVisitor(Opcodes.ASM8, base) {
                        @Override
                        public void visitCode() {
                            super.visitCode();
                            Label original = new Label();
                            super.visitVarInsn(Opcodes.ALOAD, 1);
                            super.visitFieldInsn(
                                    Opcodes.GETFIELD,
                                    "chibikun/Message",
                                    "cmd",
                                    "B");
                            super.visitIntInsn(Opcodes.BIPUSH, 125);
                            super.visitJumpInsn(Opcodes.IF_ICMPNE, original);
                            super.visitVarInsn(Opcodes.ALOAD, 1);
                            super.visitMethodInsn(
                                    Opcodes.INVOKESTATIC,
                                    VFX,
                                    "handle",
                                    "(Lchibikun/Message;)V",
                                    false);
                            super.visitInsn(Opcodes.RETURN);
                            super.visitLabel(original);
                        }
                    };
                }
                if (!messageHandler
                        && name.equals("paint")
                        && descriptor.equals("(Lchibikun/mGraphics;)V")) {
                    return new MethodVisitor(Opcodes.ASM8, base) {
                        @Override
                        public void visitInsn(int opcode) {
                            if (opcode == Opcodes.RETURN) {
                                super.visitVarInsn(Opcodes.ALOAD, 1);
                                super.visitMethodInsn(
                                        Opcodes.INVOKESTATIC,
                                        VFX,
                                        "paint",
                                        "(Lchibikun/mGraphics;)V",
                                        false);
                            }
                            super.visitInsn(opcode);
                        }
                    };
                }
                return base;
            }
        }, ClassReader.SKIP_FRAMES);
        return writer.toByteArray();
    }

    private static byte[] rewriteHelperVersion(byte[] source) {
        ClassReader reader = new ClassReader(source);
        ClassWriter writer = new ClassWriter(0);
        reader.accept(new ClassVisitor(Opcodes.ASM8, writer) {
            @Override
            public void visit(
                    int version,
                    int access,
                    String name,
                    String signature,
                    String superName,
                    String[] interfaces
            ) {
                super.visit(
                        Opcodes.V1_3,
                        access,
                        name,
                        signature,
                        superName,
                        interfaces);
            }
        }, ClassReader.SKIP_FRAMES);
        return writer.toByteArray();
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
