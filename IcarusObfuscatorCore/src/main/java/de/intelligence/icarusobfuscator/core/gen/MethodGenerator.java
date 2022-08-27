package de.intelligence.icarusobfuscator.core.gen;

import java.util.Arrays;
import java.util.stream.IntStream;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import de.intelligence.icarusobfuscator.core.Constants;

public final class MethodGenerator {

    private final MethodVisitor methodVisitor;
    private final int access;
    private final String name;
    private final String descriptor;
    private final Type[] arguments;

    public MethodGenerator(MethodVisitor methodVisitor, int access, String name, String descriptor) {
        this.methodVisitor = methodVisitor;
        this.access = access;
        this.name = name;
        this.descriptor = descriptor;
        this.arguments = Type.getArgumentTypes(this.descriptor);
    }

    public void loadImplicitSelfReference() {
        this.methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
    }

    public int getStackFrameArgumentIndex(int index) {
        return IntStream.range(0, index).map(i -> arguments[i].getSize()).sum() +
                ((this.access & Opcodes.ACC_STATIC) != 0 ? 0 : 1);
    }

    public void loadArgument(int index) {
        this.methodVisitor.visitVarInsn(this.arguments[index].getOpcode(Opcodes.ILOAD),
                this.getStackFrameArgumentIndex(index));
    }

    public void loadArguments(int... indices) {
        Arrays.stream(indices).forEach(this::loadArgument);
    }

    public void loadNullReference() {
        this.methodVisitor.visitInsn(Opcodes.ACONST_NULL);
    }

    public void callConstructor(Class<?> owner, Class<?>... params) {
        this.callConstructor(owner, GeneratorUtils.generateMethodDescriptor(void.class, params));
    }

    public void callConstructor(Class<?> owner, String descriptor) {
        this.callMethod(Opcodes.INVOKESPECIAL, Type.getInternalName(owner), Constants.INIT_NAME, descriptor,
                false);
    }

    public void callStatic(Class<?> owner, String name, Class<?> ret, Class<?>... params) {
        this.callMethod(Opcodes.INVOKESTATIC, Type.getInternalName(owner), name,
                GeneratorUtils.generateMethodDescriptor(ret, params), false);
    }

    public void callVirtual(Class<?> owner, String name, Class<?> ret, Class<?>... params) {
        this.callMethod(Opcodes.INVOKEVIRTUAL, Type.getInternalName(owner), name,
                GeneratorUtils.generateMethodDescriptor(ret, params), false);
    }

    public void callSuper(Class<?> owner, String name, Class<?> ret, Class<?>... params) {
        this.callMethod(Opcodes.INVOKESPECIAL, Type.getInternalName(owner), name,
                GeneratorUtils.generateMethodDescriptor(ret, params), false);
    }

    public void callMethod(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        this.methodVisitor.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }

    public void setNonStaticField(String owner, String name, Class<?> fieldType) {
        this.setNonStaticField(owner, name, Type.getInternalName(fieldType));
    }

    public void setNonStaticField(Class<?> owner, String name, Class<?> fieldType) {
        this.setNonStaticField(Type.getInternalName(owner), name, Type.getInternalName(fieldType));
    }

    public void setNonStaticField(String owner, String name, String descriptor) {
        this.methodVisitor.visitFieldInsn(Opcodes.PUTFIELD, owner, name, descriptor);
    }

    public void setStaticField(String owner, String name, Class<?> fieldType) {
        this.setStaticField(owner, name, Type.getInternalName(fieldType));
    }

    public void setStaticField(Class<?> owner, String name, Class<?> fieldType) {
        this.setStaticField(Type.getInternalName(owner), name, Type.getInternalName(fieldType));
    }

    public void setStaticField(String owner, String name, String descriptor) {
        this.methodVisitor.visitFieldInsn(Opcodes.PUTSTATIC, owner, name, descriptor);
    }

    public void createArray(Class<?> type) {
        final Type t = Type.getType(type);
        final int i = switch (t.getSort()) {
            case Type.BOOLEAN -> Opcodes.T_BOOLEAN;
            case Type.CHAR -> Opcodes.T_CHAR;
            case Type.BYTE -> Opcodes.T_BYTE;
            case Type.SHORT -> Opcodes.T_SHORT;
            case Type.INT -> Opcodes.T_INT;
            case Type.FLOAT -> Opcodes.T_FLOAT;
            case Type.LONG -> Opcodes.T_LONG;
            case Type.DOUBLE -> Opcodes.T_DOUBLE;
            default -> -1;
        };
        if (i == -1) {
            this.methodVisitor.visitTypeInsn(Opcodes.ANEWARRAY, t.getInternalName());
        } else {
            this.methodVisitor.visitIntInsn(Opcodes.NEWARRAY, i);
        }
    }

    public void stackPush(int value) {
        if (value > 0 && value < 6) {
            this.methodVisitor.visitInsn(Opcodes.ICONST_0 + value);
        } else if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
            this.methodVisitor.visitIntInsn(Opcodes.BIPUSH, value);
        } else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
            this.methodVisitor.visitIntInsn(Opcodes.SIPUSH, value);
        } else {
            this.methodVisitor.visitLdcInsn(value);
        }
    }

    public void stackPush(String value) {
        if (value == null) {
            this.loadNullReference();
        }
        this.methodVisitor.visitLdcInsn(value);
    }

    public void duplicate() {
        this.methodVisitor.visitInsn(Opcodes.DUP);
    }

    public void arrayStore(Class<?> type) {
        this.methodVisitor.visitInsn(Type.getType(type).getOpcode(Opcodes.IASTORE));
    }

    public void localStore(Class<?> type, int index) {
        this.methodVisitor.visitVarInsn(Type.getType(type).getOpcode(Opcodes.ISTORE), index);
    }

    public void localLoad(Class<?> type, int index) {
        this.methodVisitor.visitVarInsn(Type.getType(type).getOpcode(Opcodes.ILOAD), index);
    }

    public void ret() {
        this.methodVisitor.visitInsn(Type.getReturnType(this.descriptor).getOpcode(Opcodes.IRETURN));
    }

    public void finish() {
        this.methodVisitor.visitMaxs(0, 0);
        this.methodVisitor.visitEnd();
    }

}
