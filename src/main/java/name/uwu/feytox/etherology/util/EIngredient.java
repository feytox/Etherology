package name.uwu.feytox.etherology.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;


public class EIngredient {
    List<ItemStack> matchingStacks;
    int lastStack = 0;

    public EIngredient(List<ItemStack> matchingStacks) {
        this.matchingStacks = matchingStacks;
    }

    public EIngredient(ItemStack itemStack) {
        this.matchingStacks = new ArrayList<>(List.of(itemStack));
    }

    public ItemStack getNextStack() {
        this.lastStack += 1;
        if (this.matchingStacks.size() == this.lastStack) {
            this.lastStack = 0;
        }
        return this.matchingStacks.get(this.lastStack);
    }

    public ItemStack current() {
        return this.matchingStacks.get(this.lastStack);
    }

    public static Ingredient fromId(Identifier id) {
        TagKey<Item> tagKey = TagKey.of(Registries.ITEM.getKey(), id);
        return Ingredient.fromTag(tagKey);
    }
}
