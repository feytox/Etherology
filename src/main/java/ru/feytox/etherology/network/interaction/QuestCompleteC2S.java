package ru.feytox.etherology.network.interaction;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.gui.teldecore.data.Chapter;
import ru.feytox.etherology.network.util.AbstractC2SPacket;
import ru.feytox.etherology.registry.misc.RegistriesRegistry;
import ru.feytox.etherology.util.misc.EIdentifier;

public record QuestCompleteC2S(Identifier chapterId) implements AbstractC2SPacket {

    public static final Id<QuestCompleteC2S> ID = new Id<>(EIdentifier.of("quest_complete_c2s"));
    public static final PacketCodec<RegistryByteBuf, QuestCompleteC2S> CODEC = Identifier.PACKET_CODEC.xmap(QuestCompleteC2S::new, QuestCompleteC2S::chapterId).cast();

    public static void receive(QuestCompleteC2S packet, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();

        context.server().execute(() -> {
            Chapter chapter = player.getWorld().getRegistryManager().get(RegistriesRegistry.CHAPTERS).get(packet.chapterId);
            if (chapter != null) chapter.tryCompleteQuest(player, packet.chapterId);
            else Etherology.ELOGGER.error("Could not find chapter {}", packet.chapterId.toString());
        });
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
