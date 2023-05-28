package ru.feytox.etherology.registry.custom;

import com.google.common.collect.ImmutableMap;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EtherologyRegistry {

    @Nullable
    private static ImmutableMap<Identifier, ERegistryEntry<?>> registry = null;

    @Nullable
    private static Map<Identifier, ERegistryEntry<?>> registryBuilder = new HashMap<>();

    private static final Map<Class<?>, List<?>> registryCache = new HashMap<>();

    public static <T> T register(Identifier identifier, T value) {
        if (registryBuilder == null) throw new RuntimeException("It is not possible to register a value after the registry has been initialized.");
        registryBuilder.put(identifier, new ERegistryEntry<>(identifier, value));
        return value;
    }

    public static void buildRegistry() {
        if (registryBuilder == null || registry != null) throw new RuntimeException("It is not possible to build a registry multiple times.");
        ImmutableMap.Builder<Identifier, ERegistryEntry<?>> builder = ImmutableMap.builder();
        builder.putAll(registryBuilder);
        registry = builder.build();
        registryBuilder = null;
    }

    public static Optional<?> get(Identifier identifier) {
        if (registry == null) throw new RuntimeException("The registry has not been completed yet.");
        ERegistryEntry<?> result = registry.getOrDefault(identifier, null);
        return Optional.ofNullable(result);
    }

    @Nullable
    public static <T> T getAndCast(Class<T> cls, Identifier identifier) {
        if (registry == null) throw new RuntimeException("The registry has not been completed yet.");
        ERegistryEntry<?> result = registry.getOrDefault(identifier, null);
        if (result == null) return null;
        Object value = result.getValue();
        if (cls.isInstance(value)) return cls.cast(value);
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> getAll(Class<T> cls) {
        if (registry == null) throw new RuntimeException("The registry has not been completed yet.");
        List<T> cache = (List<T>) registryCache.getOrDefault(cls, null);
        if (cache != null) return cache;

        List<T> result = new ArrayList<>();
        registry.forEach((id, entry) -> {
            Object value = entry.getValue();
            if (cls.isInstance(value)) result.add(cls.cast(value));
        });
        registryCache.put(cls, result);
        return result;
    }
}
