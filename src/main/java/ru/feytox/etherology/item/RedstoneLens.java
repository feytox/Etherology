package ru.feytox.etherology.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import ru.feytox.etherology.entity.redstoneBlob.RedstoneBlob;
import ru.feytox.etherology.magic.lense.LensComponent;
import ru.feytox.etherology.magic.lense.RedstoneLensEffects;
import ru.feytox.etherology.registry.item.ToolItems;
import ru.feytox.etherology.util.deprecated.EVec3d;

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

        EVec3d.lineOf(entity.getBoundingBox().getCenter().add(entity.getHandPosOffset(ToolItems.STAFF)), hitPos.toCenterPos(), 0.33d)
                .forEach(pos -> serverWorld.spawnParticles(new DustParticleEffect(DustParticleEffect.RED, 0.5f), pos.x, pos.y, pos.z, 1, 0, 0, 0, 0));

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

        Vec3d entityRotation = entity.getRotationVec(0.1f);
        RedstoneBlob blob = new RedstoneBlob(world, entity.getX(), entity.getEyeY(), entity.getZ(), entityRotation, 5, charge);
        world.spawnEntity(blob);
    }
}
