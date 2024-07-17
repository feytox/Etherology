package ru.feytox.etherology.magic.aspects;

import lombok.*;
import net.minecraft.util.Identifier;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AspectContainerId {

    private final Identifier id;
    private final AspectContainerType containerType;

    public static AspectContainerId of(String id) {
        String[] splitStr = id.split(":", 2);
        val containerType = AspectContainerType.getPrefixToType().getOrDefault(splitStr[0], AspectContainerType.ITEM);
        return new AspectContainerId(Identifier.of(containerType.getPrefix() != null ? splitStr[1] : id), containerType);
    }

    public static AspectContainerId of(Identifier id, AspectContainerType containerType) {
        return new AspectContainerId(id, containerType);
    }
}
