package de.intelligence.icarusobfuscator.core.utils;

public final class Converters {

    private Converters() {}

    public static String convertInternalToPackage(String internal) {
        return internal.replace('/', '.');
    }

    public static String convertPackageToInternal(String packageName) {
        return packageName.replace('.', '/');
    }

}
