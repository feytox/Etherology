package ru.feytox.etherology.compat.emi.misc;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.item.ItemConvertible;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.util.misc.EIdentifier;

public class FeyEmiCategory extends EmiRecipeCategory {

    private final String translationKey;

    private FeyEmiCategory(Identifier id, EmiIngredient icon, String translationKey) {
        super(id, icon);
        this.translationKey = translationKey;
    }

    public static FeyEmiCategory of(String id, ItemConvertible item) {
        return new FeyEmiCategory(EIdentifier.of(id), EmiStack.of(item), item.asItem().getTranslationKey());
    }

    public static FeyEmiCategory of(String id, ItemConvertible item, String translationKey) {
        return new FeyEmiCategory(EIdentifier.of(id), EmiStack.of(item), translationKey);
    }

    public EmiIngredient getIcon() {
        return (EmiIngredient) this.icon;
    }

    @Override
    public Text getName() {
        return Text.translatable(translationKey);
    }
}
