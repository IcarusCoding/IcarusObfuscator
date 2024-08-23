package de.intelligence.icarusobfuscator.core.finalizer;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import de.intelligence.icarusobfuscator.core.classpath.ClassPathEntry;
import de.intelligence.icarusobfuscator.core.overwrites.UnloadedClasspathStackMapFramesClassWriter;

public class SimpleJarFileFinalizer extends AbstractJarFileFinalizer {

    private final int flags;

    public SimpleJarFileFinalizer(int flags, String destination, Method method, CompressionLevel compressionLevel) {
        super(destination, method, compressionLevel);
        this.flags = flags;
    }

    @Override
    protected byte[] generateClassBytes(ClassPathEntry<ClassNode> classEntry) {
        final ClassWriter classWriter = new UnloadedClasspathStackMapFramesClassWriter(super.classPath, flags);
        classEntry.source().accept(classWriter);
        return classWriter.toByteArray();
    }

}
