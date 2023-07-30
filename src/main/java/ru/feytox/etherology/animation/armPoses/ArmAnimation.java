package ru.feytox.etherology.animation.armPoses;

import dev.kosmx.playerAnim.core.util.Ease;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import ru.feytox.etherology.mixin.MinecraftClientAccessor;
import ru.feytox.etherology.mixin.RenderTickCounterAccessor;
import ru.feytox.etherology.util.feyapi.EtherologyPlayer;

import javax.annotation.Nullable;
import java.util.List;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ArmAnimation {

    private final float lengthMillis;
    private final int priority;
    private final boolean loop;
    private final boolean canReset;
    private final boolean animateArms;
    private final List<ArmKeyframe> keyFrames;
    @Nullable
    private final Trigger trigger;

    public boolean checkTrigger(LivingEntity entity) {
        return trigger == null || trigger.test(entity);
    }

    public static void tickCurrentAnimation(BipedEntityModel<?> model, LivingEntity entity) {
        if (!(entity instanceof EtherologyPlayer player)) return;
        val animation = player.etherology$getCurrentArmAnimation();
        if (animation == null) return;

        if (animation.isCompleted()) {
            player.etherology$setCurrentArmAnimation(null);
            return;
        }

        val client = MinecraftClient.getInstance();
        val tickCounter = ((MinecraftClientAccessor) client).getRenderTickCounter();
        float tickDelta = tickCounter.lastFrameDuration * ((RenderTickCounterAccessor) tickCounter).getTickTime();
        animation.tick(model, entity, client.isPaused() ? 0.0f : tickDelta);
    }

    public static void testAndInject(ArmAnimation animation, LivingEntity entity) {
        if (!animation.checkTrigger(entity)) return;
        tryInjectAnimation(animation, entity);
    }

    public static void tryInjectAnimation(ArmAnimation animation, LivingEntity entity) {
        if (!(entity instanceof EtherologyPlayer player)) return;
        val currentAnimation = player.etherology$getCurrentArmAnimation();
        if (currentAnimation == null || currentAnimation.isCompleted()) {
            player.etherology$setCurrentArmAnimation(animation.createInstance());
            return;
        }

        if (currentAnimation.animation.equals(animation)) {
            if (currentAnimation.animation.canReset) {
                currentAnimation.setStopped(true);
                player.etherology$setCurrentArmAnimation(animation.createInstance());
            }
            return;
        }

        if (currentAnimation.animation.priority >= animation.priority) return;
        currentAnimation.setStopped(true);
        player.etherology$setCurrentArmAnimation(animation.createInstance());
    }

    public Instance createInstance() {
        return new Instance(this);
    }

    @RequiredArgsConstructor
    public static class Instance {
        @Getter
        private final ArmAnimation animation;
        private final PlayerBones playerBones = new PlayerBones();
        private int currentFrame = 0;
        private PlayerBones oldBones = new PlayerBones();
        private float timeMillis = 0.0f;
        private float currentPercent = 0.0f;
        @Setter
        private boolean stopped = false;

        public boolean tick(BipedEntityModel<?> model, LivingEntity entity, float timeDelta) {
            testTrigger(entity);
            if (isCompleted()) return false;
            if (!tickAnimation(model, entity)) return false;
            playerBones.applyToModel(model, oldBones, currentPercent);
            timeMillis += timeDelta;
            return true;
        }

        public boolean isCompleted() {
            return (timeMillis >= animation.lengthMillis && !animation.loop) || stopped;
        }

        public void testTrigger(LivingEntity entity) {
            if (animation.checkTrigger(entity)) return;
            stopped = true;
        }

        private boolean tickAnimation(BipedEntityModel<?> model, LivingEntity entity) {
            List<ArmKeyframe> keyframes = animation.getKeyFrames();
            int i = currentFrame;
            boolean tickResult = tickKeyframes(model, entity, i, keyframes);
            if (animation.loop && !tickResult) {
                oldBones = playerBones.copy();
                currentFrame = 0;
                currentPercent = 0.0f;
                timeMillis = 0.0f;
                tickResult = tickKeyframes(model, entity, 0, keyframes);
            }

            return tickResult;
        }

        private boolean tickKeyframes(BipedEntityModel<?> model, LivingEntity entity, int i, List<ArmKeyframe> keyframes) {
            while (i < keyframes.size()) {
                ArmKeyframe keyframe = keyframes.get(i);
                if (keyframe.isKeyframe(timeMillis)) {
                    if (i != currentFrame) oldBones = playerBones.copy();
                    currentFrame = i;
                    currentPercent = keyframe.tickKeyframe(timeMillis, model, playerBones, entity);
                    return true;
                }
                i++;
            }
            return false;
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder {
        private final float lengthMillis;
        private final int priority;
        private final boolean canReset;
        private final List<PseudoKeyFrame> keyFrames = new ObjectArrayList<>();
        private Trigger trigger = null;
        private boolean animateArms = true;
        private boolean loop = false;

        public static Builder create(float lengthMillis, int priority) {
            return create(lengthMillis, priority, false);
        }

        public static Builder create(float lengthMillis, int priority, boolean canReset) {
            return new Builder(lengthMillis, priority, canReset);
        }

        public Builder animateArms(boolean shouldAnimateArms) {
            animateArms = shouldAnimateArms;
            return this;
        }

        public Builder loop(boolean shouldLoop) {
            loop = shouldLoop;
            return this;
        }

        public Builder keyframe(float startMillis, BonePoser bonePoser) {
            return keyframe(startMillis, Ease.LINEAR, bonePoser);
        }

        public Builder keyframe(float startMillis, Ease easing, BonePoser bonePoser) {
            keyFrames.add(new PseudoKeyFrame(startMillis, easing, bonePoser));
            return this;
        }

        public Builder trigger(@Nullable Trigger trigger) {
            this.trigger = trigger;
            return this;
        }

        public ArmAnimation build() {
            List<ArmKeyframe> result = new ObjectArrayList<>();
            for (int i = 0; i < keyFrames.size(); i++) {
                PseudoKeyFrame keyFrame = keyFrames.get(i);
                if (i + 1 >= keyFrames.size()) {
                    result.add(keyFrame.complete(lengthMillis));
                    continue;
                }
                PseudoKeyFrame nextFrame = keyFrames.get(i + 1);
                result.add(keyFrame.complete(nextFrame.startMillis));
            }

            return new ArmAnimation(lengthMillis, priority, loop, canReset, animateArms, result, trigger);
        }
    }

    @FunctionalInterface
    public interface Trigger {
        boolean test(LivingEntity entity);
    }

    private record PseudoKeyFrame(float startMillis, Ease easing, BonePoser bonePoser) {
        public ArmKeyframe complete(float endMillis) {
            return new ArmKeyframe(bonePoser, easing, startMillis, endMillis);
        }
    }
}
