package ru.feytox.etherology.network.animation;

import com.google.common.collect.ImmutableMap;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.network.util.AbstractC2SPacket;
import ru.feytox.etherology.network.util.AbstractPacketManager;
import ru.feytox.etherology.network.util.AbstractS2CPacket;


public class AnimationPacketManager extends AbstractPacketManager {

    // TODO: 05/05/2023 maybe add sync anim packet


    @Override
    public void registerC2S(ImmutableMap.Builder<Identifier, AbstractC2SPacket.C2SHandler> builder) {
        builder.put(HammerSwingC2S.HAMMER_SWING_C2S_ID, HammerSwingC2S::receive);
    }

    @Override
    public void registerS2C(ImmutableMap.Builder<Identifier, AbstractS2CPacket.S2CHandler> builder) {
        builder.put(PlayerAnimationS2C.PLAYER_ANIMATION_PACKET_ID, PlayerAnimationS2C::receive);
        builder.put(StopBlockAnimS2C.STOP_BLOCK_ANIM_PACKET_ID, StopBlockAnimS2C::receive);
        builder.put(StartBlockAnimS2C.START_BLOCK_ANIM_PACKET_ID, StartBlockAnimS2C::receive);
        builder.put(HammerSwingS2C.HAMMER_SWING_S2C_ID, HammerSwingS2C::receive);
    }
}
