package ru.feytox.etherology.block.etherealStorage;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import ru.feytox.etherology.util.gecko.EGeoBlockRenderer;

public class EtherealStorageRenderer extends EGeoBlockRenderer<EtherealStorageBlockEntity> {
    public EtherealStorageRenderer(BlockEntityRendererFactory.Context context) {
        super(new EtherealStorageModel());
    }
}
