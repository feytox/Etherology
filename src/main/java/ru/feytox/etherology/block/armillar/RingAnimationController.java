package ru.feytox.etherology.block.armillar;

import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import ru.feytox.etherology.magic.aspects.Aspect;
import software.bernie.geckolib.core.animation.AnimationController;

import java.util.List;

import static ru.feytox.etherology.block.armillar.ArmillaryMatrixBlockEntity.TICKS_PER_RUNE;

@Deprecated
public class RingAnimationController<T extends ArmillaryMatrixBlockEntity> extends AnimationController<T> {

    private final int ringId;
    private final String impulseName;
    @Nullable
    private Double trackedTick = null;
    @Nullable
    private Double centerTick = null;
    @Nullable
    private Double targetTick = null;
    @Getter
    private boolean stopped = false;
    private boolean impulsePlayed = false;
    @Nullable
    private final RingAnimationController<T> biggerRing;

    public RingAnimationController(T animatable, int ringId, String name, String impulseName, @Nullable AnimationController<T> biggerRing, int transitionTickTime, AnimationStateHandler<T> animationHandler) {
        super(animatable, name, transitionTickTime, animationHandler);
        this.ringId = ringId;
        this.impulseName = impulseName;
        this.biggerRing = biggerRing == null ? null : (RingAnimationController<T>) biggerRing;
    }

    @Override
    protected double adjustTick(double tick) {
        double original = super.adjustTick(tick);
        Double result = tryStopRing(original);
        if (result != null) return result;

        trackedTick = null;
        centerTick = null;
        stopped = false;
        return original;
    }

    private Double tryStopRing(double original) {
        val matrixState = animatable.getCachedMatrixState();
        if (!matrixState.equals(ArmillaryState.TESTED) && !matrixState.equals(ArmillaryState.PREPARED)) return null;

        if (biggerRing != null && !biggerRing.isStopped()) return null;

        List<Aspect> aspects = animatable.getCurrentAspects();
        if (aspects == null || aspects.size() < ringId + 1) return null;

        int aspectId = aspects.get(ringId).getRuneId();
        centerTick = aspectId + TICKS_PER_RUNE / 2;

        if (trackedTick == null && (original < aspectId - 0.5d || original > aspectId + TICKS_PER_RUNE + 0.5d)) {
            return original;
        }

        if (trackedTick == null) trackedTick = original;
        if (targetTick == null) targetTick = trackedTick;
        double centerDiff = centerTick - targetTick;

        if (centerDiff < 0.05d && centerDiff > -0.05d) {
            stopped = true;
            return trackedTick;
        }

        double targetDiff = targetTick - trackedTick;
        if (targetDiff < 0.05d && targetDiff > -0.05d) {
            targetTick += 1.2d * centerDiff;
            targetDiff = targetTick - trackedTick;
        }

        trackedTick += animationSpeedModifier.apply(animatable) * Math.signum(targetDiff);

        return trackedTick;

    }
}

//        if (!impulsePlayed) {
//            double startTick = aspectId - 5.5d;
//            startTick = startTick >= 0 ? startTick : 40 * ArmillaryMatrixBlockEntity.TICKS_PER_RUNE + startTick;
//            if (original >= startTick) {
//                impulsePlayed = true;
//                animatable.triggerAnim(impulseName);
//            }
//        }
