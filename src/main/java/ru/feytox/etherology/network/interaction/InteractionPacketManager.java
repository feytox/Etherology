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
        builder.put(TwoHandHeldAttackC2S.TWOHANDHELD_ATTACK_C2S_ID, TwoHandHeldAttackC2S::receive);
        builder.put(HammerMiningC2S.HAMMER_MINING_C2S_ID, HammerMiningC2S::receive);
    }

    @Override
    public void registerS2C(ImmutableMap.Builder<Identifier, AbstractS2CPacket.S2CHandler> builder) {
        builder.put(TwoHandHeldAttackS2C.TWOHANDHELD_ATTACK_S2C_ID, TwoHandHeldAttackS2C::receive);
        builder.put(HammerPealWaveS2C.HAMMER_PEAL_S2C_ID, HammerPealWaveS2C::receive);
        builder.put(SmallLightningS2C.ID, SmallLightningS2C::receive);
        builder.put(RemoveBlockEntityS2C.ID, RemoveBlockEntityS2C::receive);
    }
}
