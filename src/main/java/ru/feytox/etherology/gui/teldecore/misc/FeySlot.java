package ru.feytox.etherology.gui.teldecore.misc;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Text;
import ru.feytox.etherology.magic.aspects.Aspect;
import ru.feytox.etherology.util.misc.CountedAspect;

@RequiredArgsConstructor @Getter
public abstract class FeySlot implements FeyIngredient {

    protected final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
    protected final float x;
    protected final float y;
    protected final float width;
    protected final float height;

    public abstract void render(DrawContext context, int mouseX, int mouseY, float delta);

    public abstract void renderTooltip(DrawContext context, int mouseX, int mouseY);

    public boolean canBeFocused() {
        return true;
    }

    public boolean hasTooltip() {
        return true;
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseY >= y && mouseX <= x+width && mouseY <= y+height;
    }

    public static FeySlot of(ItemStack stack, float x, float y, float width, float height) {
        if (stack.isEmpty()) return new EmptySlot(x, y, width, height);
        return new Item(stack, x, y, width, height);
    }

    public static FeySlot of(ItemStack stack, float x, float y) {
        return of(stack, x, y, 16, 16);
    }

    public static FeySlot of(Ingredient ingredient, float x, float y, float width, float height) {
        if (ingredient.isEmpty()) return new EmptySlot(x, y, width, height);
        return new IngredientSlot(ingredient, x, y, width, height);
    }

    public static FeySlot of(Ingredient ingredient, float x, float y) {
        return of(ingredient, x, y, 16, 16);
    }

    public static FeySlot of(Aspect aspect, Integer count, float x, float y) {
        return of(aspect, count, x, y, 16, 16);
    }

    public static FeySlot of(Aspect aspect, int count, float x, float y, float width, float height) {
        return new AspectSlot(aspect, count, x, y, width, height);
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
        public void renderTooltip(DrawContext context, int mouseX, int mouseY) {
            context.drawItemTooltip(textRenderer, stack, mouseX, mouseY);
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

        private int getItemIndex() {
            return (int) ((System.currentTimeMillis() / 1000L) % stacks.length);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            int i = getItemIndex();
            context.push();
            context.translate(x, y, 0);
            context.scale(width / 16f, height / 16f, 1);
            context.drawItem(stacks[i], 0, 0);
            context.drawItemInSlot(textRenderer, stacks[i], 0, 0);
            context.pop();
        }

        @Override
        public void renderTooltip(DrawContext context, int mouseX, int mouseY) {
            int i = getItemIndex();
            context.drawItemTooltip(textRenderer, stacks[i], mouseX, mouseY);
        }

        @Override
        public Object getContent() {
            int i = getItemIndex();
            return stacks[i];
        }
    }

    private static class AspectSlot extends FeySlot {

        private final CountedAspect aspectPair;

        public AspectSlot(Aspect aspect, int count, float x, float y, float width, float height) {
            super(x, y, width, height);
            this.aspectPair = new CountedAspect(aspect, count);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            Aspect aspect = aspectPair.aspect();

            context.push();
            context.translate(x, y, 0);
            context.scale(width / aspect.getWidth(), height / aspect.getHeight(), 1);
            RenderSystem.enableBlend();
            context.drawTexture(Aspect.TEXTURE, 0, 0, aspect.getTextureMinX(), aspect.getTextureMinY(), aspect.getWidth(), aspect.getHeight(), Aspect.TEXTURE_WIDTH, Aspect.TEXTURE_HEIGHT);
            RenderSystem.disableBlend();
            context.pop();

            String count = Integer.toString(aspectPair.count());

            context.push();
            context.translate(x, y, 0);
            context.scale(width / 16f, height / 16f, 1);
            textRenderer.draw(count, 17 - textRenderer.getWidth(count), 9, 0xFFFFFF, true, context.getMatrices().peek().getPositionMatrix(), context.getVertexConsumers(), TextRenderer.TextLayerType.NORMAL, 0, 15728880);
            context.pop();
        }

        @Override
        public void renderTooltip(DrawContext context, int mouseX, int mouseY) {
            context.drawTooltip(textRenderer, Text.of(aspectPair.aspect().getDisplayName()), mouseX, mouseY);
        }

        @Override
        public Object getContent() {
            return aspectPair;
        }
    }

    private static class EmptySlot extends FeySlot {

        public EmptySlot(float x, float y, float width, float height) {
            super(x, y, width, height);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {}

        @Override
        public void renderTooltip(DrawContext context, int mouseX, int mouseY) {}

        @Override
        public Object getContent() {
            return null;
        }

        @Override
        public boolean canBeFocused() {
            return false;
        }

        @Override
        public boolean hasTooltip() {
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
        public void renderTooltip(DrawContext context, int mouseX, int mouseY) {}

        @Override
        public Object getContent() {
            return null;
        }

        @Override
        public boolean canBeFocused() {
            return false;
        }

        @Override
        public boolean hasTooltip() {
            return false;
        }
    }

    @FunctionalInterface
    public interface DrawableElement {

        void render(DrawContext context, float x, float y, float width, float height);
    }
}
