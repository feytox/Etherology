package ru.feytox.etherology.recipes.empower;

import it.unimi.dsi.fastutil.chars.Char2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.Etherology;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor(staticName = "create")
public class EmpowerRecipeBuilder implements CraftingRecipeJsonBuilder {

    private final Map<Character, Ingredient> inputs = new Char2ObjectLinkedOpenHashMap<>();
    private final List<String> pattern = new ObjectArrayList<>();
    private int rellaCount;
    private int viaCount;
    private int closCount;
    private int ketaCount;
    private boolean empty = false;
    private final ItemStack outputStack;

    public static EmpowerRecipeBuilder create(@NonNull ItemConvertible output) {
        return create(output, 1);
    }

    public static EmpowerRecipeBuilder create(@NonNull ItemConvertible output, int outputCount) {
        return create(new ItemStack(output, outputCount));
    }

    public EmpowerRecipeBuilder empty() {
        empty = true;
        return this;
    }

    public EmpowerRecipeBuilder rella(int value) {
        rellaCount = value;
        return this;
    }

    public EmpowerRecipeBuilder via(int value) {
        viaCount = value;
        return this;
    }

    public EmpowerRecipeBuilder clos(int value) {
        closCount = value;
        return this;
    }

    public EmpowerRecipeBuilder keta(int value) {
        ketaCount = value;
        return this;
    }

    public EmpowerRecipeBuilder input(Character c, ItemConvertible item) {
        inputs.put(c, Ingredient.ofItems(item));
        return this;
    }

    public EmpowerRecipeBuilder input(Character c, TagKey<Item> tag) {
        inputs.put(c, Ingredient.fromTag(tag));
        return this;
    }

    public EmpowerRecipeBuilder pattern(String patternStr) {
        if (patternStr.length() != 3) throw new IllegalArgumentException("Empowerment Pattern must have exactly 3x3 size");
        pattern.add(patternStr);
        return this;
    }

    @Override
    public CraftingRecipeJsonBuilder criterion(String name, AdvancementCriterion<?> criterion) {
        Etherology.ELOGGER.warn("Criterion is not yet supported by Empowerment recipe type.");
        return null;
    }

    @Override
    public CraftingRecipeJsonBuilder group(@Nullable String group) {
        Etherology.ELOGGER.warn("Group is not yet supported by Empowerment recipe type.");
        return null;
    }

    @Override
    public Item getOutputItem() {
        return outputStack.getItem();
    }

    @Override
    public void offerTo(RecipeExporter exporter, Identifier recipeId) {
        if (!empty && rellaCount == 0 && viaCount == 0 && closCount == 0 && ketaCount == 0) Etherology.ELOGGER.warn("{} recipe does not have any primoshard requirements", recipeId);
        if (this.pattern.size() != 3) throw new IllegalArgumentException("Empowerment Pattern must have exactly 3x3 size");
        EmpowerRecipe.Pattern pattern = EmpowerRecipe.Pattern.create(inputs, this.pattern);
        EmpowerRecipe recipe = new EmpowerRecipe(pattern, rellaCount, viaCount, closCount, ketaCount, outputStack);
        exporter.accept(recipeId, recipe, null);
    }
}
