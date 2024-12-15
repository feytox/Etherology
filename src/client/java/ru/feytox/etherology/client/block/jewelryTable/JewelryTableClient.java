package ru.feytox.etherology.client.block.jewelryTable;

import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import ru.feytox.etherology.block.jewelryTable.JewelryBlockEntity;
import ru.feytox.etherology.particle.effects.ElectricityParticleEffect;
import ru.feytox.etherology.particle.subtype.ElectricitySubtype;

@UtilityClass
public class JewelryTableClient {

    public static void clientTick(JewelryBlockEntity blockEntity, ClientWorld world, BlockPos blockPos, BlockState state) {
        if (!blockEntity.getInventory().hasRecipe()) return;
        if (world.getTime() % 4 != 0 || blockEntity.getStoredEther() == 0) return;

        val effect = ElectricityParticleEffect.of(world.getRandom(), ElectricitySubtype.JEWELRY);
        effect.spawnParticles(world, 2, 0.2d, blockPos.toCenterPos().add(0, 0.75d, 0));
    }
}
