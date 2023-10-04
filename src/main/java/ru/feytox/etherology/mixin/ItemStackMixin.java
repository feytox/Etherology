package ru.feytox.etherology.mixin;

import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.feytox.etherology.item.EtherStaff;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract void setNbt(@Nullable NbtCompound nbt);

    @Inject(method = "<init>(Lnet/minecraft/item/ItemConvertible;)V", at = @At("RETURN"))
    private void injectDefaultNbt(ItemConvertible item, CallbackInfo ci) {
        if (item == null) return;
        if (!(item instanceof EtherStaff)) return;

        NbtCompound stackNbt = new NbtCompound();
        EtherStaff.writeDefaultParts(stackNbt);
        setNbt(stackNbt);
    }
}
