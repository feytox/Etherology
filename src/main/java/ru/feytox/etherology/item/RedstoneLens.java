package ru.feytox.etherology.item;

import lombok.val;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import ru.feytox.etherology.entity.redstoneBlob.RedstoneBlob;
import ru.feytox.etherology.magic.lens.LensComponent;
import ru.feytox.etherology.magic.lens.RedstoneLensEffects;
import ru.feytox.etherology.network.interaction.RedstoneLensStreamS2C;
import ru.feytox.etherology.registry.item.ToolItems;
import ru.feytox.etherology.util.misc.PlayerSessionData;

import java.util.function.Supplier;

public class RedstoneLens extends LensItem {

    @Override
    public boolean onStreamUse(World world, LivingEntity entity, LensComponent lensData, boolean hold, Supplier<Hand> handGetter) {
        if (world.isClient || !(world instanceof ServerWorld serverWorld)) return false;

        val playerData = PlayerSessionData.get(entity);
        if (playerData == null) return false;

        playerData.redstoneStreamTicks -= 1;
        if (playerData.redstoneStreamTicks > 0) return false;

        HitResult hitResult = entity.raycast(32.0f, 1.0f, false);
        if (!(hitResult instanceof BlockHitResult blockHitResult)) return false;
        if (!hold) entity.setCurrentHand(handGetter.get());

        playerData.redstoneStreamTicks = 16;
        boolean isMiss = !hitResult.getType().equals(HitResult.Type.BLOCK);
        if (!isMiss) {
            BlockPos hitPos = blockHitResult.getBlockPos();
            RedstoneLensEffects.getServerState(serverWorld).addUsage(serverWorld, hitPos, 3, 10);
        }

        Vec3d startPos = entity.getBoundingBox().getCenter().add(entity.getHandPosOffset(ToolItems.STAFF));
        val packet = new RedstoneLensStreamS2C(startPos, blockHitResult.getPos(), isMiss);
        if (entity instanceof ServerPlayerEntity player) {
            packet.sendForTrackingAndSelf(player);
            return true;
        }

        packet.sendForTracking(serverWorld, entity.getBlockPos(), 0);
        return true;
    }

    @Override
    public void onStreamStop(World world, LivingEntity entity, LensComponent lensData, int holdTicks, Supplier<Hand> handGetter) {
        val data = PlayerSessionData.get(entity);
        if (data == null) return;
        data.redstoneStreamTicks = 0;
    }

    @Override
    public boolean onChargeUse(World world, LivingEntity entity, LensComponent lensData, boolean hold, Supplier<Hand> handGetter) {
        if (world.isClient || !(world instanceof ServerWorld serverWorld)) return false;
        if (!lensData.checkCooldown(serverWorld)) return false;
        if (hold) return true;

        entity.setCurrentHand(handGetter.get());
        return true;

    }


    @Override
    public void onChargeStop(World world, LivingEntity entity, LensComponent lensData, int holdTicks, Supplier<Hand> handGetter) {
        if (world.isClient || !(world instanceof ServerWorld serverWorld)) {
            entity.swingHand(handGetter.get());
            return;
        }

        lensData.incrementCooldown(serverWorld, 60);
        if (holdTicks == 0) return;

        Vec3d entityRotation = entity.getRotationVec(0.1f);
        RedstoneBlob blob = new RedstoneBlob(world, entity.getX(), entity.getEyeY(), entity.getZ(), entityRotation, 5, holdTicks);
        world.spawnEntity(blob);
    }
}
