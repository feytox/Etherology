package ru.feytox.etherology.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.item.ShearsItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.feytox.etherology.registry.block.DecoBlocks;

import java.util.List;
import java.util.stream.Stream;

@Mixin(ShearsItem.class)
public class ShearsItemMixin {

    @ModifyReturnValue(method = "createToolComponent", at = @At("RETURN"))
    private static ToolComponent injectForestLanternSpeed(ToolComponent original) {
        List<ToolComponent.Rule> rules = Stream.concat(original.rules().stream(), Stream.of(ToolComponent.Rule.of(List.of(DecoBlocks.FOREST_LANTERN), 15.0f))).toList();
        return new ToolComponent(rules, original.defaultMiningSpeed(), original.damagePerBlock());
    }

    @ModifyReturnValue(method = "postMine", at = @At("RETURN"))
    private boolean injectLightelet(boolean original, @Local(argsOnly = true) BlockState state) {
        return original || state.isOf(DecoBlocks.LIGHTELET);
    }
}
