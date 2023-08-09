package ru.feytox.etherology.network.animation;

import com.google.common.collect.ImmutableMap;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.network.util.AbstractPacketManager;
import ru.feytox.etherology.network.util.AbstractS2CPacket;


public class AnimationPacketManager extends AbstractPacketManager {

    // TODO: 05/05/2023 maybe add sync anim packet

    public static final AnimationPacketManager INSTANCE = new AnimationPacketManager();

    private AnimationPacketManager() {}

    @Override
    public void registerS2C(ImmutableMap.Builder<Identifier, AbstractS2CPacket.S2CHandler> builder) {
        builder.put(PlayerAnimationS2C.PLAYER_ANIMATION_S2C_ID, PlayerAnimationS2C::receive);
        builder.put(StopBlockAnimS2C.STOP_BLOCK_ANIM_PACKET_ID, StopBlockAnimS2C::receive);
        builder.put(StartBlockAnimS2C.START_BLOCK_ANIM_PACKET_ID, StartBlockAnimS2C::receive);
        builder.put(SwitchBlockAnimS2C.SWITCH_BLOCK_ANIM_PACKET_ID, SwitchBlockAnimS2C::receive);
    }
}
