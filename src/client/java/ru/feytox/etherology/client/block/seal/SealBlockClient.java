package ru.feytox.etherology.client.block.seal;

import lombok.experimental.UtilityClass;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import ru.feytox.etherology.block.seal.SealBlockEntity;

@UtilityClass
public class SealBlockClient {

    private static final int REFRESH_TIME = SealBlockRenderer.LIFETIME / 4;

    public static void clientTick(SealBlockEntity blockEntity, ClientWorld world, BlockPos blockPos, BlockState state) {
        if (world.getTime() % REFRESH_TIME == 0)
            SealBlockRenderer.refreshSeal(blockEntity, blockPos, blockEntity.getSealType(), world.getTime());
    }
}
