package de.intelligence.icarusobfuscator.core.finalizer;

import java.io.OutputStream;

import org.objectweb.asm.tree.ClassNode;

import de.intelligence.icarusobfuscator.core.classpath.ClassPathEntry;

public final class PackedJarFileFinalizer extends SimpleJarFileFinalizer {

    public PackedJarFileFinalizer(String destination, OutputStream outputStream, Method method,
                                  CompressionLevel compressionLevel) {
        super(destination, outputStream, method, compressionLevel);
    }

    @Override
    protected byte[] generateClassBytes(ClassPathEntry<ClassNode> classEntry) {
        return super.generateClassBytes(classEntry); //TODO
    }

    @Override
    protected void finish() {
        // TODO
    }

}
