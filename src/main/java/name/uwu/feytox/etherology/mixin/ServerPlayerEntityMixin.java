package name.uwu.feytox.etherology.mixin;

import name.uwu.feytox.etherology.components.IFloatComponent;
import name.uwu.feytox.etherology.util.feyapi.IdkLib;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

import static name.uwu.feytox.etherology.EtherologyComponents.ETHER_POINTS;
import static name.uwu.feytox.etherology.EtherologyComponents.ETHER_REGEN;

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
