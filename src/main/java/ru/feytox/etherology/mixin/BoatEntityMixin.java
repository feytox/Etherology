package ru.feytox.etherology.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import ru.feytox.etherology.util.misc.BoatTypes;

@Mixin(BoatEntity.class)
public abstract class BoatEntityMixin {

    @Shadow public abstract BoatEntity.Type getVariant();

    @ModifyReturnValue(method = "asItem", at = @At("RETURN"))
    private Item injectEtherBoat(Item original) {
        return BoatTypes.getBoatFromType(original, getVariant(), false);
    }
}
