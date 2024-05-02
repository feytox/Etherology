package ru.feytox.etherology.registry.custom;

import net.minecraft.util.Identifier;

@Deprecated
public interface EtherRegistrable {
    default void register(Identifier identifier) {
        EtherologyRegistry.register(identifier, this);
    }
}
