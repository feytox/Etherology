package ru.feytox.etherology.item;

import com.github.crimsondawn45.fabricshieldlib.lib.object.FabricShieldItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class EtherShield extends FabricShieldItem {

    public EtherShield(Settings settings, int coolDownTicks, int enchantability, Item... repairItems) {
        super(settings, coolDownTicks, enchantability, repairItems);
    }

    public static boolean shieldBlockCheck(Vec3d entityRotation, Vec3d entityPos, Vec3d damagePos, boolean isProjectile) {
        Vec3d damageVec = damagePos.relativize(entityPos).normalize();
        damageVec = new Vec3d(damageVec.x, 0, damageVec.z);
        double cosVal = isProjectile ? 0.0 : -0.866025;
        return damageVec.dotProduct(entityRotation) < cosVal;
    }

    public static Optional<ItemStack> getUsingShield(LivingEntity user) {
        ItemStack activeStack = user.getActiveItem();
        if (!(activeStack.getItem() instanceof EtherShield)) return Optional.empty();
        return Optional.of(activeStack);
    }
}
