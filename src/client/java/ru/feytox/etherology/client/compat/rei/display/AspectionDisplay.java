package ru.feytox.etherology.client.compat.rei.display;

import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.client.compat.rei.EtherREIPlugin;
import ru.feytox.etherology.client.compat.rei.misc.AspectPair;
import ru.feytox.etherology.data.aspects.AspectsLoader;
import ru.feytox.etherology.magic.aspects.AspectContainerId;

import java.util.Collections;
import java.util.List;

// aspect + inspection = aspection :3
public class AspectionDisplay extends BasicDisplay {

    private AspectionDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs) {
        super(inputs, outputs);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return EtherREIPlugin.ASPECTION;
    }

    public static void registerFillers(DisplayRegistry registry) {
        AspectsLoader.forEach(MinecraftClient.getInstance().world, (id, container) -> {
            EntryStack<?> holder = getHolder(id);
            if (holder == null) return;
            List<EntryIngredient> input = Collections.singletonList(EntryIngredient.of(holder));
            List<EntryIngredient> outputs = container.getAspects().entrySet().stream()
                    .map(entry -> AspectPair.entry(entry.getKey(), entry.getValue())).map(EntryIngredient::of).toList();
            registry.add(new AspectionDisplay(input, outputs));
        });
    }

    @Nullable
    private static EntryStack<?> getHolder(AspectContainerId id) {
        return switch (id.getContainerType()) {
            case ITEM -> EntryStacks.of(Registries.ITEM.get(id.getId()));
            case POTION -> getPotion(Items.POTION, id);
            case SPLASH_POTION -> getPotion(Items.SPLASH_POTION, id);
            case LINGERING_POTION -> getPotion(Items.LINGERING_POTION, id);
            case TIPPED_ARROW -> getPotion(Items.TIPPED_ARROW, id);
            case ENTITY -> null;
        };
    }

    private static EntryStack<?> getPotion(Item item, AspectContainerId id) {
        return Registries.POTION.getEntry(id.getId()).map(potion -> PotionContentsComponent.createStack(item, potion))
                .map(EntryStacks::of).orElse(null);
    }
}
