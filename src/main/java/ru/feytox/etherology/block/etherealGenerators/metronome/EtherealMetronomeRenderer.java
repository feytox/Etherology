package ru.feytox.etherology.block.etherealGenerators.metronome;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import ru.feytox.etherology.util.gecko.EGeoBlockRenderer;

public class EtherealMetronomeRenderer extends EGeoBlockRenderer<EtherealMetronomeBlockEntity> {
    public EtherealMetronomeRenderer(BlockEntityRendererFactory.Context context) {
        super(new EtherealMetronomeModel());
    }

    @Override
    protected void rotateBlock(Direction facing, MatrixStack poseStack) {}
}
