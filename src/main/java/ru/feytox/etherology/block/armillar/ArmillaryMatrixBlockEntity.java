package ru.feytox.etherology.block.armillar;

import com.google.common.collect.ImmutableList;
import io.wispforest.owo.util.ImplementedInventory;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.apache.commons.lang3.EnumUtils;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.block.pedestal.PedestalBlockEntity;
import ru.feytox.etherology.components.IFloatComponent;
import ru.feytox.etherology.data.item_aspects.AspectsLoader;
import ru.feytox.etherology.magic.aspects.Aspect;
import ru.feytox.etherology.magic.aspects.AspectContainer;
import ru.feytox.etherology.particle.effects.MovingParticleEffect;
import ru.feytox.etherology.recipes.armillary_new.ArmillaryNewRecipe;
import ru.feytox.etherology.recipes.armillary_new.ArmillaryNewRecipeSerializer;
import ru.feytox.etherology.registry.item.ToolItems;
import ru.feytox.etherology.registry.particle.ServerParticleTypes;
import ru.feytox.etherology.registry.util.RecipesRegistry;
import ru.feytox.etherology.util.feyapi.TickableBlockEntity;
import ru.feytox.etherology.util.feyapi.UniqueProvider;
import ru.feytox.etherology.util.gecko.EGeo2BlockEntity;
import ru.feytox.etherology.util.gecko.EGeoAnimation;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;
import java.util.stream.Collectors;

import static ru.feytox.etherology.block.armillar.ArmillaryState.*;
import static ru.feytox.etherology.registry.block.EBlocks.ARMILLARY_MATRIX_BLOCK_ENTITY;
import static ru.feytox.etherology.registry.util.EtherologyComponents.ETHER_POINTS;

public class ArmillaryMatrixBlockEntity extends TickableBlockEntity implements ImplementedInventory, EGeo2BlockEntity, UniqueProvider {

    // constants
    private static final int HORIZONTAL_RADIUS = 7;
    private static final int UP_RADIUS = 3;
    private static final int DOWN_RADIUS = 1;
    public static final double TICKS_PER_RUNE = 2.0d;

    // animations
    private static final EGeoAnimation IDLE_ANIM;
    private static final EGeoAnimation RUNE_0;
    private static final EGeoAnimation RUNE_1;
    private static final EGeoAnimation RUNE_2;
    private static final EGeoAnimation DECRYPTING;
    private static final EGeoAnimation RESET;
    private static final EGeoAnimation[] ANIMATIONS;
    @Getter(lazy = true)
    private static final ImmutableList<EGeoAnimation> runeAnimations = ImmutableList.of(RUNE_0, RUNE_1, RUNE_2);

    // final fields
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    @Getter
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(6, ItemStack.EMPTY);

    // armillary info
    private int currentTick = 0;
    @Getter
    @Nullable
    private List<Aspect> currentAspects = null;
    @Nullable
    private Identifier recipeId = null;
    private Set<String> activeAnimations = new ObjectOpenHashSet<>();

    // server cache
    @Nullable
    private ArmillaryNewRecipe recipeCache = null;
    @Nullable
    private List<BlockPos> pedestalsCache = null;
    @Nullable
    private BlockPos cachedTargetPedestal = null;

    // client cache
    public float animationTime;
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
        tickAnimations(state);
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
        if (matrixState.equals(PREPARED)) {
            boolean isTested = refreshAspectsPedestals(world, state);
            val stateToSet = isTested && testForRecipe(world) ? CONSUMING : IDLE;
            setMatrixState(world, state, stateToSet);
            getRuneAnimations().forEach(anim -> anim.stop(this));
            IDLE_ANIM.trigger(this);
            syncData(world);
            return;
        }
        if (!matrixState.equals(IDLE)) return;

        // matrix activation
        if (handStack.isOf(ToolItems.STAFF)) {
            setMatrixState(world, state, RESETTING);
            IDLE_ANIM.switchTo(this, RESET);
            syncData(world);
            return;
        }

