
package ru.feytox.etherology.magic.aspects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.registry.misc.RegistriesRegistry;
import ru.feytox.etherology.util.misc.IdProvider;
import ru.feytox.etherology.util.misc.RegistryCodec;

import java.util.Optional;

public record AspectNew(String name, Identifier id, Identifier textureId) implements IdProvider {

    public static final Codec<AspectNew> CODEC;
    public static final Codec<AspectNew> JSON_CODEC;

    @Override
    public Identifier getObjIdentifier() {
        return id;
    }

    static {
        CODEC = RegistryCodec.of(RegistriesRegistry.ASPECT);

        JSON_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("name").forGetter(AspectNew::name),
                Identifier.CODEC.fieldOf("id").forGetter(AspectNew::id),
                Identifier.CODEC.optionalFieldOf("texture").forGetter(a -> Optional.of(a.textureId))
        ).apply(instance, (name, id, textureOpt) -> {
            var textureId = textureOpt.orElseGet(() -> {
                var modId = id.getNamespace();
                var aspectId = id.getPath();
                return Identifier.of(modId, "gui/aspect/" + aspectId);
            });

            return new AspectNew(name, id, textureId);
        }));
    }
}
