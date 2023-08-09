package ru.feytox.etherology.block.armillar_new;

import io.wispforest.owo.util.ImplementedInventory;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.block.pedestal.PedestalBlockEntity;
import ru.feytox.etherology.components.IFloatComponent;
import ru.feytox.etherology.enums.ArmillaryState;
import ru.feytox.etherology.enums.InstabilityType;
import ru.feytox.etherology.enums.RingType;
import ru.feytox.etherology.item.MatrixRing;
import ru.feytox.etherology.network.animation.StartBlockAnimS2C;
import ru.feytox.etherology.network.animation.SwitchBlockAnimS2C;
import ru.feytox.etherology.particle.effects.ElectricityParticleEffect;
import ru.feytox.etherology.particle.effects.ItemParticleEffect;
import ru.feytox.etherology.particle.effects.LightParticleEffect;
import ru.feytox.etherology.particle.effects.SparkParticleEffect;
import ru.feytox.etherology.particle.subtypes.ElectricitySubtype;
import ru.feytox.etherology.particle.subtypes.LightSubtype;
import ru.feytox.etherology.recipes.armillary.ArmillaryRecipe;
import ru.feytox.etherology.recipes.armillary.MatrixRecipe;
import ru.feytox.etherology.registry.block.EBlocks;
import ru.feytox.etherology.registry.particle.ServerParticleTypes;
import ru.feytox.etherology.util.feyapi.TickableBlockEntity;
import ru.feytox.etherology.util.feyapi.UniqueProvider;
import ru.feytox.etherology.util.gecko.EGeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ru.feytox.etherology.registry.block.EBlocks.ARMILLARY_MATRIX_BLOCK_ENTITY_NEW;
import static ru.feytox.etherology.registry.block.EBlocks.PEDESTAL_BLOCK;
import static ru.feytox.etherology.registry.util.EtherologyComponents.ETHER_POINTS;

public class ArmillaryMatrixBlockEntity extends TickableBlockEntity implements ImplementedInventory, EGeoBlockEntity, UniqueProvider {

    // constants
    private static final int MATRIX_RADIUS = 8;
    private static final int DOWN_HEIGHT = 1;

    // animations
    private static final RawAnimation BASE_ANIM;
    private static final RawAnimation INACTIVELY_ANIM;
    private static final RawAnimation FLYING_ANIM;
    private static final RawAnimation ACCEPTED_ANIM;
    private static final RawAnimation STARTLOOP_ANIM;
    private static final RawAnimation START_ANIM;
    private static final RawAnimation WORK_ANIM;
    private static final RawAnimation END_ANIM;
    private static final RawAnimation INSTABILITY_ANIM;

    // final fields
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    @Getter
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(6, ItemStack.EMPTY);

    // armillary info
    private float matrixInstability = 0.0f;
    private int currentTick = 0;
    @Nullable
    private MatrixRecipe currentRecipe = null;

    // server cache
    @Nullable
    private List<BlockPos> pedestalsCache = null;
    @Nullable
    private BlockPos cachedTargetPedestal = null;

    // client cache
    public float animationTime = 0.0f;
    @Getter
    @Setter
    private Float cachedUniqueOffset = null;
    @Nullable
    private ArmillaryMatrixSoundInstance soundInstance = null;

    public ArmillaryMatrixBlockEntity(BlockPos pos, BlockState state) {
        super(ARMILLARY_MATRIX_BLOCK_ENTITY_NEW, pos, state);
    }

    @Override
    public void serverTick(ServerWorld world, BlockPos blockPos, BlockState state) {
        tickCraftInstability(world, state);
        tickMatrixState(world, state);
    }

    @Override
    public void clientTick(ClientWorld world, BlockPos blockPos, BlockState state) {
        tickSound(state);
        tickElectricityParticles(world, state);
        tickIdleAnimation(state);
        tickStoringParticles(world, state);
    }

