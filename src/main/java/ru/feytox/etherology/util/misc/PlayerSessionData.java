package ru.feytox.etherology.util.misc;

import lombok.NoArgsConstructor;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

// TODO: 17.05.2024 Consider replacing this with something else or remove this todo.
// non-persistent data for PlayerEntity
@NoArgsConstructor
public class PlayerSessionData {

    @Nullable
    public static PlayerSessionData get(Entity entity) {
        if (!(entity instanceof PlayerSessionDataProvider dataProvider)) return null;
        return dataProvider.etherology$getData();
    }

    public int redstoneStreamTicks;
}
