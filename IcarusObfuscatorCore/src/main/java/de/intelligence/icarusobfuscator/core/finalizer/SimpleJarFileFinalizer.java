package de.intelligence.icarusobfuscator.core.finalizer;

import java.io.OutputStream;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import de.intelligence.icarusobfuscator.core.classpath.ClassPathEntry;

public class SimpleJarFileFinalizer extends AbstractJarFileFinalizer {

    protected static final int ASM_FLAGS = Opcodes.ASM9 | ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS;

    public SimpleJarFileFinalizer(String destination, OutputStream outputStream, Method method, CompressionLevel compressionLevel) {
        super(destination, outputStream, method, compressionLevel);
    }

    @Override
    protected byte[] generateClassBytes(ClassPathEntry<ClassNode> classEntry) {
        final ClassWriter classWriter = new ClassWriter(SimpleJarFileFinalizer.ASM_FLAGS);
        classEntry.source().accept(classWriter);
        return classWriter.toByteArray();
    }

}
