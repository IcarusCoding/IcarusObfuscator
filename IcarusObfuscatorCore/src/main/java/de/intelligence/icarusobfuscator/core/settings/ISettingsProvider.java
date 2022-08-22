package de.intelligence.icarusobfuscator.core.settings;

import de.intelligence.icarusobfuscator.core.exception.SettingsException;

/**
 * @author Heinrich Töpfer (heinrich.toepfer@uni-oldenburg.de)
 */
public interface ISettingsProvider {

    ObfuscatorSettings provideSettings() throws SettingsException;

}
