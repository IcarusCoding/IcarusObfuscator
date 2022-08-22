package de.intelligence.icarusobfuscator.core.classpath;

public final class ManifestContext {

    private final ClassPathEntry<byte[]> manifestEntry;

    public ManifestContext(ClassPathEntry<byte[]> manifestEntry) {
        this.manifestEntry = manifestEntry;
    }

    public ClassPathEntry<byte[]> getManifestEntry() {
        return this.manifestEntry;
    }

    public String getMainClass() {
        // TODO
        return null;
    }

}
