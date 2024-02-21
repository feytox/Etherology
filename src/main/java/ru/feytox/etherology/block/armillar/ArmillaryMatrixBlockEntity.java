package ru.feytox.etherology.block.armillar;

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
import net.minecraft.recipe.Recipe;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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
import ru.feytox.etherology.particle.effects.*;
import ru.feytox.etherology.particle.subtypes.ElectricitySubtype;
import ru.feytox.etherology.particle.subtypes.LightSubtype;
import ru.feytox.etherology.particle.subtypes.SparkSubtype;
import ru.feytox.etherology.recipes.armillary.ArmillaryRecipe;
import ru.feytox.etherology.recipes.armillary.ArmillaryRecipeSerializer;
import ru.feytox.etherology.recipes.armillary.MatrixRecipe;
import ru.feytox.etherology.registry.particle.ServerParticleTypes;
import ru.feytox.etherology.registry.util.RecipesRegistry;
import ru.feytox.etherology.util.feyapi.TickableBlockEntity;
import ru.feytox.etherology.util.feyapi.UniqueProvider;
import ru.feytox.etherology.util.gecko.EGeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Optional;

import static ru.feytox.etherology.registry.block.EBlocks.ARMILLARY_MATRIX_BLOCK_ENTITY;
import static ru.feytox.etherology.registry.util.EtherologyComponents.ETHER_POINTS;

public class ArmillaryMatrixBlockEntity extends TickableBlockEntity implements ImplementedInventory, EGeoBlockEntity, UniqueProvider {

