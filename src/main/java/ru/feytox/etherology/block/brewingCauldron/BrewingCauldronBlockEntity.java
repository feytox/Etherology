package ru.feytox.etherology.block.brewingCauldron;

import io.wispforest.owo.util.ImplementedInventory;
import lombok.Getter;
import lombok.val;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.data.item_aspects.AspectsLoader;
import ru.feytox.etherology.magic.aspects.AspectContainer;
import ru.feytox.etherology.magic.aspects.RevelationAspectProvider;
import ru.feytox.etherology.magic.corruption.Corruption;
import ru.feytox.etherology.network.animation.StartBlockAnimS2C;
import ru.feytox.etherology.particle.effects.SimpleParticleEffect;
import ru.feytox.etherology.particle.effects.misc.FeyParticleEffect;
import ru.feytox.etherology.recipes.brewingCauldron.CauldronRecipe;
import ru.feytox.etherology.recipes.brewingCauldron.CauldronRecipeInventory;
import ru.feytox.etherology.recipes.brewingCauldron.CauldronRecipeSerializer;
import ru.feytox.etherology.registry.misc.EtherSounds;
import ru.feytox.etherology.registry.misc.RecipesRegistry;
import ru.feytox.etherology.registry.particle.EtherParticleTypes;
import ru.feytox.etherology.util.gecko.EGeoBlockEntity;
import ru.feytox.etherology.util.misc.TickableBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.feytox.etherology.registry.block.EBlocks.BREWING_CAULDRON_BLOCK_ENTITY;
import static ru.feytox.etherology.registry.particle.EtherParticleTypes.STEAM;

public class BrewingCauldronBlockEntity extends TickableBlockEntity implements ImplementedInventory, SidedInventory, EGeoBlockEntity, RevelationAspectProvider {

    private static final RawAnimation MIXING = RawAnimation.begin().thenPlay("brewing_cauldron.mixing");

    @Getter
    private AspectContainer aspects = new AspectContainer();
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(8, ItemStack.EMPTY);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    @Getter
    private int temperature = 20;
    private int cacheItemsCount = 0;
    private boolean shouldMixItems = false;
    private int mixItemsTicks = 0;
    @Getter
    private boolean wasWithAspects = false;
    @Nullable
    private BrewingCauldronSoundInstance soundInstance = null;

