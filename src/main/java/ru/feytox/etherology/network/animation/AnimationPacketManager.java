package ru.feytox.etherology.network.animation;

import com.google.common.collect.ImmutableList;
import ru.feytox.etherology.network.util.AbstractPacketManager;
import ru.feytox.etherology.network.util.AbstractS2CPacket;

public class AnimationPacketManager extends AbstractPacketManager {

    @Override
    public void registerS2C(ImmutableList.Builder<AbstractS2CPacket> builder) {
        builder.add(PlayerAnimationS2C.dummy());
    }
}
