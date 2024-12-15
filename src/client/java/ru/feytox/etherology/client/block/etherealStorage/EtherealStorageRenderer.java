package ru.feytox.etherology.client.block.etherealStorage;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import ru.feytox.etherology.block.etherealStorage.EtherealStorageBlockEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class EtherealStorageRenderer extends GeoBlockRenderer<EtherealStorageBlockEntity> {
    public EtherealStorageRenderer(BlockEntityRendererFactory.Context context) {
        super(new EtherealStorageModel());
    }
}
