package ru.feytox.etherology.particle.utility;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import ru.feytox.etherology.particle.ElectricityParticle;
import ru.feytox.etherology.util.feyapi.EIdentifier;
import ru.feytox.etherology.util.feyapi.FVec3d;

import java.util.ArrayList;
import java.util.List;

import static ru.feytox.etherology.registry.util.EtherSounds.ELECTRICITY_SOUND_EVENT;

public class SmallLightning {
    public static final EIdentifier SMALL_LIGHTNING_PACKET_ID = new EIdentifier("small_lightning_packet");

    private final FVec3d startPos;
    private List<ParticleLine> particleLines = new ArrayList<>();
    private final FVec3d endPos;
    private final int lineCount;
    private final float instability;
    private boolean shouldDamage = false;
    private Entity target = null;

    public SmallLightning(Vec3d startPos, Vec3d endPos, int lineCount, float instability) {
        this.startPos = FVec3d.of(startPos);
        this.endPos = FVec3d.of(endPos);
        this.lineCount = lineCount;
        this.instability = instability;
    }

    public SmallLightning(Vec3d startPos, Vec3d endPos, int lineCount, float instability, List<ParticleLine> particleLines) {
        this(startPos, endPos, lineCount, instability);
        this.particleLines = particleLines;
    }

    public SmallLightning(Vec3d startPos, Entity target, int lineCount, boolean shouldDamage, float instability) {
        this.startPos = FVec3d.of(startPos);
        this.lineCount = lineCount;
        this.shouldDamage = shouldDamage;
        this.target = target;
        this.instability = instability;

        Box box = target.getBoundingBox();
        double targetX = (box.minX + box.maxX) / 2;
        double targetY = (box.minY + box.maxY) / 2;
        double targetZ = (box.minZ + box.maxZ) / 2;
        this.endPos = new FVec3d(targetX, targetY, targetZ);
    }

    public void spawn(ServerWorld world) {
        if (particleLines.isEmpty()) generateLines();
        sendToClients(world);
    }

    public void generateLines(Random random) {
        Vec3d startPos = this.startPos;
        Vec3d endPos = this.startPos;
        Vec3d lineVec = new Vec3d(this.endPos.x - startPos.x, this.endPos.y - startPos.y, this.endPos.z - startPos.z);
        double lineVecLen = lineVec.length();

        for (int i = 0; i < lineCount; i++) {
            startPos = endPos;

            // получение вектора рандомной длины или 0.33 от всего
            Vec3d toEndPos = new Vec3d(this.endPos.x - startPos.x, this.endPos.y - startPos.y, this.endPos.z - startPos.z);
            double delta = Math.min(0.4, toEndPos.length() / lineVecLen);
            toEndPos = toEndPos.multiply(delta == 0.4 ? delta * random.nextDouble() : delta);

            // рандомный поворот вектора
            toEndPos = toEndPos.rotateX((float) (Math.PI * (random.nextBetween(-250, 250) / 1000f)));
            toEndPos = toEndPos.rotateY((float) (Math.PI * (random.nextBetween(-50, 0) / 1000f)));
            toEndPos = toEndPos.rotateZ((float) (Math.PI * (random.nextBetween(-250, 250) / 1000f)));

            endPos = startPos.add(toEndPos);

            if (i == 0) {
                startPos = this.startPos;
            } else if (i == lineCount -1) {
                endPos = this.endPos;
            }

            particleLines.add(new ParticleLine(ElectricityParticle.getParticleType(random), startPos, endPos, 0.1f));
        }
    }

    public void generateLines() {
        generateLines(Random.create());
    }

    public void tryDamage(ServerWorld world) {
        if (shouldDamage && target != null) {
            target.damage(DamageSource.LIGHTNING_BOLT, 1.0f);
        }
    }

    public void sendToClients(ServerWorld world) {
        PacketByteBuf buf = PacketByteBufs.create();
        this.write(buf);

        for (ServerPlayerEntity player : PlayerLookup.around(world, startPos, 100)) {
            ServerPlayNetworking.send(player, SMALL_LIGHTNING_PACKET_ID, buf);
        }

        tryDamage(world);

        world.playSound(
                null,
                new BlockPos(startPos),
                ELECTRICITY_SOUND_EVENT,
                SoundCategory.BLOCKS,
                0.1f,
                1f
        );
    }

    public void write(PacketByteBuf buf) {
        startPos.write(buf);
        endPos.write(buf);
        buf.writeInt(lineCount);
        buf.writeFloat(instability);
        particleLines.forEach(particleLine -> particleLine.write(buf));
    }

    public static SmallLightning read(PacketByteBuf buf) {
        FVec3d startPos = FVec3d.read(buf);
        FVec3d endPos = FVec3d.read(buf);
        int lineCount = buf.readInt();
        float instability = buf.readFloat();

        List<ParticleLine> particleLines = new ArrayList<>();
        for (int i = 0; i < lineCount; i++) {
            Random random = Random.create();
            ParticleLine particleLine = ParticleLine.read(buf, ElectricityParticle.getParticleType(random));
            particleLines.add(particleLine);
        }

        return new SmallLightning(startPos, endPos, lineCount, instability, particleLines);
    }

    public static void registerPacket() {
        ClientPlayNetworking.registerGlobalReceiver(SMALL_LIGHTNING_PACKET_ID, ((client, handler, buf, responseSender) -> {
            SmallLightning smallLightning = SmallLightning.read(buf);
            if (client.world == null) return;
            smallLightning.spawnLines(client.world);
        }));
    }

    public void spawnLines(ClientWorld world) {
        particleLines.forEach(particleLine -> particleLine.spawn(world, instability, 20, 0));
    }
}
