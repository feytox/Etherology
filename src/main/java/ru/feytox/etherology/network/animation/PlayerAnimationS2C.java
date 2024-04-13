package ru.feytox.etherology.network.animation;

import dev.kosmx.playerAnim.core.util.Ease;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.animation.AbstractPlayerAnimation;
import ru.feytox.etherology.network.util.AbstractS2CPacket;
import ru.feytox.etherology.network.util.S2CPacketInfo;
import ru.feytox.etherology.registry.custom.EtherologyRegistry;
import ru.feytox.etherology.util.misc.EIdentifier;
import ru.feytox.etherology.util.misc.EtherologyPlayer;

@AllArgsConstructor
public class PlayerAnimationS2C extends AbstractS2CPacket {
    public static final Identifier PLAYER_ANIMATION_S2C_ID = new EIdentifier("player_animation_s2c");

    @NonNull
    private final ServerPlayerEntity relatedPlayer;
    @NonNull
    private final Identifier animationId;
    private final int easeLength;
    @Nullable
    private final Ease ease;

    public static void receive(S2CPacketInfo packetInfo) {
        PacketByteBuf buf = packetInfo.buf();
        MinecraftClient client = packetInfo.client();

        ClientWorld world = client.world;
        if (world == null) return;
        Entity entity = world.getEntityById(buf.readInt());
        Identifier animationId = buf.readIdentifier();
        int easeLength = buf.readInt();
        Ease ease = buf.readEnumConstant(Ease.class);

        client.execute(() -> {
            if (!(entity instanceof EtherologyPlayer animatedPlayer)) return;
            AbstractPlayerAnimation animation = EtherologyRegistry.getAndCast(AbstractPlayerAnimation.class, animationId);
            if (animation == null) return;
            animation.play(animatedPlayer, easeLength, ease);
        });
    }

    @Override
    public PacketByteBuf encode(PacketByteBuf buf) {
        buf.writeInt(relatedPlayer.getId());
        buf.writeIdentifier(animationId);

        if (ease == null) {
            buf.writeInt(0);
            buf.writeEnumConstant(Ease.CONSTANT);
        }
        else {
            buf.writeInt(easeLength);
            buf.writeEnumConstant(ease);
        }
        return buf;
    }

    @Override
    public Identifier getPacketID() {
        return PLAYER_ANIMATION_S2C_ID;
    }
}
