package de.intelligence.icarusobfuscator.core.finalizer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.jar.JarEntry;
import java.util.zip.Deflater;
import java.util.zip.ZipOutputStream;

import org.objectweb.asm.tree.ClassNode;

import de.intelligence.icarusobfuscator.core.Constants;
import de.intelligence.icarusobfuscator.core.classpath.ClassPath;
import de.intelligence.icarusobfuscator.core.classpath.ClassPathEntry;
import de.intelligence.icarusobfuscator.core.utils.ImmutablePair;

public abstract class AbstractJarFileFinalizer implements IFinalizer {

    protected final String destination;
    private final ZipOutputStream outputStream;
    protected final Method method;
    protected final CompressionLevel compressionLevel;

    protected AbstractJarFileFinalizer(String destination, OutputStream outputStream, Method method,
                                       CompressionLevel compressionLevel) {
        this.destination = destination;
        this.outputStream = new ZipOutputStream(outputStream);
        this.method = method;
        this.compressionLevel = compressionLevel;
    }

    protected abstract byte[] generateClassBytes(ClassPathEntry<ClassNode> classEntry);

    @Override
    public void doFinalize(ClassPath classPath) {
        try (this.outputStream) {
            outputStream.setMethod(this.method.getValue());
            outputStream.setLevel(this.compressionLevel.getValue());
            for (final String dir : classPath.directories().stream().sorted(Comparator.comparing(String::length)).toList()) {
                final JarEntry entry = this.handleDirectory(dir);
                if (entry != null) {
                    this.addEntry(entry, null);
                }
            }
            for (final ClassPathEntry<ClassNode> classEntry : classPath.classes()) {
                final ImmutablePair<JarEntry, byte[]> entry = this.handleClass(classEntry);
                if (entry != null) {
                    this.addEntry(entry.left(), entry.right());
                }
            }
            for (final ClassPathEntry<byte[]> resourceEntry : classPath.resources()) {
                final ImmutablePair<JarEntry, byte[]> entry = this.handleResource(resourceEntry);
                if (entry != null) {
                    this.addEntry(entry.left(), entry.right());
                }
            }
            this.finish();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected final void addEntry(JarEntry entry, byte[] bytes) throws IOException {
        entry.setTime(System.currentTimeMillis());
        this.outputStream.putNextEntry(entry);
        if (bytes != null) {
            this.outputStream.write(bytes);
        }
        this.outputStream.closeEntry();
    }

    protected JarEntry handleDirectory(String directory) {
        return new JarEntry(directory);
    }

    protected ImmutablePair<JarEntry, byte[]> handleClass(ClassPathEntry<ClassNode> classEntry) {
        return new ImmutablePair<>(new JarEntry(classEntry.name() + Constants.CLASS_SUFFIX),
                this.generateClassBytes(classEntry));
    }

    protected ImmutablePair<JarEntry, byte[]> handleResource(ClassPathEntry<byte[]> resourceEntry) {
        return new ImmutablePair<>(new JarEntry(resourceEntry.name()), resourceEntry.source());
    }

    protected void finish() throws IOException {
        // no-op
    }

    @Override
    public String getDestination() {
        return this.destination;
    }

    public enum Method {

        DEFLATED(ZipOutputStream.DEFLATED),
        STORED(ZipOutputStream.STORED);

        private final int value;

        Method(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

    }

    public enum CompressionLevel {

        NO_COMPRESSION(Deflater.NO_COMPRESSION),
        BEST_SPEED(Deflater.BEST_SPEED),
        BEST_COMPRESSION(Deflater.BEST_COMPRESSION),
        DEFAULT_COMPRESSION(Deflater.DEFAULT_COMPRESSION);

        private final int value;

        CompressionLevel(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

    }

}
