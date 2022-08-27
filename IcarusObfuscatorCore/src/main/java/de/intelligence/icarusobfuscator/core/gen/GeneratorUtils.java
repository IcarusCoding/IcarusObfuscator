package de.intelligence.icarusobfuscator.core.gen;

import java.util.Arrays;

import org.objectweb.asm.Type;

public final class GeneratorUtils {

    private GeneratorUtils() {}

    public static String generateMethodDescriptor(Class<?> returnValue, Class<?>... parameters) {
        return Type.getMethodDescriptor(Type.getType(returnValue), Arrays.stream(parameters).map(Type::getType)
                .toArray(Type[]::new));
    }

    @SafeVarargs
    public static String[] generateExceptions(Class<? extends Exception>... exceptions) {
        return Arrays.stream(exceptions).map(Type::getInternalName).toArray(String[]::new);
    }

}
