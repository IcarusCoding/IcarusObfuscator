package de.intelligence.icarusobfuscator.core.settings;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Heinrich Töpfer (heinrich.toepfer@uni-oldenburg.de)
 */
class JsonSettingsProviderTest {

    @Test
    public void testJson(){
        JsonSettingsProvider provider = new JsonSettingsProvider("{\"allowDuplicatesInClassPath\":false,\"owner\":\"test\"}");
        var settings = provider.provideSettings();
        assertFalse(settings.isAllowDuplicatesInClassPath());
        assertEquals("test", settings.getOwner());
    }

}