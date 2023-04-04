package ru.feytox.etherology.blocks.empowerTable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.recipes.ether.EtherRecipe;
import ru.feytox.etherology.util.feyapi.UpdatableInventory;

import java.util.Optional;

import static ru.feytox.etherology.BlocksRegistry.EMPOWERMENT_TABLE_BLOCK_ENTITY;

public class EmpowerTableBlockEntity extends BlockEntity implements
        UpdatableInventory, NamedScreenHandlerFactory {
    // 0-4 - items, 5 - rela, 6 - via, 7 - clos, 8 - keta, 9 - result
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(10, ItemStack.EMPTY);
    private boolean hasResult = false;

    public EmpowerTableBlockEntity(BlockPos pos, BlockState state) {
        super(EMPOWERMENT_TABLE_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void onTrackedSlotTake(PlayerEntity player, ItemStack stack, int index) {
        if (index == 9) craft();
    }

    @Override
    public void onTrackedUpdate(int index) {
        if (index != 9) updateResult();
    }

    public void craft() {
        EtherRecipe recipe = getRecipe();
        if (recipe == null) return;
        for (int i = 0; i < 5; i++) {
            removeStack(i, 1);
        }
        removeStack(5, recipe.relaCount());
        removeStack(6, recipe.viaCount());
        removeStack(7, recipe.closCount());
        removeStack(8, recipe.ketaCount());
        markDirty();

        updateResult();
    }

    public void updateResult() {
        EtherRecipe recipe = getRecipe();
        ItemStack outputStack = ItemStack.EMPTY;
        if (recipe != null) outputStack = recipe.getOutput();
        setStack(9, outputStack);
        markDirty();
    }

    @Nullable
    public EtherRecipe getRecipe() {
        if (world == null) return null;
        Optional<EtherRecipe> match = world.getRecipeManager().getFirstMatch(EtherRecipe.Type.INSTANCE, this, world);
        return match.orElse(null);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.etherology.empowerment_table.title");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new EmpowerTableScreenHandler(syncId, inv, this);
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
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, inventory);
        nbt.putBoolean("has_result", hasResult);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        Inventories.readNbt(nbt, inventory);
        hasResult = nbt.getBoolean("has_result");
    }
}
