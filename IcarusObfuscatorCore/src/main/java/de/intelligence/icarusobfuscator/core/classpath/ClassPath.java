package de.intelligence.icarusobfuscator.core.classpath;

import java.util.List;

import org.objectweb.asm.tree.ClassNode;

public record ClassPath(List<String> directories, List<ClassPathEntry<ClassNode>> classes, List<ClassPathEntry<byte[]>> resources) {}
