package name.uwu.feytox.lotyh.util;

import io.wispforest.owo.util.ImplementedInventory;
import name.uwu.feytox.lotyh.recipes.ether.EtherRecipe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.slot.Slot;

public class OutputSlot extends Slot {
    // TODO: make more aboba
    private EtherRecipe recipe = null;

    public OutputSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    public void setRecipe(EtherRecipe recipe) {
        this.recipe = recipe;
    }

    public Recipe<?> getRecipe() {
        return recipe;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }

    @Override
    public void onTakeItem(PlayerEntity player, ItemStack stack) {
        if (recipe == null || this.inventory instanceof SimpleInventory) return;
        recipe.craft((ImplementedInventory) this.inventory);
        recipe = null;
    }
}
