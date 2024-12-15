package ru.feytox.etherology.item;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import lombok.val;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import ru.feytox.etherology.registry.item.ArmorItems;
import ru.feytox.etherology.util.misc.EtherProxy;

public class RevelationViewItem extends TrinketItem {

    public RevelationViewItem() {
        super(new Settings().maxCount(1));
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        World world = entity.getWorld();
        if (world == null || !world.isClient) return;
        if (!(entity instanceof PlayerEntity player)) return;

        EtherProxy.getInstance().tickRevelationView(world, player);
    }

    public static boolean isEquipped(LivingEntity entity) {
        val trinket = TrinketsApi.getTrinketComponent(entity).orElse(null);
        if (trinket == null) return false;
        return trinket.isEquipped(ArmorItems.REVELATION_VIEW);
    }
}
