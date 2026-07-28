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

/** Them lenh chat local "show" de bat/tat bang thong tin cua CCanvas. */
public final class PatchOverlayInfoToggle {

    private static final String CANVAS = "coreLG/CCanvas";
    private static final String GAME_SERVICE = "chibikun/GameService";
    private static final String TOGGLE = "chibikun/OverlayInfoToggle";

    private PatchOverlayInfoToggle() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException(
                    "Usage: PatchOverlayInfoToggle <jar> <OverlayInfoToggle.class>");
        }
        File jar = new File(args[0]).getCanonicalFile();
        byte[] helper = rewriteHelperVersion(
                Files.readAllBytes(new File(args[1]).toPath()));
        patchJar(jar, helper);
        System.out.println("Patched local chat show toggle: " + jar);
    }

    private static void patchJar(File jar, byte[] helper) throws Exception {
        Map<String, byte[]> replacements = new HashMap<String, byte[]>();
        JarFile input = new JarFile(jar);
        try {
            replacements.put(
                    CANVAS + ".class",
                    patchCanvas(read(input, CANVAS + ".class")));
            replacements.put(
                    GAME_SERVICE + ".class",
                    patchGameService(read(input, GAME_SERVICE + ".class")));
            replacements.put(TOGGLE + ".class", helper);

            File temp = new File(jar.getParentFile(), jar.getName() + ".show.tmp");
            JarOutputStream output = new JarOutputStream(new FileOutputStream(temp));
            try {
                Enumeration<JarEntry> entries = input.entries();
                while (entries.hasMoreElements()) {
                    JarEntry oldEntry = entries.nextElement();
                    String name = oldEntry.getName();
                    if (name.equals(TOGGLE + ".class")) {
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
            input.close();
            Files.move(
                    temp.toPath(),
                    jar.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } finally {
            input.close();
        }
    }

    private static byte[] patchCanvas(byte[] source) {
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
                        if (owner.equals(TOGGLE) && methodName.equals("isVisible")) {
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

        final boolean[] inserted = new boolean[1];
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
                if (!name.equals("paint")
                        || !descriptor.equals("(Ljavax/microedition/lcdui/Graphics;)V")) {
                    return base;
                }
                return new MethodVisitor(Opcodes.ASM8, base) {
                    private boolean sawMyInfo;

                    @Override
                    public void visitFieldInsn(
                            int opcode,
                            String owner,
                            String fieldName,
                            String fieldDescriptor
                    ) {
                        super.visitFieldInsn(
                                opcode, owner, fieldName, fieldDescriptor);
                        this.sawMyInfo = opcode == Opcodes.GETSTATIC
                                && owner.equals("coreLG/MyMidlet")
                                && fieldName.equals("myInfo")
                                && fieldDescriptor.equals("Lchibikun/CPlayer;");
                    }

                    @Override
                    public void visitJumpInsn(int opcode, Label label) {
                        super.visitJumpInsn(opcode, label);
                        if (this.sawMyInfo && opcode == Opcodes.IFNULL) {
                            super.visitMethodInsn(
                                    Opcodes.INVOKESTATIC,
                                    TOGGLE,
                                    "isVisible",
                                    "()Z",
                                    false);
                            super.visitJumpInsn(Opcodes.IFEQ, label);
                            inserted[0] = true;
                        }
                        this.sawMyInfo = false;
                    }
                };
            }
        }, ClassReader.SKIP_FRAMES);
        if (!inserted[0]) {
            throw new IllegalArgumentException(
                    "Khong tim thay block myInfo trong CCanvas.paint");
        }
        return writer.toByteArray();
    }

    private static byte[] patchGameService(byte[] source) {
        final boolean[] hasBoard = new boolean[1];
        final boolean[] hasRpg = new boolean[1];
        ClassReader scan = new ClassReader(source);
        scan.accept(new ClassVisitor(Opcodes.ASM8) {
            @Override
            public MethodVisitor visitMethod(
                    int access,
                    final String name,
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
                        if (owner.equals(TOGGLE) && methodName.equals("handleChat")) {
                            if (name.equals("chatToBoard")) {
                                hasBoard[0] = true;
                            } else if (name.equals("chatRPG")) {
                                hasRpg[0] = true;
                            }
                        }
                        super.visitMethodInsn(
                                opcode, owner, methodName,
                                methodDescriptor, isInterface);
                    }
                };
            }
        }, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        if (hasBoard[0] && hasRpg[0]) {
            return source;
        }

        ClassReader reader = new ClassReader(source);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        reader.accept(new ClassVisitor(Opcodes.ASM8, writer) {
            @Override
            public MethodVisitor visitMethod(
                    int access,
                    final String name,
                    String descriptor,
                    String signature,
                    String[] exceptions
            ) {
                MethodVisitor base = super.visitMethod(
                        access, name, descriptor, signature, exceptions);
                boolean target = descriptor.equals("(Ljava/lang/String;)V")
                        && ((name.equals("chatToBoard") && !hasBoard[0])
                        || (name.equals("chatRPG") && !hasRpg[0]));
                if (!target) {
                    return base;
                }
                return new MethodVisitor(Opcodes.ASM8, base) {
                    @Override
                    public void visitCode() {
                        super.visitCode();
                        Label original = new Label();
                        super.visitVarInsn(Opcodes.ALOAD, 1);
                        super.visitMethodInsn(
                                Opcodes.INVOKESTATIC,
                                TOGGLE,
                                "handleChat",
                                "(Ljava/lang/String;)Z",
                                false);
                        super.visitJumpInsn(Opcodes.IFEQ, original);
                        super.visitInsn(Opcodes.RETURN);
                        super.visitLabel(original);
                    }
                };
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
