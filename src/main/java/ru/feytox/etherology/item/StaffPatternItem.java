package ru.feytox.etherology.item;

import lombok.Getter;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import ru.feytox.etherology.magic.staff.StaffStyles;

public class StaffPatternItem extends Item {

    @Getter
    private final StaffStyles staffStyle;

    public StaffPatternItem(StaffStyles staffStyle) {
        super(new FabricItemSettings().maxCount(1));
        this.staffStyle = staffStyle;
    }
}
