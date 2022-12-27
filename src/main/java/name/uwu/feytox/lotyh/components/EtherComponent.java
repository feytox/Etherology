package name.uwu.feytox.lotyh.components;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import name.uwu.feytox.lotyh.LotyhComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import static name.uwu.feytox.lotyh.LotyhComponents.ETHER_MAX;

public class EtherComponent extends FloatComponent implements AutoSyncedComponent {
    public EtherComponent(PlayerEntity player, float def_value) {
        super(player, def_value);
    }

    @Override
    public void setValue(float value) {
        super.setValue(value);
        float min = 0;
        float max = player.getComponent(ETHER_MAX).getValue();
        this.value = Math.max(this.value, min);
        this.value = Math.min(this.value, max);
        LotyhComponents.ETHER_POINTS.sync(player);
    }

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return this.player == player;
    }
}
