package ru.feytox.etherology.block.generators.metronome;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class MetronomeRenderer extends GeoBlockRenderer<MetronomeBlockEntity> {
    public MetronomeRenderer(BlockEntityRendererFactory.Context context) {
        super(new MetronomeModel());
    }

    @Override
    protected void rotateBlock(Direction facing, MatrixStack poseStack) {}
}
