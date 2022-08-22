package de.intelligence.icarusobfuscator.core.settings;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Heinrich Töpfer (heinrich.toepfer@uni-oldenburg.de)
 */
class CommandLineSettingsProviderTest {

    @Test
    public void whenUsingProvider_getConfig(){
        CommandLineSettingsProvider provider = new CommandLineSettingsProvider();

        System.setProperty("allowDuplicatesInClassPath", "false");
        System.setProperty("owner", "test");
        var settingsNew = provider.provideSettings();

        assertFalse(settingsNew.isAllowDuplicatesInClassPath());
        assertEquals("test", settingsNew.getOwner());

        System.setProperty("allowDuplicatesInClassPath", "true");
        System.setProperty("owner", "deineMama");
        settingsNew = provider.provideSettings();

        assertTrue(settingsNew.isAllowDuplicatesInClassPath());
        assertEquals("deineMama", settingsNew.getOwner());
    }

}