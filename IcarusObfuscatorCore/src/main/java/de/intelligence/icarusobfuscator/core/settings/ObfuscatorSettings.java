package de.intelligence.icarusobfuscator.core.settings;

public final class ObfuscatorSettings {

    private boolean allowDuplicatesInClassPath = false;
    private String owner = "de\\/uol\\/swp.*";

    public boolean isAllowDuplicatesInClassPath() {
        return this.allowDuplicatesInClassPath;
    }

    public String getOwner() {
        return this.owner;
    }

}
