package de.intelligence.icarusobfuscator.core.classpath.state;

import org.objectweb.asm.tree.ClassNode;

public final class ClassState {

    private final ClassStateType type;
    private final String name;
    private final ClassNode classNode;

    public ClassState(ClassStateType type, String name, ClassNode classNode) {
        this.type = type;
        this.name = name;
        this.classNode = classNode;
    }

    public static ClassState create(ClassNode node, ClassStateType classStateType) {
        return new ClassState(classStateType, node.name, node);
    }

    public ClassStateType getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public ClassNode getClassNode() {
        return this.classNode;
    }

}
