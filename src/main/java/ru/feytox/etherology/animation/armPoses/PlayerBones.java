package ru.feytox.etherology.animation.armPoses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import org.joml.Vector3f;

public class PlayerBones {
    public final Bone head = new Bone();
    public final Bone hat = new Bone();
    public final Bone body = new Bone();
    public final Bone rightArm = new Bone();
    public final Bone leftArm = new Bone();
    public final Bone rightLeg = new Bone();
    public final Bone leftLeg = new Bone();

    public void applyToModel(BipedEntityModel<?> model) {
        applyToPart(model.head, head);
        applyToPart(model.hat, hat);
        applyToPart(model.body, body);
        applyToPart(model.rightArm, rightArm);
        applyToPart(model.leftArm, leftArm);
        applyToPart(model.rightLeg, rightLeg);
        applyToPart(model.leftLeg, leftLeg);
    }

    private static void applyToPart(ModelPart original, Bone bone) {
        original.setTransform(bone.asTransform(original.pivotX, original.pivotY, original.pivotZ));
        original.xScale = bone.scale.x;
        original.yScale = bone.scale.y;
        original.zScale = bone.scale.z;
    }

    @NoArgsConstructor
    @Getter
    public static class Bone {
        private final FloatValue pivotX = new FloatValue();
        private final FloatValue pivotY = new FloatValue();
        private final FloatValue pivotZ = new FloatValue();
        private final FloatValue pitch = new FloatValue();
        private final FloatValue yaw = new FloatValue();
        private final FloatValue roll = new FloatValue();
        private final Vector3f scale = new Vector3f(1.0f, 1.0f, 1.0f);

        public ModelTransform asTransform(float dPivotX, float dPivotY, float dPivotZ) {
            return ModelTransform.of(pivotX.getOrDefault(dPivotX), pivotY.getOrDefault(dPivotY), pivotZ.getOrDefault(dPivotZ), pitch.getOrDefault(0), yaw.getOrDefault(0), roll.getOrDefault(0));
        }
    }

    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FloatValue {
        private Float value = null;

        public void increment(float value) {
            if (this.value == null) this.value = value;
            else this.value += value;
        }

        public void multiply(float value) {
            if (this.value == null) this.value = value;
             else this.value *= value;
        }

        public float getOrDefault(float defaultVal) {
            return value == null ? defaultVal : value;
        }
    }
}
