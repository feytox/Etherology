package ru.feytox.etherology.network.animation;

import com.google.common.collect.ImmutableMap;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.network.util.AbstractPacketManager;
import ru.feytox.etherology.network.util.AbstractS2CPacket;

import static ru.feytox.etherology.network.animation.PlayerAnimationS2C.PLAYER_ANIMATION_PACKET_ID;

public class AnimationPacketManager extends AbstractPacketManager {

    // TODO: 05/05/2023 maybe add sync anim packet

    @Override
    public void registerS2C(ImmutableMap.Builder<Identifier, AbstractS2CPacket.S2CHandler> builder) {
        builder.put(PLAYER_ANIMATION_PACKET_ID, PlayerAnimationS2C::receive);
        builder.put(StopBlockAnimS2C.STOP_BLOCK_ANIM_PACKET_ID, StopBlockAnimS2C::receive);
        builder.put(StartBlockAnimS2C.START_BLOCK_ANIM_PACKET_ID, StartBlockAnimS2C::receive);
    }
}
