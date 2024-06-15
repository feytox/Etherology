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
import ru.feytox.etherology.util.misc.EtherologyPlayer;

import java.util.HashMap;
import java.util.Map;

@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin implements EtherologyPlayer {
    @Unique
    private final ModifierLayer<IAnimation> etherology$AnimationContainer = new ModifierLayer<>();

    @Unique
    private final Map<AbstractPlayerAnimation, Boolean> etherology$PredictableAnimations = new HashMap<>();

    @Unique
    private HammerState etherology$HammerState = HammerState.EMPTY;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(ClientWorld world, GameProfile profile, CallbackInfo ci) {
        PlayerAnimationAccess.getPlayerAnimLayer((AbstractClientPlayerEntity) (Object) this).addAnimLayer(1000, etherology$AnimationContainer);
        EtherologyRegistry.getAll(PredicatePlayerAnimation.class).forEach(anim -> etherology$PredictableAnimations.put(anim, false));
    }

    @Override
    public ModifierLayer<IAnimation> etherology$getAnimation() {
        return etherology$AnimationContainer;
    }

    @Override
    public boolean etherology$getLastAnimState(AbstractPlayerAnimation anim) {
        return etherology$PredictableAnimations.get(anim);
    }


    @Override
    public void etherology$setAnimState(AbstractPlayerAnimation anim, boolean state) {
        etherology$PredictableAnimations.put(anim, state);
    }

    @Override
    public HammerState etherology$getHammerState() {
        return etherology$HammerState;
    }

    @Override
    public void etherology$setHammerState(HammerState state) {
        etherology$HammerState = state;
    }


}
