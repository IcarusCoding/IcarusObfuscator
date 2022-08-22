package de.intelligence.icarusobfuscator.core;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import de.intelligence.icarusobfuscator.core.classpath.ClassPathStructureControllerImpl;
import de.intelligence.icarusobfuscator.core.classpath.IClassPathStructureController;
import de.intelligence.icarusobfuscator.core.provider.IClassPathProvider;
import de.intelligence.icarusobfuscator.core.settings.ObfuscatorSettings;

public final class IcarusObfuscatorImpl implements IIcarusObfuscator {

    private final ObfuscatorSettings settings;
    private final IClassPathProvider classPathProvider;

    public IcarusObfuscatorImpl(ObfuscatorSettings settings, IClassPathProvider classPathProvider) {
        this.settings = settings;
        this.classPathProvider = classPathProvider;
    }

    @Override
    public void obfuscate() {
        Arrays.stream(new String(Constants.BANNER, StandardCharsets.UTF_8).split("\n"))
                .forEach(IIcarusObfuscator.LOG::info);
        IIcarusObfuscator.LOG.info(" ");
        IClassPathStructureController controller = new ClassPathStructureControllerImpl(this.settings, this.classPathProvider);
        controller.init();
    }

}
