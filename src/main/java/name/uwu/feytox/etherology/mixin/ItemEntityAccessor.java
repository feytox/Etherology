package name.uwu.feytox.etherology.mixin;

import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemEntity.class)
public interface ItemEntityAccessor {
    @Accessor
    void setItemAge(int itemAge);

    @Accessor
    void setPickupDelay(int pickupDelay);
}
