package ru.feytox.etherology.animation.armPoses;

import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import ru.feytox.etherology.mixin.MinecraftClientAccessor;
import ru.feytox.etherology.mixin.RenderTickCounterAccessor;
import ru.feytox.etherology.util.feyapi.EtherologyPlayer;

@UtilityClass
public class ArmAnimUtil {

    public static boolean shouldAnimateArms(LivingEntity entity) {
        if (!(entity instanceof EtherologyPlayer player)) return true;
        val animation = player.etherology$getCurrentArmAnimation();
        if (animation == null) return true;
        return animation.getAnimation().isAnimateArms();
    }

    /**
     * Tick the current animation for a given model and entity.
     *
     * @param  model   the model.
     * @param  entity  the entity.
     */
    public static void tickCurrentAnimation(BipedEntityModel<?> model, LivingEntity entity) {
        if (!(entity instanceof EtherologyPlayer player)) return;
        val animation = player.etherology$getCurrentArmAnimation();
        if (animation == null) return;

        if (animation.isCompleted()) {
            val newAnimation = animation.getAnimation().isTransitionAfter() ? ArmAnimations.DEFAULT_ANIMATION.createInstance(animation) : null;
            player.etherology$setCurrentArmAnimation(newAnimation);
            return;
        }

        val client = MinecraftClient.getInstance();
        val tickCounter = ((MinecraftClientAccessor) client).getRenderTickCounter();
        float tickDelta = tickCounter.lastFrameDuration * ((RenderTickCounterAccessor) tickCounter).getTickTime();
        animation.tick(model, entity, client.isPaused() ? 0.0f : tickDelta);
    }

    /**
     * Injects the given ArmAnimation in the specified LivingEntity if the animation's trigger condition is met.
     *
     * @param  animation  the ArmAnimation to be tested and potentially injected
     * @param  entity     the LivingEntity in which the animation will be injected
     */
    public static void testAndInject(ArmAnimation animation, LivingEntity entity) {
        if (!animation.checkTrigger(entity)) return;
        tryInjectAnimation(animation, entity);
    }

    /**
     * Tries to inject an animation into a living entity.
     *
     * @param  animation  the animation to inject
     * @param  entity     the living entity to inject the animation into
     */
    public static void tryInjectAnimation(ArmAnimation animation, LivingEntity entity) {
        if (!(entity instanceof EtherologyPlayer player)) return;
        val currentAnimation = player.etherology$getCurrentArmAnimation();
        if (currentAnimation == null || currentAnimation.isCompleted()) {
            player.etherology$setCurrentArmAnimation(animation.createInstance(currentAnimation));
            return;
        }

        if (currentAnimation.getAnimation().equals(animation)) {
            if (currentAnimation.getAnimation().isCanReset()) {
                currentAnimation.setStopped(true);
                player.etherology$setCurrentArmAnimation(animation.createInstance(currentAnimation));
            }
            return;
        }

        if (currentAnimation.getAnimation().getPriority() >= animation.getPriority()) return;
        currentAnimation.setStopped(true);
        player.etherology$setCurrentArmAnimation(animation.createInstance(currentAnimation));
    }
}
