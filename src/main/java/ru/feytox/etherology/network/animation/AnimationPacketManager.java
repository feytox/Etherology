package ru.feytox.etherology.network.animation;

import com.google.common.collect.ImmutableMap;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.network.util.AbstractPacketManager;
import ru.feytox.etherology.network.util.AbstractS2CPacket;


public class AnimationPacketManager extends AbstractPacketManager {

    // TODO: 06.05.2024 try to use .onSyncedBlockEvent (if possible)

    public static final AnimationPacketManager INSTANCE = new AnimationPacketManager();

    private AnimationPacketManager() {}

    @Override
    public void registerS2C(ImmutableMap.Builder<Identifier, AbstractS2CPacket.S2CHandler> builder) {
        builder.put(StopBlockAnimS2C.STOP_BLOCK_ANIM_PACKET_ID, StopBlockAnimS2C::receive);
        builder.put(StartBlockAnimS2C.START_BLOCK_ANIM_PACKET_ID, StartBlockAnimS2C::receive);
        builder.put(SwitchBlockAnimS2C.SWITCH_BLOCK_ANIM_PACKET_ID, SwitchBlockAnimS2C::receive);
        builder.put(Stop2BlockAnimS2C.STOP_2BLOCK_ANIM_PACKET_ID, Stop2BlockAnimS2C::receive);
    }
}
