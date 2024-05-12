package ru.feytox.etherology.registry.item;

import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.magic.ether.EtherGlint;
import ru.feytox.etherology.magic.lens.LensMode;
import ru.feytox.etherology.registry.misc.EtherologyComponents;

import java.util.Arrays;

import static ru.feytox.etherology.registry.item.EItems.GLINT;

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

        register("staff_stream", ((stack, world, entity, seed) -> {
            if (entity == null || !entity.getActiveItem().equals(stack)) return 0;
            val lensData = EtherologyComponents.LENS.get(stack);
            if (lensData.isEmpty()) return 0;
            return lensData.getLensMode().equals(LensMode.STREAM) ? 1 : 0;
        }));

        register("staff_charge", ((stack, world, entity, seed) -> {
            if (entity == null || !entity.getActiveItem().equals(stack)) return 0;
            val lensData = EtherologyComponents.LENS.get(stack);
            if (lensData.isEmpty()) return 0;
            return lensData.getLensMode().equals(LensMode.CHARGE) ? 1 : 0;
        }));
    }
}