    public void interact(ServerWorld world, BlockState state, PlayerEntity player, Hand hand) {
        ItemStack handStack = player.getStackInHand(hand);
        val matrixState = getMatrixState(state);
        if (!matrixState.equals(ArmillaryState.OFF)) return;

        // matrix activation
        if (handStack.isOf(Items.ARROW)) {
            tryStartCrafting(world, state);
            return;
        }

        // rings place
        int rings = getRingsNum();
        if (rings < 5 && handStack.getItem() instanceof MatrixRing) {
            setStack(rings + 1, handStack.copyWithCount(1));
            handStack.decrement(1);
            player.setStackInHand(hand, handStack);
            return;
        }

        // item place
        if (!handStack.isEmpty()) {
            if (!getStack(0).isEmpty()) {
                // item take to stack
                if (!getStack(0).isItemEqual(handStack)) return;
                setStack(0, ItemStack.EMPTY);
                handStack.increment(1);
                player.setStackInHand(hand, handStack);
                return;
            }

            setStack(0, handStack.copyWithCount(1));
            handStack.decrement(1);
            player.setStackInHand(hand, handStack);
            return;
        }

        // ring take
        if (player.isSneaking()) {
            ItemStack ringStack = getStack(rings);
            player.setStackInHand(hand, ringStack);
            setStack(rings, ItemStack.EMPTY);
            return;
        }

        // item take
        if (getStack(0).isEmpty()) return;
        ItemStack stack = getStack(0);
        player.setStackInHand(hand, stack);
        setStack(0, ItemStack.EMPTY);
    }

    public void tryStartCrafting(ServerWorld world, BlockState state) {
        List<ItemStack> allItems = new ObjectArrayList<>();
        List<PedestalBlockEntity> pedestals = getAndCachePedestals(world);
        pedestals.stream().map(pedestal -> pedestal.getStack(0)).forEach(allItems::add);

        ItemStack centerStack = getStack(0);
        if (!centerStack.isEmpty()) allItems.add(centerStack);
        SimpleInventory fakeInventory = new SimpleInventory(allItems.toArray(ItemStack[]::new));

        Optional<ArmillaryRecipe> match = world.getRecipeManager().getFirstMatch(ArmillaryRecipe.Type.INSTANCE, fakeInventory, world);
        if (match.isEmpty()) return;
        val recipe = match.get();

        currentRecipe = MatrixRecipe.of(recipe);
        matrixInstability = 1.0f;
        state = setMatrixState(world, state, ArmillaryState.RAISING);
        setCraftInstability(world, state, recipe.getInstability());
        SwitchBlockAnimS2C.sendForTracking(this, "inactively", "start");
    }

    private void tickIdleAnimation(BlockState state) {
        val matrixState = getMatrixState(state);
        if (matrixState.equals(ArmillaryState.OFF)) triggerAnim("inactively");
    }

    private void tickSound(BlockState state) {
        val client = MinecraftClient.getInstance();
        if (client.player == null) return;
        val matrixState = getMatrixState(state);

        if (soundInstance == null && matrixState.isWorking() && client.player.squaredDistanceTo(getCenterPos(matrixState)) < 36) {
            soundInstance = new ArmillaryMatrixSoundInstance(this, client.player);
            client.getSoundManager().play(soundInstance);
            return;
        }

        if (soundInstance == null) return;
        if (!matrixState.isWorking() || soundInstance.isDone()) soundInstance = null;
    }

    private void tickElectricityParticles(ClientWorld world, BlockState state) {
        val matrixState = getMatrixState(state);
        if (!matrixState.isWorking()) return;

        Random random = world.getRandom();
        if (random.nextFloat() > 0.075f) return;

        val particleType = ElectricityParticleEffect.getRandomType(random);
        val effect = new ElectricityParticleEffect(particleType, ElectricitySubtype.MATRIX, matrixInstability);
        effect.spawnParticles(world, random.nextBetween(1, 3), 1, getCenterPos(matrixState));
    }

    private void tickStoringParticles(ClientWorld world, BlockState state) {
        val matrixState = getMatrixState(state);
        switch (matrixState) {
            case STORING -> {
                Vec3d centerPos = getCenterPos(matrixState);
                Optional<? extends LivingEntity> match = findClosestEntity(world, centerPos);
                match.ifPresent(entity -> spawnConsumingParticle(world, entity, centerPos));
            }
            case DAMAGING -> {
                Vec3d centerPos = getCenterPos(matrixState);
                Optional<PlayerEntity> match = findClosestPlayer(world, centerPos);
                match.ifPresent(player -> spawnConsumingParticle(world, player, centerPos));
            }
        }
    }

