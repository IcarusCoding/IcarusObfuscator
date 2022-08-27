package de.intelligence.icarusobfuscator.core.finalizer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Base64;
import java.util.jar.JarEntry;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import de.intelligence.icarusobfuscator.core.Constants;
import de.intelligence.icarusobfuscator.core.classpath.ClassPathEntry;
import de.intelligence.icarusobfuscator.core.classpath.ManifestContext;
import de.intelligence.icarusobfuscator.core.gen.ClassGenerator;
import de.intelligence.icarusobfuscator.core.gen.GeneratorUtils;
import de.intelligence.icarusobfuscator.core.gen.MethodGenerator;
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
        final ClassGenerator generator = new ClassGenerator(Opcodes.ACC_PUBLIC, name, URLClassLoader.class);
        this.generateDecryptionClass(generator);
        super.addEntry(entry, generator.toByteArray());
    }

    private void generateDecryptionClass(ClassGenerator classGenerator) {
        final String fieldName = ""; //TODO
        final String magicName = ""; // TODO
        final byte[] magic = new byte[]{73, 67, 65, 82, 85, 83};

        // Create key field
        classGenerator.generateField(Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, fieldName, byte[].class);
        // Create magic field
        classGenerator.generateField(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL, magicName,
                byte[].class);

        // Create static block for magic field initialization
        final MethodGenerator staticBlockGenerator = classGenerator.generateMethod(Opcodes.ACC_STATIC, Constants.CLINIT_NAME,
                void.class);
        staticBlockGenerator.stackPush(magic.length);
        staticBlockGenerator.createArray(byte.class);
        for (int i = 0; i < magic.length; i++) {
            staticBlockGenerator.duplicate();
            staticBlockGenerator.stackPush(i);
            staticBlockGenerator.stackPush(magic[i]);
            staticBlockGenerator.arrayStore(byte.class);
        }
        staticBlockGenerator.setStaticField(classGenerator.getInternalClassName(), magicName, byte[].class);
        // Finish static block call
        staticBlockGenerator.ret();
        staticBlockGenerator.finish();

        // Create constructor
        final MethodGenerator constructorGenerator = classGenerator.generateMethod(Opcodes.ACC_PUBLIC, Constants.INIT_NAME,
                void.class, URL[].class, String.class);
        // Call super constructor
        constructorGenerator.loadImplicitSelfReference();
        constructorGenerator.loadArgument(0);
        constructorGenerator.loadNullReference();
        constructorGenerator.callConstructor(URLClassLoader.class, URL[].class, ClassLoader.class);
        // Decode Base64 encoded key
        constructorGenerator.loadImplicitSelfReference();
        constructorGenerator.callStatic(Base64.class, "getDecoder", Base64.Decoder.class);
        constructorGenerator.loadArgument(1);
        constructorGenerator.callVirtual(Base64.Decoder.class, "decode", byte[].class, String.class);
        constructorGenerator.setNonStaticField(classGenerator.getInternalClassName(), fieldName, byte[].class);
        // End constructor call
        constructorGenerator.ret();
        constructorGenerator.finish();

        // Create main method
        final MethodGenerator mainMethodGenerator = classGenerator.generateMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                Constants.MAIN_NAME, void.class, String[].class);
        // End main method call
        mainMethodGenerator.ret();
        mainMethodGenerator.finish();

        classGenerator.finish();

        // Override loadClass method
        final MethodGenerator loadClassMethodGenerator = classGenerator.generateMethod(Opcodes.ACC_PUBLIC, "loadClass",
                GeneratorUtils.generateExceptions(ClassNotFoundException.class), Class.class, String.class, boolean.class);
        loadClassMethodGenerator.loadImplicitSelfReference();
        loadClassMethodGenerator.loadArgument(0);
        loadClassMethodGenerator.stackPush('.');
        loadClassMethodGenerator.stackPush('/');
        loadClassMethodGenerator.callVirtual(String.class, "replace", String.class, char.class, char.class);
        loadClassMethodGenerator.stackPush(Constants.CLASS_SUFFIX);
        loadClassMethodGenerator.callVirtual(String.class, "concat", String.class, String.class);
        loadClassMethodGenerator.callSuper(ClassLoader.class, "getResource", URL.class, String.class);
        loadClassMethodGenerator.localStore(URL.class, 3);
        loadClassMethodGenerator.localLoad(URL.class, 3);
        loadClassMethodGenerator.loadNullReference();
        // End loadClass method call
        loadClassMethodGenerator.ret();
        loadClassMethodGenerator.finish();
    }

}
