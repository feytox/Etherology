package ru.feytox.etherology.registry.custom;

import net.minecraft.util.Identifier;

public interface EtherRegistrable {
    default void register(Identifier identifier) {
        EtherologyRegistry.register(identifier, this);
    }
}
