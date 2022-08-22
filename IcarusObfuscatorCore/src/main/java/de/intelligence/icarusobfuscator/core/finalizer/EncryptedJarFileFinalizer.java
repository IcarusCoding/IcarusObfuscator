package de.intelligence.icarusobfuscator.core.finalizer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLClassLoader;
import java.util.Base64;
import java.util.jar.JarEntry;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import de.intelligence.icarusobfuscator.core.Constants;
import de.intelligence.icarusobfuscator.core.classpath.ClassPathEntry;
import de.intelligence.icarusobfuscator.core.classpath.ManifestContext;
import de.intelligence.icarusobfuscator.core.utils.ImmutablePair;

public final class EncryptedJarFileFinalizer extends SimpleJarFileFinalizer {

    private String realMainClass;

    public EncryptedJarFileFinalizer(String destination, OutputStream outputStream, Method method,
                                     CompressionLevel compressionLevel) {
        super(destination, outputStream, method, compressionLevel);
    }

    @Override
    protected byte[] generateClassBytes(ClassPathEntry<ClassNode> classEntry) {
        return super.generateClassBytes(classEntry); // TODO
    }

    @Override
    protected ImmutablePair<JarEntry, byte[]> handleResource(ClassPathEntry<byte[]> resourceEntry) {
        if (resourceEntry.name().equals(Constants.MANIFEST)) {
            final ManifestContext ctx = new ManifestContext(resourceEntry);
            this.realMainClass = ctx.getMainClass();
            // TODO replace main class

        }
        return super.handleResource(resourceEntry);
    }

    @Override
    protected void finish() throws IOException {
        String name = ""; // TODO generate unique name (also needed for handleResource above)
        final JarEntry entry = new JarEntry(name);
        final ClassWriter classWriter = new ClassWriter(SimpleJarFileFinalizer.ASM_FLAGS);
        this.generateDecryptionClass(classWriter, name);
        super.addEntry(entry, classWriter.toByteArray());
    }

    private void generateDecryptionClass(ClassWriter writer, String name) {
        final String fieldName = ""; //TODO
        final String magicName = ""; // TODO
        final Label endURLNullCheckLbl = new Label();
        // Create class
        writer.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, name, null, "java/net/URLClassLoader", null);
        // Create key field
        writer.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, fieldName, "[B", null, null);
        // Create magic field
        writer.visitField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL, magicName,
                "[B", null, null);
        // Create static block for magic field initialization
        //TODO
        // Initialize magic field
        //TODO
        // Create main method
        final MethodVisitor mainMethodVisitor = writer.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, Constants.MAIN_NAME,
                "([Ljava/lang/String;)V", null, null);
        //TODO load real main class with custom class loader
        mainMethodVisitor.visitEnd();
        // Create constructor
        final MethodVisitor constructorVisitor = writer.visitMethod(Opcodes.ACC_PUBLIC, Constants.INIT_NAME,
                "([Ljava/net/URL;Ljava/lang/String;)V", null, null);
        // Push local variables to stack for super constructor invocation
        constructorVisitor.visitVarInsn(Opcodes.ALOAD, 0); // implicit self reference
        constructorVisitor.visitVarInsn(Opcodes.ALOAD, 1); // url array reference
        constructorVisitor.visitInsn(Opcodes.ACONST_NULL); // null reference for parent class loader
        // Call super constructor
        constructorVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, Type.getInternalName(URLClassLoader.class),
                Constants.INIT_NAME, "(Ljava/net/URL;Ljava/lang/ClassLoader;)V", false);
        // Decode Base64 encoded key
        constructorVisitor.visitVarInsn(Opcodes.ALOAD, 0); // implicit self reference
        constructorVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(Base64.class), "getDecoder",
                "()Ljava/util/Base64$Decoder;", false); // Call getDecoder
        constructorVisitor.visitVarInsn(Opcodes.ALOAD, 2); // key reference
        constructorVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(Base64.class), "decode",
                "(Ljava/lang/String;)[B", false); // Call decode
        constructorVisitor.visitFieldInsn(Opcodes.PUTFIELD, name, fieldName, "[B"); // Set key field
        constructorVisitor.visitInsn(Opcodes.RETURN); // Return
        constructorVisitor.visitEnd();
        // Override loadClass method
        final MethodVisitor loadClassMethodVisitor = writer.visitMethod(Opcodes.ACC_PUBLIC, "loadClass",
                "(Ljava/lang/String;Z)Ljava/lang/Class;", null,
                new String[]{"java/lang/ClassNotFoundException"});
        // Find resource for class name
        loadClassMethodVisitor.visitVarInsn(Opcodes.ALOAD, 0); // implicit self reference
        loadClassMethodVisitor.visitVarInsn(Opcodes.ALOAD, 1); // class name reference
        // 1. Replace package name by internal name
        loadClassMethodVisitor.visitIntInsn(Opcodes.BIPUSH, 46); // Push '.' to stack
        loadClassMethodVisitor.visitIntInsn(Opcodes.BIPUSH, 47); // Push '/' to stack
        loadClassMethodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "replace",
                "(CC)Ljava/lang/String;", false); // Call replace
        // 2. Concat with .class
        loadClassMethodVisitor.visitLdcInsn(".class"); // Push ".class" to stack
        loadClassMethodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "concat",
                "(Ljava/lang/String;)Ljava/lang/String;", false); // Call concat
        // 3. Call super getResource
        loadClassMethodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/ClassLoader", "getResource",
                "(Ljava/lang/String;)Ljava/net/URL;", false); // Call getResource
        // 4. Store URL in local variable
        loadClassMethodVisitor.visitVarInsn(Opcodes.ASTORE, 3);
        // 5. Check if URL is null
        loadClassMethodVisitor.visitVarInsn(Opcodes.ALOAD, 3); // URL reference
        loadClassMethodVisitor.visitJumpInsn(Opcodes.IFNONNULL, endURLNullCheckLbl); // If not null jump over if body
        // Call super loadClass if URL is null
        loadClassMethodVisitor.visitVarInsn(Opcodes.ALOAD, 0); // implicit self reference
        loadClassMethodVisitor.visitVarInsn(Opcodes.ALOAD, 1); // class name reference
        loadClassMethodVisitor.visitVarInsn(Opcodes.ILOAD, 2); // resolve value
        loadClassMethodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/net/URLClassLoader", "loadClass",
                "(Ljava/lang/String;Z)Ljava/lang/Class;", false); // Call loadClass
        // Return loaded class
        loadClassMethodVisitor.visitInsn(Opcodes.ARETURN);
        loadClassMethodVisitor.visitLabel(endURLNullCheckLbl); // Label for end of if body
        // Open URL stream
        loadClassMethodVisitor.visitVarInsn(Opcodes.ALOAD, 3); // URL reference
        loadClassMethodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/net/URL", "openStream",
                "()Ljava/io/InputStream;", false); // Call openStream
        loadClassMethodVisitor.visitVarInsn(Opcodes.ASTORE, 4); // Store input stream in local variable
        // TODO continue here
        loadClassMethodVisitor.visitEnd();
        writer.visitEnd();
    }

}
