package ru.feytox.etherology.blocks.etherealGenerators;

import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.magic.ether.EtherStorage;
import ru.feytox.etherology.magic.zones.EssenceDetectable;
import ru.feytox.etherology.magic.zones.EssenceSupplier;
import ru.feytox.etherology.util.feyapi.TickableBlockEntity;
import ru.feytox.etherology.util.gecko.EGeoBlockEntity;
import ru.feytox.etherology.util.gecko.EGeoNetwork;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import static ru.feytox.etherology.blocks.etherealGenerators.AbstractEtherealGenerator.STALLED;

public abstract class AbstractEtherealGeneratorBlockEntity extends TickableBlockEntity
        implements EtherStorage, EssenceDetectable, EGeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private float storedEther;
    private int nextGenTime = 40*20;
    private boolean isInZone = false;
    private boolean isLaunched = false;

    public AbstractEtherealGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void unstall(ServerWorld world, BlockState state) {
        world.setBlockState(pos, state.with(STALLED, false));

        EGeoNetwork.sendStopAnim(world, pos, "stalled");
        EGeoNetwork.sendStartAnim(world, pos, "spin");
    }

    @Override
    public void clientTick(ClientWorld world, BlockPos blockPos, BlockState state) {
        if (!isLaunched) triggerAnim("spin");
    }

    @Override
    public void serverTick(ServerWorld world, BlockPos blockPos, BlockState state) {
        transferTick(world);
        generateTick(world);
    }

    public void generateTick(ServerWorld world) {
        if (getCachedState().get(STALLED)) return;

        Random random = world.getRandom();
        if (nextGenTime-- > 0) {
            if (isInZone) nextGenTime -= random.nextBetween(0, 1);
            return;
        }
        increment(1);

        if (getCachedState().getBlock() instanceof AbstractEtherealGenerator generator) {
            int min = generator.getMinCooldown();
            int max = generator.getMaxCooldown();
            nextGenTime = random.nextBetween(min, max);

            if (random.nextDouble() <= generator.getStopChance(isInZone)) {
                world.setBlockState(pos, world.getBlockState(pos).with(STALLED, true));
            }
        }
    }

    @Override
    public float getMaxEther() {
        return 1;
    }

    @Override
    public float getStoredEther() {
        return storedEther;
    }

    @Override
    public float getTransferSize() {
        return 1;
    }

    @Override
    public void setStoredEther(float value) {
        storedEther = value;
    }

    @Override
    public boolean isInputSide(Direction side) {
        return false;
    }

    @Override
    public Direction getOutputSide() {
        // FIXME: 22/03/2023 исправить баг с определением оутпут сайда
        Direction output = getCachedState().get(FacingBlock.FACING);
        return !output.equals(Direction.DOWN) && !output.equals(Direction.UP) ? output.getOpposite() : output;
    }

    @Override
    public BlockPos getStoragePos() {
        return pos;
    }

    @Override
    public void transferTick(ServerWorld world) {
        if (world.getTime() % 5 == 0) transfer(world);
    }

    @Override
    public boolean isActivated() {
        return false;
    }

    @Override
    public float getRadius() {
        return 15;
    }

    @Override
    public boolean sync(EssenceSupplier supplier) {
        isInZone = true;
        return true;
    }

    @Override
    public void unsync(EssenceSupplier supplier) {
        isInZone = false;
    }

    @Override
    public BlockPos getDetectablePos() {
        return pos;
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
    protected void writeNbt(NbtCompound nbt) {
        nbt.putFloat("stored_ether", storedEther);
        nbt.putBoolean("is_in_zone", isInZone);
        nbt.putInt("next_gen_time", nextGenTime);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        storedEther = nbt.getFloat("stored_ether");
        isInZone = nbt.getBoolean("is_in_zone");
        nextGenTime = nbt.getInt("next_gen_time");
    }

    public abstract RawAnimation getSpinAnimation();
    public abstract RawAnimation getStalledAnimation();

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(getTriggerController("spin", getSpinAnimation()));
        controllers.add(getTriggerController("stalled", getStalledAnimation()));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
