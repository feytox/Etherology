package ru.feytox.etherology.item.revelationView;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.magic.aspects.Aspect;
import ru.feytox.etherology.magic.aspects.EtherologyAspect;
import ru.feytox.etherology.mixin.TessellatorAccessor;
import ru.feytox.etherology.util.misc.EIdentifier;
import ru.feytox.etherology.util.misc.RenderUtils;

import java.util.List;

public abstract class RevelationViewData {

    public abstract boolean isEmpty();

    public abstract void render(MinecraftClient client, MatrixStack matrices, float progress);

    @RequiredArgsConstructor
    public static class Aspects extends RevelationViewData {

        private static final int ROW_SIZE = 5;

        @NonNull
        private final List<Pair<Aspect, Integer>> aspects;

        @Override
        public boolean isEmpty() {
            return aspects.isEmpty();
        }

        @Override
        public void render(MinecraftClient client, MatrixStack matrices, float progress) {
            var lastRowIndex = (aspects.size() / ROW_SIZE) * ROW_SIZE;
            var lastRowSize = aspects.size() % ROW_SIZE;
            lastRowSize = lastRowSize == 0 ? ROW_SIZE : lastRowSize;

            int i = 0;
            for (Pair<Aspect, Integer> pair : aspects) {
                var row = i / ROW_SIZE;
                var col = i % ROW_SIZE;

                var rowSize = i >= lastRowIndex ? lastRowSize : ROW_SIZE;
                var startOffset = rowSize * 0.5f * 0.25f;

                renderAspect(matrices, pair.getFirst(), -col * 0.25f + startOffset, row * 0.275f, progress);
                renderCount(client, matrices, pair.getSecond(), col, row, startOffset, progress);
                i++;
            }
        }

        private void renderAspect(MatrixStack matrices, Aspect aspect, float dx, float dy, float progress) {
            matrices.push();
            RenderSystem.setShaderTexture(0, Aspect.TEXTURE);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();
            RenderSystem.disableDepthTest();
            matrices.scale(progress, progress, progress);
            matrices.translate(dx, dy, 0);
            RenderUtils.renderTexture(matrices, 0, 0, 0, aspect.getTextureMinX(), aspect.getTextureMinY(), 0.25f, 0.25f, aspect.getWidth(), aspect.getHeight(), EtherologyAspect.TEXTURE_WIDTH, EtherologyAspect.TEXTURE_HEIGHT);
            matrices.pop();

            RenderSystem.enableCull();
        }

        private void renderCount(MinecraftClient client, MatrixStack matrices, Integer count, int col, int row, float startOffset, float progress) {
            matrices.push();
            matrices.scale(progress, progress, progress);
            matrices.translate(-col * 0.25f + startOffset - 0.18f, row * 0.275f - 0.18f, -0.0001);
            matrices.scale(-0.008F, -0.008F, 0.025F);
            var immediate = VertexConsumerProvider.immediate(((TessellatorAccessor) Tessellator.getInstance()).getAllocator());
            client.textRenderer.draw(count.toString(), 0, 0, 0xFFFFFF, false, matrices.peek().getPositionMatrix(), immediate, TextRenderer.TextLayerType.SEE_THROUGH, 0, 15728880);
            immediate.draw();
            matrices.pop();
        }
    }

    @RequiredArgsConstructor
    public static class Ether extends RevelationViewData {

        private static final Identifier ETHER_TEXTURE = EIdentifier.of("textures/item/ether.png");

        private final float ether;
        private final float maxEther;

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public void render(MinecraftClient client, MatrixStack matrices, float progress) {
            matrices.push();
            matrices.scale(progress, progress, progress);
            matrices.scale(-0.016F, -0.016F, 0.025F);

            var etherText = String.format("%.1f / %.1f", ether, maxEther);
            var textWidth = client.textRenderer.getWidth(etherText);
            matrices.translate(-textWidth/2f - 8f, 0, 0);

            matrices.push();
            matrices.translate(0, 0, -8);
            matrices.scale(-1, -1, 1);
            RenderSystem.enableBlend();
            RenderSystem.disableDepthTest();
            RenderSystem.setShaderTexture(0, ETHER_TEXTURE);
            RenderUtils.renderTexture(matrices, 0, 0, 0, 0, 0, 16, 16, 16, 16);
            matrices.pop();

            var immediate = VertexConsumerProvider.immediate(((TessellatorAccessor) Tessellator.getInstance()).getAllocator());
            client.textRenderer.draw(etherText, 16, 0, 0xFFFFFF, false, matrices.peek().getPositionMatrix(), immediate, TextRenderer.TextLayerType.SEE_THROUGH, 0, 15728880);
            immediate.draw();

            matrices.pop();
        }
    }
}
