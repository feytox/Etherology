package ru.feytox.etherology.gui.teldecore.content;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Getter
public class RecipeContent extends AbstractContent {

    public static final MapCodec<RecipeContent> CODEC;

    private final Identifier recipeId;
    @Nullable @Setter
    private Recipe<?> cachedRecipe = null;

    private RecipeContent(Identifier recipeId, float offsetUp, float offsetDown, ContentBehaviour behaviour) {
        super(offsetUp, offsetDown, behaviour);
        this.recipeId = recipeId;
    }

    @Override
    public String getType() {
        return "recipe";
    }

    static {
        CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(c -> c.recipeId),
                codecOffsetUp(), codecOffsetDown(), codecBehaviour()
        ).apply(instance, RecipeContent::new));
    }
}
