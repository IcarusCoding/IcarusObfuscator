package de.intelligence.icarusobfuscator.core.gen;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import de.intelligence.icarusobfuscator.core.utils.Converters;

public final class ClassGenerator {

    private final ClassWriter classWriter;
    private final String className;

    public ClassGenerator(int access, String name, Class<?> superClass) {
        this(access, name, null, superClass, null);
    }

    public ClassGenerator(int access, String name, String sig, Class<?> superClass, String[] interfaces) {
        this(access, name, sig, Type.getInternalName(superClass), interfaces);
    }

    public ClassGenerator(int access, String name, String sig, String superName, String[] interfaces) {
        this.classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        this.className = name;
        this.classWriter.visit(Opcodes.V17, access, this.className, sig, superName, interfaces);
    }

    public void generateField(int access, String name, Class<?> type) {
        this.generateField(access, name, type, null, null);
    }

    public void generateField(int access, String name, String descriptor) {
        this.generateField(access, name, descriptor, null, null);
    }

    public void generateField(int access, String name, Class<?> type, String sig, Object initialValue) {
        this.generateField(access, name, Type.getDescriptor(type), sig, initialValue);
    }

    public void generateField(int access, String name, String descriptor, String sig, Object initialValue) {
        this.classWriter.visitField(access, name, descriptor, sig, initialValue);
    }

    public MethodGenerator generateMethod(int access, String name, Class<?> ret, Class<?>... params) {
        return this.generateMethod(access, name, null, ret, params);
    }

    public MethodGenerator generateMethod(int access, String name, String[] exceptions, Class<?> ret, Class<?>... params) {
        return this.generateMethod(access, name, null, exceptions, ret, params);
    }

    public MethodGenerator generateMethod(int access, String name, String sig, String[] exceptions, Class<?> ret,
                                          Class<?>... params) {
        final String descriptor = GeneratorUtils.generateMethodDescriptor(ret, params);
        return new MethodGenerator(this.classWriter.visitMethod(access, name, descriptor, sig, exceptions), access,
                name, descriptor);
    }

    public void finish() {
        this.classWriter.visitEnd();
    }

    public ClassWriter getClassWriter() {
        return this.classWriter;
    }

    public byte[] toByteArray() {
        return this.classWriter.toByteArray();
    }

    public String getInternalClassName() {
        return this.className;
    }

    public String getPackageClassName() {
        return Converters.convertInternalToPackage(this.className);
    }

}
