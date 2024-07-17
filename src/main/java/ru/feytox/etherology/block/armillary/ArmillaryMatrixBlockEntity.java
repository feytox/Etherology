package ru.feytox.etherology.block.armillary;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.block.pedestal.PedestalBlockEntity;
import ru.feytox.etherology.data.item_aspects.AspectsLoader;
import ru.feytox.etherology.magic.aspects.Aspect;
import ru.feytox.etherology.magic.aspects.AspectContainer;
import ru.feytox.etherology.magic.aspects.RevelationAspectProvider;
import ru.feytox.etherology.magic.ether.EtherComponent;
import ru.feytox.etherology.particle.effects.*;
import ru.feytox.etherology.particle.effects.misc.FeyParticleEffect;
import ru.feytox.etherology.particle.subtypes.ElectricitySubtype;
import ru.feytox.etherology.particle.subtypes.LightSubtype;
import ru.feytox.etherology.particle.subtypes.SparkSubtype;
import ru.feytox.etherology.recipes.armillary.ArmillaryRecipe;
import ru.feytox.etherology.recipes.armillary.ArmillaryRecipeSerializer;
import ru.feytox.etherology.registry.item.ToolItems;
import ru.feytox.etherology.registry.misc.RecipesRegistry;
import ru.feytox.etherology.registry.particle.EtherParticleTypes;
import ru.feytox.etherology.util.gecko.EGeo2BlockEntity;
import ru.feytox.etherology.util.gecko.EGeoAnimation;
import ru.feytox.etherology.util.misc.InventoryRecipeInput;
import ru.feytox.etherology.util.misc.TickableBlockEntity;
import ru.feytox.etherology.util.misc.UniqueProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.feytox.etherology.block.armillary.ArmillaryState.*;
import static ru.feytox.etherology.registry.block.EBlocks.ARMILLARY_MATRIX_BLOCK_ENTITY;

public class ArmillaryMatrixBlockEntity extends TickableBlockEntity implements InventoryRecipeInput, SidedInventory, EGeo2BlockEntity, UniqueProvider, RevelationAspectProvider {

    // constants
    private static final int HORIZONTAL_RADIUS = 7;
    private static final int UP_RADIUS = 3;
    private static final int DOWN_RADIUS = 1;

    // animations
    private static final EGeoAnimation IDLE_ANIM;
    private static final EGeoAnimation RUNE_0;
    private static final EGeoAnimation RUNE_1;
    private static final EGeoAnimation RUNE_2;
    private static final EGeoAnimation DECRYPTING_START;
    private static final EGeoAnimation DECRYPTING_LOOP;
    private static final EGeoAnimation DECRYPTING_END;
    private static final EGeoAnimation RESET;
    private static final EGeoAnimation[] ANIMATIONS;
    @Getter(lazy = true)
    private static final ImmutableList<EGeoAnimation> runeAnimations = ImmutableList.of(RUNE_0, RUNE_1, RUNE_2);

