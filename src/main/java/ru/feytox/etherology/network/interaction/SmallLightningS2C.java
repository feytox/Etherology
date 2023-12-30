package ru.feytox.etherology.network.interaction;

import lombok.RequiredArgsConstructor;
import lombok.val;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import ru.feytox.etherology.network.EtherologyNetwork;
import ru.feytox.etherology.network.util.AbstractS2CPacket;
import ru.feytox.etherology.network.util.S2CPacketInfo;
import ru.feytox.etherology.particle.effects.ElectricityParticleEffect;
import ru.feytox.etherology.particle.effects.args.SimpleArgs;
import ru.feytox.etherology.particle.subtypes.ElectricitySubtype;
import ru.feytox.etherology.registry.util.EtherSounds;
import ru.feytox.etherology.util.feyapi.EIdentifier;

import java.util.List;

@RequiredArgsConstructor
public class SmallLightningS2C extends AbstractS2CPacket {

    public static final Identifier ID = new EIdentifier("small_lightning_s2c");

    private final Vec3d startPos;
    private final Vec3d endPos;
    private final int lineCount;
    private final float matrixInstability;

    public static <T extends BlockEntity> void sendForTracking(ServerWorld world, T blockEntity, Vec3d startPos, Vec3d endPos, int lineCount, float matrixInstability) {
        val packet = new SmallLightningS2C(startPos, endPos, lineCount, matrixInstability);
        EtherologyNetwork.sendForTracking(packet, blockEntity);
        tryDamage(world, endPos);
    }

    @Override
    public PacketByteBuf encode(PacketByteBuf buf) {
        SimpleArgs.writeVec3d(buf, startPos);
        SimpleArgs.writeVec3d(buf, endPos);
        buf.writeInt(lineCount);
        buf.writeFloat(matrixInstability);
        return buf;
    }

    @Override
    public Identifier getPacketID() {
        return ID;
    }

    public static void receive(S2CPacketInfo packetInfo) {
        val client = packetInfo.client();
        val buf = packetInfo.buf();
        Vec3d startPos = SimpleArgs.readVec3d(buf);
        Vec3d endPos = SimpleArgs.readVec3d(buf);
        int lineCount = buf.readInt();
        float matrixInstability = buf.readFloat();

        client.execute(() -> {
            ClientWorld world = client.world;
            if (world == null) return;

            spawnLightning(world, startPos, endPos, lineCount, matrixInstability);
            world.playSound(startPos.x, startPos.y, startPos.z, EtherSounds.ELECTRICITY, SoundCategory.BLOCKS, 0.1f, 1.0f, true);
            world.playSound(endPos.x, endPos.y, endPos.z, EtherSounds.ELECTRICITY, SoundCategory.BLOCKS, 0.1f, 1.0f, true);
        });
    }

    private static void spawnLightning(ClientWorld world, Vec3d startPos, Vec3d endPos, int lineCount, float matrixInstability) {
        Random random = world.getRandom();

        for (int i = 0; i < lineCount; i++) {
            if (i == lineCount - 1) {
                spawnLine(world, startPos, endPos, random, matrixInstability);
                return;
            }

            int lines = lineCount - i;
            float percent = (0.9f + 0.2f * random.nextFloat()) / lines;
            Vec3d lineVec = endPos.subtract(startPos)
                    .multiply(percent)
                    .rotateX(getRandomAngle(random, 1/4f))
                    .rotateY(getRandomAngle(random, 1/20f))
                    .rotateZ(getRandomAngle(random, 1/4f));

            Vec3d subEndPos = startPos.add(lineVec);
            spawnLine(world, startPos, subEndPos, random, matrixInstability);
            startPos = subEndPos;
        }
    }

    private static void spawnLine(ClientWorld world, Vec3d startPos, Vec3d endPos, Random random, float matrixInstability) {
        Vec3d lineVec = endPos.subtract(startPos);
        double invertedLen = MathHelper.fastInverseSqrt(lineVec.lengthSquared());
        Vec3d stepVec = lineVec.multiply(0.1 * invertedLen);

        int steps = MathHelper.ceil(1 / (invertedLen * 0.1));
        for (int i = 0; i <= steps; i++) {
            Vec3d particlePos = startPos.add(stepVec.multiply(i));
            val effect = ElectricityParticleEffect.of(random, ElectricitySubtype.SIMPLE, matrixInstability);
            effect.spawnParticles(world, 1, 0, particlePos);
        }
    }

    private static void tryDamage(ServerWorld world, Vec3d damagePos) {
        Box damageBox = new Box(damagePos.subtract(1.0, 1.5, 1.0), damagePos.add(1.0, 1.5, 1.0));
        List<LivingEntity> entities = world.getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class), damageBox, entity -> true);
        entities.forEach(entity -> entity.damage(DamageSource.LIGHTNING_BOLT, 2.0f));
    }

    private static float getRandomAngle(Random random, float piMulti) {
        return MathHelper.PI * (piMulti * (2 * random.nextFloat() - 1));
    }
}
