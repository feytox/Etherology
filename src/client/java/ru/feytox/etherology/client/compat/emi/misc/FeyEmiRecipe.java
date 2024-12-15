package ru.feytox.etherology.client.compat.emi.misc;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.Identifier;

import java.util.List;

@Getter
@RequiredArgsConstructor
public abstract class FeyEmiRecipe implements EmiRecipe {

    protected final List<EmiIngredient> inputs;
    protected final List<EmiStack> outputs;
    protected final Identifier id;

    @Override
    public int getDisplayWidth() {
        return 118;
    }

    @Override
    public int getDisplayHeight() {
        return 54;
    }
}
