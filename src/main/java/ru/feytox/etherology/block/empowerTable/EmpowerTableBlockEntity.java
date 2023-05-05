package ru.feytox.etherology.block.empowerTable;

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
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.recipes.empower.EmpowerRecipe;
import ru.feytox.etherology.util.feyapi.UpdatableInventory;

import java.util.Optional;

import static ru.feytox.etherology.registry.block.BlocksRegistry.EMPOWERMENT_TABLE_BLOCK_ENTITY;

public class EmpowerTableBlockEntity extends BlockEntity implements
        UpdatableInventory, NamedScreenHandlerFactory {
    // 0-4 - items, 5 - rela, 6 - via, 7 - clos, 8 - keta, 9 - result
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(10, ItemStack.EMPTY);
    private boolean hasResult = false;
    private int cachedRela = 0;
    private int cachedVia = 0;
    private int cachedClos = 0;
    private int cachedKeta = 0;
    private EmpowerRecipe currentRecipe = null;
    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0 -> {
                    return cachedRela;
                }
                case 1 -> {
                    return cachedVia;
                }
                case 2 -> {
                    return cachedClos;
                }
                case 3 -> {
                    return cachedKeta;
                }
            }

            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> cachedRela = value;
                case 1 -> cachedVia = value;
                case 2 -> cachedClos = value;
                case 3 -> cachedKeta = value;
            }
        }

        @Override
        public int size() {
            return 4;
        }
    };

    public EmpowerTableBlockEntity(BlockPos pos, BlockState state) {
        super(EMPOWERMENT_TABLE_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void onTrackedSlotTake(PlayerEntity player, ItemStack stack, int index) {
        if (index == 9) craft(true);
    }

    @Override
    public void onTrackedUpdate(int index) {
        if (index != 9) updateResult();
    }

    @Override
    public void onSpecialEvent(int eventId, ItemStack stack) {
        if (eventId == 0) craftAll(stack);
    }

    @Override
    public void onOpen(PlayerEntity player) {
        updateResult();
    }

    public void craftAll(ItemStack resultStack) {
        craft(true);
        while (canCraft() && resultStack.getCount() + currentRecipe.getOutput().getCount() < resultStack.getMaxCount()) {
            if (craft(false)) resultStack.increment(currentRecipe.getOutput().getCount());
        }
        updateResult();
    }

    public boolean craft(boolean shouldUpdate) {
        EmpowerRecipe recipe = getRecipe();
        if (recipe == null || !recipe.checkShards(this)) return false;
        for (int i = 0; i < 5; i++) {
            removeStack(i, 1);
        }
        removeStack(5, recipe.relaCount());
        removeStack(6, recipe.viaCount());
        removeStack(7, recipe.closCount());
        removeStack(8, recipe.ketaCount());
        markDirty();

        if (shouldUpdate) updateResult();
        return true;
    }

    public boolean canCraft() {
        currentRecipe = getRecipe();
        return currentRecipe != null && currentRecipe.checkShards(this);
    }

    public void updateResult() {
        ItemStack outputStack = ItemStack.EMPTY;
        if (canCraft()) outputStack = currentRecipe.getOutput();
        cacheShards(currentRecipe);
        setStack(9, outputStack);
        markDirty();
    }

    public void cacheShards(EmpowerRecipe recipe) {
        boolean isNull = recipe == null;
        cachedRela = isNull ? 0 : recipe.relaCount();
        cachedVia = isNull ? 0 : recipe.viaCount();
        cachedClos = isNull ? 0 : recipe.closCount();
        cachedKeta = isNull ? 0 : recipe.ketaCount();
    }

    @Nullable
    public EmpowerRecipe getRecipe() {
        if (world == null) return null;
        Optional<EmpowerRecipe> match = world.getRecipeManager().getFirstMatch(EmpowerRecipe.Type.INSTANCE, this, world);
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
        return new EmpowerTableScreenHandler(syncId, inv, this, propertyDelegate);
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
        nbt.putInt("cached_rela", cachedRela);
        nbt.putInt("cached_via", cachedVia);
        nbt.putInt("cached_clos", cachedClos);
        nbt.putInt("cached_keta", cachedKeta);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        Inventories.readNbt(nbt, inventory);
        hasResult = nbt.getBoolean("has_result");
        cachedRela = nbt.getInt("cached_rela");
        cachedVia = nbt.getInt("cached_via");
        cachedClos = nbt.getInt("cached_clos");
        cachedKeta = nbt.getInt("cached_keta");
    }
}
