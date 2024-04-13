package ru.feytox.etherology.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ToolMaterial;
import ru.feytox.etherology.util.misc.DoubleModel;
import ru.feytox.etherology.util.misc.EtherologyPlayer;

public class GlaiveItem extends TwoHandheldSword implements DoubleModel {
    public GlaiveItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed) {
        this(toolMaterial, attackDamage, attackSpeed, new FabricItemSettings());
    }

    public GlaiveItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, FabricItemSettings itemSettings) {
        super(toolMaterial, attackDamage, attackSpeed, itemSettings);
    }

    /**
     * @param player player to check
     * @return true if player has glaive in main hand and offhand is empty
     */
    public static boolean checkGlaive(PlayerEntity player) {
        return check(player, GlaiveItem.class);
    }

    public static boolean checkGlaive(EtherologyPlayer animatedPlayer) {
        if (!(animatedPlayer instanceof AbstractClientPlayerEntity clientPlayer)) return false;
        return checkGlaive(clientPlayer);
    }
}