    private void tickCraftInstability(ServerWorld world, BlockState state) {
        if (!getMatrixState(state).isWorking()) return;
        if (world.getTime() % 10 != 0) return;

        InstabilityType craftInstability = getCraftInstability(state);
        if (craftInstability.equals(InstabilityType.NULL)) return;
        if (craftInstability.corruptionEvent(world, matrixInstability, pos)) return;
        if (craftInstability.lightningEvent(world, matrixInstability, pos)) return;
        if (craftInstability.dropEvent(world, matrixInstability, pos)) return;
        if (craftInstability.boomEvent(world, matrixInstability, pos)) return;
        craftInstability.removeEvent(world, matrixInstability, pos);
    }

    private void tickMatrixState(ServerWorld world, BlockState state) {
        val matrixState = getMatrixState(state);
        switch (matrixState) {
            case LOWERING -> {
                if (currentTick++ >= 40) setMatrixState(world, state, ArmillaryState.OFF);
            }
            case RAISING -> {
                if (currentTick++ >= 41) {
                    setMatrixState(world, state, ArmillaryState.STORING);
                    SwitchBlockAnimS2C.sendForTracking(this, "start", "work");
                }
            }
            case STORING -> {
                tickStoring(world);
                if (currentRecipe != null && currentRecipe.getEtherPoints() <= 0.0f) setMatrixState(world, state, ArmillaryState.CRAFTING);
            }
            case DAMAGING -> {
                tickDamaging(world);
                tickCrafting(world, state);
            }
            case CRAFTING -> tickCrafting(world, state);
            case CONSUMING -> tickItemConsuming(world, state);
            case OFF -> {
                return;
            }
        }

        syncData(world);
    }

    private void tickStoring(ServerWorld world) {
        if (world.getTime() % 20 != 0 || currentRecipe == null) return;
        if (currentRecipe.getEtherPoints() <= 0.0f) return;
        Vec3d centerPos = getCenterPos(ArmillaryState.STORING);

        Optional<? extends LivingEntity> entityMatch = findClosestEntity(world, centerPos);
        if (entityMatch.isEmpty()) return;
        LivingEntity entity = entityMatch.get();
        consumeEther(entity, entity.isPlayer() ? 0.75f: 0.5f);
    }

    private void tickDamaging(ServerWorld world) {
        if (world.getTime() % 20 != 0) return;

        Vec3d centerPos = getCenterPos(ArmillaryState.DAMAGING);
        Optional<PlayerEntity> match = findClosestPlayer(world, centerPos);
        match.ifPresent(player -> consumeEther(player, 0.3f));
    }

    private Optional<? extends LivingEntity> findClosestEntity(World world, Vec3d centerPos) {
        Optional<LivingEntity> match = findClosestMob(world);
        if (match.isPresent()) return match;
        return findClosestPlayer(world, centerPos);
    }

