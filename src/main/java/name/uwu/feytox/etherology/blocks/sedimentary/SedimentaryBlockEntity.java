package name.uwu.feytox.etherology.blocks.sedimentary;

import name.uwu.feytox.etherology.enums.SedimentaryStates;
import name.uwu.feytox.etherology.magic.zones.EssenceConsumer;
import name.uwu.feytox.etherology.magic.zones.EssenceZones;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static name.uwu.feytox.etherology.BlocksRegistry.SEDIMENTARY_BLOCK;
import static name.uwu.feytox.etherology.BlocksRegistry.SEDIMENTARY_BLOCK_ENTITY;
import static name.uwu.feytox.etherology.blocks.sedimentary.SedimentaryBlock.ESSENCE_LEVEL;
import static name.uwu.feytox.etherology.blocks.sedimentary.SedimentaryBlock.ESSENCE_STATE;

public class SedimentaryBlockEntity extends BlockEntity implements EssenceConsumer {
    private BlockPos cachedZonePos = null;
    private int consumingTicks = 0;
    private EssenceZones zoneType = EssenceZones.NULL;
    private int points = 0;

    public SedimentaryBlockEntity(BlockPos pos, BlockState state) {
        super(SEDIMENTARY_BLOCK_ENTITY, pos, state);
    }

    public static void clientTick(World world, BlockPos blockPos, BlockState state, BlockEntity blockEntity) {

    }

    public static void serverTick(World world, BlockPos blockPos, BlockState state, BlockEntity blockEntity) {
        ServerWorld serverWorld = (ServerWorld) world;
        SedimentaryBlockEntity sedimentaryBlockEntity = (SedimentaryBlockEntity) blockEntity;

        sedimentaryBlockEntity.consumingTick(serverWorld);
    }

    public void consumingTick(ServerWorld world) {
        if (consumingTicks++ % 20 != 0) return;

        tickConsume(world);

        float k = points / 128f;
        BlockState state = SEDIMENTARY_BLOCK.getDefaultState();
        SedimentaryStates sedState = SedimentaryStates.getFromZone(zoneType);

        if (k == 0 || sedState.equals(SedimentaryStates.EMPTY)) {
            world.setBlockState(pos, state);
        } else if (k < 1/3f) {
            world.setBlockState(pos, state.with(ESSENCE_STATE, sedState).with(ESSENCE_LEVEL, 1));
        } else if (k < 2/3f) {
            world.setBlockState(pos, state.with(ESSENCE_STATE, sedState).with(ESSENCE_LEVEL, 2));
        } else {
            world.setBlockState(pos, state.with(ESSENCE_STATE, sedState).with(ESSENCE_LEVEL, 3));
        }
    }

    @Override
    public float getConsumingValue() {
        return 5;
    }

    @Override
    public float getRadius() {
        return 15;
    }

    @Override
    public BlockPos getConsumerPos() {
        return pos;
    }

    @Nullable
    @Override
    public BlockPos getCachedZonePos() {
        return cachedZonePos;
    }

    @Override
    public void setCachedCorePos(BlockPos blockPos) {
        cachedZonePos = blockPos;
    }

    @Override
    public EssenceZones getZoneType() {
        return zoneType;
    }

    @Override
    public void setZoneType(EssenceZones zoneType) {
        this.zoneType = zoneType;
    }

    @Override
    public void increment(float value) {
        points += value;
        System.out.println(value);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        zoneType.writeNbt(nbt);
        nbt.putInt("points", points);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        zoneType = EssenceZones.readFromNbt(nbt);
        points = nbt.getInt("points");
    }

    @Override
    public void setEmpty() {
        if (points == 0) EssenceConsumer.super.setEmpty();
    }

    @Override
    public boolean isEmpty() {
        return points == 0;
    }
}
