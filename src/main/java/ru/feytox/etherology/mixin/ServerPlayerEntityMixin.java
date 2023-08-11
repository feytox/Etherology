package ru.feytox.etherology.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.feytox.etherology.components.IFloatComponent;
import ru.feytox.etherology.util.deprecated.IdkLib;

import java.util.Optional;

import static ru.feytox.etherology.registry.util.EtherologyComponents.ETHER_POINTS;
import static ru.feytox.etherology.registry.util.EtherologyComponents.ETHER_REGEN;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Inject(method = "playerTick", at = @At("HEAD"))
    public void onPlayerTick(CallbackInfo ci) {
        PlayerEntity player = ((PlayerEntity) ((Object) this));
        Optional<IFloatComponent> etherPoints = ETHER_POINTS.maybeGet(player);
        if (etherPoints.isPresent()) {
            float etherRegen = ETHER_REGEN.get(this).getValue() / 20;
            etherPoints.get().increment(etherRegen);
            IdkLib.tickExhaustion(player);
        }
    }
}
