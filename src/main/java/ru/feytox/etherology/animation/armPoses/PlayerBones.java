package ru.feytox.etherology.animation.armPoses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import org.joml.Vector3f;

import javax.annotation.CheckReturnValue;

import static net.minecraft.util.math.MathHelper.lerp;

@AllArgsConstructor
public class PlayerBones {
    public final Bone head;
    public final Bone hat;
    public final Bone body;
    public final Bone rightArm;
    public final Bone leftArm;
    public final Bone rightLeg;
    public final Bone leftLeg;

    public PlayerBones() {
        this(new Bone(), new Bone(), new Bone(), new Bone(), new Bone(), new Bone(), new Bone());
    }

    public void applyToModel(BipedEntityModel<?> model, PlayerBones oldBones, float percent) {
        applyToPart(model.head, head, oldBones.head, percent);
        applyToPart(model.hat, hat, oldBones.hat, percent);
        applyToPart(model.body, body, oldBones.body, percent);
        applyToPart(model.rightArm, rightArm, oldBones.rightArm, percent);
        applyToPart(model.leftArm, leftArm, oldBones.leftArm, percent);
        applyToPart(model.rightLeg, rightLeg, oldBones.rightLeg, percent);
        applyToPart(model.leftLeg, leftLeg, oldBones.leftLeg, percent);
    }

    @CheckReturnValue
    public PlayerBones copy() {
        return new PlayerBones(head.copy(), hat.copy(), body.copy(), rightArm.copy(), leftArm.copy(), rightLeg.copy(), leftLeg.copy());
    }

    private static void applyToPart(ModelPart original, Bone bone, Bone oldBone, float percent) {
        original.translate(oldBone.pivot.add(bone.pivot.sub(oldBone.pivot).mul(percent)).mul(-1));
        original.rotate(new Vector3f(lerp(percent, oldBone.pitch, bone.pitch), lerp(percent, oldBone.yaw, bone.yaw), lerp(percent, oldBone.roll, bone.roll)));
        original.xScale *= lerp(percent, oldBone.scale.x, bone.scale.x);
        original.yScale *= lerp(percent, oldBone.scale.y, bone.scale.y);
        original.zScale *= lerp(percent, oldBone.scale.z, bone.scale.z);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Bone {
        public float pitch = 0.0f;
        public float yaw = 0.0f;
        public float roll = 0.0f;
        public Vector3f pivot = new Vector3f(0.0f);
        public Vector3f scale = new Vector3f(1.0f);

        @CheckReturnValue
        public Bone copy() {
            return new Bone(pitch, yaw, roll, pivot, new Vector3f(0.0f).add(scale));
        }
    }
}
