package ru.feytox.etherology.block.generators.spinner;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class SpinnerRenderer extends GeoBlockRenderer<SpinnerBlockEntity> {
    public SpinnerRenderer(BlockEntityRendererFactory.Context context) {
        super(new SpinnerModel());
    }

    @Override
    protected void rotateBlock(Direction facing, MatrixStack poseStack) {}
}
