package name.uwu.feytox.etherology.particle.utility;

import name.uwu.feytox.etherology.particle.ElectricityParticle;
import name.uwu.feytox.etherology.util.EIdentifier;
import name.uwu.feytox.etherology.util.FVec3d;
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
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;
import java.util.List;

public class SmallLightning {
    public static final EIdentifier SMALL_LIGHTNING_PACKET_ID = new EIdentifier("small_lightning_packet");

    private final FVec3d startPos;
    private List<ParticleLine> particleLines = new ArrayList<>();
    private final FVec3d endPos;
    private final int lineCount;
    private boolean shouldDamage = false;
    private Entity target = null;

    public SmallLightning(Vec3d startPos, Vec3d endPos, int lineCount) {
        this.startPos = FVec3d.of(startPos);
        this.endPos = FVec3d.of(endPos);
        this.lineCount = lineCount;
    }

    public SmallLightning(Vec3d startPos, Vec3d endPos, int lineCount, List<ParticleLine> particleLines) {
        this(startPos, endPos, lineCount);
        this.particleLines = particleLines;
    }

    public SmallLightning(Vec3d startPos, Entity target, int lineCount, boolean shouldDamage) {
        this.startPos = FVec3d.of(startPos);
        this.lineCount = lineCount;
        this.shouldDamage = shouldDamage;
        this.target = target;

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
    }

    public void write(PacketByteBuf buf) {
        startPos.write(buf);
        endPos.write(buf);
        buf.writeInt(lineCount);
        particleLines.forEach(particleLine -> particleLine.write(buf));
    }

    public static SmallLightning read(PacketByteBuf buf) {
        FVec3d startPos = FVec3d.read(buf);
        FVec3d endPos = FVec3d.read(buf);
        int lineCount = buf.readInt();

        List<ParticleLine> particleLines = new ArrayList<>();
        for (int i = 0; i < lineCount; i++) {
            ParticleLine particleLine = ParticleLine.read(buf, ElectricityParticle.getParticleType());
            particleLines.add(particleLine);
        }

        return new SmallLightning(startPos, endPos, lineCount, particleLines);
    }

    public static void registerPacket() {
        ClientPlayNetworking.registerGlobalReceiver(SMALL_LIGHTNING_PACKET_ID, ((client, handler, buf, responseSender) -> {
            SmallLightning smallLightning = SmallLightning.read(buf);

            client.execute(() -> smallLightning.spawnLines(client.world));
        }));
    }

    public void spawnLines(ClientWorld world) {
        particleLines.forEach(particleLine -> particleLine.spawn(world));
    }
}
