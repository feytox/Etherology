package ru.feytox.etherology.util.gecko;

import lombok.val;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;

public interface EGeo2BlockEntity extends GeoBlockEntity {

    default void triggerAnimByName(String animName) {
        triggerAnim(animName + "_controller", animName);
    }

    // TODO: 10.03.2024 rename maybe
    default void triggerOnce(String animName) {
        if (isAnimationPlaying(animName)) return;
        triggerAnimByName(animName);
    }

    default void stopClientAnim(String animName) {
        AnimationController<?> controller = getAnimationController(animName);
        boolean test = controller != null;
        if (test) controller.stop();
    }

    void markAnimationActive(String animName);
    void markAnimationStop(String animName);

    default AnimationController<EGeo2BlockEntity> createBaseController(EGeoAnimation animation) {
        return new AnimationController<>(this, state -> state.setAndContinue(animation.getAnimation()));
    }

    default boolean isAnimationPlaying(String animName) {
        val controller = getAnimationController(animName);
        if (controller == null) return false;
        val state = controller.getAnimationState();
        return state.equals(AnimationController.State.RUNNING) || state.equals(AnimationController.State.PAUSED);
    }

    @Nullable
    default AnimationController<?> getAnimationController(String animName) {
        return getAnimatableInstanceCache().getManagerForId(0).getAnimationControllers()
                .getOrDefault(animName + "_controller", null);
    }
}
