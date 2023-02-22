package name.uwu.feytox.etherology.blocks.zone_blocks;

import name.uwu.feytox.etherology.BlocksRegistry;
import name.uwu.feytox.etherology.magic.zones.EssenceConsumer;
import name.uwu.feytox.etherology.magic.zones.EssenceSupplier;
import name.uwu.feytox.etherology.magic.zones.EssenceZones;
import name.uwu.feytox.etherology.particle.ZoneParticle;
import name.uwu.feytox.etherology.util.nbt.NbtBlockPos;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static name.uwu.feytox.etherology.BlocksRegistry.ZONE_CORE_BLOCK;

public class ZoneCoreBlockEntity extends BlockEntity implements EssenceSupplier {
    public static final int ZONE_RADIUS = 16;
    private BlockPos corePos;
    private float maxPoints;
    private float points;
    private EssenceZones zoneType = EssenceZones.NULL;
    private List<BlockPos> cachedFillingZone = new ArrayList<>();
    private int particleRenewTicks = 0;
    private int refreshTicks = 0;
    private List<EssenceConsumer> cachedConsumers;

    public ZoneCoreBlockEntity(BlockPos pos, BlockState state) {
        super(BlocksRegistry.ZONE_CORE_BLOCK_ENTITY, pos, state);
        this.corePos = pos;

        // FIXME: 21/02/2023 УБРАТЬ ПОСЛЕ ТЕСТОВ!!!!!!!!!!!!!!!
        setup(EssenceZones.RELA, 128);
    }

    public void setup(EssenceZones zone, float maxPoints) {
        setType(zone);
        this.maxPoints = maxPoints;
        this.points = maxPoints;
    }

    public boolean isTruePos() {
        return corePos.equals(pos);
    }

    public float getPoints() {
        return points;
    }

    @Override
    public int getCheckRadius() {
        return ZONE_RADIUS + EssenceConsumer.MAX_RADIUS - 1;
    }

    @Override
    public BlockPos getSupplierPos() {
        return corePos;
    }

    @Override
    public List<EssenceConsumer> getCachedConsumers() {
        return cachedConsumers;
    }

    @Override
    public void setCachedConsumers(List<EssenceConsumer> consumers) {
        cachedConsumers = consumers;
    }

    public static void clientTick(World world, BlockPos blockPos, BlockState state, BlockEntity blockEntity) {
        ClientWorld clientWorld = (ClientWorld) world;
        ZoneCoreBlockEntity zoneCore = (ZoneCoreBlockEntity) blockEntity;

        zoneCore.particleTick(clientWorld);
    }

    public static void serverTick(World world, BlockPos blockPos, BlockState state, BlockEntity blockEntity) {
        ServerWorld serverWorld = (ServerWorld) world;
        ZoneCoreBlockEntity zoneCore = (ZoneCoreBlockEntity) blockEntity;

        zoneCore.checkExisting(serverWorld);
        zoneCore.tickRefresh(serverWorld);
    }

    @Override
    public void tickRefresh(ServerWorld world) {
        if (refreshTicks++ % 3 * 20 == 0) refreshConsumers(world);
    }

    public void checkExisting(ServerWorld world) {
        if (maxPoints != 0 && points == 0) {
            world.setBlockState(this.pos, Blocks.AIR.getDefaultState());
            markRemoved();
        }
    }

    public void particleTick(ClientWorld world) {
        List<BlockPos> particleBlocks = getFillingZone(world);
        Random random = Random.create();
        particleBlocks.forEach(blockPos -> ZoneParticle.spawnParticles(world, points, zoneType, blockPos, random));
    }

    public List<BlockPos> getFillingZone(ClientWorld world) {
        // TODO: 22/02/2023 рассмотреть вопрос с различными настройками оптимизации
        if (cachedFillingZone.isEmpty() || particleRenewTicks++ % 2 * 20 == 0) {
            Vec3i radiusVec = new Vec3i(ZONE_RADIUS, ZONE_RADIUS, ZONE_RADIUS);
            BlockPos minPos = corePos.subtract(radiusVec);
            BlockPos maxPos = corePos.add(radiusVec);
            Iterator<BlockPos> blockIter = BlockPos.iterate(minPos, maxPos).iterator();

            List<BlockPos> result = new ArrayList<>();
            while (blockIter.hasNext()) {
                BlockPos blockPos = blockIter.next();
                BlockState state = world.getBlockState(blockPos);
                if (state.isAir() || state.isOf(ZONE_CORE_BLOCK)) {
                    result.add(new BlockPos(blockPos));
                }
            }
            cachedFillingZone = result;
        }
        return cachedFillingZone;
    }

    public void setCorePos(BlockPos corePos) {
        this.corePos = corePos;
    }

    public void setPoints(float points) {
        this.points = points;
        markDirty();

        if (this.world != null && this.world instanceof ServerWorld serverWorld) {
            serverWorld.getChunkManager().markForUpdate(pos);
        }
    }

    public void increment(float value) {
        setPoints(points + value);
    }

    @Override
    public float decrement(float value) {
        float min = Math.min(value, points);
        setPoints(points - min);
        return min;
    }

    @Override
    public EssenceZones getZoneType() {
        return zoneType;
    }

    public void setType(EssenceZones zone) {
        zoneType = zone;
        markDirty();
    }

    public void copy(ZoneCoreBlockEntity oldBlock) {
        corePos = oldBlock.corePos;
        points = oldBlock.points;
        markDirty();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        new NbtBlockPos("core_pos", corePos).writeNbt(nbt);
        nbt.putFloat("points", points);
        zoneType.writeNbt(nbt);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        zoneType = EssenceZones.readFromNbt(nbt);
        corePos = NbtBlockPos.readFromNbt("core_pos", nbt);
        points = nbt.getFloat("points");
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public boolean isDead() {
        return isRemoved();
    }

    @Override
    public void markDead() {
        markRemoved();
    }
}
