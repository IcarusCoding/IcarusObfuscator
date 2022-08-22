package de.intelligence.icarusobfuscator.core.settings;

import de.intelligence.icarusobfuscator.core.annotation.ConfigValue;
import de.intelligence.icarusobfuscator.core.exception.SettingsException;

import java.io.File;
import java.util.Arrays;

/**
 * Set settings from command line properties. Search the Obfuscation settings for fields and corresponding command line properties.
 *
 * @author Heinrich Töpfer (heinrich.toepfer@uni-oldenburg.de)
 */
public class CommandLineSettingsProvider implements ISettingsProvider {
    @Override
    public ObfuscatorSettings provideSettings() throws SettingsException {

        ObfuscatorSettings settings = new ObfuscatorSettings();
        Class<? extends ObfuscatorSettings> settingsClass = settings.getClass();
        Arrays.stream(settingsClass.getDeclaredFields())
                .filter(field -> System.getProperties().containsKey(field.getName()))
                .filter(field -> field.getAnnotation(ConfigValue.class) != null)
                .forEach(field -> {
                    try {
                        var value = System.getProperty(field.getName());
                        field.setAccessible(true);
                        switch (field.getType().getSimpleName()) {
                            case "boolean", "Boolean" -> field.set(settings, Boolean.parseBoolean(value));
                            case "int", "Integer" -> field.set(settings, Integer.parseInt(value));
                            case "String" -> field.set(settings, value);
                            case "File" -> field.set(settings, new File(value));
                            case "float", "Float" -> field.set(settings, Float.parseFloat(value));
                            case "double", "Double" -> field.set(settings, Double.parseDouble(value));
                            default -> throw new SettingsException("Unknown type: " + field.getType().getSimpleName());
                        }
                    } catch (IllegalAccessException ex) {
                        throw new SettingsException("Could not set value for field \"" + field.getName() + "\": " + ex.getMessage(), ex);
                    }
                });
        return settings;
    }
}
