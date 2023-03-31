package ru.feytox.etherology.blocks.etherealGenerators.spinner;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class EtherealSpinnerRenderer extends GeoBlockRenderer<EtherealSpinnerBlockEntity> {
    public EtherealSpinnerRenderer(BlockEntityRendererFactory.Context context) {
        super(new EtherealSpinnerModel());
    }

    @Override
    protected void rotateBlock(Direction facing, MatrixStack poseStack) {}
}
