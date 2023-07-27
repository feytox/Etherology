package ru.feytox.etherology.registry.item;

import lombok.experimental.UtilityClass;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.enums.HammerState;
import ru.feytox.etherology.item.GlaiveItem;
import ru.feytox.etherology.item.HammerItem;
import ru.feytox.etherology.magic.ether.EtherGlint;
import ru.feytox.etherology.util.feyapi.EtherologyPlayer;

import java.util.Arrays;

import static ru.feytox.etherology.registry.item.EItems.GLINT;
import static ru.feytox.etherology.registry.item.ToolItems.GLAIVES;
import static ru.feytox.etherology.registry.item.ToolItems.HAMMERS;

@UtilityClass
public class ModelPredicates {

    private static void register(String id, ClampedModelPredicateProvider provider, Item... items) {
        Arrays.stream(items).forEach(item -> ModelPredicateProviderRegistry.register(item, new Identifier(id), provider));
    }

    public static void registerAll() {
        register("ether_percentage", ((stack, world, entity, seed) -> {
            EtherGlint glint = new EtherGlint(stack);
            return glint.getStoredEther() / glint.getMaxEther();
        }), GLINT);

        register("hammer_handle", ((stack, world, entity, seed) -> {
            if (!(entity instanceof EtherologyPlayer player)) return 0;
            HammerState hammerState = player.etherology$getHammerState();
            return hammerState.equals(HammerState.IDLE) && HammerItem.checkHammer(player) ? 1 : 0;
        }), HAMMERS);

        register("glaive_handle", ((stack, world, entity, seed) -> {
            if (!(entity instanceof EtherologyPlayer player)) return 0;
            HammerState hammerState = player.etherology$getHammerState();
            return hammerState.equals(HammerState.IDLE) && GlaiveItem.checkGlaive(player) ? 1 : 0;
        }), GLAIVES);
    }
}
