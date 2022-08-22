package de.intelligence.icarusobfuscator.core.provider;

import de.intelligence.icarusobfuscator.core.exception.ClassPathException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * @author Heinrich Töpfer (heinrich.toepfer@uni-oldenburg.de)
 */
@ExtendWith(MockitoExtension.class)
class JarFileClassPathProviderTest {

    @Test
    void whenProviderIsCreated_thenAssertClassPathExceptionThrown() throws IOException {
        File file = File.createTempFile("test", "test");
        file.deleteOnExit();
        assertThrows(ClassPathException.class, () -> {
            JarFileClassPathProvider provider = new JarFileClassPathProvider(file);
        });
    }

    @Test
    void whenProviderIsCreated_thenTestJarIsReadSuccessfully() {
        final String path = "testjar.jar";
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(path).getFile());
        JarFileClassPathProvider provider = new JarFileClassPathProvider(file);
        assertTrue(provider.getSource().endsWith(path));
    }

    @Test
    void whenProviderIsCreated_thenReadProvider() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("testjar.jar").getFile());
        JarFileClassPathProvider provider = new JarFileClassPathProvider(file);
        var cp = provider.provide();
        assertFalse(cp.classes().isEmpty());
        assertFalse(cp.directories().isEmpty());
        assertFalse(cp.resources().isEmpty());
    }
}