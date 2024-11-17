package ru.feytox.etherology.block.generators;

import lombok.val;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.item.OculusItem;
import ru.feytox.etherology.magic.ether.EtherStorage;
import ru.feytox.etherology.magic.seal.EssenceDetector;
import ru.feytox.etherology.magic.seal.EssenceSupplier;
import ru.feytox.etherology.network.animation.StartBlockAnimS2C;
import ru.feytox.etherology.network.animation.StopBlockAnimS2C;
import ru.feytox.etherology.particle.effects.LightParticleEffect;
import ru.feytox.etherology.particle.subtypes.LightSubtype;
import ru.feytox.etherology.registry.particle.EtherParticleTypes;
import ru.feytox.etherology.util.gecko.EGeoBlockEntity;
import ru.feytox.etherology.util.misc.TickableBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static ru.feytox.etherology.block.generators.AbstractGenerator.STALLED;

public abstract class AbstractGeneratorBlockEntity extends TickableBlockEntity implements EtherStorage, EGeoBlockEntity, EssenceDetector {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private float storedEther;
    private int nextGenTime = 40 * 20;
    private boolean isLaunched = false;
    private boolean isMess = false;
    private CompletableFuture<Boolean> messCheck = null;
    @Nullable
    private EssenceSupplier cachedSeal;

    public AbstractGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void spinAnim() {
        StopBlockAnimS2C.sendForTracking(this, "stalled");
        StartBlockAnimS2C.sendForTracking(this, "spin");
    }

    public void unstall(ServerWorld world, BlockState state) {
        world.setBlockState(pos, state.with(STALLED, false));
        world.playSound(null, pos, SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.BLOCKS,
                1.0f, 0.9f + world.getRandom().nextFloat() * 0.2f);
        spinAnim();
    }

    public void stallAnim() {
        StopBlockAnimS2C.sendForTracking(this, "spin");
        StartBlockAnimS2C.sendForTracking(this, "stalled");
    }

    public void stall(ServerWorld world, BlockState state) {
        world.setBlockState(pos, state.with(STALLED, true));
        stallAnim();
    }

    @Override
    public void clientTick(ClientWorld world, BlockPos blockPos, BlockState state) {
        if (!isLaunched) {
            triggerAnim(state.get(STALLED) ? "stalled" : "spin");
            isLaunched = true;
        }

        if (world.getTime() % 10 != 0) return;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        if (!OculusItem.isInHands(player)) return;
        if (state.get(STALLED)) return;

        Vec3d targetPos = blockPos.toCenterPos();
        Direction direction = state.get(AbstractGenerator.FACING).getOpposite();
        Vec3d particlePos = blockPos.add(direction.getVector()).toCenterPos();
        val effect = new LightParticleEffect(EtherParticleTypes.LIGHT, LightSubtype.GENERATOR, targetPos);
        effect.spawnParticles(world, 4, 1.0d, particlePos);
    }

    @Override
    public void serverTick(ServerWorld world, BlockPos blockPos, BlockState state) {
        tickMessCheck(world);
        generateTick(world, state);
        transferTick(world);
    }

    public void tickMessCheck(ServerWorld world) {
        if (messCheck != null && !messCheck.isDone()) return;
        if (messCheck != null) {
            boolean result = messCheck.join();
            if (result != isMess) {
                isMess = result;
                markDirty();
                messCheck = null;
                return;
            }
        }

        if (world.getTime() % 20 != 0) return;
        messCheck = CompletableFuture.supplyAsync(() -> messChecker(world));
    }

    public boolean messChecker(ServerWorld world) {
        Vec3i radiusVec = new Vec3i(3, 3, 3);
        Iterable<BlockPos> blockPoses = BlockPos.iterate(pos.subtract(radiusVec), pos.add(radiusVec));

        boolean result = true;
        for (BlockPos blockPos : blockPoses) {
            if (blockPos.equals(pos)) continue;
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.getBlock() instanceof AbstractGenerator && !blockState.get(STALLED)) {
                result = false;
                break;
            }
            if (world.getBlockEntity(pos) instanceof AbstractGeneratorBlockEntity generator) {
                if (!generator.isFull()) {
                    result = false;
                    break;
                }
            }
        }

        return !result;
    }

    public void generateTick(ServerWorld world, BlockState state) {
        if (state.get(STALLED) || isMess) return;

        boolean isInSeal = isInSeal(world);
        Random random = world.getRandom();
        if (nextGenTime-- > 0) {
            if (isInSeal && random.nextDouble() <= 0.5) nextGenTime -= random.nextBetween(0, 1);
            return;
        }
        increment(1);

        if (getCachedState().getBlock() instanceof AbstractGenerator generator) {
            int min = generator.getMinCooldown();
            int max = generator.getMaxCooldown();
            nextGenTime = random.nextBetween(min, max);

            if (random.nextDouble() <= generator.getStopChance(isInSeal)) {
                stall(world, state);
            }
        }
    }

    public boolean isInSeal(World world) {
        return getAndCacheSeal(world, pos).isPresent();
    }

    @Override
    public float getMaxEther() {
        return 1;
    }

    public boolean isFull() {
        return getStoredEther() >= getMaxEther();
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
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putFloat("stored_ether", storedEther);
        nbt.putInt("next_gen_time", nextGenTime);
        nbt.putBoolean("is_mess", isMess);

        super.writeNbt(nbt, registryLookup);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        storedEther = nbt.getFloat("stored_ether");
        nextGenTime = nbt.getInt("next_gen_time");
        isMess = nbt.getBoolean("is_mess");
    }

    public abstract RawAnimation getSpinAnimation();

    public abstract RawAnimation getStalledAnimation();

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(createTriggerController("spin", getSpinAnimation()));
        controllers.add(createTriggerController("stalled", getStalledAnimation()));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public Optional<EssenceSupplier> getCachedSeal() {
        return Optional.ofNullable(cachedSeal);
    }

    @Override
    public void setCachedSeal(EssenceSupplier seal) {
        cachedSeal = seal;
    }
}
