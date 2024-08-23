package de.intelligence.icarusobfuscator.core;

import javax.crypto.Cipher;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.util.CheckClassAdapter;

import de.intelligence.icarusobfuscator.core.classpath.ClassPath;
import de.intelligence.icarusobfuscator.core.finalizer.AbstractJarFileFinalizer;
import de.intelligence.icarusobfuscator.core.finalizer.EncryptedJarFileFinalizer;
import de.intelligence.icarusobfuscator.core.finalizer.IFinalizer;
import de.intelligence.icarusobfuscator.core.gen.ClassGenerator;
import de.intelligence.icarusobfuscator.core.gen.GeneratorUtils;
import de.intelligence.icarusobfuscator.core.gen.MethodGenerator;
import de.intelligence.icarusobfuscator.core.provider.IClassPathProvider;
import de.intelligence.icarusobfuscator.core.provider.JarFileClassPathProvider;
import de.intelligence.icarusobfuscator.core.settings.ObfuscatorSettings;

public final class Core {

    //Possible fernflower vulnerability: add jump nonnull/null to label but dont add else code

    public static void madin(String[] args) throws IOException {
        final String name = "AllahuAkbar";
        final String fieldName = "key";
        final String magicName = "ICARUS_MAGIC";

        final byte[] magic = new byte[]{73, 67, 65, 82, 85, 83};

        final ClassGenerator classGenerator = new ClassGenerator(Opcodes.ACC_PUBLIC, name, URLClassLoader.class);
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
        loadClassMethodGenerator.stackPush(".class");
        loadClassMethodGenerator.callVirtual(String.class, "concat", String.class, String.class);
        loadClassMethodGenerator.callSuper(ClassLoader.class, "getResource", URL.class, String.class);
        loadClassMethodGenerator.localStoreAndLoad(URL.class, 3);
        final int endURLNullCheckLbl = loadClassMethodGenerator.createLabel();
        loadClassMethodGenerator.jumpIfNonNull(endURLNullCheckLbl);
        loadClassMethodGenerator.loadImplicitSelfReference();
        loadClassMethodGenerator.loadArguments(0, 1);
        loadClassMethodGenerator.callSuper(URLClassLoader.class, "loadClass", Class.class, String.class, boolean.class);
        loadClassMethodGenerator.ret();
        loadClassMethodGenerator.label(endURLNullCheckLbl);
        loadClassMethodGenerator.localLoad(URL.class, 3);
        loadClassMethodGenerator.callVirtual(URL.class, "openStream", InputStream.class);
        loadClassMethodGenerator.localStoreAndLoad(InputStream.class, 4);
        loadClassMethodGenerator.getStaticField(classGenerator.getInternalClassName(), magicName, byte[].class);
        loadClassMethodGenerator.pushArrayLength();
        loadClassMethodGenerator.callVirtual(InputStream.class, "loadNBytes", byte[].class, int.class);
        loadClassMethodGenerator.getStaticField(classGenerator.getInternalClassName(), magicName, byte[].class);
        loadClassMethodGenerator.callStatic(Arrays.class, "equals", boolean.class, byte[].class, byte[].class);
        final int endArrayEqualsCheckLbl = loadClassMethodGenerator.createLabel();
        loadClassMethodGenerator.jumpIfNotEqual(endArrayEqualsCheckLbl); // TODO validate
        loadClassMethodGenerator.loadImplicitSelfReference();
        loadClassMethodGenerator.loadArguments(0, 1);
        loadClassMethodGenerator.callSuper(URLClassLoader.class, "loadClass", Class.class, String.class, boolean.class);
        loadClassMethodGenerator.ret(); //TODO close input stream
        loadClassMethodGenerator.label(endArrayEqualsCheckLbl);
        loadClassMethodGenerator.stackPush("AES/GCM/NoPadding");
        loadClassMethodGenerator.callStatic(Cipher.class, "getInstance", Cipher.class, String.class);
        loadClassMethodGenerator.localStore(Cipher.class, 5);
        loadClassMethodGenerator.localLoad(InputStream.class, 4);
        loadClassMethodGenerator.stackPush(12);
        loadClassMethodGenerator.callVirtual(InputStream.class, "readNBytes", byte[].class, int.class);
        loadClassMethodGenerator.localStore(byte[].class, 6);
        loadClassMethodGenerator.stackPush(4);
        loadClassMethodGenerator.callStatic(ByteBuffer.class, "allocate", ByteBuffer.class, int.class);
        loadClassMethodGenerator.getStaticField(ByteOrder.class, "LITTLE_ENDIAN", ByteOrder.class);
        loadClassMethodGenerator.callVirtual(ByteBuffer.class, "order", ByteBuffer.class, ByteOrder.class);
        loadClassMethodGenerator.loadArguments(0, 0);
        loadClassMethodGenerator.stackPush('.');
        loadClassMethodGenerator.callVirtual(String.class, "lastIndexOf", int.class, int.class);
        loadClassMethodGenerator.stackPush(1);
        loadClassMethodGenerator.add(int.class);
        loadClassMethodGenerator.callVirtual(String.class, "substring", String.class, int.class);
        loadClassMethodGenerator.callVirtual(String.class, "hashCode", int.class);
        loadClassMethodGenerator.callVirtual(ByteBuffer.class, "putInt", ByteBuffer.class, int.class);
        loadClassMethodGenerator.callVirtual(ByteBuffer.class, "array", byte[].class);
        loadClassMethodGenerator.localStore(byte[].class, 7);
        loadClassMethodGenerator.stackPush(0);
        loadClassMethodGenerator.localStoreAndLoad(int.class, 8);
        loadClassMethodGenerator.localLoad(byte[].class, 6);
        loadClassMethodGenerator.pushArrayLength();

        loadClassMethodGenerator.loadNullReference();
        // End loadClass method call
        loadClassMethodGenerator.ret();
        loadClassMethodGenerator.finish();

        byte[] arr = classGenerator.toByteArray();
        FileOutputStream outputStream = new FileOutputStream("C:\\Users\\Master\\Desktop\\AllahuAkbar.class");
        outputStream.write(arr);
        outputStream.flush();
        outputStream.close();
        ClassReader reader = new ClassReader(arr);
        reader.accept(new CheckClassAdapter(new ClassWriter(0)), 0);

       /*
        loadClassMethodVisitor.visitVarInsn(Opcodes.ASTORE, 4); // Store input stream in local variable
        // TODO continue here
        loadClassMethodVisitor.visitEnd();*/
        //writer.visitEnd();

    }

    public static void main(String[] args) {
        final ObfuscatorSettings settings = new ObfuscatorSettings();
        byte[] key = Base64.getDecoder().decode("lOinGiXyveP7FgS+HqI+0w==");
        System.out.println(key.length);
        final IClassPathProvider provider = new JarFileClassPathProvider("C:\\Users\\Master\\IdeaProjects\\Dominion\\Server\\target\\server-1.1.0-jar-with-dependencies.jar");
        final IFinalizer finalizer = new EncryptedJarFileFinalizer(Opcodes.ASM9 | ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS,
                "C:\\Users\\Master\\IdeaProjects\\Dominion\\Server\\target\\server-1.1.0-jar-with-dependencies-OBF.jar",
                AbstractJarFileFinalizer.Method.DEFLATED, AbstractJarFileFinalizer.CompressionLevel.NO_COMPRESSION, new SecureRandom());
        final ClassPath classPath = provider.provide();
        finalizer.doFinalize(classPath);
        //  final IIcarusObfuscator obfuscator = new IcarusObfuscatorImpl(settings, provider);
        //   obfuscator.obfuscate();
    }

}
