package ru.feytox.etherology.compat.rei.misc;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.entry.renderer.BatchedEntryRenderer;
import me.shedaniel.rei.api.client.entry.renderer.EntryRenderer;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.client.gui.widgets.TooltipContext;
import me.shedaniel.rei.api.common.entry.EntrySerializer;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.comparison.ComparisonContext;
import me.shedaniel.rei.api.common.entry.type.EntryDefinition;
import me.shedaniel.rei.api.common.entry.type.EntryType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.compat.rei.EtherREIPlugin;
import ru.feytox.etherology.magic.aspects.Aspect;
import ru.feytox.etherology.magic.aspects.EtherologyAspect;
import ru.feytox.etherology.util.misc.RenderUtils;

import java.util.stream.Stream;

public class AspectEntryDefinition implements EntryDefinition<AspectPair> {

    private final EntryRenderer<AspectPair> renderer = new AspectEntryRenderer();

    @Override
    public Class<AspectPair> getValueType() {
        return AspectPair.class;
    }

    @Override
    public EntryType<AspectPair> getType() {
        return EtherREIPlugin.ASPECT_ENTRY;
    }

    @Override
    public EntryRenderer<AspectPair> getRenderer() {
        return renderer;
    }

    @Override
    public Identifier getIdentifier(EntryStack<AspectPair> entry, AspectPair value) {
        return value.aspect().getId();
    }

    @Override
    public boolean isEmpty(EntryStack<AspectPair> entry, AspectPair value) {
        return value.value() == 0;
    }

    @Override
    public AspectPair copy(EntryStack<AspectPair> entry, AspectPair value) {
        return new AspectPair(value.aspect(), value.value());
    }

    @Override
    public AspectPair normalize(EntryStack<AspectPair> entry, AspectPair value) {
        return value.normalize();
    }

    @Override
    public AspectPair wildcard(EntryStack<AspectPair> entry, AspectPair value) {
        return value.normalize();
    }

    @Override
    public AspectPair add(AspectPair o1, AspectPair o2) {
        return new AspectPair(o1.aspect(), o1.value() + o2.value());
    }

    @Override
    public long hash(EntryStack<AspectPair> entry, AspectPair value, ComparisonContext context) {
        return switch (context) {
            case FUZZY -> value.aspectHash();
            case EXACT -> value.hashCode();
        };
    }

    @Override
    public boolean equals(AspectPair o1, AspectPair o2, ComparisonContext context) {
        return switch (context) {
            case FUZZY -> o1.aspectEquals(o2);
            case EXACT -> o1.equals(o2);
        };
    }

    @Override @Nullable
    public EntrySerializer<AspectPair> getSerializer() {
        return null;
    }

    @Override
    public Text asFormattedText(EntryStack<AspectPair> entry, AspectPair value) {
        return Text.of(StringUtils.capitalize(value.aspect().getAspectName()) + " " + value.value() + "x");
    }

    @Override
    public Stream<? extends TagKey<?>> getTagsFor(EntryStack<AspectPair> entry, AspectPair value) {
        return Stream.empty();
    }

    public static class AspectEntryRenderer implements BatchedEntryRenderer<AspectPair, Void> {

        @Override
        public Void getExtraData(EntryStack<AspectPair> entry) {
            // TODO: 26.07.2024 replace with sprite getter after rewriting aspects
            return null;
        }

        @Override
        public int getBatchIdentifier(EntryStack<AspectPair> entry, Rectangle bounds, Void extraData) {
            return 42524252;
        }

        @Override
        public void startBatch(EntryStack<AspectPair> entry, Void extraData, DrawContext graphics, float delta) {
            RenderSystem.setShaderTexture(0, Aspect.TEXTURE);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
        }

        @Override
        public void renderBase(EntryStack<AspectPair> entry, Void extraData, DrawContext graphics, VertexConsumerProvider.Immediate immediate, Rectangle bounds, int mouseX, int mouseY, float delta) {
            if (entry.isEmpty()) return;
            Aspect aspect = entry.getValue().aspect();

            graphics.push();
            graphics.translate(bounds.x, bounds.y, 0);
            graphics.scale((float) bounds.width / aspect.getWidth(), (float) bounds.height / aspect.getHeight(), 1.0f);

            RenderUtils.renderTexture(graphics, 0, 0, aspect.getTextureMinX(), aspect.getTextureMinY(), aspect.getWidth(), aspect.getHeight(), aspect.getWidth(), aspect.getHeight(), EtherologyAspect.TEXTURE_WIDTH, EtherologyAspect.TEXTURE_HEIGHT);
            graphics.pop();
        }

        @Override
        public void afterBase(EntryStack<AspectPair> entry, Void extraData, DrawContext graphics, float delta) {
            RenderSystem.enableDepthTest();
        }

        @Override
        public void renderOverlay(EntryStack<AspectPair> entry, Void extraData, DrawContext graphics, VertexConsumerProvider.Immediate immediate, Rectangle bounds, int mouseX, int mouseY, float delta) {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            int count = entry.getValue().value();
            if (count == 1) return;
            String value = Integer.toString(count);

            graphics.push();
            graphics.translate(bounds.x, bounds.y, 0);
            graphics.scale(bounds.width / 16f, (bounds.getWidth() + bounds.getHeight()) / 2f / 16f, 1.0f);

            textRenderer.draw(value, 17 - textRenderer.getWidth(value), 9, 0xFFFFFF, true, graphics.getMatrices().peek().getPositionMatrix(), immediate, TextRenderer.TextLayerType.NORMAL, 0, 15728880);
            graphics.pop();
        }

        @Override
        public void endBatch(EntryStack<AspectPair> entry, Void extraData, DrawContext graphics, float delta) {}

        @Override @Nullable
        public Tooltip getTooltip(EntryStack<AspectPair> entry, TooltipContext context) {
            if (entry.isEmpty()) return null;
            return Tooltip.create(Text.of(entry.getValue().aspect().getDisplayName()));
        }
    }
}
