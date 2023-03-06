package name.uwu.feytox.etherology.blocks.sedimentary;

import name.uwu.feytox.etherology.enums.SedimentaryStates;
import name.uwu.feytox.etherology.magic.zones.EssenceConsumer;
import name.uwu.feytox.etherology.magic.zones.EssenceSupplier;
import name.uwu.feytox.etherology.magic.zones.EssenceZones;
import name.uwu.feytox.etherology.particle.SparkParticle;
import name.uwu.feytox.etherology.util.feyapi.EIdentifier;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static name.uwu.feytox.etherology.BlocksRegistry.SEDIMENTARY_BLOCK;
import static name.uwu.feytox.etherology.BlocksRegistry.SEDIMENTARY_BLOCK_ENTITY;
import static name.uwu.feytox.etherology.blocks.sedimentary.SedimentaryBlock.ESSENCE_LEVEL;
import static name.uwu.feytox.etherology.blocks.sedimentary.SedimentaryBlock.ESSENCE_STATE;

public class SedimentaryBlockEntity extends BlockEntity implements EssenceConsumer {
    private static final float MAX_POINTS = 32.0f;
    EssenceSupplier cachedSupplier = null;
    private int consumingTicks = 0;
    private EssenceZones zoneType = EssenceZones.NULL;
    private float points = 0;

    public SedimentaryBlockEntity(BlockPos pos, BlockState state) {
        super(SEDIMENTARY_BLOCK_ENTITY, pos, state);
    }

    public boolean onUseAxe(BlockState state, World world, PlayerEntity player) {
        if (state.get(ESSENCE_LEVEL) < 4) return false;
        if (!world.isClient) {
            points = 0;
            markDirty();
            checkState((ServerWorld) world);

            Optional<Item> match = Registries.ITEM.getOrEmpty(new EIdentifier("primoshard_" + zoneType.name().toLowerCase()));
            match.ifPresent(item ->
                    ItemScatterer.spawn(world, pos.getX(), pos.getY() + 1, pos.getZ(), item.getDefaultStack()));
        } else {
            EssenceZones essenceZone = state.get(ESSENCE_STATE).getZoneType();
            SparkParticle.spawnSedimentaryParticle((ClientWorld) world, pos, essenceZone);
        }
        world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.BLOCKS, 1.0f, 1.0f, true);
        return true;
    }

    public static void serverTick(World world, BlockPos blockPos, BlockState state, BlockEntity blockEntity) {
        ServerWorld serverWorld = (ServerWorld) world;
        SedimentaryBlockEntity sedimentaryBlockEntity = (SedimentaryBlockEntity) blockEntity;

        sedimentaryBlockEntity.consumingTick(serverWorld);
    }

    public void consumingTick(ServerWorld world) {
        if (consumingTicks++ % 10*20 != 0 || points >= MAX_POINTS) return;
        tickConsume();
        checkState(world);
    }

    public void checkState(ServerWorld world) {
        float k = points / MAX_POINTS;
        BlockState state = SEDIMENTARY_BLOCK.getDefaultState();
        SedimentaryStates sedState = SedimentaryStates.getFromZone(zoneType);

        if (k < 1/4f) {
            world.setBlockState(pos, state);
        } else if (k > 0) {
            world.setBlockState(pos, state.with(ESSENCE_STATE, sedState).with(ESSENCE_LEVEL, MathHelper.floor(4*k)));
        }
    }

    @Override
    public float getConsumingValue() {
        return 0.5f;
    }

    @Override
    public float getRadius() {
        return 15;
    }

    @Override
    public BlockPos getDetectablePos() {
        return pos;
    }

    @Nullable
    @Override
    public EssenceSupplier getCachedSupplier() {
        return cachedSupplier;
    }

    @Override
    public void setCachedSupplier(EssenceSupplier supplier) {
        cachedSupplier = supplier;
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
        points = Math.min(MAX_POINTS, points);
        System.out.println(value);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        zoneType.writeNbt(nbt);
        nbt.putFloat("points", points);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        zoneType = EssenceZones.readFromNbt(nbt);
        points = nbt.getFloat("points");
    }

    @Override
    public void setEmpty() {
        if (points == 0) EssenceConsumer.super.setEmpty();
    }

    @Override
    public boolean isEmpty() {
        return points == 0;
    }

    @Override
    public boolean isDead() {
        return isRemoved();
    }

    @Override
    public void markDead() {
        markRemoved();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}
