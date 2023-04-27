package ru.feytox.etherology.network.animation;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.network.util.AbstractPacketManager;

import static ru.feytox.etherology.network.animation.PlayerAnimationS2C.PLAYER_ANIMATION_PACKET_ID;

public class AnimationPacketManager extends AbstractPacketManager {

    @Override
    public void registerS2C(ImmutableMap.Builder<Identifier, ClientPlayNetworking.PlayChannelHandler> builder) {
        builder.put(PLAYER_ANIMATION_PACKET_ID, PlayerAnimationS2C::receive);
    }
}
