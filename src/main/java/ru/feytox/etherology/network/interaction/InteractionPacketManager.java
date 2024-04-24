package ru.feytox.etherology.network.interaction;

import com.google.common.collect.ImmutableMap;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.network.util.AbstractC2SPacket;
import ru.feytox.etherology.network.util.AbstractPacketManager;
import ru.feytox.etherology.network.util.AbstractS2CPacket;

public class InteractionPacketManager extends AbstractPacketManager {
    public static final InteractionPacketManager INSTANCE = new InteractionPacketManager();

    private InteractionPacketManager() {}

    @Override
    public void registerC2S(ImmutableMap.Builder<Identifier, AbstractC2SPacket.C2SHandler> builder) {
        builder.put(StaffMenuSelectionC2S.ID, StaffMenuSelectionC2S::receive);
        builder.put(StaffTakeLensC2S.ID, StaffTakeLensC2S::receive);
    }

    @Override
    public void registerS2C(ImmutableMap.Builder<Identifier, AbstractS2CPacket.S2CHandler> builder) {
        builder.put(RemoveBlockEntityS2C.ID, RemoveBlockEntityS2C::receive);
        builder.put(RedstoneLensStreamS2C.ID, RedstoneLensStreamS2C::receive);
    }
}
