package ru.feytox.etherology.animation.armPoses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.client.model.ModelPart;
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
        original.translate(bone.pivot);
        original.rotate(new Vector3f(bone.pitch, bone.yaw, bone.roll));
        original.xScale *= bone.scale.x;
        original.yScale *= bone.scale.y;
        original.zScale *= bone.scale.z;
    }

    @NoArgsConstructor
    @Getter
    public static class Bone {
        public float pitch = 0.0f;
        public float yaw = 0.0f;
        public float roll = 0.0f;
        public Vector3f pivot = new Vector3f(0.0f);
        public Vector3f scale = new Vector3f(1.0f);
    }
}
