package ru.feytox.etherology.client.compat.emi.misc;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.serializer.EmiStackSerializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.component.ComponentChanges;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;
import ru.feytox.etherology.client.util.RenderUtils;
import ru.feytox.etherology.magic.aspects.Aspect;
import ru.feytox.etherology.magic.aspects.EtherologyAspect;

import java.util.List;

public class AspectStack extends EmiStack {

    private final Aspect aspect;
    
    public AspectStack(Aspect aspect, long amount) {
        this.aspect = aspect;
        this.amount = amount;
    }

    @Override
    public long getAmount() {
        return super.getAmount();
    }

    @Override
    public EmiStack copy() {
        return new AspectStack(aspect, amount);
    }

    @Override
    public void render(DrawContext context, int x, int y, float delta, int flags) {
        RenderSystem.setShaderTexture(0, Aspect.TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        if ((flags & RENDER_ICON) != 0) {
            context.push();
            context.translate(x, y, 0);
            context.scale(16f / aspect.getWidth(), 16f / aspect.getHeight(), 1.0f);
            RenderUtils.renderTexture(context, 0, 0, aspect.getTextureMinX(), aspect.getTextureMinY(), aspect.getWidth(), aspect.getHeight(), aspect.getWidth(), aspect.getHeight(), EtherologyAspect.TEXTURE_WIDTH, EtherologyAspect.TEXTURE_HEIGHT);
            context.pop();
        }
        if ((flags & RENDER_AMOUNT) != 0) {
            RenderSystem.enableDepthTest();
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            if (amount == 1) return;
            String count = Long.toString(amount);

            context.push();
            context.translate(x, y, 0);

            textRenderer.draw(count, 17 - textRenderer.getWidth(count), 9, 0xFFFFFF, true, context.getMatrices().peek().getPositionMatrix(), context.getVertexConsumers(), TextRenderer.TextLayerType.NORMAL, 0, 15728880);
            context.pop();
        }
    }

    @Override
    public boolean isEmpty() {
        return amount == 0;
    }

    @Override
    public ComponentChanges getComponentChanges() {
        return ComponentChanges.EMPTY;
    }

    @Override
    public Object getKey() {
        return aspect;
    }

    @Override
    public Identifier getId() {
        return aspect.getId();
    }

    @Override
    public List<TooltipComponent> getTooltip() {
        return List.of(
                TooltipComponent.of(getName().asOrderedText()),
                TooltipComponent.of(Text.literal(StringUtils.capitalize(aspect.getId().getNamespace())).formatted(Formatting.BLUE, Formatting.ITALIC).asOrderedText())
        );
    }

    @Override
    public List<Text> getTooltipText() {
        return List.of();
    }

    @Override
    public Text getName() {
        return Text.of(aspect.getDisplayName());
    }

    public static class Serializer implements EmiStackSerializer<AspectStack> {

        @Override
        public EmiStack create(Identifier id, ComponentChanges componentChanges, long amount) {
            return new AspectStack(Aspect.get(id), amount);
        }

        @Override
        public String getType() {
            return "etherology_aspect";
        }
    }
}
