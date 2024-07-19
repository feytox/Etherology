package ru.feytox.etherology.magic.aspects;

import com.mojang.serialization.Codec;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.Identifier;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AspectContainerId {

    public static final Codec<AspectContainerId> CODEC = Codec.STRING.xmap(AspectContainerId::of, AspectContainerId::toString).stable();

    private final Identifier id;
    private final AspectContainerType containerType;

    public static AspectContainerId of(String id) {
        String[] splitStr = id.split(":", 2);
        AspectContainerType containerType = AspectContainerType.getByPrefix(splitStr[0], AspectContainerType.ITEM);
        return new AspectContainerId(Identifier.of(containerType.getPrefix() != null ? splitStr[1] : id), containerType);
    }

    public static AspectContainerId of(Identifier id, AspectContainerType containerType) {
        return new AspectContainerId(id, containerType);
    }

    @Override
    public String toString() {
        String prefix = containerType.getPrefix();
        return (prefix != null ? prefix + ":" : "") + id.toString();
    }
}
