package ru.feytox.etherology.network.interaction;

import lombok.RequiredArgsConstructor;
import lombok.val;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.network.util.AbstractS2CPacket;
import ru.feytox.etherology.network.util.S2CPacketInfo;
import ru.feytox.etherology.particle.effects.SimpleParticleEffect;
import ru.feytox.etherology.particle.effects.args.SimpleArgs;
import ru.feytox.etherology.registry.particle.EtherParticleTypes;
import ru.feytox.etherology.util.deprecated.EVec3d;
import ru.feytox.etherology.util.misc.EIdentifier;

@RequiredArgsConstructor
public class RedstoneLensStreamS2C extends AbstractS2CPacket {

    public static final EIdentifier ID = new EIdentifier("redstone_lens_stream_s2c");

    private final Vec3d startPos;
    private final Vec3d endPos;
    private final boolean isMiss;

    public static void receive(S2CPacketInfo packetInfo) {
        PacketByteBuf buf = packetInfo.buf();
        Vec3d startPos = SimpleArgs.readVec3d(buf);
        Vec3d endPos = SimpleArgs.readVec3d(buf);
        boolean isMiss = buf.readBoolean();
        MinecraftClient client = packetInfo.client();

        client.execute(() -> {
            ClientWorld world = client.world;
            if (world == null) return;

            val effect = new SimpleParticleEffect(EtherParticleTypes.REDSTONE_FLASH);
            EVec3d.lineOf(startPos, endPos, 0.5d).forEach(pos -> effect.spawnParticles(world, 1, 0, pos));
            if (isMiss) return;

            val redstoneEffect = new SimpleParticleEffect(EtherParticleTypes.REDSTONE_STREAM);
            redstoneEffect.spawnParticles(world, world.random.nextBetween(8, 12), 0.1, endPos);
        });
    }

    @Override
    public PacketByteBuf encode(PacketByteBuf buf) {
        SimpleArgs.writeVec3d(buf, startPos);
        SimpleArgs.writeVec3d(buf, endPos);
        buf.writeBoolean(isMiss);
        return buf;
    }

    @Override
    public Identifier getPacketID() {
        return ID;
    }
}
