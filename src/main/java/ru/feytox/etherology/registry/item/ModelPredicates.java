package ru.feytox.etherology.registry.item;

import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.item.LensItem;
import ru.feytox.etherology.item.WarpCounter;
import ru.feytox.etherology.magic.ether.EtherGlint;
import ru.feytox.etherology.magic.lens.LensComponent;
import ru.feytox.etherology.magic.lens.LensMode;
import ru.feytox.etherology.magic.lens.LensPattern;

import java.util.Arrays;

import static ru.feytox.etherology.registry.item.EItems.GLINT;
import static ru.feytox.etherology.registry.item.EItems.UNADJUSTED_LENS;
import static ru.feytox.etherology.registry.item.ToolItems.STAFF;
import static ru.feytox.etherology.registry.item.ToolItems.WARP_COUNTER;

@UtilityClass
public class ModelPredicates {

    private static void register(String id, ClampedModelPredicateProvider provider, Item... items) {
        if (items.length == 0) throw new IllegalArgumentException("Expected > 1 items to register, found 0");
        Arrays.stream(items).forEach(item -> ModelPredicateProviderRegistry.register(item, new Identifier(id), provider));
    }

    public static void registerAll() {
        register("ether_percentage", (stack, world, entity, seed) -> {
            EtherGlint glint = new EtherGlint(stack);
            return glint.getStoredEther() / glint.getMaxEther();
        }, GLINT);

        register("staff_stream", (stack, world, entity, seed) -> {
            if (entity == null || !entity.getActiveItem().equals(stack)) return 0;
            val lensData = LensItem.getStaffLens(stack);
            if (lensData == null) return 0;
            return lensData.mode().equals(LensMode.STREAM) ? 1 : 0;
        }, STAFF);

        register("staff_charge", (stack, world, entity, seed) -> {
            if (entity == null || !entity.getActiveItem().equals(stack)) return 0;
            val lensData = LensItem.getStaffLens(stack);
            if (lensData == null) return 0;
            return lensData.mode().equals(LensMode.CHARGE) ? 1 : 0;
        }, STAFF);

        register("lens_cracked", (stack, world, entity, seed) -> LensComponent.get(stack).map(LensComponent::pattern)
                .filter(LensPattern::isCracked).map(pattern -> 1).orElse(0), UNADJUSTED_LENS);

        register("warp_counter", (stack, world, entity, seed) -> WarpCounter.getWarpLevel(stack, world, entity), WARP_COUNTER);
    }
}
