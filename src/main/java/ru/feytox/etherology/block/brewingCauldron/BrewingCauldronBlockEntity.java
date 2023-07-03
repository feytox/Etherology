package ru.feytox.etherology.block.brewingCauldron;

import io.wispforest.owo.util.ImplementedInventory;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.data.item_aspects.ItemAspectsLoader;
import ru.feytox.etherology.magic.aspects.EtherAspectsContainer;
import ru.feytox.etherology.recipes.brewingCauldron.CauldronRecipe;
import ru.feytox.etherology.recipes.brewingCauldron.CauldronRecipeInventory;
import ru.feytox.etherology.util.feyapi.TickableBlockEntity;

import java.util.Optional;

import static ru.feytox.etherology.registry.block.EBlocks.BREWING_CAULDRON_BLOCK_ENTITY;

public class BrewingCauldronBlockEntity extends TickableBlockEntity implements ImplementedInventory {
    private EtherAspectsContainer aspects = new EtherAspectsContainer(new Object2ObjectOpenHashMap<>());
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(8, ItemStack.EMPTY);

    public BrewingCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(BREWING_CAULDRON_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void serverTick(ServerWorld world, BlockPos blockPos, BlockState state) {
        if (!BrewingCauldronBlock.isFilled(state)) return;
        tickAspects(world, state);
        tickTemperature(world, blockPos, state);
    }

    private void tickAspects(ServerWorld world, BlockState state) {
        if (!BrewingCauldronBlock.isFilled(state)) {
            clearAspects(world);
            return;
        }

        if (world.getTime() % 20 != 0) return;
        Random random = world.getRandom();

        aspects = aspects.map(value -> {
            double chance = 0.1 + 0.0140625 * value;
            if (random.nextDouble() > chance) return value;
            return value - 1;
        });
        syncData(world);
    }

    private void tickTemperature(ServerWorld world, BlockPos blockPos, BlockState state) {
        if (world.getTime() % 5 != 0) return;

        BlockState downState = world.getBlockState(blockPos.down());
        if (!downState.isOf(Blocks.TORCH)) return;

        int newTemperature = Math.min(100, state.get(BrewingCauldronBlock.TEMPERATURE) + 1);
        world.setBlockState(blockPos, state.with(BrewingCauldronBlock.TEMPERATURE, newTemperature));
    }

    public void clearAspects(ServerWorld world) {
        aspects = new EtherAspectsContainer(new Object2ObjectOpenHashMap<>());
        syncData(world);
    }

    public void consumeItem(ServerWorld world, ItemEntity itemEntity, BlockState state) {
        if (itemEntity instanceof CauldronItemEntity) return;

        ItemStack stack = itemEntity.getStack();
        if (isEmpty() && tryCraft(world, stack, state)) {
            itemEntity.discard();
            return;
        }

        if (!BrewingCauldronBlock.isFilled(world, pos)) return;
        if (!ItemAspectsLoader.containsItem(stack.getItem())) return;
        if (putStack(stack).isEmpty()) itemEntity.discard();
        syncData(world);
    }

    private void syncData(ServerWorld world) {
        markDirty();
        world.getChunkManager().markForUpdate(pos);
    }

    public boolean mixWater(ServerWorld world) {
        items.forEach(stack -> {
            EtherAspectsContainer itemAspects = ItemAspectsLoader.getAspectsOf(stack).orElse(null);
            if (itemAspects == null) return;

            aspects = aspects.add(itemAspects);
        });
        clear();
        syncData(world);
        return true;
    }

    private boolean tryCraft(ServerWorld world, ItemStack inputStack, BlockState state) {
        CauldronRecipeInventory inventory = new CauldronRecipeInventory(aspects, inputStack);
        Optional<CauldronRecipe> match = world.getRecipeManager()
                .getFirstMatch(CauldronRecipe.Type.INSTANCE, inventory, world);
        if (match.isEmpty()) return false;

        ItemStack resultStack = craft(world, inputStack, match.get(), state);
        CauldronItemEntity.spawn(world, pos.up().toCenterPos(), resultStack);
        syncData(world);
        return inputStack.isEmpty();
    }

    @NotNull
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

        if (!BrewingCauldronBlock.isFilled(state)) state = state.with(BrewingCauldronBlock.TEMPERATURE, 20);
        world.setBlockState(pos, state);
        return new ItemStack(outputItem, count);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        aspects.writeNbt(nbt);
        Inventories.writeNbt(nbt, items);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        aspects = (EtherAspectsContainer) aspects.readNbt(nbt);
        items.clear();
        Inventories.readNbt(nbt, items);
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
}
