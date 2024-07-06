package ru.feytox.etherology.util.gecko;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.network.animation.Stop2BlockAnimS2C;
import ru.feytox.etherology.network.animation.SwitchBlockAnimS2C;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class EGeoAnimation {

    private final String animName;
    @Getter
    private final RawAnimation animation;
    private final int transitionTicks;
    private boolean generateController = true;
    private boolean markable = true;

    public static EGeoAnimation begin(String animName) {
        return begin(animName, 0);
    }

    public static EGeoAnimation begin(String animName, int transitionTicks) {
        return new EGeoAnimation(animName, RawAnimation.begin(), transitionTicks);
    }

    @Nullable
    public <T extends EGeo2BlockEntity> AnimationController<T> generateController(T animatable) {
        return !generateController ? null : forceGenerateController(animatable);
    }

    public <T extends EGeo2BlockEntity> AnimationController<T> forceGenerateController(T animatable) {
        return new AnimationController<>(animatable, animName + "_controller", transitionTicks, state -> PlayState.STOP).triggerableAnim(animName, animation);
    }

    public <T extends EGeo2BlockEntity> void trigger(T animatable) {
        animatable.triggerAnimByName(animName);
        markActive(animatable);
    }

    public <T extends EGeo2BlockEntity> void triggerOnce(T animatable) {
        animatable.triggerOnce(animName);
        markActive(animatable);
    }
    
    public <T extends BlockEntity & EGeo2BlockEntity> void switchTo(T animatable, EGeoAnimation newAnim) {
        SwitchBlockAnimS2C.sendForTracking(animatable, animName, newAnim.animName);
        markStop(animatable);
        newAnim.markActive(animatable);
    }
    
    public <T extends BlockEntity & EGeo2BlockEntity> void stop(T animatable) {
        Stop2BlockAnimS2C.sendForTracking(animatable, animName);
        markStop(animatable);
    }

    public <T extends EGeo2BlockEntity> void markActive(T animatable) {
        if (markable) animatable.markAnimationActive(animName);
    }

    public <T extends EGeo2BlockEntity> void markStop(T animatable) {
        if (markable) animatable.markAnimationStop(animName);
    }

    public <T extends EGeo2BlockEntity> void stopOnClient(T animatable) {
        animatable.stopClientAnim(animName);
    }

    public <T extends EGeo2BlockEntity> boolean isPlaying(T animatable) {
        return animatable.isAnimationPlaying(animName);
    }

    @Nullable
    public <T extends EGeo2BlockEntity> AnimationController<?> getAnimationController(T animatable) {
        return animatable.getAnimationController(animName);
    }

    public EGeoAnimation withoutController() {
        generateController = false;
        return this;
    }

    public EGeoAnimation withoutMarking() {
        markable = false;
        return this;
    }

    public EGeoAnimation thenPlay(String animName) {
        animation.thenPlay(animName);
        return this;
    }

    public EGeoAnimation thenLoop(String animName) {
        animation.thenLoop(animName);
        return this;
    }

    public EGeoAnimation thenPlayAndHold(String animName) {
        animation.thenPlayAndHold(animName);
        return this;
    }

    // TODO: 10.03.2024 Implement other methods from RawAnimation as needed
}
