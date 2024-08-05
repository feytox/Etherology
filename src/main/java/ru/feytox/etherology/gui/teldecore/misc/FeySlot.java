package ru.feytox.etherology.gui.teldecore.misc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

@RequiredArgsConstructor @Getter
public abstract class FeySlot implements FeyIngredient {

    protected final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
    protected final float x;
    protected final float y;
    protected final float width;
    protected final float height;

    public abstract void render(DrawContext context, int mouseX, int mouseY, float delta);

    public boolean canBeFocused() {
        return true;
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseY >= y && mouseX <= x+width && mouseY <= y+height;
    }

    public static FeySlot of(ItemStack stack, float x, float y, float width, float height) {
        if (stack.isEmpty()) return new EmptySlot(x, y, width, height);
        return new Item(stack, x, y, width, height);
    }

    public static FeySlot of(Ingredient ingredient, float x, float y, float width, float height) {
        if (ingredient.isEmpty()) return new EmptySlot(x, y, width, height);
        return new IngredientSlot(ingredient, x, y, width, height);
    }

    public static FeySlot drawable(DrawableElement drawable, float x, float y, float width, float height) {
        return new Drawable(drawable, x, y, width, height);
    }

    private static class Item extends FeySlot {

        private final ItemStack stack;

        public Item(ItemStack stack, float x, float y, float width, float height) {
            super(x, y, width, height);
            this.stack = stack;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            context.push();
            context.translate(x, y, 0);
            context.scale(width / 16f, height / 16f, 1);
            context.drawItem(stack, 0, 0);
            context.drawItemInSlot(textRenderer, stack, 0, 0);
            context.pop();
        }

        @Override
        public Object getContent() {
            return stack;
        }
    }

    private static class IngredientSlot extends FeySlot {

        private final ItemStack[] stacks;

        public IngredientSlot(Ingredient ingredient, float x, float y, float width, float height) {
            super(x, y, width, height);
            this.stacks = ingredient.getMatchingStacks();
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            int i = (int) ((System.currentTimeMillis() / 1000L) % stacks.length);
            context.push();
            context.translate(x, y, 0);
            context.scale(width / 16f, height / 16f, 1);
            context.drawItem(stacks[i], 0, 0);
            context.drawItemInSlot(textRenderer, stacks[i], 0, 0);
            context.pop();
        }

        @Override
        public Object getContent() {
            int i = (int) ((System.currentTimeMillis() / 1000L) % stacks.length);
            return stacks[i];
        }
    }

    private static class EmptySlot extends FeySlot {

        public EmptySlot(float x, float y, float width, float height) {
            super(x, y, width, height);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {}

        @Override
        public Object getContent() {
            return null;
        }

        @Override
        public boolean canBeFocused() {
            return false;
        }
    }

    private static class Drawable extends FeySlot {

        private final DrawableElement drawable;

        public Drawable(DrawableElement drawable, float x, float y, float width, float height) {
            super(x, y, width, height);
            this.drawable = drawable;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            drawable.render(context, x, y, width, height);
        }

        @Override
        public Object getContent() {
            return null;
        }

        @Override
        public boolean canBeFocused() {
            return false;
        }
    }

    @FunctionalInterface
    public interface DrawableElement {

        void render(DrawContext context, float x, float y, float width, float height);
    }
}