        // item place
        if (!handStack.isEmpty()) {
            if (!matrixStack.isEmpty()) {
                // item take to stack
                if (!ItemStack.canCombine(matrixStack, handStack) || handStack.getCount() >= handStack.getMaxCount()) return;
                setStack(0, ItemStack.EMPTY);
                handStack.increment(1);
                player.setStackInHand(hand, handStack);
                PedestalBlockEntity.playItemTakeSound(world,  pos);
                return;
            }

            setStack(0, handStack.copyWithCount(1));
            handStack.decrement(1);
            player.setStackInHand(hand, handStack);
            PedestalBlockEntity.playItemPlaceSound(world, pos);
            return;
        }

        // item take
        if (matrixStack.isEmpty()) return;
        player.setStackInHand(hand, matrixStack);
        setStack(0, ItemStack.EMPTY);
        PedestalBlockEntity.playItemTakeSound(world, pos);
    }

    public boolean refreshAspectsPedestals(ServerWorld world, BlockState state) {
        List<PedestalBlockEntity> pedestals = getAndCachePedestals(world);
        val container = pedestals.stream()
                .map(inv -> inv.getStack(0))
                .map(stack -> AspectsLoader.getAspects(stack, false))
                .filter(Optional::isPresent).map(Optional::get)
                .reduce(AspectContainer::add).orElse(null);

        if (container == null) return false;

        val aspectsMap = container.getAspects();
        List<Aspect> sortedAspects = aspectsMap.keySet().stream()
                .sorted(Comparator.comparingInt(aspectsMap::get))
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toCollection(ObjectArrayList::new));

        int size = sortedAspects.size();
        if (size > 3) currentAspects = new ObjectArrayList<>(sortedAspects.subList(0, 3));
        else currentAspects = sortedAspects;

        setMatrixState(world, state, TESTED);
        return true;
    }

    public boolean testForRecipe(ServerWorld world) {
        val recipe = RecipesRegistry.getFirstMatch(world, this, ArmillaryNewRecipeSerializer.INSTANCE);
        if (recipe == null) return false;

        recipeId = recipe.getId();
        recipeCache = recipe;
        return true;
    }

    private static EGeoAnimation getRandomRuneAnimation(World world) {
        val runes = getRuneAnimations();
        Random random = world.getRandom();
        return runes.get(random.nextInt(runes.size()));
    }

    private void tickAnimations(BlockState state) {
        val matrixState = getMatrixState(state);
        if (matrixState.equals(IDLE)) IDLE_ANIM.triggerOnce(this);
        else activeAnimations.forEach(this::triggerOnce);
    }

    /**
     * Tick the sound if matrix is working.
     *
     * @param  state  the block state
     */
    private void tickSound(BlockState state) {
        // TODO: 03.03.2024 ask about sound

        val client = MinecraftClient.getInstance();
        if (client.player == null) return;
        val matrixState = getMatrixState(state);

        if (soundInstance == null && matrixState.isWorking() && client.player.squaredDistanceTo(getCenterPos()) < 36) {
            soundInstance = new ArmillaryMatrixSoundInstance(this, client.player);
            client.getSoundManager().play(soundInstance);
            return;
        }

        if (soundInstance == null) return;
        if (!matrixState.isWorking() || soundInstance.isDone()) soundInstance = null;
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
            case RESETTING -> {
                if (currentTick++ >= 16) {
                    if (!refreshAspectsPedestals(world, state)) {
                        setMatrixState(world, state, IDLE);
                        break;
                    }
                    testForRecipe(world);
                    RESET.switchTo(this, getRandomRuneAnimation(world));
                }
            }
            case IDLE -> {
                return;
            }
        }

        syncData(world);
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

        // TODO: 04.03.2024 ether points increment
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
     * Calculates and returns the matrix center position for the given matrix state.
     *
     * @return              the matrix center position
     */
    public Vec3d getCenterPos() {
        return pos.toCenterPos().add(0, 2.25, 0);
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public void markAnimationActive(String animName) {
        activeAnimations.add(animName);
    }

    @Override
    public void markAnimationStop(String animName) {
        activeAnimations.remove(animName);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putInt("current_tick", currentTick);
        writeAspects(nbt);
        Inventories.writeNbt(nbt, items);
        writeActiveAnimations(nbt);

        if (recipeId == null) nbt.putString("recipe_id", "");
        else nbt.putString("recipe_id", recipeId.toString());

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        currentTick = nbt.getInt("current_tick");
        currentAspects = readAspects(nbt);
        items.clear();
        Inventories.readNbt(nbt, items);
        activeAnimations = readActiveAnimations(nbt);

        String id = nbt.getString("recipe_id");
        recipeId = id.isEmpty() ? null : new Identifier(id);
    }

    private void writeActiveAnimations(NbtCompound nbt) {
        if (activeAnimations.isEmpty()) return;
        NbtList nbtList = new NbtList();
        activeAnimations.stream().map(NbtString::of).forEach(nbtList::add);
        nbt.put("active_animations", nbtList);
    }

    private Set<String> readActiveAnimations(NbtCompound nbt) {
        if (!nbt.contains("active_animations")) return new ObjectOpenHashSet<>();

        NbtList nbtList = nbt.getList("active_animations", NbtElement.STRING_TYPE);
        return nbtList.stream()
                .map(element -> (NbtString) element).map(NbtString::asString)
                .collect(Collectors.toCollection(ObjectOpenHashSet::new));
    }

    private void writeAspects(NbtCompound nbt) {
        if (currentAspects == null) {
            nbt.putInt("current_aspects", -1);
            return;
        }
        NbtList nbtList = new NbtList();
        currentAspects.stream().map(Aspect::name).map(NbtString::of).forEach(nbtList::add);
        nbt.put("current_aspects", nbtList);
    }

    @Nullable
    private List<Aspect> readAspects(NbtCompound nbt) {
        if (!nbt.contains("current_aspects")) return null;
        if (!nbt.contains("current_aspects", NbtElement.LIST_TYPE)) return null;

        NbtList nbtList = nbt.getList("current_aspects", NbtElement.STRING_TYPE);
        return nbtList.stream()
                .map(element -> (NbtString) element)
                .map(NbtString::asString)
                .map(s -> EnumUtils.getEnumIgnoreCase(Aspect.class, s))
                .collect(Collectors.toCollection(ObjectArrayList::new));
    }

    @Override
    public double getBoneResetTime() {
        return 0.0000001d;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        Arrays.stream(ANIMATIONS).map(anim -> anim.generateController(this))
                        .forEach(controllers::add);
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

    public ArmillaryState getCachedMatrixState() {
        return getMatrixState(getCachedState());
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

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    static {
        IDLE_ANIM = EGeoAnimation.begin("idle")
                .thenLoop("animation.armillary_matrix.idle");
        RUNE_0 = EGeoAnimation.begin("rune_0")
                .thenPlayAndHold("animation.armillary_matrix.rune_0");
        RUNE_1 = EGeoAnimation.begin("rune_1")
                .thenPlayAndHold("animation.armillary_matrix.rune_1");
        RUNE_2 = EGeoAnimation.begin("rune_2")
                .thenPlayAndHold("animation.armillary_matrix.rune_2");
        DECRYPTING = EGeoAnimation.begin("decrypting")
                .thenPlay("animation.armillary_matrix.decrypting_start")
                .thenLoop("animation.armillary_matrix.decrypting_loop");
        RESET = EGeoAnimation.begin("reset").withoutMarking()
                .thenPlay("animation.armillary_matrix.reset");

        ANIMATIONS = new EGeoAnimation[]{IDLE_ANIM, RUNE_0, RUNE_1, RUNE_2, DECRYPTING, RESET};
    }
}
