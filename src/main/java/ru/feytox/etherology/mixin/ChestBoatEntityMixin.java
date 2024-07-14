package ru.feytox.etherology.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.vehicle.ChestBoatEntity;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.feytox.etherology.util.misc.BoatTypes;

@Mixin(ChestBoatEntity.class)
public class ChestBoatEntityMixin {

    @ModifyReturnValue(method = "asItem", at = @At("RETURN"))
    private Item injectEtherBoat(Item original) {
        return BoatTypes.getBoatFromType(original, ((ChestBoatEntity)(Object) this).getVariant(), true);
    }
}
