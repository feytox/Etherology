package ru.feytox.etherology.block.inventorTable;

import io.wispforest.owo.util.ImplementedInventory;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.item.EtherStaff;
import ru.feytox.etherology.item.StaffPatternItem;
import ru.feytox.etherology.magic.staff.StaffMetals;
import ru.feytox.etherology.magic.staff.StaffPart;
import ru.feytox.etherology.magic.staff.StaffStyles;
import ru.feytox.etherology.registry.item.ToolItems;
import ru.feytox.etherology.util.feyapi.TickableBlockEntity;
import ru.feytox.etherology.util.feyapi.UpdatableInventory;

import static ru.feytox.etherology.registry.block.EBlocks.INVENTOR_TABLE_BLOCK_ENTITY;

public class InventorTableBlockEntity extends TickableBlockEntity implements ImplementedInventory, NamedScreenHandlerFactory, UpdatableInventory {

    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(4, ItemStack.EMPTY);

    @Nullable
    @Getter
    @Setter
    private StaffPart selectedPart = null;

    public InventorTableBlockEntity(BlockPos pos, BlockState state) {
        super(INVENTOR_TABLE_BLOCK_ENTITY, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, items);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        items.clear();
        Inventories.readNbt(nbt, items);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new InventorTableScreenHandler(syncId, inv, this);
    }

    @Override
    public void onTrackedSlotTake(PlayerEntity player, ItemStack stack, int index) {
        if (index != 3) return;
        // take output staff
        setStack(0, ItemStack.EMPTY);

        if (!(world instanceof ServerWorld serverWorld)) return;
        syncData(serverWorld);
    }

    @Override
    public void onTrackedUpdate(int index) {
        if (index == 3) return;

        updateResult();
        if (!(world instanceof ServerWorld serverWorld)) return;
        syncData(serverWorld);
    }

    public void updateResult() {
        ItemStack inputStaff = getStack(0);
        ItemStack patternItem = getStack(1);
        ItemStack itemForPattern = getStack(2);
        StaffMetals patternMetal = StaffMetals.getFromStack(itemForPattern);

        if (selectedPart == null || patternMetal == null || !inputStaff.isOf(ToolItems.ETHER_STAFF) || !(patternItem.getItem() instanceof StaffPatternItem pattern)) {
            setStack(3, ItemStack.EMPTY);
            return;
        }

        if (!selectedPart.isStyled()) {
            Etherology.ELOGGER.error("Selected part has incompatible first StaffPattern");
            setStack(3, ItemStack.EMPTY);
            return;
        }

        StaffStyles patternStyle = pattern.getStaffStyle();
        ItemStack result = inputStaff.copy();
        EtherStaff.setPartInfo(result, selectedPart, patternStyle, patternMetal);
        setStack(3, result);
    }

    @Override
    public void onSpecialEvent(int eventId, ItemStack stack) {

    }
}