    private Optional<LivingEntity> findClosestMob(World world) {
        Box entitiesBox = new Box(pos.add(-MATRIX_RADIUS, -DOWN_HEIGHT, -MATRIX_RADIUS), pos.add(MATRIX_RADIUS, MATRIX_RADIUS, MATRIX_RADIUS));
        val mobs = world.getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class), entitiesBox, entity -> !entity.isPlayer());
        return mobs.isEmpty() ? Optional.empty() : Optional.of(mobs.get(0));
    }

    private Optional<PlayerEntity> findClosestPlayer(World world, Vec3d centerPos) {
        return Optional.ofNullable(world.getClosestPlayer(centerPos.x, centerPos.y, centerPos.z, MATRIX_RADIUS, false));
    }

    private void consumeEther(LivingEntity entity, float value) {
        if (entity instanceof PlayerEntity player) {
            IFloatComponent playerEther = ETHER_POINTS.get(player);
            if (playerEther.getValue() < value) return;
            playerEther.decrement(value);
        }
        else entity.damage(DamageSource.MAGIC, value);
        if (currentRecipe == null) return;

        float currentEther = currentRecipe.getEtherPoints();
        currentRecipe.setEtherPoints(currentEther - value);
    }

    private void spawnConsumingParticle(World world, LivingEntity entity, Vec3d centerPos) {
        LightParticleEffect effect = new LightParticleEffect(ServerParticleTypes.LIGHT, LightSubtype.VITAL, centerPos);
        effect.spawnParticles(world, 5, 0.1, entity.getBoundingBox().getCenter());
    }

    private void tickCrafting(ServerWorld world, BlockState state) {
        if (world.getTime() % 120 != 0 || currentRecipe == null) return;

        if (currentRecipe.isFinished()) {
            craft(world, state);
            return;
        }

        List<PedestalBlockEntity> pedestals = getCachedPedestals(world);
        Optional<PedestalBlockEntity> optionalPedestal = currentRecipe.findMatchedPedestal(pedestals);
        if (optionalPedestal.isEmpty()) {
            if (currentRecipe.testCenterStack(this)) {
                craft(world, state);
                return;
            }

            matrixInstability += matrixInstability * 0.42f;
            setMatrixState(world, state, ArmillaryState.DAMAGING);
            return;
        }

        PedestalBlockEntity targetPedestal = optionalPedestal.get();
        cachedTargetPedestal = targetPedestal.getPos();
        currentTick = 0;
        setMatrixState(world, state, ArmillaryState.CONSUMING);
    }

    private void craft(ServerWorld world, BlockState state) {
        if (currentRecipe == null) return;

        Optional<? extends Recipe<?>> match = world.getRecipeManager().get(currentRecipe.getRecipeId());
        if (match.isEmpty()) return;
        setStack(0, match.get().getOutput());

        currentRecipe = null;
        matrixInstability = 0.0f;
        SwitchBlockAnimS2C.sendForTracking(this, "work", "end");
        StartBlockAnimS2C.sendForTracking(this, "inactively");
        state = setMatrixState(world, state, ArmillaryState.LOWERING);
        setCraftInstability(world, state, InstabilityType.NULL);
    }

    private void tickItemConsuming(ServerWorld world, BlockState state) {
        if (cachedTargetPedestal == null || currentRecipe == null || !(world.getBlockEntity(cachedTargetPedestal) instanceof PedestalBlockEntity pedestal)) {
            setMatrixState(world, state, ArmillaryState.CRAFTING);
            return;
        }
        if (currentTick++ >= 60) {
            pedestal.clear();
            pedestal.syncData(world);
            currentRecipe.getInputs().remove(0);
            setMatrixState(world, state, ArmillaryState.CRAFTING);
            return;
        }
        if (world.getTime() % 5 != 0) return;

        Vec3d pedestalPos = cachedTargetPedestal.toCenterPos().add(0, 1, 0);
        Vec3d centerPos = getCenterPos(ArmillaryState.CONSUMING);
        Random random = world.getRandom();

        ItemParticleEffect itemEffect = new ItemParticleEffect(ServerParticleTypes.ITEM, pedestal.getStack(0), centerPos);
        itemEffect.spawnParticles(world, 5, 0.2, pedestalPos);

        LightParticleEffect sparkLightEffect = new LightParticleEffect(ServerParticleTypes.LIGHT, LightSubtype.SPARK, centerPos);
        sparkLightEffect.spawnParticles(world, random.nextBetween(10, 25), 0.35, pedestalPos);

        SparkParticleEffect sparkEffect = new SparkParticleEffect(ServerParticleTypes.SPARK, centerPos);
        sparkEffect.spawnParticles(world, random.nextBetween(1, 5), 0.35, pedestalPos);
    }

    public Vec3d getCenterPos(ArmillaryState matrixState) {
        Vec3d notActivated = pos.toCenterPos().add(0, 0.25, 0);
        if (matrixState.equals(ArmillaryState.OFF)) return notActivated;

        return notActivated.add(0, 2, 0);
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putFloat("matrix_instability", matrixInstability);
        nbt.putInt("current_tick", currentTick);
        if (currentRecipe != null) currentRecipe.writeNbt(nbt);
        Inventories.writeNbt(nbt, items);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        matrixInstability = nbt.getFloat("matrix_instability");
        currentTick = nbt.getInt("current_tick");
        currentRecipe = MatrixRecipe.readFromNbt(nbt);
        items.clear();
        Inventories.readNbt(nbt, items);
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
    public double getBoneResetTime() {
        return 0.0000001d;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                getController(BASE_ANIM), getTriggerController("inactively", INACTIVELY_ANIM),
                getTriggerController("flying", FLYING_ANIM), getTriggerController("startloop", STARTLOOP_ANIM),
                getTriggerController("start", START_ANIM), getTriggerController("work", WORK_ANIM),
                getTriggerController("end", END_ANIM), getTriggerController("accepted", ACCEPTED_ANIM),
                getTriggerController("instability", INSTABILITY_ANIM)
        );
    }

    public List<PedestalBlockEntity> getCachedPedestals(ServerWorld world) {
        if (pedestalsCache == null) return getAndCachePedestals(world);
        for (BlockPos cachedPos : pedestalsCache) {
            BlockState blockState = world.getBlockState(cachedPos);
            if (!blockState.isOf(PEDESTAL_BLOCK)) return getAndCachePedestals(world);
        }
        return pedestalsCache.stream()
                .map(cachedPos -> world.getBlockEntity(pos, EBlocks.PEDESTAL_BLOCK_ENTITY))
                .map(optionalBe -> optionalBe.orElse(null)).filter(Objects::nonNull).toList();
    }

    private List<PedestalBlockEntity> getAndCachePedestals(ServerWorld world) {
        val result = new ObjectArrayList<PedestalBlockEntity>();
        val newCache = new ObjectArrayList<BlockPos>();
        BlockPos.iterate(pos.add(-MATRIX_RADIUS, -DOWN_HEIGHT, -MATRIX_RADIUS), pos.add(MATRIX_RADIUS, MATRIX_RADIUS, MATRIX_RADIUS)).forEach(blockPos -> {
            if (!(world.getBlockEntity(blockPos) instanceof PedestalBlockEntity pedestal)) return;
            result.add(pedestal);
            newCache.add(blockPos);
        });
        pedestalsCache = newCache;
        return result;
    }

    public ArmillaryState getMatrixState(BlockState blockState) {
        return blockState.get(ArmillaryMatrixBlock.MATRIX_STATE);
    }

    public BlockState setMatrixState(ServerWorld world, BlockState state, ArmillaryState matrixState) {
        state = state.with(ArmillaryMatrixBlock.MATRIX_STATE, matrixState);
        world.setBlockState(pos, state);
        currentTick = 0;
        return state;
    }

    public InstabilityType getCraftInstability(BlockState blockState) {
        return blockState.get(ArmillaryMatrixBlock.CRAFT_INSTABILITY);
    }

    public BlockState setCraftInstability(ServerWorld world, BlockState state, InstabilityType craftInstability) {
        state = state.with(ArmillaryMatrixBlock.CRAFT_INSTABILITY, craftInstability);
        world.setBlockState(pos, state);
        return state;
    }

    public int getRingsNum() {
        int result = 0;
        for (int i = 1; i < 6; i++) {
            if (getStack(i).getItem() instanceof MatrixRing) result++;
        }
        return result;
    }

    public List<RingType> getRingTypes() {
        List<RingType> result = new ObjectArrayList<>();

        for (int i = 1; i < 6; i++) {
            Item item = getStack(i).getItem();
            if (item instanceof MatrixRing ring) result.add(ring.getRingType());
        }
        return result;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    static {
        BASE_ANIM = RawAnimation.begin()
                .thenLoop("animation.ring_matrix.base");
        INACTIVELY_ANIM = RawAnimation.begin()
                .thenLoop("animation.ring_matrix.inactively_loop");
        FLYING_ANIM = RawAnimation.begin()
                .thenLoop("animation.ring_matrix.flying_loop");
        ACCEPTED_ANIM = RawAnimation.begin()
                .thenPlay("animation.ring_matrix.work_accepted");
        STARTLOOP_ANIM = RawAnimation.begin()
                .thenPlay("animation.ring_matrix.work_accepted")
                .thenLoop("animation.ring_matrix.work_startloop");
        START_ANIM = RawAnimation.begin()
                .thenPlay("animation.ring_matrix.work_start");
        WORK_ANIM = RawAnimation.begin()
                .thenLoop("animation.ring_matrix.work_loop");
        END_ANIM = RawAnimation.begin()
                .thenPlay("animation.ring_matrix.work_end");
        INSTABILITY_ANIM = RawAnimation.begin()
                .thenLoop("animation.ring_matrix.instability_loop");
    }
}
