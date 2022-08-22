package de.intelligence.icarusobfuscator.core.settings;

import com.google.gson.Gson;
import de.intelligence.icarusobfuscator.core.exception.SettingsException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @author Heinrich Töpfer (heinrich.toepfer@uni-oldenburg.de)
 */
public class JsonSettingsProvider implements ISettingsProvider {

    private final String input;
    private final Gson gson = new Gson();

    public JsonSettingsProvider(File file) {
        try {
            this.input = Files.readString(file.toPath());
        } catch (IOException e) {
            throw new SettingsException("Config from file \"" + file.getPath() + "\" could not be read", e);
        }
    }

    public JsonSettingsProvider(String input) {
        this.input = input;
    }

    @Override
    public ObfuscatorSettings provideSettings() {
        return gson.fromJson(input, ObfuscatorSettings.class);
    }
}
