package de.intelligence.icarusobfuscator.core.provider;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import de.intelligence.icarusobfuscator.core.Constants;
import de.intelligence.icarusobfuscator.core.classpath.ClassPath;
import de.intelligence.icarusobfuscator.core.classpath.ClassPathEntry;
import de.intelligence.icarusobfuscator.core.exception.ClassPathException;
import de.intelligence.icarusobfuscator.core.utils.JarUtil;

public final class JarFileClassPathProvider implements IClassPathProvider {

    private final File jarInput;
    private final JarFile jarFile;

    public JarFileClassPathProvider(String path) {
        this(new File(path));
    }

    public JarFileClassPathProvider(Path path) {
        this(path.toFile());
    }

    public JarFileClassPathProvider(File file) {
        if (!file.exists()) {
            throw new ClassPathException("Provided file \"" + file.getAbsolutePath() + "\" does not exist");
        }
        if (file.isDirectory()) {
            throw new ClassPathException("Provided file \"" + file.getAbsolutePath() + "\" is a directory");
        }
        if (!file.canRead()) {
            throw new ClassPathException("Provided file \"" + file.getAbsolutePath() + "\" is not readable");
        }
        this.jarInput = file;
        this.jarFile = JarUtil.validate(this.jarInput);
    }

    @Override
    public ClassPath provide() {
        final List<String> directories = new ArrayList<>();
        final List<ClassPathEntry<ClassNode>> classes = new ArrayList<>();
        final List<ClassPathEntry<byte[]>> resources = new ArrayList<>();
        final Enumeration<JarEntry> entryEnumeration = this.jarFile.entries();
        try {
            while (entryEnumeration.hasMoreElements()) {
                final JarEntry entry = entryEnumeration.nextElement();
                if (entry.isDirectory()) {
                    directories.add(entry.getName());
                } else if (entry.getName().endsWith(Constants.CLASS_SUFFIX)) {
                    final ClassNode classNode = new ClassNode(Opcodes.ASM9);
                    new ClassReader(this.jarFile.getInputStream(entry)).accept(classNode, Opcodes.ASM9);
                    classes.add(new ClassPathEntry<>(entry.getName().substring(0,
                            entry.getName().length() - Constants.CLASS_SUFFIX.length()), classNode));
                } else {
                    resources.add(new ClassPathEntry<>(entry.getName(), this.jarFile.getInputStream(entry).readAllBytes()));
                }
            }
        } catch (Exception ex) {
            throw new ClassPathException("An exception occurred while reading file \"" +
                    this.jarInput.getAbsolutePath() + "\": ", ex);
        }
        return new ClassPath(directories, classes, resources);
    }

}
