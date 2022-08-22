package de.intelligence.icarusobfuscator.core.classpath;

import java.util.jar.Manifest;

public final class ManifestContext {

    private final Manifest manifest;
    private final ClassPathEntry<byte[]> manifestEntry;

    public ManifestContext(Manifest manifest, ClassPathEntry<byte[]> manifestEntry) {
        this.manifest = manifest;
        this.manifestEntry = manifestEntry;
    }

    public Manifest getManifest() {
        return this.manifest;
    }

    public ClassPathEntry<byte[]> getManifestEntry() {
        return this.manifestEntry;
    }

}
