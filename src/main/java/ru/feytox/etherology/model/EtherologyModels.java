package ru.feytox.etherology.model;

import lombok.experimental.UtilityClass;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.item.Item;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.item.GlaiveItem;
import ru.feytox.etherology.item.OculusItem;
import ru.feytox.etherology.magic.staff.StaffPart;
import ru.feytox.etherology.magic.staff.StaffPartInfo;
import ru.feytox.etherology.util.feyapi.EIdentifier;

import java.util.Optional;

@UtilityClass
public class EtherologyModels {
    // models
    public static final Model GLAIVE_IN_HAND;
    public static final Model GLAIVE_IN_HAND_HANDLE;
    public static final Model HAMMER;
    public static final Model HAMMER_HANDLE;

    // texture keys
    public static final TextureKey GLAIVE = TextureKey.of("glaive");
    public static final TextureKey HAMMER_KEY = TextureKey.of("hammer");
    public static final TextureKey STYLE = TextureKey.of("style");

    private static Model item(String parent, TextureKey... requiredTextureKeys) {
        return new Model(Optional.of(new EIdentifier("item/" + parent)), Optional.empty(), requiredTextureKeys);
    }

    @Nullable
    public static ModelIdentifier getItemModel(Item item, boolean isInHand) {
        if (!(item instanceof GlaiveItem) && !(item instanceof OculusItem)) return null;
        String modelPath = item + (isInHand ? "_in_hand" : "");
        return createItemModelId(modelPath);
    }

    public static ModelIdentifier createItemModelId(String modelPath) {
        return new ModelIdentifier(new EIdentifier(modelPath), "inventory");
    }

    public static Model getStaffPartModel(StaffPartInfo partInfo) {
        StaffPart part = partInfo.getPart();
        if (!part.isStyled()) return item("staff_" + part.getName(), TextureKey.PARTICLE, STYLE);
        return item("staff_" + part.getName() + "_" + partInfo.getFirstPattern().getName(), TextureKey.PARTICLE, STYLE);
    }

    static {
        GLAIVE_IN_HAND = item("glaive_in_hand", GLAIVE);
        GLAIVE_IN_HAND_HANDLE = item("glaive_in_hand_handle", GLAIVE);
        HAMMER = item("hammer", HAMMER_KEY);
        HAMMER_HANDLE = item("hammer_handle", HAMMER_KEY);
    }
}
