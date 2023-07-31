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

    /**
     * Applies the given transformations to the specified parts of a BipedEntityModel.
     *
     * @param  model     the BipedEntityModel to apply the transformations to
     * @param  oldBones  the PlayerBones object representing the old bone positions
     * @param  percent   the percentage of the transformation to apply
     */
    public void applyToModel(BipedEntityModel<?> model, PlayerBones oldBones, float percent) {
        applyToPart(model.head, head, oldBones.head, percent);
        applyToPart(model.hat, hat, oldBones.hat, percent);
        applyToPart(model.body, body, oldBones.body, percent);
        applyToPart(model.rightArm, rightArm, oldBones.rightArm, percent);
        applyToPart(model.leftArm, leftArm, oldBones.leftArm, percent);
        applyToPart(model.rightLeg, rightLeg, oldBones.rightLeg, percent);
        applyToPart(model.leftLeg, leftLeg, oldBones.leftLeg, percent);
    }

    /**
     * Creates a copy of the PlayerBones object.
     *
     * @return         	A new PlayerBones object that is a copy of the original.
     */
    @CheckReturnValue
    public PlayerBones copy() {
        return new PlayerBones(head.copy(), hat.copy(), body.copy(), rightArm.copy(), leftArm.copy(), rightLeg.copy(), leftLeg.copy());
    }

    /**
     * Applies the transformation of a bone to a model part.
     *
     * @param  original  the original model part
     * @param  bone      the bone to apply
     * @param  oldBone   the previous bone state
     * @param  percent   the percentage of the transformation
     */
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

        /**
         * Creates a copy of the Bone object.
         *
         * @return         	a new Bone object that is an exact copy of the original Bone object
         */
        @CheckReturnValue
        public Bone copy() {
            return new Bone(pitch, yaw, roll, pivot, new Vector3f(0.0f).add(scale));
        }
    }
}
