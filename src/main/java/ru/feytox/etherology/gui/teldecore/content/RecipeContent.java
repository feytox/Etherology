package ru.feytox.etherology.gui.teldecore.content;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.gui.teldecore.misc.ParentedWidget;
import ru.feytox.etherology.gui.teldecore.recipe.*;
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

public class RecipeContent extends AbstractContent {

    public static final MapCodec<RecipeContent> CODEC;
    @Nullable
    private static DisplayRegistry DISPLAYS = null;

    private final Identifier recipeId;
    private Recipe<?> cachedRecipe = null;

    private RecipeContent(Identifier recipeId, float offsetUp, float offsetDown, ContentBehaviour behaviour) {
        super(offsetUp, offsetDown, behaviour);
        this.recipeId = recipeId;
    }

    @Override
    public String getType() {
        return "recipe";
    }

    @Override
    public float getHeight(TextRenderer textRenderer) {
        return getOrCacheRecipe(recipeId)
                .flatMap(recipe -> Optional.ofNullable(DISPLAYS).flatMap(registry -> registry.getHeight(recipe)))
                .orElseThrow(() -> new NoSuchElementException("Failed to get recipe %s".formatted(recipeId.toString())));
    }

    @Override
    public ParentedWidget toWidget(TeldecoreScreen parent, float x, float y) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) throw new NullPointerException("Failed to get current client world.");

        Recipe<?> recipe = RecipesRegistry.maybeGet(client.world, recipeId).map(RecipeEntry::value)
                .orElseThrow(() -> new NoSuchElementException("Failed to get recipe %s".formatted(recipeId.toString())));

        AbstractRecipeDisplay<?> display = Optional.ofNullable(DISPLAYS).flatMap(registry -> registry.getDisplay(recipe))
                .orElseThrow(() -> new NoSuchElementException("Failed to get display filler for recipe %s".formatted(recipeId)));

        return display.toWidget(parent, x, y);
    }

    private Optional<Recipe<?>> getOrCacheRecipe(Identifier recipeId) {
        if (cachedRecipe != null) return Optional.of(cachedRecipe);

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return Optional.empty();

        Optional<Recipe<?>> recipe = RecipesRegistry.maybeGet(client.world, recipeId).map(RecipeEntry::value);
        recipe.ifPresent(value -> cachedRecipe = value);
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
        CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(c -> c.recipeId),
                codecOffsetUp(), codecOffsetDown(), codecBehaviour()
        ).apply(instance, RecipeContent::new));
        registerDisplays();
    }

    // TODO: 04.08.2024 consider replacing with something else
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