    public BrewingCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(BREWING_CAULDRON_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void serverTick(ServerWorld world, BlockPos blockPos, BlockState state) {
        if (!BrewingCauldronBlock.isFilled(state)) return;
        tickMixingItems(world, state);
        tickAspects(world, state);
        tickTemperature(world, blockPos);
    }

    @Override
    public void clientTick(ClientWorld world, BlockPos blockPos, BlockState state) {
        if (!BrewingCauldronBlock.isFilled(state)) return;

        tickBubbleParticles(world, state);
        tickCorruptionParticles(world, state);
        tickSound(world);
    }

    private void tickSound(ClientWorld world) {
        if (temperature < 100) return;
        val client = MinecraftClient.getInstance();
        if (client.player == null) return;
        if (world.getTime() % 3 != 0) return;
        if (world.getRandom().nextFloat() > 0.25f) return;

        if (soundInstance == null && client.player.squaredDistanceTo(pos.toCenterPos()) < 36) {
            soundInstance = new BrewingCauldronSoundInstance(this, client.player);
            client.getSoundManager().play(soundInstance);
            return;
        }

        if (soundInstance == null) return;
        if (soundInstance.isDone() || !client.getSoundManager().isPlaying(soundInstance)) soundInstance = null;
    }

    private void tickBubbleParticles(ClientWorld world, BlockState state) {
        if (world.getTime() % 3 != 0) return;
        if (temperature < 100) return;

        Random random = world.getRandom();
        for (int i = 0; i < random.nextBetween(1, 4); i++) {
            SimpleParticleType effect = random.nextBoolean() ? ParticleTypes.BUBBLE : ParticleTypes.BUBBLE_POP;
            Vec3d start = getWaterPos(state).add(Vec3d.of(pos));
            start = start.add(FeyParticleEffect.getRandomPos(random, 0.25, 0.05, 0.25));
            world.addParticle(effect, start.x, start.y, start.z, 0, 0.001, 0);
        }
    }

    private void tickMixingItems(ServerWorld world, BlockState state) {
        if (!shouldMixItems || mixItemsTicks-- > 0) return;

        shouldMixItems = false;
        mixItems(world, state);
    }

    private void tickAspects(ServerWorld world, BlockState state) {
        if (!BrewingCauldronBlock.isFilled(state)) {
            clearAspects(world);
            updateAspectsLvl(world, state, 0);
            return;
        }

        int oldCount = aspects.sum().orElse(0);
        updateAspectsLvl(world, state, oldCount);

        if (world.getTime() % 100 != 0 || oldCount == 0) return;
        Random random = world.getRandom();

        vaporizeAspects(world, 0.1d, 0.05d, random, oldCount);
    }

    private void vaporizeAspects(ServerWorld world, double minChance, double perAspectChance, Random random, int oldCount) {
        aspects = aspects.map(value -> {
            double chance = minChance + perAspectChance * value;
            if (random.nextDouble() > chance) return value;
            return value - 1;
        });

        int deltaCount = oldCount - aspects.sum().orElse(0);
        if (deltaCount > 0) {
            Corruption corruption = Corruption.ofAspects(deltaCount);
            corruption.placeInChunk(world, pos);
        }

        syncData(world);
    }

    private void updateAspectsLvl(ServerWorld world, BlockState state, int count) {
        int aspectsLvl = (int) Math.min(100, 100 * count / 64f);
        if (state.get(BrewingCauldronBlock.ASPECTS_LVL) == aspectsLvl) return;

        world.setBlockState(pos, state.with(BrewingCauldronBlock.ASPECTS_LVL, aspectsLvl));
    }

    private void tickCorruptionParticles(ClientWorld world, BlockState state) {
        if (temperature < 100) return;
        int count = aspects.sum().orElse(0);
        if (count == 0) return;
        if (world.getTime() % Math.max(MathHelper.floor(250.0f / count + 2), 1) != 0) return;
        if (world.getRandom().nextFloat() < 0.5) return;

        Vec3d start = getWaterPos(state).add(Vec3d.of(pos));
        val effect = new SimpleParticleEffect(EtherParticleTypes.HAZE);
        effect.spawnParticles(world, 1, 0.1, start);
    }

    private void tickTemperature(ServerWorld world, BlockPos blockPos) {
        if (world.getTime() % 10 != 0) return;

        BlockState downState = world.getBlockState(blockPos.down());
        boolean isHotBlock = downState.isIn(BlockTags.FIRE) || downState.isIn(BlockTags.CAMPFIRES) || downState.isOf(Blocks.LAVA) || downState.isOf(Blocks.MAGMA_BLOCK);
        if (!isHotBlock && world.getRandom().nextBoolean()) return;

        int change = isHotBlock ? 1 : -1;
        temperature = MathHelper.clamp(temperature + change, 20, 100);
        syncData(world);
    }

    public void clearAspects(ServerWorld world) {
        aspects = new AspectContainer();
        wasWithAspects = false;
        syncData(world);
    }

    public void consumeItem(ServerWorld world, ItemEntity itemEntity, BlockState state) {
        if (itemEntity instanceof CauldronItemEntity) return;

        ItemStack stack = itemEntity.getStack();
        if (isEmpty() && tryCraft(world, stack, state)) {
            itemEntity.discard();
            spawnResultParticles(world, state);
            Random random = world.getRandom();
            world.playSound(null, pos, EtherSounds.POUF, SoundCategory.BLOCKS, 1.0f, random.nextFloat()*0.2f+0.9f);
            return;
        }

        if (!BrewingCauldronBlock.isFilled(world, pos)) return;
        if (putStack(stack).isEmpty()) {
            itemEntity.discard();
        }
        syncData(world);
    }

    private void spawnResultParticles(ServerWorld world, BlockState state) {
        Random random = world.getRandom();
        Vec3d start = getWaterPos(state).add(Vec3d.of(pos));
        val effect = new SimpleParticleEffect(EtherParticleTypes.ALCHEMY);
        effect.spawnParticles(world, random.nextBetween(6, 10), 0.1f, start);
    }

    public void mixWater(World world) {
        StartBlockAnimS2C.sendForTracking(this, "mixing");
        scheduleMixItems();
        world.playSound(null, pos, EtherSounds.BREWING_DISSOLUTION, SoundCategory.BLOCKS, 1.5f, world.getRandom().nextFloat()*0.2f+0.9f);
    }

    private void scheduleMixItems() {
        shouldMixItems = true;
        mixItemsTicks = 10;
    }

    private void mixItems(ServerWorld world, BlockState state) {
        AtomicInteger count = new AtomicInteger();
        items.stream()
                .filter(stack -> !stack.isEmpty())
                .map(stack -> AspectsLoader.getAspects(stack, true).orElse(null))
                .filter(Objects::nonNull)
                .forEach(itemAspects -> {
                    count.addAndGet(1);
                    aspects = aspects.add(itemAspects);
                });

        if (!aspects.isEmpty()) wasWithAspects = true;
        clear();

        int oldCount = aspects.sum().orElse(0);
        if (oldCount != 0) vaporizeAspects(world, 0.2d, 0.1d, world.getRandom(), oldCount);

        Vec3d centerPos = getWaterPos(state).add(Vec3d.of(pos));
        val effect = new SimpleParticleEffect(STEAM);
        effect.spawnParticles(world, count.get(), 0.35, centerPos);

        syncData(world);
    }

    private boolean tryCraft(ServerWorld world, ItemStack inputStack, BlockState state) {
        CauldronRecipeInventory inventory = new CauldronRecipeInventory(aspects, inputStack);
        val recipeEntry = RecipesRegistry.getFirstMatch(world, inventory, CauldronRecipeSerializer.INSTANCE);
        if (recipeEntry == null) return false;

        ItemStack resultStack = craft(world, inputStack, recipeEntry.value(), state);
        CauldronItemEntity.spawn(world, pos.up().toCenterPos(), resultStack);
        syncData(world);
        spawnCraftParticle(world);

        return inputStack.isEmpty();
    }

    private void spawnCraftParticle(ServerWorld world) {
        BlockState state = getCachedState();
        Random random = world.getRandom();

        for (int i = 0; i < random.nextBetween(1, 3); i++) {
            Vec3d start = getWaterPos(state).add(Vec3d.of(pos));
            start = start.add(FeyParticleEffect.getRandomPos(random, 0.1, 0.05, 0.1));
            world.spawnParticles(ParticleTypes.CLOUD, start.x, start.y, start.z, 1, 0, 0, 0, 0);
        }
    }

    private ItemStack craft(ServerWorld world, ItemStack itemStack, CauldronRecipe recipe, BlockState state) {
        CauldronRecipeInventory inventory;
        Item outputItem = recipe.getOutput().getItem();
        int count = 0;

        do {
            itemStack.decrement(recipe.getInputAmount());
            aspects = aspects.subtract(recipe.getInputAspects());
            count += recipe.getOutput().getCount();

            int oldLevel = state.get(BrewingCauldronBlock.LEVEL);
            state = state.with(BrewingCauldronBlock.LEVEL, oldLevel-1);
            inventory = new CauldronRecipeInventory(aspects, itemStack);
        } while (recipe.matches(inventory, world) && BrewingCauldronBlock.isFilled(state));

        if (!BrewingCauldronBlock.isFilled(state)) temperature = 20;
        world.setBlockState(pos, state);
        return new ItemStack(outputItem, count);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putInt("temperature", temperature);
        nbt.putBoolean("wasWithAspects", wasWithAspects);
        aspects.writeNbt(nbt);
        Inventories.writeNbt(nbt, items, registryLookup);

        super.writeNbt(nbt, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        temperature = nbt.getInt("temperature");
        wasWithAspects = nbt.getBoolean("wasWithAspects");
        aspects = aspects.readNbt(nbt);
        items.clear();
        Inventories.readNbt(nbt, items, registryLookup);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    public ItemStack takeLastStack(ServerWorld world) {
        int lastSlot = getLastStackSlot();
        ItemStack result = lastSlot == -1 ? ItemStack.EMPTY : removeStack(lastSlot);
        syncData(world);
        return result;
    }

    public int getLastStackSlot() {
        for (int i = items.size()-1; i >= 0; i--) {
            ItemStack slotStack = getStack(i);
            if (!slotStack.isEmpty()) return i;
        }
        return -1;
    }

    public ItemStack putStack(ItemStack remainingStack) {
        if (!getStack(7).isEmpty()) return remainingStack;

        for (int i = 0; i < size(); i++) {
            if (remainingStack.isEmpty()) return ItemStack.EMPTY;
            ItemStack slotStack = getStack(i);
            if (!slotStack.isEmpty()) continue;
            setStack(i, remainingStack.copyWithCount(1));
            remainingStack.decrement(1);
        }

        return remainingStack;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(createTriggerController("mixing", MIXING));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getBoneResetTime() {
        return 0.0000001d;
    }

    public static Vec3d getWaterPos(BlockState state) {
        double y = 0.4475 + 0.0625 * (state.get(BrewingCauldronBlock.LEVEL) - 1);
        return new Vec3d(0.5, y, 0.5);
    }

    public void cacheItems() {
        cacheItemsCount = getLastStackSlot() + 1;
    }

    public int checkCacheItems() {
        if (!isEmpty()) return 0;
        return cacheItemsCount;
    }

    @Override
    public @Nullable AspectContainer getRevelationAspects() {
        return aspects;
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
}
