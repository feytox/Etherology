package ru.feytox.etherology.client.util;

import lombok.RequiredArgsConstructor;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import ru.feytox.etherology.util.misc.ClientSideObject;
import ru.feytox.etherology.util.misc.TickableBlockEntity;

@RequiredArgsConstructor
public abstract class ClientTickableBlock<T extends TickableBlockEntity> implements ClientSideObject {

    protected final T blockEntity;

    public abstract void clientTick(ClientWorld world, BlockPos blockPos, BlockState state);
}
