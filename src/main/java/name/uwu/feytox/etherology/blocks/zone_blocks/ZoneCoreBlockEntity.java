package name.uwu.feytox.etherology.blocks.zone_blocks;

import name.uwu.feytox.etherology.BlocksRegistry;
import name.uwu.feytox.etherology.magic.zones.EssenceZones;
import name.uwu.feytox.etherology.particle.ZoneParticle;
import name.uwu.feytox.etherology.util.nbt.NbtBlockPos;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static name.uwu.feytox.etherology.BlocksRegistry.*;

public class ZoneCoreBlockEntity extends BlockEntity {
    private BlockPos corePos;
    private float maxPoints;
    private float points;
    private ZoneBlock fillBlock = null;
    private List<BlockPos> cachedFilledBlocks = new ArrayList<>();
    private boolean shouldRegenerate = false;
    private int serverTicks;
    private int particleTicks = 121;

    public ZoneCoreBlockEntity(BlockPos pos, BlockState state) {
        super(BlocksRegistry.ZONE_CORE_BLOCK_ENTITY, pos, state);
        this.corePos = pos;

        // TODO: 17/02/2023 убери финально
        // TODO: 01/02/2023 убери test режим
//        setup(EssenceZones.KETA, 128);
    }

    public void setup(EssenceZones zone, float maxPoints) {
        setType(zone);
        this.maxPoints = maxPoints;
        this.points = maxPoints;
    }

    public boolean isTruePos() {
        return corePos.equals(pos);
    }

    public boolean isServerTick() {
        return serverTicks++ % 100 == 0;
    }

    public float getPoints() {
        return points;
    }

    public static void clientTick(World world, BlockPos blockPos, BlockState state, BlockEntity blockEntity) {
        ClientWorld clientWorld = (ClientWorld) world;
        ZoneCoreBlockEntity zoneCore = (ZoneCoreBlockEntity) blockEntity;

        zoneCore.particleTick(clientWorld);
    }

    public static void serverTick(World world, BlockPos blockPos, BlockState state, BlockEntity blockEntity) {
        ServerWorld serverWorld = (ServerWorld) world;
        ZoneCoreBlockEntity zoneCore = (ZoneCoreBlockEntity) blockEntity;


        if (zoneCore.isServerTick()) {
            zoneCore.checkExisting(serverWorld);
            zoneCore.generateZone(serverWorld, false);
        }
    }

    public void checkExisting(ServerWorld world) {
        boolean isEmpty = maxPoints != 0 && points == 0;
        if (!corePos.equals(pos) || isEmpty) {
            BlockState state = world.getBlockState(corePos);
            if (isEmpty || (state.isAir() && !state.isOf(ZONE_CORE_BLOCK))) {
                world.setBlockState(corePos, ZONE_CORE_BLOCK.getDefaultState());
                ZoneCoreBlockEntity newBlock = (ZoneCoreBlockEntity) world.getBlockEntity(pos);
                if (newBlock != null) newBlock.copy(this);

                world.setBlockState(pos, Blocks.AIR.getDefaultState());
                markRemoved();
                clearZone(world);
            }
        }
    }

    public void clearZone(ServerWorld world) {
        cachedFilledBlocks.forEach(blockPos -> {
            BlockState state = world.getBlockState(blockPos);
            if (state.isOf(KETA_ZONE_BLOCK) && state.isOf(RELA_ZONE_BLOCK) && state.isOf(CLOS_ZONE_BLOCK) && state.isOf(VIA_ZONE_BLOCK)) {
                world.breakBlock(blockPos, false);
            }
        });
    }

    public void generateZone(ServerWorld world, boolean isForceGenerate) {
        List<BlockPos> blocksForFill = getFillingZone(world, 256 * maxPoints);

        if (isForceGenerate || shouldRegenerate || !blocksForFill.equals(cachedFilledBlocks)) {
            blocksForFill.forEach(blockPos -> {
                world.setBlockState(blockPos, fillBlock.getDefaultState());
                Optional<ZoneBlockEntity> match = world.getBlockEntity(blockPos, ZONE_BLOCK_ENTITY);
                match.ifPresent(zoneBlock -> zoneBlock.setCurrentCorePos(pos));
            });
            cachedFilledBlocks = blocksForFill;
        }
    }

    public void particleTick(ClientWorld world) {
//        if (particleTicks++ < 120) return;
        particleTicks = 0;
        ZoneParticle.spawnParticles(world, points, pos);
    }

    private List<BlockPos> getFillingZone(ServerWorld world, float blockPoints) {
        int height = 32;
        while (height > 8 && blockPoints / height < height * height) {
            height--;
        }

        int width = MathHelper.floor(MathHelper.sqrt(blockPoints / height));
        int length = MathHelper.floor(blockPoints / (height * width));

        BlockPos minPos = corePos.add(-width / 2, -height / 2, -length / 2);
        BlockPos maxPos = corePos.add(width / 2 - 1, height / 2 - 1, length / 2 - 1);
        Iterator<BlockPos> blockIter = BlockPos.iterate(minPos, maxPos).iterator();

        List<BlockPos> result = new ArrayList<>();
        while (blockIter.hasNext()) {
            BlockPos blockPos = blockIter.next();
            BlockState state = world.getBlockState(blockPos);
            if (state.isAir() && !state.isOf(ZONE_CORE_BLOCK)) {
                result.add(new BlockPos(blockPos));
            }
        }

        return result;
    }

    public void setCorePos(BlockPos corePos) {
        this.corePos = corePos;
    }

    public void setPoints(float points) {
        this.points = points;
        markDirty();
    }

    public void increment(float value) {
        setPoints(points + value);
    }

    public float decrement(float value) {
        float min = Math.min(value, points);
        setPoints(points - min);
        return min;
    }

    public void setType(EssenceZones zone) {
        fillBlock = zone.getFillBlock();
        markDirty();
    }

    public void copy(ZoneCoreBlockEntity oldBlock) {
        corePos = oldBlock.corePos;
        points = oldBlock.points;
        fillBlock = oldBlock.fillBlock;
        cachedFilledBlocks = oldBlock.cachedFilledBlocks;
        shouldRegenerate = true;
        markDirty();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        new NbtBlockPos("core_pos", corePos).writeNbt(nbt);
        nbt.putFloat("points", points);
        nbt.putString("fill_block", Registries.BLOCK.getId(fillBlock).toString());
        nbt.putBoolean("should_regenerate", shouldRegenerate);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        corePos = NbtBlockPos.readFromNbt("core_pos", nbt);
        points = nbt.getFloat("points");
        fillBlock = (ZoneBlock) Registries.BLOCK.get(new Identifier(nbt.getString("fill_block")));
        shouldRegenerate = nbt.getBoolean("should_regenerate");
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}
