package ru.feytox.etherology.block.etherealGenerators.spinner;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import ru.feytox.etherology.util.gecko.EGeoBlockRenderer;

public class EtherealSpinnerRenderer extends EGeoBlockRenderer<EtherealSpinnerBlockEntity> {
    public EtherealSpinnerRenderer(BlockEntityRendererFactory.Context context) {
        super(new EtherealSpinnerModel());
    }

    @Override
    protected void rotateBlock(Direction facing, MatrixStack poseStack) {}
}
