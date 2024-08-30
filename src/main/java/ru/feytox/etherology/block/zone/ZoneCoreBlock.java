package ru.feytox.etherology.block.zone;

import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.magic.zones.EssenceZoneType;
import ru.feytox.etherology.registry.block.EBlocks;

import java.util.List;
import java.util.Optional;

@Getter
public class ZoneCoreBlock extends Block implements BlockEntityProvider {

    private static final float ROTATE_ANGLE = 45.0f;
    private final EssenceZoneType zoneType;

    public ZoneCoreBlock(EssenceZoneType zoneType) {
        super(Settings.copy(Blocks.BEDROCK).noBlockBreakParticles().noCollision());
        this.zoneType = zoneType;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        return tryRotateZone(world, pos, player).orElse(super.onUse(state, world, pos, player, hit));
    }

    private Optional<ActionResult> tryRotateZone(World world, BlockPos pos, PlayerEntity player) {
        if (!(world.getBlockEntity(pos) instanceof ZoneCoreBlockEntity zoneCore)) return Optional.empty();

        zoneCore.setPitch(toAngle(player.getPitch()));
        zoneCore.setYaw(toAngle(-player.getYaw()));

        if (world instanceof ServerWorld serverWorld) zoneCore.syncData(serverWorld);
        return Optional.of(ActionResult.success(world.isClient));
    }

    private float toAngle(float playerAngle) {
        return Math.round(playerAngle / ROTATE_ANGLE) * ROTATE_ANGLE;
    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        Vec3d collisionVec = entity.getBoundingBox().getCenter().subtract(pos.toCenterPos());
        collisionVec = collisionVec.multiply(1 / Math.max(0.001d, collisionVec.length()))
                .multiply(0.2d);

        Vec3d oldVelocity = entity.getVelocity();
        Vec3d newVelocity = oldVelocity.add(collisionVec);
        entity.setVelocity(newVelocity);

        if (!world.isClient && entity instanceof ServerPlayerEntity player && player.velocityModified) {
            player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player));
            player.velocityModified = false;
            player.setVelocity(oldVelocity);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        super.appendTooltip(stack, context, tooltip, options);
        tooltip.add(1, Text.translatable("lore.etherology.primoshard", StringUtils.capitalize(zoneType.asString())).formatted(Formatting.DARK_PURPLE));
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return ZoneCoreBlockEntity.getTicker(world, type, EBlocks.ZONE_CORE_BLOCK_ENTITY);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ZoneCoreBlockEntity(pos, state, zoneType);
    }

    @Override
    public String getTranslationKey() {
        return "block.etherology.zone_core";
    }

    public static String createId(EssenceZoneType zoneType) {
        return "zone_core_" + zoneType.asString();
    }
}
