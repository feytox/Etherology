package ru.feytox.etherology.util.misc;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import lombok.RequiredArgsConstructor;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryOps;
import net.minecraft.util.Identifier;


@RequiredArgsConstructor(staticName = "of")
public class RegistryCodec<E extends IdProvider> implements Codec<E> {

    private final RegistryKey<? extends Registry<E>> registryKey;

    public <T> DataResult<T> encode(E input, DynamicOps<T> ops, T prefix) {
        if (!(ops instanceof RegistryOps<?> registryOps))
            return errorResult();
            
        var owner = registryOps.getOwner(this.registryKey);

        if (owner.isEmpty())
            return errorResult();
        return Identifier.CODEC.encode(input.getObjIdentifier(), ops, prefix);
    }

    public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> ops, T input) {
        if (!(ops instanceof RegistryOps<?> registryOps))
            return errorResult();

        var lookup = registryOps.getEntryLookup(this.registryKey);
        if (lookup.isEmpty())
            return errorResult();

        return Identifier.CODEC.decode(ops, input).flatMap(pair -> {
            var id = pair.getFirst();
            var element = lookup.get().getOptional(RegistryKey.of(this.registryKey, id)).map(entry -> entry.value());
            return element
                    .map(e -> DataResult.success(Pair.of(e, pair.getSecond())))
                    .orElseGet(() -> DataResult.error(() -> "Failed to get element " + id));
        });
    }

    private <R> DataResult<R> errorResult() {
        return DataResult.error(() -> "Can't access registry " + this.registryKey);
    }

    public String toString() {
        return "RegistryCodec[" + this.registryKey + "]";
    }
}
