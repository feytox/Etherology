package ru.feytox.etherology.mixin;

import com.mojang.authlib.GameProfile;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.feytox.etherology.util.feyapi.IAnimatedPlayer;
import ru.feytox.etherology.util.feyapi.PlayerAnimations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Mixin(AbstractClientPlayerEntity.class)
public class ClientPlayerEntityMixin implements IAnimatedPlayer {
    @Unique
    private final ModifierLayer<IAnimation> etherologyAnimationContainer = new ModifierLayer<>();

    @Unique
    private final Map<PlayerAnimations, Boolean> etherologyPredictableAnimations = new HashMap<>();

    @Unique
    private boolean etherologyAnimationTicks = false;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(ClientWorld world, GameProfile profile, CallbackInfo ci) {
        PlayerAnimationAccess.getPlayerAnimLayer((AbstractClientPlayerEntity) (Object) this).addAnimLayer(1000, etherologyAnimationContainer);
        Arrays.stream(PlayerAnimations.values()).forEach(anim -> etherologyPredictableAnimations.put(anim, false));
    }

    @Override
    public ModifierLayer<IAnimation> getEtherologyAnimation() {
        return etherologyAnimationContainer;
    }

    @Override
    public boolean getLastAnimState(PlayerAnimations anim) {
        return etherologyPredictableAnimations.get(anim);
    }

    @Override
    public boolean hadEtherologyAnimationsTick() {
        return etherologyAnimationTicks;
    }

    @Override
    public void setHadEtherologyAnimationsTick() {
        etherologyAnimationTicks = true;
    }

    @Override
    public void setAnimState(PlayerAnimations anim, boolean state) {
        etherologyPredictableAnimations.put(anim, state);
    }
}
