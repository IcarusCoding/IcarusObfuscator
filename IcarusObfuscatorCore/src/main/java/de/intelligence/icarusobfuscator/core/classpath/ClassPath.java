package de.intelligence.icarusobfuscator.core.classpath;

import java.util.List;
import java.util.Optional;

import org.objectweb.asm.tree.ClassNode;

public record ClassPath(List<String> directories, List<ClassPathEntry<ClassNode>> classes,
                        List<ClassPathEntry<byte[]>> resources) {

    public Optional<ClassPathEntry<ClassNode>> getByName(String internalName) {
        return this.classes.stream().filter(entry -> entry.name().equals(internalName)).findAny();
    }

}
