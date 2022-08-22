package de.intelligence.icarusobfuscator.core.settings;

import de.intelligence.icarusobfuscator.core.exception.ClassPathException;
import de.intelligence.icarusobfuscator.core.exception.SettingsException;
import de.intelligence.icarusobfuscator.core.provider.JarFileClassPathProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Heinrich Töpfer (heinrich.toepfer@uni-oldenburg.de)
 */
class JsonSettingsProviderTest {

    @Test
    public void testJson() {
        JsonSettingsProvider provider = new JsonSettingsProvider("{\"allowDuplicatesInClassPath\":false,\"owner\":\"test\"}");
        var settings = provider.provideSettings();
        assertFalse(settings.isAllowDuplicatesInClassPath());
        assertEquals("test", settings.getOwner());
    }

    @Test
    public void testJsonOnlyOne() {
        JsonSettingsProvider provider = new JsonSettingsProvider("{\"allowDuplicatesInClassPath\":false}");
        var settings = provider.provideSettings();
        assertFalse(settings.isAllowDuplicatesInClassPath());
    }

    @Test
    public void onWrongJson_ThrowSettingsException() {
        assertThrows(SettingsException.class, () -> {
            JsonSettingsProvider provider = new JsonSettingsProvider("{JSON}");
            var settings = provider.provideSettings();
        });


    }

}