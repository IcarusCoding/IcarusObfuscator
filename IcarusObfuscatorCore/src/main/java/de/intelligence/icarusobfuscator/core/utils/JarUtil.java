package de.intelligence.icarusobfuscator.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.jar.JarFile;

import de.intelligence.icarusobfuscator.core.Constants;
import de.intelligence.icarusobfuscator.core.exception.ClassPathException;

public final class JarUtil {

    private JarUtil() {
    }

    public static JarFile validate(File jarInput) {
        try (PushbackInputStream jarIn = new PushbackInputStream(new FileInputStream(jarInput), 2)) {
            final byte[] magic = jarIn.readNBytes(2);
            if (magic.length < 2 || magic[0] != Constants.JAR_MAGIC[0] || magic[1] != Constants.JAR_MAGIC[1]) {
                throw new ClassPathException("Provided file \"" + jarInput.getAbsolutePath() + "\" is not a valid jar file");
            }
            jarIn.unread(magic);
            return new JarFile(jarInput);
        } catch (IOException ex) {
            throw new ClassPathException("An exception occurred while reading file \"" + jarInput + "\"", ex);
        }
    }

}
