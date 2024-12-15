package ru.feytox.etherology.client.gui.teldecore.widget;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.experimental.UtilityClass;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.client.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.client.gui.teldecore.recipe.*;
import ru.feytox.etherology.gui.teldecore.content.RecipeContent;
import ru.feytox.etherology.recipes.FeyRecipeSerializer;
import ru.feytox.etherology.recipes.alchemy.AlchemyRecipeSerializer;
import ru.feytox.etherology.recipes.empower.EmpowerRecipeSerializer;
import ru.feytox.etherology.recipes.jewelry.LensRecipeSerializer;
import ru.feytox.etherology.recipes.jewelry.ModifierRecipeSerializer;
import ru.feytox.etherology.recipes.matrix.MatrixRecipeSerializer;
import ru.feytox.etherology.registry.misc.RecipesRegistry;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@UtilityClass
public class RecipeWidget {

    @Nullable
    private static DisplayRegistry DISPLAYS = null;

    public static float getHeight(RecipeContent content, TextRenderer textRenderer) {
        var recipeId = content.getRecipeId();
        return getOrCacheRecipe(content, recipeId)
                .flatMap(recipe -> Optional.ofNullable(DISPLAYS)
                        .flatMap(registry -> registry.getHeight(recipe)))
                .orElseThrow(() -> new NoSuchElementException("Failed to get recipe %s".formatted(recipeId.toString())));
    }

    public static ParentedWidget toWidget(RecipeContent content, TeldecoreScreen parent, float x, float y) {
        var recipeId = content.getRecipeId();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) throw new NullPointerException("Failed to get current client world.");

        Recipe<?> recipe = RecipesRegistry.maybeGet(client.world, recipeId).map(RecipeEntry::value)
                .orElseThrow(() -> new NoSuchElementException("Failed to get recipe %s".formatted(recipeId.toString())));

        AbstractRecipeDisplay<?> display = Optional.ofNullable(DISPLAYS).flatMap(registry -> registry.getDisplay(recipe))
                .orElseThrow(() -> new NoSuchElementException("Failed to get display filler for recipe %s".formatted(recipeId)));

        return display.toWidget(parent, x, y);
    }

    private static Optional<Recipe<?>> getOrCacheRecipe(RecipeContent content, Identifier recipeId) {
        var cachedRecipe = content.getCachedRecipe();
        if (cachedRecipe != null) return Optional.of(cachedRecipe);

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return Optional.empty();

        Optional<Recipe<?>> recipe = RecipesRegistry.maybeGet(client.world, recipeId).map(RecipeEntry::value);
        recipe.ifPresent(content::setCachedRecipe);
        return recipe;
    }

    private static void registerDisplays() {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) return;
        DISPLAYS = new DisplayRegistry();
        DISPLAYS.add(RecipeType.CRAFTING, CraftingRecipeDisplay::new, 123, 72);
        DISPLAYS.add(EmpowerRecipeSerializer.INSTANCE, EmpowerRecipeDisplay::new, 123, 72);
        DISPLAYS.add(RecipeType.SMELTING, SmeltingRecipeDisplay::new, 123, 76);
        DISPLAYS.add(AlchemyRecipeSerializer.INSTANCE, AlchemyRecipeDisplay::new, 123, 72);
        DISPLAYS.add(MatrixRecipeSerializer.INSTANCE, MatrixRecipeDisplay::new, 123, 72);
        DISPLAYS.add(LensRecipeSerializer.INSTANCE, JewelryRecipeDisplay.Lens::new, 123, 72);
        DISPLAYS.add(ModifierRecipeSerializer.INSTANCE, JewelryRecipeDisplay.Modifier::new, 123, 72);
    }

    static {
        registerDisplays();
    }

    // TODO: 04.08.2024 replace with something else
    private static class DisplayRegistry {
        private final Map<RecipeType<?>, Pair<DisplaySize, DisplayFiller<?>>> typeToDisplay = new Object2ObjectOpenHashMap<>();

        <V extends Recipe<?>> void add(RecipeType<V> type, DisplayFiller<V> displayFunction, int width, int height) {
            typeToDisplay.put(type, Pair.of(new DisplaySize(width, height), displayFunction));
        }

        <V extends Recipe<?>> void add(FeyRecipeSerializer<V> feySerializer, DisplayFiller<V> displayFunction, int width, int height) {
            add(feySerializer.getRecipeType(), displayFunction, width, height);
        }

        Optional<Pair<DisplaySize, DisplayFiller<?>>> get(Recipe<?> recipe) {
            return Optional.ofNullable(typeToDisplay.get(recipe.getType()));
        }

        Optional<AbstractRecipeDisplay<?>> getDisplay(Recipe<?> recipe) {
            return get(recipe).map(pair -> pair.second().toDisplay(recipe, pair.first()));
        }

        Optional<Integer> getHeight(Recipe<?> recipe) {
            return get(recipe).map(pair -> pair.first().height);
        }
    }

    @FunctionalInterface
    private interface DisplayFiller<T extends Recipe<?>> {

        @SuppressWarnings("unchecked")
        default AbstractRecipeDisplay<T> toDisplay(Recipe<?> recipe, DisplaySize size) {
            return apply((T) recipe, size.width, size.height);
        }

        AbstractRecipeDisplay<T> apply(T recipe, int width, int height);
    }

    private record DisplaySize(int width, int height) {}
}
