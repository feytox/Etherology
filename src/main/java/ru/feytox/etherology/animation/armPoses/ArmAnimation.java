package ru.feytox.etherology.animation.armPoses;

import dev.kosmx.playerAnim.core.util.Ease;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;

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
    private final boolean transitionAfter;
    private final List<ArmKeyframe> keyFrames;
    @Nullable
    private final Trigger trigger;

    /**
     * Checks the animation condition for a given entity.
     *
     * @param  entity the entity to check the animation condition for
     * @return        true if the animation condition is met, false otherwise
     */
    public boolean checkTrigger(LivingEntity entity) {
        return trigger == null || trigger.test(entity);
    }

    /**
     * Creates a new instance of the animation.
     *
     * @param  prevAnimation  the previous animation instance (nullable)
     * @return                the newly created instance
     */
    public Instance createInstance(@Nullable Instance prevAnimation) {
        return prevAnimation == null ? new Instance(this) : new Instance(this, prevAnimation.playerBones);
    }

    @RequiredArgsConstructor
    public static class Instance {
        @Getter
        private final ArmAnimation animation;
        private final PlayerBones playerBones = new PlayerBones();
        @NonNull
        private PlayerBones oldBones;
        private int currentFrame = 0;
        private float timeMillis = 0.0f;
        private float currentPercent = 0.0f;
        @Setter
        private boolean stopped = false;

        public Instance(ArmAnimation animation) {
            this(animation, new PlayerBones());
        }

        /**
         * Executes a tick of the animation for the given entity model and entity.
         *
         * @param  model      the entity model to animate
         * @param  entity     the living entity being animated
         * @param  timeDelta  the time elapsed since the last tick
         * @return            true if the animation tick was successful, false otherwise
         */
        public boolean tick(BipedEntityModel<?> model, LivingEntity entity, float timeDelta) {
            testTrigger(entity);
            if (isCompleted()) return false;
            if (!tickAnimation(model, entity)) return false;
            playerBones.applyToModel(model, oldBones, currentPercent);
            timeMillis += timeDelta;
            return true;
        }

        /**
         * Checks if the animation is completed.
         *
         * @return  true if the animation is completed, false otherwise
         */
        public boolean isCompleted() {
            return (timeMillis >= animation.lengthMillis && !animation.loop) || stopped;
        }

        /**
         * Checks the animation condition for a given entity.
         *
         * @param  entity the entity to check the animation condition for
         */
        public void testTrigger(LivingEntity entity) {
            if (animation.checkTrigger(entity)) return;
            stopped = true;
        }

        /**
         * Executes a tick animation for a given model and entity.
         *
         * @param  model  the BipedEntityModel representing the model to be animated
         * @param  entity the LivingEntity representing the entity to be animated
         * @return        true if the animation was successfully executed, false otherwise
         */
        private boolean tickAnimation(BipedEntityModel<?> model, LivingEntity entity) {
            List<ArmKeyframe> keyframes = animation.getKeyFrames();
            boolean tickResult = tickKeyframes(model, entity, keyframes);
            if (animation.loop && !tickResult) {
                oldBones = playerBones.copy();
                currentFrame = 0;
                currentPercent = 0.0f;
                timeMillis = 0.0f;
                tickResult = tickKeyframes(model, entity, keyframes);
            }

            return tickResult;
        }

        /**
         * Ticks the keyframes of the given model and updates the current frame and percent.
         *
         * @param  model     the BipedEntityModel to apply the keyframes to
         * @param  entity    the LivingEntity associated with the model
         * @param  keyframes the list of ArmKeyframes to iterate through
         * @return           true if a keyframe was found and applied, false otherwise
         */
        private boolean tickKeyframes(BipedEntityModel<?> model, LivingEntity entity, List<ArmKeyframe> keyframes) {
            int i = currentFrame;
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
        private boolean transitionAfter = true;

        /**
         * Creates a new animation Builder.
         *
         * @param  lengthMillis  the length of the timer in milliseconds
         * @param  priority      the priority of the timer
         * @return               a new Builder object
         */
        public static Builder create(float lengthMillis, int priority) {
            return create(lengthMillis, priority, false);
        }

        /**
         * Creates a new animation Builder.
         *
         * @param  lengthMillis  the length of the Builder in milliseconds
         * @param  priority      the priority of the Builder
         * @param  canReset      whether the Builder can be reset
         * @return               a new Builder object
         */
        public static Builder create(float lengthMillis, int priority, boolean canReset) {
            return new Builder(lengthMillis, priority, canReset);
        }

        /**
         * Sets whether the arms should be animated by vanilla.
         *
         * @param  shouldAnimateArms  true if the arms should be animated, false otherwise
         * @return                    the Builder object for chaining method calls
         */
        public Builder animateArms(boolean shouldAnimateArms) {
            animateArms = shouldAnimateArms;
            return this;
        }

        /**
         * Sets whether the function should loop.
         *
         * @param  shouldLoop  true if the function should loop, false otherwise
         * @return             the Builder object for chaining method calls
         */
        public Builder loop(boolean shouldLoop) {
            loop = shouldLoop;
            return this;
        }
        
        /**
         * Sets whether the function should transition to default state after completed.
         *
         * @param  shouldTransitionAfter  true if the function should loop, false otherwise
         * @return                        the Builder object for chaining method calls
         */
        public Builder transitionAfter(boolean shouldTransitionAfter) {
            transitionAfter = shouldTransitionAfter;
            return this;
        }

        /**
         * Adds a keyframe to the builder.
         *
         * @param  startMillis   the start time of the keyframe in milliseconds
         * @param  bonePoser     the bone poser for the keyframe
         * @return               the Builder object for chaining method calls
         */
        public Builder keyframe(float startMillis, BonePoser bonePoser) {
            return keyframe(startMillis, Ease.LINEAR, bonePoser);
        }

        /**
         * Adds a keyframe to the builder.
         *
         * @param  startMillis  the start time of the keyframe in milliseconds
         * @param  easing       the easing function to be applied to the keyframe
         * @param  bonePoser    the bone poser for the keyframe
         * @return              the Builder object for chaining method calls
         */
        public Builder keyframe(float startMillis, Ease easing, BonePoser bonePoser) {
            keyFrames.add(new PseudoKeyFrame(startMillis, easing, bonePoser));
            return this;
        }

        /**
         * Sets the trigger for the animation.
         *
         * @param  trigger  the trigger to be set
         * @return          the Builder object for chaining method calls
         */
        public Builder trigger(@Nullable Trigger trigger) {
            this.trigger = trigger;
            return this;
        }

        /**
         * Builds an ArmAnimation object.
         *
         * @return         	the ArmAnimation object built
         */
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

            return new ArmAnimation(lengthMillis, priority, loop, canReset, animateArms, transitionAfter, result, trigger);
        }
    }

    @FunctionalInterface
    public interface Trigger {
        boolean test(LivingEntity entity);
    }

    private record PseudoKeyFrame(float startMillis, Ease easing, BonePoser bonePoser) {
        
        /**
         * Creates a new ArmKeyframe with the given end time.
         *
         * @param  endMillis the end time in milliseconds
         * @return           the newly created ArmKeyframe
         */
        public ArmKeyframe complete(float endMillis) {
            return new ArmKeyframe(bonePoser, easing, startMillis, endMillis);
        }
    }
}
