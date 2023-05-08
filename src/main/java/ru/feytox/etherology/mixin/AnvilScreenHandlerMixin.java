package ru.feytox.etherology.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import ru.feytox.etherology.item.BattlePickaxe;

import java.util.Map;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin {
    @Final
    @Shadow
    private Property levelCost;

    @Inject(method = "updateResult",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;keySet()Ljava/util/Set;", ordinal = 1),
            locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void checkBattlePick(CallbackInfo ci, ItemStack itemStack, int i, int j, int k, ItemStack itemStack2, ItemStack itemStack3, Map<Enchantment, Integer> map, boolean bl, Map<Enchantment, Integer> map2) {
        if (!(itemStack.getItem() instanceof BattlePickaxe)) return;
        if (!map2.containsKey(Enchantments.SILK_TOUCH) && !map2.containsKey(Enchantments.FORTUNE)) return;

        ((ForgingScreenHandlerAccessor) this).getOutput().setStack(0, ItemStack.EMPTY);
        levelCost.set(0);
        ci.cancel();
    }
}
