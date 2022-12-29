package name.uwu.feytox.etherology.blocks.crucible;

import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class CrucibleBlockItemRenderer extends GeoItemRenderer<CrucibleBlockItem> {
    public CrucibleBlockItemRenderer() {
        super(new CrucibleBlockItemModel());
    }
}
