package ru.feytox.etherology.compat.emi.recipe;

import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.compat.emi.EtherEMIPlugin;
import ru.feytox.etherology.compat.emi.misc.AspectStack;
import ru.feytox.etherology.compat.emi.misc.FeyEmiRecipe;
import ru.feytox.etherology.data.aspects.AspectsLoader;
import ru.feytox.etherology.magic.aspects.AspectContainerId;

import java.util.Collections;
import java.util.List;

public class AspectionERecipe extends FeyEmiRecipe {

    public AspectionERecipe(List<EmiIngredient> inputs, List<EmiStack> outputs, Identifier id) {
        super(inputs, outputs, id);
    }

    public static void registerRecipes(EmiRegistry registry) {
        AspectsLoader.forEach((id, container) -> {
            EmiStack holder = getHolder(id);
            if (holder == null) return;
            List<EmiIngredient> input = Collections.singletonList(holder);
            List<EmiStack> outputs = new ObjectArrayList<>();
            container.getAspects().entrySet().stream().map(entry -> new AspectStack(entry.getKey(), entry.getValue())).forEach(outputs::add);
            registry.addRecipe(new AspectionERecipe(input, outputs, id.toTypedId()));
        });
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EtherEMIPlugin.ASPECTION;
    }

    @Override
    public int getDisplayWidth() {
        return Math.max(128, 128 + (outputs.size() - 4) * 18);
    }

    @Override
    public int getDisplayHeight() {
        return 38;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 26, 15);
        widgets.addSlot(inputs.getFirst(), 1, 14);

        for (int i = 0; i < outputs.size(); i++) {
            widgets.addSlot(outputs.get(i), 56 + i*18, 14).recipeContext(this);
        }
    }

    @Nullable
    private static EmiStack getHolder(AspectContainerId id) {
        return switch (id.getContainerType()) {
            case ITEM -> EmiStack.of(Registries.ITEM.get(id.getId()));
            case POTION -> getPotion(Items.POTION, id);
            case SPLASH_POTION -> getPotion(Items.SPLASH_POTION, id);
            case LINGERING_POTION -> getPotion(Items.LINGERING_POTION, id);
            case TIPPED_ARROW -> getPotion(Items.TIPPED_ARROW, id);
            case ENTITY -> null;
        };
    }

    private static EmiStack getPotion(Item item, AspectContainerId id) {
        return Registries.POTION.getEntry(id.getId()).map(potion -> PotionContentsComponent.createStack(item, potion))
                .map(EmiStack::of).orElse(null);
    }
}
