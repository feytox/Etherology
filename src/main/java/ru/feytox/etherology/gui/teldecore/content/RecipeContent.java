package ru.feytox.etherology.gui.teldecore.content;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.gui.teldecore.misc.ParentedWidget;
import ru.feytox.etherology.gui.teldecore.recipe.AbstractRecipeDisplay;
import ru.feytox.etherology.gui.teldecore.recipe.CraftingRecipeDisplay;
import ru.feytox.etherology.recipes.FeyRecipeSerializer;
import ru.feytox.etherology.registry.misc.RecipesRegistry;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class RecipeContent extends AbstractContent {

    public static final MapCodec<RecipeContent> CODEC;
    private static final DisplayRegistry DISPLAYS;

    private final Identifier recipeId;

    private RecipeContent(Identifier recipeId, float offsetUp, float offsetDown) {
        super(offsetUp, offsetDown);
        this.recipeId = recipeId;
    }

    @Override
    public String getType() {
        return "recipe";
    }

    @Override
    public float getHeight(TextRenderer textRenderer) {
        // TODO: 04.08.2024 change
        return 72;
    }

    @Override
    public ParentedWidget toWidget(TeldecoreScreen parent, float x, float y) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) throw new NullPointerException("Failed to get current client world.");

        Recipe<?> recipe = RecipesRegistry.maybeGet(client.world, recipeId).map(RecipeEntry::value)
                .orElseThrow(() -> new NoSuchElementException("Failed to get recipe %s".formatted(recipeId.toString())));

        AbstractRecipeDisplay<?> display = DISPLAYS.get(recipe).orElseThrow(() -> new NoSuchElementException("Failed to get display filler for recipe %s".formatted(recipeId)));

        return display.toWidget(parent, x, y);
    }

    static {
        CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(c -> c.recipeId),
                codecOffsetUp(), codecOffsetDown()
        ).apply(instance, RecipeContent::new));
        DISPLAYS = new DisplayRegistry();

        DISPLAYS.add(RecipeType.CRAFTING, CraftingRecipeDisplay::new);
    }

    // TODO: 04.08.2024 consider replacing with something else
    private static class DisplayRegistry {
        private final Map<RecipeType<?>, DisplayFiller<?>> typeToDisplay = new Object2ObjectOpenHashMap<>();

        public <V extends Recipe<?>> DisplayRegistry add(RecipeType<V> type, DisplayFiller<V> displayFunction) {
            typeToDisplay.put(type, displayFunction);
            return this;
        }

        public <V extends Recipe<?>> DisplayRegistry add(FeyRecipeSerializer<V> feySerializer, DisplayFiller<V> displayFunction) {
            return add(feySerializer.getRecipeType(), displayFunction);
        }

        public Optional<AbstractRecipeDisplay<?>> get(Recipe<?> recipe) {
            return Optional.ofNullable(typeToDisplay.get(recipe.getType())).map(filler -> filler.toDisplay(recipe));
        }
    }

    @FunctionalInterface
    private interface DisplayFiller<T extends Recipe<?>> {

        @SuppressWarnings("unchecked")
        default AbstractRecipeDisplay<T> toDisplay(Recipe<?> recipe) {
            return apply((T) recipe);
        }

        AbstractRecipeDisplay<T> apply(T recipe);
    }
}
