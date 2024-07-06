package ru.feytox.etherology.block.pedestal;

import io.wispforest.owo.util.ImplementedInventory;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.BlockState;
import net.minecraft.block.DyedCarpetBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.data.item_aspects.AspectsLoader;
import ru.feytox.etherology.magic.aspects.AspectContainer;
import ru.feytox.etherology.magic.aspects.RevelationAspectProvider;
import ru.feytox.etherology.util.misc.TickableBlockEntity;
import ru.feytox.etherology.util.misc.UniqueProvider;

import static ru.feytox.etherology.registry.block.EBlocks.PEDESTAL_BLOCK_ENTITY;

public class PedestalBlockEntity extends TickableBlockEntity
        implements ImplementedInventory, RevelationAspectProvider, UniqueProvider, SidedInventory {
    // 0 - item, 1 - carpet
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(2, ItemStack.EMPTY);
    @Getter
    @Setter
    private Float cachedUniqueOffset = null;

    public PedestalBlockEntity(BlockPos pos, BlockState state) {
        super(PEDESTAL_BLOCK_ENTITY, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    public boolean hasItem() {
        return !getStack(0).isEmpty();
    }

    // TODO: 24.03.2024 consider simplifying so PedestalDispenserBehavior can use simpler code
    public void interact(ServerWorld world, BlockState state, PlayerEntity player, Hand hand) {
        ItemStack handStack = player.getStackInHand(hand);
        ItemStack pedestalStack = getStack(0);
        ItemStack carpetStack = getStack(1);

        if (!handStack.isEmpty()) {
            // размещение ковра
            if (placeCarpet(world, state, player, hand, handStack, carpetStack)) return;

            // размещение предмета на пустом пьедестале
            if (pedestalStack.isEmpty()) {
                setStack(0, handStack.copyWithCount(1));
                handStack.decrement(1);
                player.setStackInHand(hand, handStack);
                playItemPlaceSound(world, pos);
                return;
            }

            // взятие похожего предмета с пьедестала
            if (ItemStack.areItemsAndComponentsEqual(handStack, pedestalStack) && handStack.getCount() < handStack.getMaxCount()) {
                setStack(0, ItemStack.EMPTY);
                handStack.increment(1);
                player.setStackInHand(hand, handStack);
                playItemTakeSound(world, pos);
                return;
            }
        }

        // взятие ковра в пустую руку
        if (pedestalStack.isEmpty() && handStack.isEmpty()) {
            if (carpetStack.isEmpty()) return;

            player.setStackInHand(hand, carpetStack);
            setStack(1, ItemStack.EMPTY);
            setCarpetColor(world, player, state, DyeColor.WHITE, false);
            return;
        }

        // взятие предмета в пустую руку
        if (handStack.isEmpty()) {
            player.setStackInHand(hand, pedestalStack);
            setStack(0, ItemStack.EMPTY);
            playItemTakeSound(world, pos);
        }
    }

    private boolean placeCarpet(ServerWorld world, BlockState state, PlayerEntity player, Hand hand, ItemStack handStack, ItemStack carpetStack) {
        if (!(handStack.getItem() instanceof BlockItem blockItem)) return false;
        if (!(blockItem.getBlock() instanceof DyedCarpetBlock carpet)) return false;

        // взятие ковра в стак с коврами
        if (ItemStack.areItemsAndComponentsEqual(handStack, carpetStack) && handStack.getCount() < handStack.getMaxCount()) {
            setStack(1, ItemStack.EMPTY);
            handStack.increment(1);
            player.setStackInHand(hand, handStack);
            setCarpetColor(world, player, state, DyeColor.WHITE, false);
            return true;
        }

        ItemStack copyStack = handStack.copyWithCount(1);
        if (handStack.getCount() > 1) {
            if (!carpetStack.isEmpty()) return true;

            // размещение 1 ковра из стака с коврами
            setStack(1, copyStack);
            handStack.decrement(1);
            player.setStackInHand(hand, handStack);
            setCarpetColor(world, player, state, carpet.getDyeColor(), true);
            return true;
        }

        // замена ковра (или пустоты) на пьедестале ковром из руки
        player.setStackInHand(hand, carpetStack);
        setStack(1, copyStack);
        setCarpetColor(world, player, state, carpet.getDyeColor(), true);
        return true;
    }

    private void setCarpetColor(ServerWorld world, PlayerEntity player, BlockState state, DyeColor dyeColor, boolean withCarpet) {
        setCarpetColor(world, player.getHorizontalFacing().getOpposite(), state, dyeColor, withCarpet);
    }

    public void setCarpetColor(ServerWorld world, Direction direction, BlockState state, DyeColor dyeColor, boolean withCarpet) {
        world.setBlockState(pos, state
                .with(PedestalBlock.CLOTH_COLOR, dyeColor)
                .with(PedestalBlock.DECORATION, withCarpet)
                .with(PedestalBlock.FACING, direction));
        playCarpetSound(world);
    }

    private void playCarpetSound(ServerWorld world) {
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_HORSE_SADDLE, SoundCategory.BLOCKS, 0.5f, 0.9f + 0.2f * world.getRandom().nextFloat());
    }

    public static void playItemTakeSound(ServerWorld world, BlockPos pos) {
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 0.5f, 0.9f + 0.2f * world.getRandom().nextFloat());
    }

    public static void playItemPlaceSound(ServerWorld world, BlockPos pos) {
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, 0.5f, 0.9f + 0.2f * world.getRandom().nextFloat());
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.writeNbt(nbt, items, registryLookup);
        nbt.putBoolean("removed", removed);

        super.writeNbt(nbt, registryLookup);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        items.clear();
        Inventories.readNbt(nbt, items, registryLookup);
        removed = nbt.getBoolean("removed");
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public AspectContainer getRevelationAspects() {
        if (items.getFirst().isEmpty()) return null;
        return AspectsLoader.getAspects(items.getFirst(), false).orElse(null);
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
