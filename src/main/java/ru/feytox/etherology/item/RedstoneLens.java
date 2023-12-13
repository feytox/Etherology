package ru.feytox.etherology.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.feytox.etherology.magic.lense.LensComponent;
import ru.feytox.etherology.magic.lense.RedstoneLensEffects;

public class RedstoneLens extends LensItem {

    @Override
    public boolean onStreamUse(World world, LivingEntity entity, LensComponent lenseData, boolean hold) {
        if (world.isClient || !(world instanceof ServerWorld serverWorld)) return false;

        // TODO: 29.11.2023 use projectile
        HitResult hitResult = entity.raycast(32.0f, 1.0f, false);
        if (!hitResult.getType().equals(HitResult.Type.BLOCK)) return false;
        if (!(hitResult instanceof BlockHitResult blockHitResult)) return false;

        BlockPos hitPos = blockHitResult.getBlockPos();
        RedstoneLensEffects.getServerState(serverWorld).addUsage(serverWorld, hitPos, 5, 4);

        return true;
    }

    @Override
    public boolean onChargeUse(World world, LivingEntity entity, LensComponent lenseData, boolean hold) {
        if (world.isClient || !(world instanceof ServerWorld serverWorld)) return false;
        if (!lenseData.checkCooldown(serverWorld)) {
            entity.sendMessage(Text.of("Cooldown!"));
            return false;
        }
        if (!hold) return true;

        lenseData.incrementCharge(1, 120);
        entity.sendMessage(Text.of("Increment charge"));

        return true;
    }


    @Override
    public void onChargeStop(World world, LivingEntity entity, LensComponent lenseData) {
        if (world.isClient || !(world instanceof ServerWorld serverWorld)) return;

        int charge = lenseData.getCharge();
        lenseData.setCharge(0);
        lenseData.incrementCooldown(serverWorld, 60);
        if (charge == 0) return;

        HitResult hitResult = entity.raycast(32.0f, 1.0f, false);
        if (!hitResult.getType().equals(HitResult.Type.BLOCK)) return;
        if (!(hitResult instanceof BlockHitResult blockHitResult)) return;

        BlockPos hitPos = blockHitResult.getBlockPos();
        RedstoneLensEffects.getServerState(serverWorld).addUsage(serverWorld, hitPos, 5, charge);
    }
}
