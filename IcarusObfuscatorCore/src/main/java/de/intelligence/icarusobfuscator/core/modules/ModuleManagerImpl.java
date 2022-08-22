package de.intelligence.icarusobfuscator.core.modules;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.TreeSet;

import de.intelligence.icarusobfuscator.core.annotation.ObfuscationProcessor;
import de.intelligence.icarusobfuscator.core.exception.InvalidModuleException;

public final class ModuleManagerImpl implements IModuleManager {

    private final Map<Class<?>, Object> allowedParameters;
    private final NavigableSet<ModuleInfo> modules;

    public ModuleManagerImpl() {
        this.allowedParameters = new HashMap<>();
        this.modules = new TreeSet<>(Comparator.comparing(ModuleInfo::priority));
    }

    @Override
    public void registerParameter(Class<?> clazz, Object param) {
        this.allowedParameters.put(clazz, param);
    }

    @Override
    public void registerModule(Class<? extends IObfuscationModule> moduleClass) {
        if (!moduleClass.isAnnotationPresent(ObfuscationProcessor.class)) {
            throw new InvalidModuleException("Module \"" + moduleClass.getName() + "\" is not annotated with @ObfuscationProcessor");
        }
        final ObfuscationProcessor obfuscationProcessor = moduleClass.getAnnotation(ObfuscationProcessor.class);
        final String name = obfuscationProcessor.name();
        if (this.modules.stream().anyMatch(module -> module.name().equals(name))) {
            throw new InvalidModuleException("Module \"" + moduleClass.getName() + "\" has the same name as another module");
        }
        this.getConstructor(moduleClass).ifPresent(constructor -> this.modules.add(new ModuleInfo(name,
                obfuscationProcessor.description(), obfuscationProcessor.priority(), (IObfuscationModule)
                this.createInstance(constructor))));
    }

    @Override
    public NavigableSet<ModuleInfo> getModules() {
        return this.modules;
    }

    private Optional<Constructor<?>> getConstructor(Class<? extends IObfuscationModule> moduleClass) {
        for (final Constructor<?> constructor : Arrays.stream(moduleClass.getDeclaredConstructors())
                .sorted(Comparator.<Constructor<?>, Integer>comparing(Constructor::getParameterCount)
                        .reversed()).toArray(Constructor<?>[]::new)) {
            if (constructor.getParameterCount() == 0) {
                return Optional.of(constructor);
            }
            if (Arrays.stream(constructor.getParameterTypes()).allMatch(this.allowedParameters::containsKey)) {
                return Optional.of(constructor);
            }
        }
        return Optional.empty();
    }

    private Object createInstance(Constructor<?> constructor) {
        constructor.setAccessible(true);
        try {
            return constructor.newInstance(Arrays.stream(constructor.getParameterTypes())
                    .map(this.allowedParameters::get)
                    .toArray(Object[]::new));
        } catch (ReflectiveOperationException ex) {
            throw new InvalidModuleException("Could not create instance of module \"" + constructor.getDeclaringClass().getName() + "\":", ex);
        }
    }

}