    // final fields
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    @Getter
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);

    // armillary info
    private int currentTick;
    private AspectContainer allCurrentAspects = new AspectContainer();
    private List<Item> decryptedItems = new ObjectArrayList<>();
    @Nullable
    private Identifier recipeId = null;
    private Set<String> activeAnimations = new ObjectOpenHashSet<>();
    private float storedEther;

    // server cache
    @Nullable
    private ArmillaryRecipe recipeCache = null;
    @Nullable
    private List<BlockPos> pedestalsCache = null;
    @Nullable
    private PedestalBlockEntity cachedTargetPedestal = null;

    // client cache
    @Getter
    @Setter
    private Float cachedUniqueOffset = null;
    @Nullable
    private ArmillaryMatrixSoundInstance soundInstance = null;
    private boolean animationsRefreshed = false;

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
        tickSound();
        tickAnimations(state);
        tickClientParticles(world, state);
    }

    private void tickClientParticles(ClientWorld world, BlockState state) {
        val matrixState = getMatrixState(state);
        Vec3d centerPos = getCenterPos();
        switch (matrixState) {
            case CONSUMING -> {
                Optional<? extends LivingEntity> match = findClosestEntity(world, centerPos);
                match.ifPresent(entity -> spawnConsumingParticle(world, entity, centerPos));
                if (world.getTime() % 2 == 0 && world.getRandom().nextBoolean()) {
                    world.playSound(centerPos.x, centerPos.y, centerPos.z, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 0.066f, 0.7f * world.getRandom().nextFloat() + 0.55f, true);
                }
            }
            case RESETTING -> spawnSphereParticles(world);
            case DECRYPTING_START, RESULTING -> spawnElectricityParticles(world);
            case DECRYPTING -> {
                spawnElectricityParticles(world);
                spawnSphereParticles(world);
            }
        }
    }

    private void spawnSphereParticles(ClientWorld world) {
        Random random = world.getRandom();
        Vec3d centerPos = getCenterPos();
        Vec3d randomVec = new Vec3d(0.4 + random.nextDouble()*0.4, 0.4 + random.nextDouble()*0.4, 0.4 + random.nextDouble()*0.4);
        randomVec = randomVec.multiply(random.nextInt(2)*2 - 1, random.nextInt(2)*2 - 1, random.nextInt(2)*2 - 1);
        Vec3d startPos = centerPos.add(randomVec);

        val effect = new MovingParticleEffect(EtherParticleTypes.ARMILLARY_SPHERE, randomVec);
        effect.spawnParticles(world, 2, 0, startPos);
    }

    private void spawnElectricityParticles(ClientWorld world) {
        Random random = world.getRandom();
        val effect = ElectricityParticleEffect.of(random, ElectricitySubtype.MATRIX);
        effect.spawnParticles(world, 1, 0.45, getCenterPos());
    }

    private void spawnResultingParticles(ServerWorld world) {
        Random random = world.getRandom();
        FeyParticleEffect.spawnParticles(ParticleTypes.CLOUD, world, random.nextBetween(4, 8), 0.3, getCenterPos());
    }

    /**
     * Handles server-side the event when the player uses their hand on a matrix base.
     *
     * @param  world   the server world
     * @param  state   the block state
     * @param  player  the player entity
     */
    public void onHandUse(ServerWorld world, BlockState state, PlayerEntity player, Hand hand) {
        ItemStack handStack = player.getMainHandStack();
        ItemStack matrixStack = getStack(0);
        val matrixState = getMatrixState(state);
        switch (matrixState) {
            case PREPARED -> {
                if (!handStack.isOf(ToolItems.STAFF)) break;
                boolean canCraft = refreshAspectsPedestals(world) && testForRecipe(world);
                val stateToSet = canCraft ? CONSUMING : IDLE;
                setMatrixState(world, state, stateToSet);
                if (!canCraft) {
                    stopAllRuneAnimations();
                    IDLE_ANIM.trigger(this);
                }
                syncData(world);
                return;
            }
            case TESTED -> {
                if (!handStack.isOf(ToolItems.STAFF)) break;
                setMatrixState(world, state, RESETTING);
                stopAllRuneAnimations();
                RESET.triggerOnce(this);
                syncData(world);
                return;
            }
            case IDLE -> {}
            default -> {
                return;
            }
        }

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
                if (!ItemStack.areItemsAndComponentsEqual(matrixStack, handStack) || handStack.getCount() >= handStack.getMaxCount()) return;
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

    private void stopAllRuneAnimations() {
        getRuneAnimations().forEach(anim -> anim.stop(this));
    }

    public boolean refreshAspectsPedestals(ServerWorld world) {
        List<PedestalBlockEntity> pedestals = getAndCachePedestals(world);
        Stream<ItemStack> stackStream = pedestals.stream().map(inv -> inv.getStack(0));
        if (!decryptedItems.isEmpty()) {
            val decryptedStream = decryptedItems.stream().map(Item::getDefaultStack);
            stackStream = Stream.concat(stackStream, decryptedStream);
        }
        val container = stackStream
                .filter(stack -> !stack.isEmpty())
                .map(stack -> AspectsLoader.getAspects(stack, false))
                .filter(Optional::isPresent).map(Optional::get)
                .reduce(AspectContainer::add).orElse(null);

        if (container == null) {
            allCurrentAspects = new AspectContainer();
            return false;
        }

        allCurrentAspects = container;
        return true;
    }

    public boolean testForRecipe(ServerWorld world) {
        val recipeEntry = RecipesRegistry.getFirstMatch(world, this, ArmillaryRecipeSerializer.INSTANCE);
        if (recipeEntry == null) return false;

        recipeId = recipeEntry.id();
        recipeCache = recipeEntry.value();
        return true;
    }

    @Nullable
    public ArmillaryRecipe getRecipe(ServerWorld world, boolean refresh) {
        if (refresh || recipeId == null) {
            if (!refreshAspectsPedestals(world) || !testForRecipe(world)) return null;
        }
        if (recipeCache != null) return recipeCache;
        if (recipeId == null) return null;

        return RecipesRegistry.maybeGet(world, recipeId).map(entry -> {
                    if (!(entry.value() instanceof ArmillaryRecipe armillaryRecipe)) return null;
                    recipeCache = armillaryRecipe;
                    return armillaryRecipe;
                }).orElse(null);
    }

    private static EGeoAnimation getRandomRuneAnimation(World world) {
        val runes = getRuneAnimations();
        Random random = world.getRandom();
        return runes.get(random.nextInt(runes.size()));
    }

    private void tickAnimations(BlockState state) {
        if (animationsRefreshed) return;

        val matrixState = getMatrixState(state);
        if (matrixState.equals(IDLE)) IDLE_ANIM.triggerOnce(this);
        else activeAnimations.forEach(this::triggerOnce);

        animationsRefreshed = true;
    }

    /**
     * Tick the sound if matrix is working.
     */
    private void tickSound() {
        val client = MinecraftClient.getInstance();
        if (client.player == null) return;

        if (soundInstance == null && client.player.squaredDistanceTo(getCenterPos()) < 36) {
            soundInstance = new ArmillaryMatrixSoundInstance(this, client.player);
            client.getSoundManager().play(soundInstance);
            return;
        }

        if (soundInstance == null) return;
        if (soundInstance.isDone()) soundInstance = null;
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
            case IDLE -> {
                return;
            }
            case RESETTING -> {
                if (currentTick++ >= 16) {
                    if (!refreshAspectsPedestals(world)) {
                        resetMatrix(world, state);
                        RESET.switchTo(this, IDLE_ANIM);
                        break;
                    } else state = setMatrixState(world, state, TESTED);
                    if (testForRecipe(world)) setMatrixState(world, state, PREPARED);
                    RESET.switchTo(this, getRandomRuneAnimation(world));
                }
            }
            case CONSUMING -> {
                if (tickConsuming(world)) break;
                val recipe = getRecipe(world, true);
                stopAllRuneAnimations();
                if (recipe == null) {
                    resetMatrix(world, state);
                    IDLE_ANIM.trigger(this);
                    return;
                }
                DECRYPTING_START.trigger(this);
                setMatrixState(world, state, ArmillaryState.DECRYPTING_START);
            }
            case DECRYPTING_START -> {
                if (currentTick++ >= 59) {
                    DECRYPTING_START.switchTo(this, DECRYPTING_LOOP);
                    setMatrixState(world, state, DECRYPTING);
                }
            }
            case DECRYPTING -> {
                if (tickItemDecrypting(world)) break;
                currentTick = 0;
                val recipe = getRecipe(world, true);
                if (recipe == null) {
                    DECRYPTING_LOOP.switchTo(this, IDLE_ANIM);
                    resetMatrix(world, state);
                    return;
                }

                if (!isPedestalsEmpty(world)) break;

                setMatrixState(world, state, RESULTING);
                DECRYPTING_LOOP.switchTo(this, DECRYPTING_END);
            }
            case RESULTING -> {
                if (currentTick++ >= 74) {
                    DECRYPTING_END.switchTo(this, IDLE_ANIM);
                    val recipe = getRecipe(world, true);
                    if (recipe != null) {
                        setStack(0, recipe.getOutput());
                        spawnResultingParticles(world);
                    }

                    resetMatrix(world, state);
                    return;
                }
            }
        }

        syncData(world);
    }

    private void resetMatrix(ServerWorld world, BlockState state) {
        setMatrixState(world, state, IDLE);
        allCurrentAspects = new AspectContainer();
        decryptedItems.clear();
        recipeId = null;
        storedEther = 0.0f;
        syncData(world);
    }

    private boolean tickConsuming(ServerWorld world) {
        val recipe = getRecipe(world, false);
        if (world.getTime() % 20 != 0 || recipe == null) return true;
        if (storedEther >= recipe.getEtherPoints()) return false;

        Vec3d centerPos = getCenterPos();
        Optional<? extends LivingEntity> entityMatch = findClosestEntity(world, centerPos);
        if (entityMatch.isEmpty()) return true;
        LivingEntity entity = entityMatch.get();
        consumeEther(entity, entity.isPlayer() ? 0.75f: 0.5f);
        return true;
    }

    private boolean tickItemDecrypting(ServerWorld world) {
        val target = getCachedTargetPedestal(world);
        if (target == null || !target.hasItem()) return false;

        if (currentTick++ >= 60) {
            decryptedItems.add(target.getStack(0).getItem());
            target.removeStack(0);
            target.syncData(world);
            cachedTargetPedestal = null;
            return false;
        }
        if (world.getTime() % 5 != 0) return true;

        Vec3d pedestalPos = target.getPos().toCenterPos().add(0, 1, 0);
        Vec3d centerPos = getCenterPos();
        Random random = world.getRandom();

        ItemParticleEffect itemEffect = new ItemParticleEffect(EtherParticleTypes.ITEM, target.getStack(0).getItem(), centerPos);
        itemEffect.spawnParticles(world, 5, 0.2, pedestalPos);

        LightParticleEffect sparkLightEffect = new LightParticleEffect(EtherParticleTypes.LIGHT, LightSubtype.SPARK, centerPos);
        sparkLightEffect.spawnParticles(world, random.nextBetween(10, 25), 0.35, pedestalPos);

        SparkParticleEffect sparkEffect = new SparkParticleEffect(EtherParticleTypes.SPARK, centerPos, SparkSubtype.SIMPLE);
        sparkEffect.spawnParticles(world, random.nextBetween(1, 5), 0.35, pedestalPos);
        return true;
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
        Box entitiesBox = Box.enclosing(pos.add(-HORIZONTAL_RADIUS, -DOWN_RADIUS, -HORIZONTAL_RADIUS), pos.add(HORIZONTAL_RADIUS, UP_RADIUS, HORIZONTAL_RADIUS));
        val mobs = world.getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class), entitiesBox, entity -> !entity.isPlayer());
        return mobs.isEmpty() ? Optional.empty() : Optional.of(mobs.getFirst());
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
        if (entity.isPlayer()) {
            if (!EtherComponent.decrement(entity, value)) return;
        }
        else entity.damage(entity.getDamageSources().magic(), value);
        storedEther += value;
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
        val effect = new MovingParticleEffect(EtherParticleTypes.VITAL, centerPos);
        effect.spawnParticles(world, 1, 0.1, entity.getBoundingBox().getCenter());
    }

    /**
     * Calculates and returns the matrix center position for the given matrix state.
     *
     * @return              the matrix center position
     */
    public Vec3d getCenterPos() {
        return getCenterBlockPos().toCenterPos();
    }

    public BlockPos getCenterBlockPos() {
        return pos.up(2);
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
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putInt("current_tick", currentTick);
        Inventories.writeNbt(nbt, items, registryLookup);
        writeActiveAnimations(nbt);
        writeDecryptedItems(nbt);
        allCurrentAspects.writeNbt(nbt);

        if (recipeId == null) nbt.putString("recipe_id", "");
        else nbt.putString("recipe_id", recipeId.toString());

        super.writeNbt(nbt, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        currentTick = nbt.getInt("current_tick");
        items.clear();
        Inventories.readNbt(nbt, items, registryLookup);
        activeAnimations = readActiveAnimations(nbt);
        decryptedItems = readDecryptedItems(nbt);
        allCurrentAspects = allCurrentAspects.readNbt(nbt);

        String id = nbt.getString("recipe_id");
        recipeId = id.isEmpty() ? null : Identifier.of(id);
    }

    private void writeDecryptedItems(NbtCompound nbt) {
        NbtList nbtList = new NbtList();
        decryptedItems.stream()
                .map(Registries.ITEM::getId).map(Identifier::toString)
                .map(NbtString::of).forEach(nbtList::add);
        nbt.put("decrypted_items", nbtList);
    }

    private List<Item> readDecryptedItems(NbtCompound nbt) {
        if (!nbt.contains("decrypted_items")) return new ObjectArrayList<>();

        NbtList nbtList = nbt.getList("decrypted_items", NbtElement.STRING_TYPE);
        return nbtList.stream()
                .map(element -> (NbtString) element).map(NbtString::asString)
                .map(Identifier::of).map(Registries.ITEM::get)
                .collect(Collectors.toCollection(ObjectArrayList::new));
    }

    private void writeActiveAnimations(NbtCompound nbt) {
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

    @Override
    public double getBoneResetTime() {
        return 0.0000001d;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        Arrays.stream(ANIMATIONS).map(anim -> anim.generateController(this))
                .filter(Objects::nonNull).forEach(controllers::add);
        controllers.add(registerRuneController(RUNE_0), registerRuneController(RUNE_1), registerRuneController(RUNE_2));
    }

    private AnimationController<?> registerRuneController(EGeoAnimation animation) {
        return animation.forceGenerateController(this)
                .setSoundKeyframeHandler(state -> {
                    PlayerEntity player = MinecraftClient.getInstance().player;
                    if (player == null || player.getWorld() == null) return;

                    Vec3d pos = getCenterPos();
                    player.getWorld().playSound(pos.x, pos.y, pos.z, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 0.75f, 1.0f, true);
                });
    }

    @Nullable
    public PedestalBlockEntity getCachedTargetPedestal(ServerWorld world) {
        if (cachedTargetPedestal != null) return cachedTargetPedestal;
        cachedTargetPedestal = getFirstNonEmptyPedestal(world);
        return cachedTargetPedestal;
    }

    @Nullable
    public PedestalBlockEntity getFirstNonEmptyPedestal(ServerWorld world) {
        val pedestals = getCachedPedestals(world);
        if (pedestals.isEmpty()) return null;
        for (val pedestal : pedestals) {
            if (pedestal.hasItem()) return pedestal;
        }
        return null;
    }

    private boolean isPedestalsEmpty(ServerWorld world) {
        val pedestals = getCachedPedestals(world);
        if (pedestals.isEmpty()) return true;
        for (val pedestal : pedestals) {
            if (pedestal.hasItem()) return false;
        }
        return true;
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
    private List<PedestalBlockEntity> getAndCachePedestals(World world) {
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

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }


    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[0];
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    static {
        IDLE_ANIM = EGeoAnimation.begin("idle")
                .thenLoop("animation.armillary_matrix.idle");
        RUNE_0 = EGeoAnimation.begin("rune_0").withoutController()
                .thenPlayAndHold("animation.armillary_matrix.rune_0");
        RUNE_1 = EGeoAnimation.begin("rune_1").withoutController()
                .thenPlayAndHold("animation.armillary_matrix.rune_1");
        RUNE_2 = EGeoAnimation.begin("rune_2").withoutController()
                .thenPlayAndHold("animation.armillary_matrix.rune_2");
        DECRYPTING_START = EGeoAnimation.begin("decrypting_start")
                .thenPlay("animation.armillary_matrix.decrypting_start");
        DECRYPTING_LOOP = EGeoAnimation.begin("decrypting_loop")
                .thenLoop("animation.armillary_matrix.decrypting_loop");
        DECRYPTING_END = EGeoAnimation.begin("decrypting_end").withoutMarking()
                .thenPlay("animation.armillary_matrix.decrypting_end");
        RESET = EGeoAnimation.begin("reset").withoutMarking()
                .thenPlay("animation.armillary_matrix.reset");

        ANIMATIONS = new EGeoAnimation[]{IDLE_ANIM, RUNE_0, RUNE_1, RUNE_2, DECRYPTING_START, DECRYPTING_LOOP, DECRYPTING_END, RESET};
    }

    @Override
    public @Nullable AspectContainer getRevelationAspects() {
        return allCurrentAspects;
    }

    @Override
    public int getRevelationAspectsLimit() {
        return 3;
    }

    public List<Aspect> getSortedAspects() {
        return allCurrentAspects.sorted(true, 3).stream()
                .map(Pair::key).collect(Collectors.toCollection(ObjectArrayList::new));
    }
}
