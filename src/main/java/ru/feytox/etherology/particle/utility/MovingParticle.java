package ru.feytox.etherology.particle.utility;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.particle.types.misc.FeyParticleEffect;

public abstract class MovingParticle<T extends FeyParticleEffect<T>> extends FeyParticle<T> {
    public MovingParticle(ClientWorld clientWorld, double x, double y, double z, T parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);
    }

    @Override
    public abstract void tick();

    protected void markPrevPos() {
        prevPosX = x;
        prevPosY = y;
        prevPosZ = z;
    }

    /**
     * Performs a tick to move with static speed.
     */
    protected void simpleMovingTick(float speed, Vec3d endPos, boolean deadOnEnd) {
        if (tickAge()) return;
        markPrevPos();

        Vec3d pathVec = getFullPathVec(endPos);
        double inverseLen = getInverseLen(pathVec);
        if (inverseCheckDeadPos(deadOnEnd, inverseLen)) return;

        Vec3d deltaVec = pathVec.multiply(inverseLen).multiply(speed);
        modifyPos(deltaVec);
    }


    /**
     * Performs a tick to move with acceleration. The speed increases every tick.
     */
    protected void timeAcceleratedMovingTick(float acceleration, float start_speed, Vec3d endPos, boolean deadOnEnd) {
        float speed = start_speed + acceleration * age;
        simpleMovingTick(speed, endPos, deadOnEnd);
    }

    /**
     * Performs a tick to move with acceleration. The speed increases as particle approach the end point.
     */
    protected void acceleratedMovingTick(float speed_k, float start_speed, boolean deadOnEnd, Vec3d endPos) {
        if (tickAge()) return;
        markPrevPos();

        Vec3d pathVec = getPathVec(endPos);
        double pathLen = pathVec.length();
        if (checkDeadPos(deadOnEnd, pathLen)) return;

        double fullPathLen = getFullPathVec(endPos).length();
        double acceleration = (fullPathLen - pathLen) / fullPathLen;
        double delta = speed_k * Math.pow(acceleration + start_speed, 3);

        Vec3d deltaVec = pathVec.multiply(Math.min(delta / pathLen, 1));
        modifyPos(deltaVec);
    }

    protected Vec3d getPathVec(Vec3d endPos) {
        return new Vec3d(endPos.x-x, endPos.y-y, endPos.z-z);
    }

    protected Vec3d getFullPathVec(Vec3d endPos) {
        return new Vec3d(endPos.x-startPos.x, endPos.y-startPos.y, endPos.z-startPos.z);
    }
}
