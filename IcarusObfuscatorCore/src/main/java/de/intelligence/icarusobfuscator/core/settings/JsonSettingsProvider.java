package de.intelligence.icarusobfuscator.core.settings;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
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
        //todo filter fields that have no annotation
        try {
            return gson.fromJson(input, ObfuscatorSettings.class);
        } catch (JsonParseException e) {
            throw new SettingsException("Config from input could not be read", e);
        }
    }
}
