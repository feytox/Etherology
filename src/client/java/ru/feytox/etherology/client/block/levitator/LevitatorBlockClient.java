package ru.feytox.etherology.client.block.levitator;

import lombok.experimental.UtilityClass;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import ru.feytox.etherology.block.levitator.LevitatorBlockEntity;

@UtilityClass
public class LevitatorBlockClient {

    public static void clientTick(LevitatorBlockEntity blockEntity, ClientWorld world, BlockPos blockPos, BlockState state) {
        blockEntity.tickLevitation(world, blockPos, state);
    }
}
