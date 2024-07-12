package ru.feytox.etherology.mixin;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PlacementModifierType.class)
public interface PlacementModifierTypeAccessor {

    @Invoker
    static <P extends PlacementModifier> PlacementModifierType<P> callRegister(String id, MapCodec<P> codec) {
        throw new UnsupportedOperationException();
    }
}
