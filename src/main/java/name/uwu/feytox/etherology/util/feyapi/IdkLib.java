package name.uwu.feytox.etherology.util.feyapi;

import name.uwu.feytox.etherology.components.IFloatComponent;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

import static name.uwu.feytox.etherology.EtherologyComponents.ETHER_POINTS;

/**
 * random 'useful' things
 */

public class IdkLib {
    public static boolean isExhaustion(PlayerEntity player) {
        Optional<IFloatComponent> component = ETHER_POINTS.maybeGet(player);
        return component.filter(iFloatComponent -> iFloatComponent.getValue() < 5).isPresent();
    }

    public static void tickExhaustion(PlayerEntity player) {
        if (!isExhaustion(player)) return;
        float ether_points = ETHER_POINTS.get(player).getValue();

        if (ether_points < 5 && ether_points >= 2.5F) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 1));
        } else if (ether_points < 2.5F && ether_points >= 1) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 200, 2));
        } else if (ether_points < 1) {
            float multiplier = player.world.getDifficulty().getId() + 1;
            player.damage(DamageSource.MAGIC, multiplier);
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 200, 3));
        }
    }

    /**
     * took from Vanilla Chiseled Bookshelf
     * @see ChiseledBookshelfBlock
     */
    public static Optional<Vec2f> getHitPos(BlockHitResult hit, Direction facing) {
        Direction direction = hit.getSide();
        if (facing != direction) {
            return Optional.empty();
        } else {
            BlockPos blockPos = hit.getBlockPos().offset(direction);
            Vec3d vec3d = hit.getPos().subtract(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            double d = vec3d.getX();
            double e = vec3d.getY();
            double f = vec3d.getZ();

            return switch (direction) {
                case NORTH -> Optional.of(new Vec2f((float) (1.0 - d), (float) e));
                case SOUTH -> Optional.of(new Vec2f((float) d, (float) e));
                case WEST -> Optional.of(new Vec2f((float) f, (float) e));
                case EAST -> Optional.of(new Vec2f((float) (1.0 - f), (float) e));
                case DOWN, UP -> Optional.empty();
            };
        }
    }
}
