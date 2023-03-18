package ru.feytox.etherology.blocks.etherWorkbench;

import io.wispforest.owo.util.ImplementedInventory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.recipes.ether.EtherRecipe;

import java.util.Optional;

import static ru.feytox.etherology.BlocksRegistry.ETHER_WORKBENCH_BLOCK_ENTITY;

public class EtherWorkbenchBlockEntity extends BlockEntity implements ImplementedInventory,
        ExtendedScreenHandlerFactory, SidedInventory {
    // 9 - grid, 4 - shards, 1 - output
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(14, ItemStack.EMPTY);

    public EtherWorkbenchBlockEntity(BlockPos pos, BlockState state) {
        super(ETHER_WORKBENCH_BLOCK_ENTITY, pos, state);
    }

    public static void clientTick(World world, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        EtherWorkbenchBlockEntity etherWorkbench = (EtherWorkbenchBlockEntity) blockEntity;
        ClientWorld clientWorld = (ClientWorld) world;
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        EtherWorkbenchBlockEntity etherWorkbench = (EtherWorkbenchBlockEntity) blockEntity;
        ServerWorld serverWorld = (ServerWorld) world;

        etherWorkbench.tickResult(serverWorld);

        etherWorkbench.markDirty();
    }

    public void tickResult(ServerWorld world) {
        ItemStack result = ItemStack.EMPTY;
        Optional<EtherRecipe> match = world.getRecipeManager().getFirstMatch(EtherRecipe.Type.INSTANCE, this, world);

        EtherRecipe recipe;
        if (match.isPresent()) {
            recipe = match.get();
            result = recipe.getOutput();
        }

        this.setStack(13, result);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    public DefaultedList<ItemStack> getDrop() {
        DefaultedList<ItemStack> copyItems = getItems();
        copyItems.set(13, ItemStack.EMPTY);
        return copyItems;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return ImplementedInventory.super.canPlayerUse(player);
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new EtherWorkbenchScreenHandler(syncId, inv, this, ScreenHandlerContext.create(world, pos));
    }

    @Override
    public Text getDisplayName() {
        return Text.empty();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        Inventories.readNbt(nbt, this.items);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, this.items);

        super.writeNbt(nbt);
    }

    @Override
    public void markDirty() {
        super.markDirty();

        if (world != null) world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
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
    public void setStack(int slot, ItemStack stack) {
        ImplementedInventory.super.setStack(slot, stack);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}
