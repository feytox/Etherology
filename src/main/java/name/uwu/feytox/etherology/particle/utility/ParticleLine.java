package name.uwu.feytox.etherology.particle.utility;

import name.uwu.feytox.etherology.util.FVec3d;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class ParticleLine {
    private final FVec3d startPos;
    private final FVec3d endPos;
    private final ParticleEffect particleType;
    private final float step;

    public ParticleLine(ParticleEffect particleType, Vec3d startPos, Vec3d endPos, float step) {
        this.startPos = FVec3d.of(startPos);
        this.endPos = FVec3d.of(endPos);
        this.particleType = particleType;
        this.step = step;
    }

    public void spawn(ClientWorld world) {
        double vx = endPos.x - startPos.x;
        double vy = endPos.y - startPos.y;
        double vz = endPos.z - startPos.z;
        Vec3d vec = new Vec3d(vx, vy, vz);

        Vec3d stepVec = vec.multiply(step / vec.length());

        for (int i = 1; i <= MathHelper.ceil(vec.length() / step); i++) {
            Vec3d particlePos = stepVec.multiply(i);

            world.addParticle(particleType, startPos.x + particlePos.x, startPos.y + particlePos.y,
                    startPos.z + particlePos.z, 0, 0, 0);
        }
    }

    public void write(PacketByteBuf buf) {
        startPos.write(buf);
        endPos.write(buf);
        buf.writeFloat(step);
    }

    public static ParticleLine read(PacketByteBuf buf, ParticleEffect particleType) {
        FVec3d startPos = FVec3d.read(buf);
        FVec3d endPos = FVec3d.read(buf);
        float step = buf.readFloat();
        return new ParticleLine(particleType, startPos, endPos, step);
    }
}
