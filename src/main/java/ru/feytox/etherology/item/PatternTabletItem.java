package ru.feytox.etherology.item;

import lombok.Getter;
import net.minecraft.item.Item;
import ru.feytox.etherology.magic.staff.StaffStyles;

@Getter
public class PatternTabletItem extends Item {

    private final StaffStyles staffStyle;

    public PatternTabletItem(StaffStyles staffStyle) {
        super(new Settings());
        this.staffStyle = staffStyle;
    }
}
