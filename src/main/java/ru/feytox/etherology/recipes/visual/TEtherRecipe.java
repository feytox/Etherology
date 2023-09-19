package ru.feytox.etherology.recipes.visual;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import ru.feytox.etherology.util.deprecated.SimpleItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TEtherRecipe extends TCraftRecipe {
    List<ItemStack> shards = new ArrayList<>();

    public TEtherRecipe(Map<Integer, Ingredient> ingredients, ItemStack result, int hShardNum, int tShardNum, int aShardNum, int dShardNum) {
        super(ingredients, result);
    }

    public List<ItemStack> getShards() {
        return shards;
    }

    public void addShard(int num, SimpleItem shardItem) {
        if (num > 0) {
            this.shards.add(shardItem.asStack(num));
        }
    }
}