    // constants
    private static final int HORIZONTAL_RADIUS = 7;
    private static final int UP_RADIUS = 3;
    private static final int DOWN_RADIUS = 1;

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
        super(ARMILLARY_MATRIX_BLOCK_ENTITY, pos, state);
    }

    /**
     * Updates the server-side tick logic for a block.
     *
     * @param  world     the server world
     * @param  blockPos  the position of the block
     * @param  state     the block state
     */
    @Override
    public void serverTick(ServerWorld world, BlockPos blockPos, BlockState state) {
        tickCraftInstability(world, state);
        tickMatrixState(world, state);
    }

    /**
     * Updates the client-side tick logic for a block.
     *
     * @param  world     the client world
     * @param  blockPos  the position of the block
     * @param  state     the block state
     */
    @Override
    public void clientTick(ClientWorld world, BlockPos blockPos, BlockState state) {
        tickSound(state);
        tickElectricityParticles(world, state);
        tickIdleAnimation(state);
        tickClientParticles(world, state);
    }

    /**
     * Handles server-side the event when the player uses their hand on a matrix base.
     *
     * @param  world   the server world
     * @param  state   the block state
     * @param  player  the player entity
     * @param  hand    the hand used
     */
    public void onHandUse(ServerWorld world, BlockState state, PlayerEntity player, Hand hand) {
        ItemStack handStack = player.getStackInHand(hand);
        ItemStack matrixStack = getStack(0);
        val matrixState = getMatrixState(state);
        if (!matrixState.equals(ArmillaryState.OFF)) return;

        // matrix activation
        if (handStack.isOf(Items.ARROW)) {
            tryStartCrafting(world, state);
            return;
        }

        // rings place
        int rings = getRingsNum();
        if (rings < 5 && handStack.getItem() instanceof MatrixRing handRing) {
            if (rings == 0 || (getStack(rings).getItem() instanceof MatrixRing ring && ring.getRingType().equals(handRing.getRingType()))) {
                setStack(rings + 1, handStack.copyWithCount(1));
                handStack.decrement(1);
                player.setStackInHand(hand, handStack);
                return;
            }
        }

        // item place
        if (!handStack.isEmpty()) {
            if (!matrixStack.isEmpty()) {
                // item take to stack
                if (!ItemStack.canCombine(matrixStack, handStack) || handStack.getCount() >= handStack.getMaxCount()) return;
                setStack(0, ItemStack.EMPTY);
                handStack.increment(1);
                player.setStackInHand(hand, handStack);
                PedestalBlockEntity.playItemTakeSound(world, pos);
                return;
            }

            setStack(0, handStack.copyWithCount(1));
            handStack.decrement(1);
            player.setStackInHand(hand, handStack);
            PedestalBlockEntity.playItemPlaceSound(world, pos);
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
        if (matrixStack.isEmpty()) return;
        player.setStackInHand(hand, matrixStack);
        setStack(0, ItemStack.EMPTY);
        PedestalBlockEntity.playItemTakeSound(world, pos);
    }

    /**
     * Tries to start the crafting process and matrix raising.
     *
     * @param  world  the server world
     * @param  state  the block state
     */
    public void tryStartCrafting(ServerWorld world, BlockState state) {
        List<ItemStack> allItems = new ObjectArrayList<>();
        List<PedestalBlockEntity> pedestals = getAndCachePedestals(world);
        pedestals.stream().map(pedestal -> pedestal.getStack(0)).forEach(allItems::add);

        ItemStack centerStack = getStack(0);
        if (!centerStack.isEmpty()) allItems.add(centerStack);
        SimpleInventory fakeInventory = new SimpleInventory(allItems.toArray(ItemStack[]::new));

        ArmillaryRecipe recipe = RecipesRegistry.getFirstMatch(world, fakeInventory, ArmillaryRecipeSerializer.INSTANCE);
        if (recipe == null) return;

        currentRecipe = MatrixRecipe.of(recipe);
        matrixInstability = 1.0f;
        state = setMatrixState(world, state, ArmillaryState.RAISING);
        setCraftInstability(world, state, recipe.getInstability());
        SwitchBlockAnimS2C.sendForTracking(this, "inactively", "start");
    }

    /**
     * Activates idle animation if matrix is off.
     *
     * @param  state  the current state of the block
     */
    private void tickIdleAnimation(BlockState state) {
        val matrixState = getMatrixState(state);
        if (matrixState.equals(ArmillaryState.OFF)) triggerAnim("inactively");
    }

    /**
     * Tick the sound if matrix is working.
     *
     * @param  state  the block state
     */
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

    /**
     * Tick the client-side electricity particles if matrix is working.
     *
     * @param  world  the client world
     * @param  state  the block state
     */
    private void tickElectricityParticles(ClientWorld world, BlockState state) {
        val matrixState = getMatrixState(state);
        if (!matrixState.isWorking()) return;

        Random random = world.getRandom();
        if (random.nextFloat() > 0.075f) return;

        val particleType = ElectricityParticleEffect.getRandomType(random);
        val effect = new ElectricityParticleEffect(particleType, ElectricitySubtype.MATRIX, matrixInstability);
        effect.spawnParticles(world, random.nextBetween(1, 3), 1, getCenterPos(matrixState));
    }

    /**
     * Tick the client-side particles if matrix is working.
     *
     * @param  world  the client world
     * @param  state  the block state
     */
    private void tickClientParticles(ClientWorld world, BlockState state) {
        val matrixState = getMatrixState(state);
        Vec3d centerPos = getCenterPos(matrixState);
        switch (matrixState) {
            case STORING -> {
                Optional<? extends LivingEntity> match = findClosestEntity(world, centerPos);
                match.ifPresent(entity -> spawnConsumingParticle(world, entity, centerPos));
                if (world.getTime() % 2 == 0 && world.getRandom().nextBoolean()) {
                    world.playSound(centerPos.x, centerPos.y, centerPos.z, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 0.066f, 0.7f * world.getRandom().nextFloat() + 0.55f, true);
                }
            }
            case DAMAGING -> {
                Optional<PlayerEntity> match = findClosestPlayer(world, centerPos);
                match.ifPresent(player -> spawnConsumingParticle(world, player, centerPos));
            }
            case SHINING -> {
                if (world.getTime() % 2 == 0) break;
                val risingEffect = new SimpleParticleEffect(ServerParticleTypes.RISING);
                risingEffect.spawnParticles(world, 1, 0.175, centerPos.subtract(0, 0.25, 0));
                val sparkEffect = new SparkParticleEffect(ServerParticleTypes.SPARK, new Vec3d(0, 1, 0), SparkSubtype.RISING);
                sparkEffect.spawnParticles(world, 1, 0.25, centerPos.subtract(0, 0.25, 0));
            }
        }
    }

    /**
     * Executes server-side various events related to craft instability.
     *
     * @param  world  the server world in which the events are executed
     * @param  state  the block state for which the events are executed
     */
    private void tickCraftInstability(ServerWorld world, BlockState state) {
        if (!getMatrixState(state).isWorking()) return;
        if (world.getTime() % 10 != 0) return;

        val craftInstability = getCraftInstability(state);
        val matrixState = getMatrixState(state);
        if (craftInstability.equals(InstabilityType.NULL)) return;

        craftInstability.corruptionEvent(world, matrixInstability, pos);
        craftInstability.lightningEvent(world, this, matrixState, matrixInstability);
        craftInstability.dropEvent(world, this, matrixInstability);
        craftInstability.boomEvent(world, matrixInstability, pos);
        craftInstability.removeEvent(world, this, matrixInstability);
    }

    /**
     * Executes the tick behavior related to the matrix state.
     *
     * @param  world  the server world
     * @param  state  the block state
     */
    private void tickMatrixState(ServerWorld world, BlockState state) {
        val matrixState = getMatrixState(state);
        switch (matrixState) {
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
            case SHINING -> {
                if (currentTick++ < 21) break;
                currentRecipe = null;
                matrixInstability = 0.0f;
                SwitchBlockAnimS2C.sendForTracking(this, "work", "end");
                StartBlockAnimS2C.sendForTracking(this, "inactively");
                state = setMatrixState(world, state, ArmillaryState.LOWERING);
                setCraftInstability(world, state, InstabilityType.NULL);
            }
            case LOWERING -> {
                if (currentTick++ >= 40) setMatrixState(world, state, ArmillaryState.OFF);
            }
            case OFF -> {
                return;
            }
        }

        syncData(world);
    }

    /**
     * Tick server-side the ether storing process.
     *
     * @param  world  the server world
     */
    private void tickStoring(ServerWorld world) {
        if (world.getTime() % 20 != 0 || currentRecipe == null) return;
        if (currentRecipe.getEtherPoints() <= 0.0f) return;
        Vec3d centerPos = getCenterPos(ArmillaryState.STORING);

        Optional<? extends LivingEntity> entityMatch = findClosestEntity(world, centerPos);
        if (entityMatch.isEmpty()) return;
        LivingEntity entity = entityMatch.get();
        consumeEther(entity, entity.isPlayer() ? 0.75f: 0.5f);
    }

    /**
     * Tick server-side the damaging matrix state.
     *
     * @param  world the server world.
     */
    private void tickDamaging(ServerWorld world) {
        if (world.getTime() % 20 != 0) return;

        Vec3d centerPos = getCenterPos(ArmillaryState.DAMAGING);
        Optional<PlayerEntity> match = findClosestPlayer(world, centerPos);
        match.ifPresent(player -> consumeEther(player, 0.3f));
    }

    /**
     * Finds the closest mob; otherwise - the closest player.
     *
     * @param  world     the world to search in
     * @param  centerPos the matrix center position to search from
     * @return           an Optional containing the closest living entity, or empty if no entity is found
     */
    public Optional<? extends LivingEntity> findClosestEntity(World world, Vec3d centerPos) {
        Optional<LivingEntity> match = findClosestMob(world);
        if (match.isPresent()) return match;
        return findClosestPlayer(world, centerPos);
    }

    /**
     * Finds the closest mob.
     *
     * @param  world  the world to search in
     * @return        an Optional containing the closest mob, or empty if no entity is found
     */
    private Optional<LivingEntity> findClosestMob(World world) {
        Box entitiesBox = new Box(pos.add(-HORIZONTAL_RADIUS, -DOWN_RADIUS, -HORIZONTAL_RADIUS), pos.add(HORIZONTAL_RADIUS, UP_RADIUS, HORIZONTAL_RADIUS));
        val mobs = world.getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class), entitiesBox, entity -> !entity.isPlayer());
        return mobs.isEmpty() ? Optional.empty() : Optional.of(mobs.get(0));
    }

    /**
     * Finds the closest player.
     *
     * @param  world       the world to search in
     * @param  centerPos   the matrix center position to search from
     * @return             an Optional containing the closest PlayerEntity, or empty if no player is found
     */
    private Optional<PlayerEntity> findClosestPlayer(World world, Vec3d centerPos) {
        return Optional.ofNullable(world.getClosestPlayer(centerPos.x, centerPos.y, centerPos.z, 2 * HORIZONTAL_RADIUS, false));
    }

    /**
     * Consumes ether from the given entity.
     *
     * @param  entity  the entity from which ether is consumed
     * @param  value   the amount of ether to consume
     */
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

    /**
     * Spawns a consuming particle effect from a given entity at a specific position.
     *
     * @param  world      the world in which to spawn the particles
     * @param  entity     the entity from which to spawn the particles
     * @param  centerPos  the matrix center position
     */
    private void spawnConsumingParticle(World world, LivingEntity entity, Vec3d centerPos) {
        if (world.getTime() % 2 == 0) return;
        val effect = new MovingParticleEffect(ServerParticleTypes.VITAL, centerPos);
        effect.spawnParticles(world, 1, 0.1, entity.getBoundingBox().getCenter());
    }

    /**
     * Tick the crafting process.
     *
     * @param  world  the server world
     * @param  state  the block state
     */
    private void tickCrafting(ServerWorld world, BlockState state) {
        if (world.getTime() % 120 != 0 || currentRecipe == null) return;

        if (currentRecipe.isFinished()) {
            endCrafting(world, state);
            return;
        }

        if (currentRecipe.getInputs().isEmpty() && currentRecipe.testCenterStack(this)) {
            endCrafting(world, state);
            return;
        }

        List<PedestalBlockEntity> pedestals = getCachedPedestals(world);
        Optional<PedestalBlockEntity> optionalPedestal = currentRecipe.findMatchedPedestal(pedestals);
        if (optionalPedestal.isEmpty()) {
            matrixInstability += matrixInstability * 0.42f;
            setMatrixState(world, state, ArmillaryState.DAMAGING);
            return;
        }

        PedestalBlockEntity targetPedestal = optionalPedestal.get();
        cachedTargetPedestal = targetPedestal.getPos();
        currentTick = 0;
        setMatrixState(world, state, ArmillaryState.CONSUMING);
    }

    /**
     * Ends the crafting process.
     *
     * @param  world  the server world
     * @param  state  the block state
     */
    private void endCrafting(ServerWorld world, BlockState state) {
        if (currentRecipe == null) return;

        Optional<? extends Recipe<?>> match = world.getRecipeManager().get(currentRecipe.getRecipeId());
        if (match.isEmpty()) return;
        setStack(0, match.get().getOutput());
        setMatrixState(world, state, ArmillaryState.SHINING);
    }

    /**
     * Tick the item consuming process and spawns particles while crafting.
     *
     * @param  world  the server world
     * @param  state  the block state
     */
    private void tickItemConsuming(ServerWorld world, BlockState state) {
        if (cachedTargetPedestal == null || currentRecipe == null || !(world.getBlockEntity(cachedTargetPedestal) instanceof PedestalBlockEntity pedestal) || pedestal.isEmpty()) {
            setMatrixState(world, state, ArmillaryState.CRAFTING);
            return;
        }
        if (currentTick++ >= 60 && currentRecipe.testPedestal(pedestal)) {
            pedestal.removeStack(0);
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

        SparkParticleEffect sparkEffect = new SparkParticleEffect(ServerParticleTypes.SPARK, centerPos, SparkSubtype.SIMPLE);
        sparkEffect.spawnParticles(world, random.nextBetween(1, 5), 0.35, pedestalPos);
    }

/**
 * Calculates and returns the matrix center position for the given matrix state.
 *
 * @param  matrixState  the ArmillaryState representing the matrix state
 * @return              the matrix center position
 */
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

    /**
     * Retrieves the cached server-side list of Pedestals.
     *
     * @param  world  the ServerWorld
     * @return        the list of Pedestals
     */
    public List<PedestalBlockEntity> getCachedPedestals(ServerWorld world) {
        if (pedestalsCache == null) return getAndCachePedestals(world);

        List<PedestalBlockEntity> result = new ObjectArrayList<>();
        for (BlockPos cachedPos : pedestalsCache) {
            if (!(world.getBlockEntity(cachedPos) instanceof PedestalBlockEntity pedestal)) return getAndCachePedestals(world);
            result.add(pedestal);
        }

        return result;
    }

    /**
     * Finds and caches server-side the list of Pedestals.
     *
     * @param  world  the ServerWorld
     * @return        the list of Pedestals
     */
    private List<PedestalBlockEntity> getAndCachePedestals(ServerWorld world) {
        val result = new ObjectArrayList<PedestalBlockEntity>();
        val newCache = new ObjectArrayList<BlockPos>();
        BlockPos.iterate(pos.add(-HORIZONTAL_RADIUS, -DOWN_RADIUS, -HORIZONTAL_RADIUS), pos.add(HORIZONTAL_RADIUS, UP_RADIUS, HORIZONTAL_RADIUS)).forEach(blockPos -> {
            if (!(world.getBlockEntity(blockPos) instanceof PedestalBlockEntity pedestal)) return;
            result.add(pedestal);
            newCache.add(blockPos);
        });
        pedestalsCache = newCache;
        return result;
    }

    /**
     * Gets the ArmillaryState of a given BlockState.
     *
     * @param  blockState  the BlockState to get the ArmillaryState from
     * @return             the ArmillaryState of the BlockState
     */
    public ArmillaryState getMatrixState(BlockState blockState) {
        return blockState.get(ArmillaryMatrixBlock.MATRIX_STATE);
    }

    /**
     * Sets the matrix state of a block.
     *
     * @param  world         the server world in which the block is located
     * @param  state         the current block state
     * @param  matrixState   the new matrix state to set
     * @return               the updated block state with the new matrix state
     */
    public BlockState setMatrixState(ServerWorld world, BlockState state, ArmillaryState matrixState) {
        state = state.with(ArmillaryMatrixBlock.MATRIX_STATE, matrixState);
        world.setBlockState(pos, state);
        currentTick = 0;
        return state;
    }

    /**
     * Retrieves the craft instability of a given BlockState.
     *
     * @param  blockState  the block state for which to retrieve the instability type
     * @return             the instability type associated with the given block state
     */
    public InstabilityType getCraftInstability(BlockState blockState) {
        return blockState.get(ArmillaryMatrixBlock.CRAFT_INSTABILITY);
    }

    /**
     * Sets the craft instability of a block.
     *
     * @param world            the server world where the block state is located
     * @param state            the block state to modify
     * @param craftInstability the new craft instability value to set
     */
    public void setCraftInstability(ServerWorld world, BlockState state, InstabilityType craftInstability) {
        state = state.with(ArmillaryMatrixBlock.CRAFT_INSTABILITY, craftInstability);
        world.setBlockState(pos, state);
    }

    /**
     * Returns the number of rings in the matrix.
     *
     * @return         	the number of rings
     */
    public int getRingsNum() {
        int result = 0;
        for (int i = 1; i < 6; i++) {
            if (getStack(i).getItem() instanceof MatrixRing) result++;
        }
        return result;
    }

    /**
     * Retrieves a list of matrix ring types.
     *
     * @return a list of RingTypes
     */
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
