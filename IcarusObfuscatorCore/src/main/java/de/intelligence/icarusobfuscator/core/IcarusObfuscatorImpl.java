package de.intelligence.icarusobfuscator.core;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import de.intelligence.icarusobfuscator.core.classpath.ClassPathStructureControllerImpl;
import de.intelligence.icarusobfuscator.core.settings.ObfuscatorSettings;

public final class IcarusObfuscatorImpl implements IIcarusObfuscator {

    private final ObfuscatorSettings settings;

    public IcarusObfuscatorImpl(ObfuscatorSettings settings) {
        this.settings = settings;
    }

    @Override
    public void obfuscate() {
        Arrays.stream(new String(Constants.BANNER, StandardCharsets.UTF_8).split("\n"))
                .forEach(IIcarusObfuscator.LOG::info);
        IIcarusObfuscator.LOG.info(" ");
        IIcarusObfuscator.LOG.info("Initializing obfuscator with settings: ...");
    }

}
