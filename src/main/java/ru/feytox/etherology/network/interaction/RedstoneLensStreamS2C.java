package ru.feytox.etherology.network.interaction;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.network.util.AbstractS2CPacket;
import ru.feytox.etherology.network.util.S2CPacketInfo;
import ru.feytox.etherology.particle.effects.args.SimpleArgs;
import ru.feytox.etherology.util.deprecated.EVec3d;
import ru.feytox.etherology.util.misc.EIdentifier;

@RequiredArgsConstructor
public class RedstoneLensStreamS2C extends AbstractS2CPacket {

    public static final EIdentifier ID = new EIdentifier("redstone_lens_stream_s2c");

    private final Vec3d startPos;
    private final Vec3d endPos;

    public static void receive(S2CPacketInfo packetInfo) {
        PacketByteBuf buf = packetInfo.buf();
        Vec3d startPos = SimpleArgs.readVec3d(buf);
        Vec3d endPos = SimpleArgs.readVec3d(buf);
        MinecraftClient client = packetInfo.client();

        client.execute(() -> {
            ClientWorld world = client.world;
            if (world == null) return;

            EVec3d.lineOf(startPos, endPos, 0.5d).forEach(pos ->
                    world.addParticle(new DustParticleEffect(DustParticleEffect.RED, 0.5f), pos.x, pos.y, pos.z, 0.0d, 0.0d, 0.0d));
        });
    }

    @Override
    public PacketByteBuf encode(PacketByteBuf buf) {
        SimpleArgs.writeVec3d(buf, startPos);
        SimpleArgs.writeVec3d(buf, endPos);
        return buf;
    }

    @Override
    public Identifier getPacketID() {
        return ID;
    }
}
