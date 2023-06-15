package ru.feytox.etherology.animation;

import dev.kosmx.playerAnim.core.util.Ease;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.enums.HammerState;
import ru.feytox.etherology.mixin.ClientPlayerInteractionManagerAccessor;
import ru.feytox.etherology.network.EtherologyNetwork;
import ru.feytox.etherology.network.interaction.HammerMiningC2S;
import ru.feytox.etherology.registry.custom.EtherologyRegistry;
import ru.feytox.etherology.util.feyapi.EIdentifier;
import ru.feytox.etherology.util.feyapi.IAnimatedPlayer;

import java.util.function.Consumer;

public class TriggerAnimations {
    public static final TriggerPlayerAnimation LEFT_HAMMER_HIT = new TriggerPlayerAnimation(new EIdentifier("left_hammer_hit"), false, true,
            PlayerAnimationController.setHammerState(HammerState.FULL_ATTACK),
            playOrMine("left_hammer_idle"));
    public static final TriggerPlayerAnimation RIGHT_HAMMER_HIT = new TriggerPlayerAnimation(new EIdentifier("right_hammer_hit"), false, true,
            PlayerAnimationController.setHammerState(HammerState.FULL_ATTACK),
            playOrMine("right_hammer_idle"));
    public static final TriggerPlayerAnimation LEFT_HAMMER_HIT_WEAK = new TriggerPlayerAnimation(new EIdentifier("left_hammer_hit_weak"), false, true,
            PlayerAnimationController.setHammerState(HammerState.WEAK_ATTACK),
            playOrMine("left_hammer_idle"));
    public static final TriggerPlayerAnimation RIGHT_HAMMER_HIT_WEAK = new TriggerPlayerAnimation(new EIdentifier("right_hammer_hit_weak"), false, true,
            PlayerAnimationController.setHammerState(HammerState.WEAK_ATTACK),
            playOrMine("right_hammer_idle"));

    private static Consumer<IAnimatedPlayer> play(String id) {
        return player -> {
            AbstractPlayerAnimation anim = EtherologyRegistry.getAndCast(PredicatePlayerAnimation.class, new EIdentifier(id));
            if (anim == null) return;
            anim.play(player, 2, Ease.INCUBIC);
        };
    }

    private static Consumer<IAnimatedPlayer> playOrMine(String id) {
        return player -> {
            if (!(player instanceof ClientPlayerEntity clientPlayer)) {
                play(id).accept(player);
                return;
            }

            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerInteractionManager interactionManager = client.interactionManager;
            if (interactionManager == null || !((ClientPlayerInteractionManagerAccessor) interactionManager).isBreakingBlock()) {
                play(id).accept(player);
                return;
            }

            boolean isRightArm = clientPlayer.getMainArm().equals(Arm.RIGHT);
            HammerMiningC2S packet = new HammerMiningC2S(isRightArm);
            EtherologyNetwork.sendToServer(packet);

            String prefix = isRightArm ? "right" : "left";
            Identifier animationId = new EIdentifier(prefix + "_hammer_hit_weak");
            AbstractPlayerAnimation anim = EtherologyRegistry.getAndCast(TriggerPlayerAnimation.class, animationId);
            if (anim == null) return;
            anim.play(player, 0, null);
        };
    }

    public static void registerAll() {
        LEFT_HAMMER_HIT.register();
        RIGHT_HAMMER_HIT.register();
        LEFT_HAMMER_HIT_WEAK.register();
        RIGHT_HAMMER_HIT_WEAK.register();
    }
}
