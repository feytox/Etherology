package ru.feytox.etherology.util.gecko;

import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

@Deprecated
public interface EGeoBlockEntity extends GeoBlockEntity {

    default void triggerAnim(String animName) {
        triggerAnim(animName + "_controller", animName);
    }

    default AnimationController<EGeoBlockEntity> createTriggerController(String animName, RawAnimation animation) {
        return createTriggerController(animName, animation, 0);
    }

    default AnimationController<EGeoBlockEntity> createTriggerController(String animName, RawAnimation animation, int transitionTicks) {
        return new AnimationController<>(this, animName + "_controller", transitionTicks, state -> PlayState.STOP)
                .triggerableAnim(animName, animation);
    }

    default void stopClientAnim(String animName) {
        AnimationController<?> controller = getAnimationController(animName);
        if (controller != null) controller.stop();
    }

    @Nullable
    private AnimationController<?> getAnimationController(String animName) {
        return getAnimatableInstanceCache().getManagerForId(0).getAnimationControllers()
                .getOrDefault(animName + "_controller", null);
    }
}
