package ru.feytox.etherology.registry.misc;

import lombok.experimental.UtilityClass;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import ru.feytox.etherology.registry.item.EItems;

@UtilityClass
public class TradeOffersModificationRegistry {

    public static void registerAll() {
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.TOOLSMITH, 2, factories ->
                factories.add(new TradeOffers.SellItemFactory(EItems.TRADITIONAL_PATTERN_TABLET, 12, 1, 8, 1)));
    }
}
