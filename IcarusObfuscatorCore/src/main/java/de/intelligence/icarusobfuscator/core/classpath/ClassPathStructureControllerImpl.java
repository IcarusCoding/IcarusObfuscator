package de.intelligence.icarusobfuscator.core.classpath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.objectweb.asm.tree.ClassNode;

import de.intelligence.icarusobfuscator.core.IIcarusObfuscator;
import de.intelligence.icarusobfuscator.core.classpath.state.ClassState;
import de.intelligence.icarusobfuscator.core.classpath.state.ClassStateType;
import de.intelligence.icarusobfuscator.core.provider.IClassPathProvider;
import de.intelligence.icarusobfuscator.core.settings.ObfuscatorSettings;

public final class ClassPathStructureControllerImpl implements IClassPathStructureController {

    private final ObfuscatorSettings settings;
    private final IClassPathProvider provider;
    private final Map<String, ClassState> classes;

    private ClassPath classPath;

    public ClassPathStructureControllerImpl(ObfuscatorSettings settings, IClassPathProvider provider) {
        this.settings = settings;
        this.provider = provider;
        this.classes = new HashMap<>();
    }

    @Override
    public void init() {
        long millis = System.currentTimeMillis();
        IIcarusObfuscator.LOG.info("Loading classpath from {}", this.provider.getSource());
        this.classPath = this.provider.provide();
        final List<ClassNode> loadedClasses = this.classPath.classes().stream().map(ClassPathEntry::source)
                .collect(Collectors.toList());
        final List<ClassNode> duplicates = new ArrayList<>();
        final List<String> classNames = new ArrayList<>();
        for (final ClassNode node : loadedClasses) {
            if (classNames.contains(node.name)) {
                duplicates.add(node);
            } else {
                classNames.add(node.name);
                this.classes.put(node.name, ClassState.create(node, node.name.matches(this.settings.getOwner()) ?
                        ClassStateType.SOURCE : ClassStateType.LIBRARY));
            }
        }

    }

}
