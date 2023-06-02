package ru.feytox.etherology.network.interaction;

import lombok.AllArgsConstructor;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.item.HammerItem;
import ru.feytox.etherology.mixin.ServerPlayerInteractionManagerAccessor;
import ru.feytox.etherology.network.EtherologyNetwork;
import ru.feytox.etherology.network.animation.PlayerAnimationS2C;
import ru.feytox.etherology.network.util.AbstractC2SPacket;
import ru.feytox.etherology.network.util.C2SPacketInfo;
import ru.feytox.etherology.util.feyapi.EIdentifier;

@AllArgsConstructor
public class HammerMiningC2S extends AbstractC2SPacket {
    public static final Identifier HAMMER_MINING_C2S_ID = new EIdentifier("hammer_mining_c2s");

    private final boolean isRightArm;

    public static void receive(C2SPacketInfo packetInfo) {
        boolean isRightArm = packetInfo.buf().readBoolean();
        ServerPlayerEntity player = packetInfo.player();
        MinecraftServer server = packetInfo.server();
        ServerWorld world = player.getWorld();

        server.execute(() -> {
            if (!HammerItem.checkHammer(player)) return;
            String prefix = isRightArm ? "right" : "left";
            Identifier animationId = new EIdentifier(prefix + "_hammer_hit_weak");

            ServerPlayerInteractionManager interactionManager = player.interactionManager;
            if (!((ServerPlayerInteractionManagerAccessor) interactionManager).isMining()) return;

            PlayerAnimationS2C packet = new PlayerAnimationS2C(player, animationId, 0, null);
            EtherologyNetwork.sendForTracking(world, player.getBlockPos(), player.getId(), packet);
        });
    }

    @Override
    public PacketByteBuf encode(PacketByteBuf buf) {
        buf.writeBoolean(isRightArm);
        return buf;
    }

    @Override
    public Identifier getPacketID() {
        return HAMMER_MINING_C2S_ID;
    }
}
