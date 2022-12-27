package name.uwu.feytox.lotyh.util;

import name.uwu.feytox.lotyh.client.LotyhClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;


public class LIngredient {
    List<ItemStack> matchingStacks;
    int lastStack = 0;

    public LIngredient(List<ItemStack> matchingStacks) {
        this.matchingStacks = matchingStacks;
    }

    public LIngredient(ItemStack itemStack) {
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
        TagKey<Item> tagKey = TagKey.of(Registry.ITEM_KEY, id);
        return Ingredient.fromTag(tagKey);
    }
}
