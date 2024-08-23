package de.intelligence.icarusobfuscator.core.finalizer;

import org.objectweb.asm.tree.ClassNode;

import de.intelligence.icarusobfuscator.core.classpath.ClassPathEntry;

public final class PackedJarFileFinalizer extends SimpleJarFileFinalizer {

    public PackedJarFileFinalizer(int flags, String destination, Method method, CompressionLevel compressionLevel) {
        super(flags, destination, method, compressionLevel);
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
