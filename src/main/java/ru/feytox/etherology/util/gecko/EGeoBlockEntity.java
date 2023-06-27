package ru.feytox.etherology.util.gecko;

import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public interface EGeoBlockEntity extends GeoBlockEntity {
    default void triggerAnim(String animName) {
        triggerAnim(animName + "_controller", animName);
    }

    default AnimationController<EGeoBlockEntity> getTriggerController(String animName, RawAnimation animation) {
        return getTriggerController(animName, animation, 0);
    }

    default AnimationController<EGeoBlockEntity> getTriggerController(String animName, RawAnimation animation, int transitionTicks) {
        return new AnimationController<>(this, animName + "_controller", transitionTicks, state -> PlayState.STOP)
                .triggerableAnim(animName, animation);
    }

    default AnimationController<EGeoBlockEntity> getController(RawAnimation animation) {
        return new AnimationController<>(this, state -> state.setAndContinue(animation));
    }

    default void stopAnim(String animName) {
        AnimationController<?> controller = getAnimatableInstanceCache().getManagerForId(0).getAnimationControllers()
                .getOrDefault(animName + "_controller", null);
        if (controller != null) controller.stop();
    }
}
