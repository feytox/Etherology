package ru.feytox.etherology.data.client;

import lombok.experimental.UtilityClass;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.item.Item;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.item.GlaiveItem;
import ru.feytox.etherology.util.feyapi.EIdentifier;

import java.util.Optional;

@UtilityClass
public class EtherologyModels {
    // models
    public static final Model GLAIVE_IN_HAND;
    public static final Model GLAIVE_IN_HAND_HANDLE;

    // texture keys
    public static final TextureKey GLAIVE = TextureKey.of("glaive");

    private static Model item(String parent, TextureKey... requiredTextureKeys) {
        return new Model(Optional.of(new EIdentifier("item/" + parent)), Optional.empty(), requiredTextureKeys);
    }

    @Nullable
    public static ModelIdentifier getItemModel(Item item, boolean isInHand) {
        if (!(item instanceof GlaiveItem)) return null;
        String modelPath = item + (isInHand ? "_in_hand" : "");
        return new ModelIdentifier(new EIdentifier(modelPath), "inventory");
    }

    static {
        GLAIVE_IN_HAND = item("glaive_in_hand", GLAIVE);
        GLAIVE_IN_HAND_HANDLE = item("glaive_in_hand_handle", GLAIVE);
    }
}
