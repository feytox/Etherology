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
import ru.feytox.etherology.util.misc.EIdentifier;
import ru.feytox.etherology.util.misc.EtherologyPlayer;

import java.util.function.Consumer;

public class TriggerAnimations {
    // hammer animations (weak = hammer + glaive)
    public static final TriggerPlayerAnimation HAMMER_HIT_LEFT = new TriggerPlayerAnimation(new EIdentifier("hammer_hit_left"), false, true,
            PlayerAnimationController.setHammerState(HammerState.FULL_ATTACK),
            playOrMine("hammer_idle_left"));
    public static final TriggerPlayerAnimation HAMMER_HIT_RIGHT = new TriggerPlayerAnimation(new EIdentifier("hammer_hit_right"), false, true,
            PlayerAnimationController.setHammerState(HammerState.FULL_ATTACK),
            playOrMine("hammer_idle_right"));
    public static final TriggerPlayerAnimation HAMMER_WEAK_HIT_LEFT = new TriggerPlayerAnimation(new EIdentifier("hammer_weak_hit_left"), false, true,
            PlayerAnimationController.setHammerState(HammerState.WEAK_ATTACK),
            playOrMine("hammer_idle_left"));
    public static final TriggerPlayerAnimation HAMMER_WEAK_HIT_RIGHT = new TriggerPlayerAnimation(new EIdentifier("hammer_weak_hit_right"), false, true,
            PlayerAnimationController.setHammerState(HammerState.WEAK_ATTACK),
            playOrMine("hammer_idle_right"));

    // glaive animations
    public static final TriggerPlayerAnimation GLAIVE_HIT_LEFT = new TriggerPlayerAnimation(new EIdentifier("glaive_hit_left"), false, true,
            PlayerAnimationController.setHammerState(HammerState.FULL_ATTACK),
            playOrMine("hammer_idle_left"));
    public static final TriggerPlayerAnimation GLAIVE_HIT_RIGHT = new TriggerPlayerAnimation(new EIdentifier("glaive_hit_right"), false, true,
            PlayerAnimationController.setHammerState(HammerState.FULL_ATTACK),
            playOrMine("hammer_idle_right"));


    /**
     * Returns a Consumer function that plays an animation for the specified player.
     *
     * @param  id    the ID of the animation to play
     * @return       a Consumer function that plays the animation for the player
     */
    private static Consumer<EtherologyPlayer> play(String id) {
        return player -> {
            AbstractPlayerAnimation anim = EtherologyRegistry.getAndCast(PredicatePlayerAnimation.class, new EIdentifier(id));
            if (anim == null) return;
            anim.play(player, 2, Ease.INCUBIC);
        };
    }

    /**
     * Returns a Consumer that either plays an animation or loop current animation (like mining).
     *
     * @param  id  the ID of the animation to play
     * @return     a Consumer that either plays an animation or loop current animation
     */
    private static Consumer<EtherologyPlayer> playOrMine(String id) {
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
            Identifier animationId = new EIdentifier("hammer_weak_hit_" + prefix);
            AbstractPlayerAnimation anim = EtherologyRegistry.getAndCast(TriggerPlayerAnimation.class, animationId);
            if (anim == null) return;
            anim.play(player, 0, null);
        };
    }

    /**
     * Returns the appropriate animation for a two-handed weapon based on the given parameters.
     *
     * @param  mainArm         the main arm holding the weapon
     * @param  isHammer        true if the weapon is a hammer, false if a glaive
     * @param  isStrongAttack  true if it is a strong attack, false otherwise
     * @return                 the corresponding animation for the given parameters
     */
    public static TriggerPlayerAnimation getTwohandheldAnim(Arm mainArm, boolean isHammer, boolean isStrongAttack) {
        if (!isStrongAttack) return mainArm.equals(Arm.LEFT) ? HAMMER_WEAK_HIT_LEFT : HAMMER_WEAK_HIT_RIGHT;
        if (isHammer) return mainArm.equals(Arm.LEFT) ? HAMMER_HIT_LEFT : HAMMER_HIT_RIGHT;
        return mainArm.equals(Arm.LEFT) ? GLAIVE_HIT_LEFT : GLAIVE_HIT_RIGHT;
    }

    /**
     * Registers all Trigger Animations.
     */
    public static void registerAll() {
        HAMMER_HIT_LEFT.register();
        HAMMER_HIT_RIGHT.register();
        HAMMER_WEAK_HIT_LEFT.register();
        HAMMER_WEAK_HIT_RIGHT.register();
        GLAIVE_HIT_LEFT.register();
        GLAIVE_HIT_RIGHT.register();
    }
}
