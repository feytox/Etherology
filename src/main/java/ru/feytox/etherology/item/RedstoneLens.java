package ru.feytox.etherology.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.feytox.etherology.magic.lense.LensComponent;

public class RedstoneLens extends LensItem {

    @Override
    public boolean onStreamUse(World world, LivingEntity entity, LensComponent lenseData, boolean hold) {
        if (world.isClient) return true;

        int cooldown = lenseData.getCooldown();
        if (cooldown > 0) {
            entity.sendMessage(Text.of("Reloading: " + cooldown + " ticks"));
            return false;
        }

        // TODO: 29.11.2023 use projectile
        HitResult hitResult = entity.raycast(16.0f, 1.0f, false);
        if (!hitResult.getType().equals(HitResult.Type.BLOCK)) return false;
        if (!(hitResult instanceof BlockHitResult blockHitResult)) return false;

        BlockPos hitPos = blockHitResult.getBlockPos();
        BlockState state = world.getBlockState(hitPos);
        if (!state.contains(Properties.LIT)) return false;

        boolean result = world.setBlockState(hitPos, state.with(Properties.LIT, true), Block.NOTIFY_ALL);
        if (!result) return false;

        lenseData.incrementCooldown(100, 100);
        return true;
    }

    @Override
    public boolean onChargeUse(World world, LivingEntity entity, LensComponent lenseData, boolean hold) {
        if (world.isClient) return true;
        if (!hold) return true;


        lenseData.incrementCooldown(2, 100);
        int cooldown = lenseData.getCooldown();
        entity.sendMessage(Text.of("Charge: " + cooldown + "%"));
        return true;
    }
}
