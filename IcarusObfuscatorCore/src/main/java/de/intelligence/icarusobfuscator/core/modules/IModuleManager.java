package de.intelligence.icarusobfuscator.core.modules;

import java.util.NavigableSet;

public interface IModuleManager {

    void registerParameter(Class<?> clazz, Object param);

    void registerModule(Class<? extends IObfuscationModule> moduleClass);

    NavigableSet<ModuleInfo> getModules();

}
