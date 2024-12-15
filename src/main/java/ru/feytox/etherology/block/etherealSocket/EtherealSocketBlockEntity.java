package ru.feytox.etherology.block.etherealSocket;

import io.wispforest.owo.util.ImplementedInventory;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.item.glints.GlintItem;
import ru.feytox.etherology.magic.ether.EtherDisplay;
import ru.feytox.etherology.magic.ether.EtherGlint;
import ru.feytox.etherology.magic.ether.EtherStorage;
import ru.feytox.etherology.util.misc.TickableBlockEntity;

import static ru.feytox.etherology.block.etherealSocket.EtherealSocketBlock.WITH_GLINT;
import static ru.feytox.etherology.registry.block.EBlocks.ETHEREAL_SOCKET_BLOCK_ENTITY;

public class EtherealSocketBlockEntity extends TickableBlockEntity implements EtherStorage, EtherDisplay, ImplementedInventory, SidedInventory {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    // TODO: 09.08.2023 deprecate
    private boolean isUpdated = false;
    @Getter @Setter
    private float cachedPercent = 0;

    public EtherealSocketBlockEntity(BlockPos pos, BlockState state) {
        super(ETHEREAL_SOCKET_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void serverTick(ServerWorld world, BlockPos blockPos, BlockState state) {
        transferTick(world);

        if (isUpdated) {
            world.getChunkManager().markForUpdate(pos);
            isUpdated = false;
        }
    }

    public ActionResult onUse(ServerWorld world, PlayerEntity player, BlockState state) {
        ItemStack glintStack = getStack(0);
        ItemStack useStack = player.getMainHandStack();

        if (glintStack.isEmpty() && useStack.getItem() instanceof GlintItem) {
            // вставка глинта
            ItemStack takingStack = useStack.copy();
            useStack = glintStack;
            glintStack = takingStack;
            player.setStackInHand(Hand.MAIN_HAND, useStack);
            setStack(0, glintStack);

            world.setBlockState(pos, state.with(WITH_GLINT, true));
            isUpdated = true;
            world.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.BLOCKS, 0.6f, 0.95f);
            syncData(world);
            return ActionResult.CONSUME;

        } else if (glintStack.getItem() instanceof GlintItem && useStack.isEmpty()) {
            // забирание глинта
            ItemStack takingStack = glintStack.copy();
            glintStack.setCount(0);
            player.setStackInHand(Hand.MAIN_HAND, takingStack);

            world.setBlockState(pos, state.with(WITH_GLINT, false));
            syncData(world);
            isUpdated = true;
            world.playSound(null, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.BLOCKS, 0.8f, 0.95f);
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    public float getMaxEther() {
        if (inventory.isEmpty()) return 0;

        ItemStack glintStack = getStack(0);
        if (!(glintStack.getItem() instanceof GlintItem glint)) return 0;

        return glint.getMaxEther();
    }

    @Override
    public float getStoredEther() {
        if (inventory.isEmpty()) return 0;

        ItemStack glintStack = getStack(0);
        if (!(glintStack.getItem() instanceof GlintItem)) return 0;

        return new EtherGlint(glintStack).getStoredEther();
    }

    @Override
    public float getTransferSize() {
        return 1;
    }

    @Override
    public float increment(float value) {
        if (inventory.isEmpty()) return value;

        ItemStack glintStack = getStack(0);
        if (!(glintStack.getItem() instanceof GlintItem)) return value;

        float exceed = new EtherGlint(glintStack).increment(value);
        isUpdated = true;
        return exceed;
    }

    @Override
    public float decrement(float value) {
        if (inventory.isEmpty()) return 0;

        ItemStack glintStack = getStack(0);
        if (!(glintStack.getItem() instanceof GlintItem)) return 0;

        float decremented = new EtherGlint(glintStack).decrement(value);
        isUpdated = true;
        return decremented;
    }

    @Override
    public boolean isInputSide(Direction side) {
        return false;
    }

    @Nullable
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
        if (world.getTime() % 20 == 0) transfer(world);
    }

    @Override
    public boolean isActivated() {
        return false;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    /**
     * @deprecated use increment/decrement
     */
    @Deprecated
    @Override
    public void setStoredEther(float value) {}

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        Inventories.writeNbt(nbt, inventory, registryLookup);

        super.writeNbt(nbt, registryLookup);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        inventory.clear();
        Inventories.readNbt(nbt, inventory, registryLookup);
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

    @Override
    public float getDisplayEther() {
        return getStoredEther();
    }

    @Override
    public float getDisplayMaxEther() {
        return getMaxEther();
    }
}
