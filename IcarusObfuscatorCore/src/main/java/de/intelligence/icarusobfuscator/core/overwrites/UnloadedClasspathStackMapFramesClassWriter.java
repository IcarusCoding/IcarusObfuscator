package de.intelligence.icarusobfuscator.core.overwrites;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import de.intelligence.icarusobfuscator.core.Constants;
import de.intelligence.icarusobfuscator.core.classpath.ClassPath;
import de.intelligence.icarusobfuscator.core.classpath.ClassPathEntry;
import de.intelligence.icarusobfuscator.core.exception.FinalizerException;
import de.intelligence.icarusobfuscator.core.utils.Converters;

/*
 * Maybe not as efficient as the original method to calculate stack map frames
 * BUT processed classes don't need to be in the current classpath and don't need to be loaded via a ClassLoader
 * Only uses a ClassLoader if class is a library or JRE class
 */
public final class UnloadedClasspathStackMapFramesClassWriter extends ClassWriter {

    private final Map<String, ClassNode> cachedNodes;
    private final ClassPath classPath;

    public UnloadedClasspathStackMapFramesClassWriter(ClassPath classPath, int flags) {
        super(flags);
        this.cachedNodes = new HashMap<>();
        this.classPath = classPath;
    }

    public UnloadedClasspathStackMapFramesClassWriter(ClassPath classPath, ClassReader classReader, int flags) {
        super(classReader, flags);
        this.cachedNodes = new HashMap<>();
        this.classPath = classPath;
    }

    @Override
    protected String getCommonSuperClass(String type1, String type2) {
        final ClassNode type1Node = this.getSpec(type1);
        final ClassNode type2Node = this.getSpec(type2);
        if (Modifier.isInterface(type1Node.access)) {
            System.out.println("INTERFACE ABORT");
            return this.implementsInterface(type2Node, type1) ? type1 : Type.getInternalName(Object.class);
        }
        if (Modifier.isInterface(type2Node.access)) {
            System.out.println("INTERFACE ABORT");
            return this.implementsInterface(type1Node, type2) ? type2 : Type.getInternalName(Object.class);
        }
        final List<String> type1Hierarchy = this.getHierarchy(type1Node);
        final List<String> type2Hierarchy = this.getHierarchy(type2Node);
        type1Hierarchy.retainAll(type2Hierarchy);
        if (type1Hierarchy.isEmpty()) {
            System.out.println("COMMON: Object");
            return Type.getInternalName(Object.class);
        }
        final String s = type1Hierarchy.get(0);
        System.out.println("COMMON: " + s);
        return s;
    }

    private List<String> getHierarchy(ClassNode spec) {
        return Stream.iterate(spec, n -> !n.name.equals(Type.getInternalName(Object.class)),
                        n -> this.getSpec(n.superName)).map(n -> n.name).collect(Collectors.toList());
    }

    private boolean implementsInterface(ClassNode spec, String interfaceName) {
        ClassNode currentSpec = spec;
        while (!Type.getInternalName(Object.class).equals(spec.name)) {
            final List<String> interfaces = currentSpec.interfaces;
            if (interfaces.stream().anyMatch(name -> name.equals(interfaceName))) {
                return true;
            }
            if (interfaces.stream().anyMatch(name -> this.implementsInterface(this.getSpec(name), interfaceName))) {
                return true;
            }
            spec = this.getSpec(spec.superName);
        }
        return false;
    }

    private ClassNode getSpec(String name) {
        return this.cachedNodes.computeIfAbsent(name, n -> this.classPath.getByName(n).map(ClassPathEntry::source).orElseGet(() -> {
                    final ClassNode classNode = new ClassNode();
                    final InputStream is = this.getClass().getClassLoader()
                            .getResourceAsStream(name + Constants.CLASS_SUFFIX);
                    if (is == null) {
                        System.out.println(classNode.sourceFile);
                        throw new FinalizerException("Could not find class " + Converters.convertInternalToPackage(n));
                    }
                    try {
                        new ClassReader(is).accept(classNode, Opcodes.ASM9);
                    } catch (IOException ex) {
                        throw new FinalizerException("Could not find class " + Converters.convertInternalToPackage(n));
                    }
                    return classNode;
                }));
    }

}
