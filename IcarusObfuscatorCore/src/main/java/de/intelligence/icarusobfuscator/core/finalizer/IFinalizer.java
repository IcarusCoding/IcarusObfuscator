package de.intelligence.icarusobfuscator.core.finalizer;

import de.intelligence.icarusobfuscator.core.classpath.ClassPath;

public interface IFinalizer {

    void doFinalize(ClassPath classPath);

    String getDestination();

}
