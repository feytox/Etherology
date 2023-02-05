package name.uwu.feytox.etherology.blocks.zone_blocks;

import name.uwu.feytox.etherology.magic.zones.EssenceZones;
import name.uwu.feytox.etherology.particle.MovingParticle;
import name.uwu.feytox.etherology.util.nbt.NbtBlockPos;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static name.uwu.feytox.etherology.BlocksRegistry.ZONE_BLOCK_ENTITY;
import static name.uwu.feytox.etherology.Etherology.ZONE_PARTICLE;

public class ZoneBlockEntity extends BlockEntity {
    private final EssenceZones zone;
    private BlockPos currentCorePos = null;
    private int particleTicks = 121;

    public ZoneBlockEntity(BlockPos pos, BlockState state) {
        super(ZONE_BLOCK_ENTITY, pos, state);
        zone = EssenceZones.NULL;
    }

    public ZoneBlockEntity(EssenceZones zone, BlockPos pos, BlockState state) {
        super(ZONE_BLOCK_ENTITY, pos, state);
        this.zone = zone;
    }

    public void setCurrentCorePos(BlockPos currentCorePos) {
        this.currentCorePos = currentCorePos;
        markDirty();
    }

    public void randomDisplayTick(ClientWorld world, Random random) {
//        if (particleTicks++ < 120) return;
        particleTicks = 0;

        ZoneCoreBlockEntity zoneCore = getCore(world);
        if (zoneCore == null) return;

        float k = zoneCore.getPoints() / 128;

        if (random.nextDouble() > k) return;

        int count = MathHelper.ceil(5 * k);

        MovingParticle.spawnParticles(world, ZONE_PARTICLE, count, 0.5,
                pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5,
                pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, random);
    }

    @Nullable
    public ZoneCoreBlockEntity getCore(World world) {
        if (currentCorePos == null) return null;
        BlockEntity blockEntity = world.getBlockEntity(currentCorePos);
        if (blockEntity instanceof ZoneCoreBlockEntity zoneCore) {
            return zoneCore;
        }
        return null;
    }

    public float decrement(ServerWorld world, float value) {
        ZoneCoreBlockEntity zoneCore = getCore(world);
        float result = zoneCore == null ? 0 : zoneCore.decrement(value);
        if (result == 0) markRemoved();
        return result;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        if (currentCorePos != null) new NbtBlockPos("current_core_pos", currentCorePos).writeNbt(nbt);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        currentCorePos = NbtBlockPos.readFromNbt("current_core_pos", nbt);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}
