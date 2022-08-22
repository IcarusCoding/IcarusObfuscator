package de.intelligence.icarusobfuscator.core.settings;

import de.intelligence.icarusobfuscator.core.annotation.ConfigValue;

import java.net.URL;

public final class ObfuscatorSettings {

    @ConfigValue
    private Boolean allowDuplicatesInClassPath = true;
    @ConfigValue
    private String owner = "de\\/uol\\/swp.*";

    public boolean isAllowDuplicatesInClassPath() {
        return this.allowDuplicatesInClassPath;
    }

    public String getOwner() {
        return this.owner;
    }

}
