package de.intelligence.icarusobfuscator.core.classpath;

import java.util.List;
import java.util.stream.Collectors;

import org.objectweb.asm.tree.ClassNode;

import de.intelligence.icarusobfuscator.core.IIcarusObfuscator;
import de.intelligence.icarusobfuscator.core.provider.IClassPathProvider;

public final class ClassPathStructureControllerImpl implements IClassPathStructureController {

    private final IClassPathProvider provider;

    private ClassPath classPath;

    public ClassPathStructureControllerImpl(IClassPathProvider provider) {
        this.provider = provider;
    }

    @Override
    public void init() {
        long millis = System.currentTimeMillis();
        IIcarusObfuscator.LOG.info("Loading classpath from {}", this.provider.getSource());
        this.classPath = this.provider.provide();
        final List<ClassNode> loadedClasses = this.classPath.classes().stream().map(ClassPathEntry::source)
                .collect(Collectors.toList());


    }

}
