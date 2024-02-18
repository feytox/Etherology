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

import java.util.function.Supplier;

public class RedstoneLens extends LensItem {

    @Override
    public boolean onStreamUse(World world, LivingEntity entity, LensComponent lenseData, boolean hold, Supplier<Hand> handGetter) {
        if (world.isClient || !(world instanceof ServerWorld serverWorld)) return false;
        
        HitResult hitResult = entity.raycast(32.0f, 1.0f, false);
        if (!hitResult.getType().equals(HitResult.Type.BLOCK)) return false;
        if (!(hitResult instanceof BlockHitResult blockHitResult)) return false;
        if (!hold) entity.setCurrentHand(handGetter.get());

        BlockPos hitPos = blockHitResult.getBlockPos();
        RedstoneLensEffects.getServerState(serverWorld).addUsage(serverWorld, hitPos, 5, 4);
        // TODO: 31.12.2023 optimize or not
        if (world.getTime() % 3 != 0) return true;

        Vec3d startPos = entity.getBoundingBox().getCenter().add(entity.getHandPosOffset(ToolItems.STAFF));
        val packet = new RedstoneLensStreamS2C(startPos, blockHitResult.getPos());
        if (entity instanceof ServerPlayerEntity player) {
            packet.sendForTrackingAndSelf(player);
            return true;
        }

        packet.sendForTracking(serverWorld, entity.getBlockPos(), 0);
        return true;
    }

    @Override
    public boolean onChargeUse(World world, LivingEntity entity, LensComponent lenseData, boolean hold, Supplier<Hand> handGetter) {
        if (world.isClient || !(world instanceof ServerWorld serverWorld)) return false;
        if (!lenseData.checkCooldown(serverWorld)) return false;
        if (hold) return true;

        entity.setCurrentHand(handGetter.get());
        return true;

    }


    @Override
    public void onChargeStop(World world, LivingEntity entity, LensComponent lenseData, int holdTicks, Supplier<Hand> handGetter) {
        if (world.isClient || !(world instanceof ServerWorld serverWorld)) {
            entity.swingHand(handGetter.get());
            return;
        }

        lenseData.incrementCooldown(serverWorld, 60);
        if (holdTicks == 0) return;

        Vec3d entityRotation = entity.getRotationVec(0.1f);
        RedstoneBlob blob = new RedstoneBlob(world, entity.getX(), entity.getEyeY(), entity.getZ(), entityRotation, 5, holdTicks);
        world.spawnEntity(blob);
    }
}
