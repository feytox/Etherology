package ru.feytox.etherology.magic.aspects;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.val;
import net.minecraft.util.Identifier;

@Getter
@EqualsAndHashCode(callSuper = true)
public class AspectContainerId extends Identifier {

    private final AspectContainerType containerType;

    private AspectContainerId(String namespace, String path, AspectContainerType containerType) {
        super(namespace, path);
        this.containerType = containerType;
    }

    public AspectContainerId(String id, AspectContainerType containerType) {
        super(id);
        this.containerType = containerType;
    }

    public static AspectContainerId of(String id) {
        String[] splitStr = id.split(":", 2);
        val containerType = AspectContainerType.getPrefixToType().getOrDefault(splitStr[0], AspectContainerType.ITEM);
        String resultId = id;
        if (containerType.getPrefix() != null) resultId = splitStr[1];
        return new AspectContainerId(resultId, containerType);
    }

    public static AspectContainerId of(Identifier id, AspectContainerType containerType) {
        return new AspectContainerId(id.getNamespace(), id.getPath(), containerType);
    }
}
