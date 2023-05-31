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
import ru.feytox.etherology.animation.AbstractPlayerAnimation;
import ru.feytox.etherology.animation.PredicatePlayerAnimation;
import ru.feytox.etherology.enums.HammerState;
import ru.feytox.etherology.registry.custom.EtherologyRegistry;
import ru.feytox.etherology.util.feyapi.IAnimatedPlayer;

import java.util.HashMap;
import java.util.Map;

@Mixin(AbstractClientPlayerEntity.class)
public class ClientPlayerEntityMixin implements IAnimatedPlayer {
    @Unique
    private final ModifierLayer<IAnimation> etherologyAnimationContainer = new ModifierLayer<>();

    @Unique
    private final Map<AbstractPlayerAnimation, Boolean> etherologyPredictableAnimations = new HashMap<>();

    @Unique
    private HammerState etherologyHammerState = HammerState.EMPTY;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(ClientWorld world, GameProfile profile, CallbackInfo ci) {
        PlayerAnimationAccess.getPlayerAnimLayer((AbstractClientPlayerEntity) (Object) this).addAnimLayer(1000, etherologyAnimationContainer);
        EtherologyRegistry.getAll(PredicatePlayerAnimation.class).forEach(anim -> etherologyPredictableAnimations.put(anim, false));
    }

    @Override
    public ModifierLayer<IAnimation> getEtherologyAnimation() {
        return etherologyAnimationContainer;
    }

    @Override
    public boolean getLastAnimState(AbstractPlayerAnimation anim) {
        return etherologyPredictableAnimations.get(anim);
    }


    @Override
    public void setAnimState(AbstractPlayerAnimation anim, boolean state) {
        etherologyPredictableAnimations.put(anim, state);
    }

    @Override
    public HammerState getHammerState() {
        return etherologyHammerState;
    }

    @Override
    public void setHammerState(HammerState state) {
        etherologyHammerState = state;
    }
}
