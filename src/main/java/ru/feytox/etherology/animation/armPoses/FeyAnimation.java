package ru.feytox.etherology.animation.armPoses;

import dev.kosmx.playerAnim.core.util.Ease;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import ru.feytox.etherology.util.feyapi.EtherologyPlayer;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FeyAnimation {

    private final int lengthTicks;
    private final List<FeyKeyframe> keyFrames;
    @Nullable
    private final Trigger trigger;

    public Optional<Instance> testAndTryInject(LivingEntity entity, boolean isRightArm) {
        if (trigger == null) return Optional.empty();
        if (!trigger.test(entity, isRightArm)) return Optional.empty();
        return tryInject(entity);
    }

    public Optional<Instance> tryInject(LivingEntity entity) {
        if (!(entity instanceof EtherologyPlayer player)) return Optional.empty();
        val currentAnimation = player.etherology$getCurrentArmAnimation();
        if (currentAnimation != null && currentAnimation.animation.equals(this) && currentAnimation.isActive()) return Optional.of(currentAnimation);

        val animation = new Instance(this);
        player.etherology$setCurrentArmAnimation(animation);
        return Optional.of(animation);
    }

    public static void stopAnimations(LivingEntity entity) {
        if (!(entity instanceof EtherologyPlayer player)) return;
        player.etherology$setCurrentArmAnimation(null);
    }

    @RequiredArgsConstructor
    public static class Instance {
        private final FeyAnimation animation;
        private final PlayerBones playerBones = new PlayerBones();
        private int currentFrame = 0;
        private int ticks;

        public boolean tick(BipedEntityModel<?> model, LivingEntity entity, boolean isRightArm) {
            if (!isActive()) return false;
            if (!tickAnimation(entity, isRightArm)) return false;
            playerBones.applyToModel(model);
            ticks++;
            return true;
        }

        public boolean isActive() {
            return ticks < animation.lengthTicks;
        }

        private boolean tickAnimation(LivingEntity entity, boolean isRightArm) {
            List<FeyKeyframe> keyframes = animation.getKeyFrames();
            int i = currentFrame;
            while (i < keyframes.size()) {
                FeyKeyframe keyframe = keyframes.get(i);
                if (keyframe.isKeyframe(ticks)) {
                    currentFrame = i;
                    keyframe.tickKeyframe(ticks, playerBones, entity, isRightArm);
                    return true;
                }
                i++;
            }
            return false;
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder {
        private final int lengthTicks;
        private final List<PseudoKeyFrame> keyFrames = new ObjectArrayList<>();
        private Trigger trigger = null;

        public static Builder create(int lengthTicks) {
            return new Builder(lengthTicks);
        }

        public Builder keyframe(int startTick, BonePoser bonePoser) {
            return keyframe(startTick, Ease.LINEAR, bonePoser);
        }

        public Builder keyframe(int startTick, Ease easing, BonePoser bonePoser) {
            keyFrames.add(new PseudoKeyFrame(startTick, easing, bonePoser));
            return this;
        }

        public Builder trigger(@Nullable Trigger trigger) {
            this.trigger = trigger;
            return this;
        }

        public FeyAnimation build() {
            List<FeyKeyframe> result = new ObjectArrayList<>();
            for (int i = 0; i < keyFrames.size(); i++) {
                PseudoKeyFrame keyFrame = keyFrames.get(i);
                if (i + 1 >= keyFrames.size()) {
                    result.add(keyFrame.complete(lengthTicks));
                    continue;
                }
                PseudoKeyFrame nextFrame = keyFrames.get(i + 1);
                result.add(keyFrame.complete(nextFrame.startTick()));
            }

            return new FeyAnimation(lengthTicks, result, trigger);
        }
    }

    @FunctionalInterface
    public interface Trigger {
        boolean test(LivingEntity entity, boolean isRightArm);
    }

    private record PseudoKeyFrame(int startTick, Ease easing, BonePoser bonePoser) {
        public FeyKeyframe complete(int endTick) {
            return new FeyKeyframe(bonePoser, easing, startTick, endTick);
        }
    }
}
