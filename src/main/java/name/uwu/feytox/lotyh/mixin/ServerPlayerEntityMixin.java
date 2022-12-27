package name.uwu.feytox.lotyh.mixin;

import name.uwu.feytox.lotyh.components.IFloatComponent;
import name.uwu.feytox.lotyh.util.IdkLib;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

import static name.uwu.feytox.lotyh.LotyhComponents.ETHER_POINTS;
import static name.uwu.feytox.lotyh.LotyhComponents.ETHER_REGEN;

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
