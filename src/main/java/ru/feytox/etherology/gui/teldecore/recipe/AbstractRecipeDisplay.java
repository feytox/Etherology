package ru.feytox.etherology.gui.teldecore.recipe;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.gui.teldecore.TeldecoreScreen;
import ru.feytox.etherology.gui.teldecore.misc.FeyIngredient;
import ru.feytox.etherology.gui.teldecore.misc.FeySlot;
import ru.feytox.etherology.gui.teldecore.misc.FocusedIngredientProvider;
import ru.feytox.etherology.gui.teldecore.misc.ParentedWidget;
import ru.feytox.etherology.util.misc.RenderUtils;

import java.util.List;

@RequiredArgsConstructor
public abstract class AbstractRecipeDisplay<T extends Recipe<?>> {

    protected final T recipe;
    private final Identifier texture;

    public abstract List<FeySlot> toSlots(float x, float y);

    public ParentedWidget toWidget(TeldecoreScreen parent, float x, float y) {
        return new Widget(parent, toSlots(x, y), texture, x, y);
    }

    private static class Widget extends ParentedWidget implements FocusedIngredientProvider {

        private final List<FeySlot> slots;
        private final Identifier texture;

        public Widget(TeldecoreScreen parent, List<FeySlot> slots, Identifier texture, float baseX, float baseY) {
            super(parent, baseX, baseY);
            this.slots = slots;
            this.texture = texture;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            RenderSystem.setShaderTexture(0, texture);
            // TODO: 04.08.2024 replace width and height
            RenderUtils.renderTexture(context, baseX, baseY, 0, 0, 123, 72, 123, 72);

            slots.forEach(slot -> slot.render(context, mouseX, mouseY, delta));
        }

        @Override
        public SelectionType getType() {
            return SelectionType.NONE;
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder) {}

        @Override @Nullable
        public FeyIngredient getFocusedIngredient(int mouseX, int mouseY) {
            return slots.stream().filter(FeySlot::canBeFocused)
                    .filter(slot -> slot.isMouseOver(mouseX, mouseY)).findAny().orElse(null);
        }
    }
}
