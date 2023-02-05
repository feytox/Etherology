package name.uwu.feytox.etherology.gui.teldecore.pages;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import name.uwu.feytox.etherology.gui.teldecore.IngredientComponent;
import name.uwu.feytox.etherology.gui.teldecore.TitleComponent;
import name.uwu.feytox.etherology.recipes.visual.TEtherRecipe;
import name.uwu.feytox.etherology.util.feyapi.EIdentifier;
import name.uwu.feytox.etherology.util.feyapi.EIngredient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;

import java.util.Map;

public class EtherPage extends EmptyPage {
    public EtherPage(boolean is_left, TEtherRecipe recipe) {
        super(is_left, new EIdentifier("textures/gui/teldecore/teldecore_ether_workbench.png"));
        String title = "Эфирное ремесло";

        int dx = 0;
        if (!is_left) {
            dx = 5;
        }

        // Title + result
        this.child(new TitleComponent(title));
        this.child(
                Components.item(recipe.getResult())
                        .showOverlay(true)
                        .tooltip(recipe.getResult()
                                .getTooltip(MinecraftClient.getInstance().player, TooltipContext.BASIC))
                        .positioning(Positioning.absolute(61+dx, 146))
        );

        // shards
        GridLayout shardGrid = Containers.grid(Sizing.content(), Sizing.content(), 1, recipe.getShards().size());
        shardGrid.allowOverflow(true);
        for (int i = 0; i < recipe.getShards().size(); i++) {
            ItemStack shard = recipe.getShards().get(i);
            shardGrid.child(
                    new IngredientComponent(new EIngredient(shard))
                            .positioning(Positioning.layout()),
                    0, i
            );
        }
        this.child(shardGrid.positioning(Positioning.relative(is_left ? 47 : 53, 96)));

        // ingredients
        Map<Integer, EIngredient> matchingItems = recipe.getMatchingItems();
        int finalDx = dx;
        matchingItems.forEach(((slot_num, lIngredient) -> {
            int slot_x = 40;
            int slot_y = 44;
            switch (slot_num) {
                case 2 -> {
                    slot_x = 61;
                    slot_y = 44;
                }
                case 3 -> {
                    slot_x = 82;
                    slot_y = 44;
                }
                case 4 -> {
                    slot_x = 40;
                    slot_y = 65;
                }
                case 5 -> {
                    slot_x = 61;
                    slot_y = 65;
                }
                case 6 -> {
                    slot_x = 82;
                    slot_y = 65;
                }
                case 7 -> {
                    slot_x = 40;
                    slot_y = 86;
                }
                case 8 -> {
                    slot_x = 61;
                    slot_y = 86;
                }
                case 9 -> {
                    slot_x = 82;
                    slot_y = 86;
                }
            }

            IngredientComponent itemComponent = new IngredientComponent(lIngredient);
            this.child(itemComponent
                    .positioning(Positioning.absolute(slot_x+ finalDx, slot_y)));
            itemComponent.blink();
        }));
    }
}
