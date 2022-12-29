package name.uwu.feytox.etherology.gui.teldecore.pages;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import name.uwu.feytox.etherology.gui.teldecore.IngredientComponent;
import name.uwu.feytox.etherology.gui.teldecore.TitleComponent;
import name.uwu.feytox.etherology.recipes.visual.TTransRecipe;
import name.uwu.feytox.etherology.util.EIdentifier;
import name.uwu.feytox.etherology.util.EIngredient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.text.Text;

import java.util.Map;

public class TransPage extends EmptyPage {
    public TransPage(boolean is_left, TTransRecipe recipe) {
        super(is_left, new EIdentifier("textures/gui/teldecore/teldecore_transformation.png"));
        String title = "Преобразование";

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
                                .getTooltip(MinecraftClient.getInstance().player, TooltipContext.Default.NORMAL))
                        .positioning(Positioning.absolute(61+dx, 35))
        );

        // instability
        this.child(
                Components.label(recipe.getInstabLevel().getTranslation())
                        .color(recipe.getInstabLevel().getTextColor())
                        .positioning(Positioning.relative(50, 96))
        );

        // energy level
        this.child(
                Containers.horizontalFlow(Sizing.fixed(12), Sizing.fixed(9))
                        .child(Components.label(Text.of(String.valueOf(recipe.getEnergyLevel())))
                                .color(Color.BLACK)
                                .positioning(Positioning.relative(50, 0)))
                        .allowOverflow(true)
                        .positioning(Positioning.absolute(63+dx, 164))
        );

        // ingredients
        Map<Integer, EIngredient> matchingItems = recipe.getMatchingItems();
        int finalDx = dx;
        matchingItems.forEach(((slot_num, lIngredient) -> {
            int slot_x = 61;
            int slot_y = 60;
            int kx = 16;
            int ky = 16;
            switch (slot_num) {
                case 2 -> {
                    slot_x = 89;
                    slot_y = 70;
                }
                case 3 -> {
                    slot_x = 61 + 32 + 8;
                    slot_y = 60 + 32 + 4;
                }
                case 4 -> {
                    slot_x = 89;
                    slot_y = 126;
                }
                case 5 -> {
                    slot_x = 61;
                    slot_y = 60 + 64 + 16;
                }
                case 6 -> {
                    slot_x = 33;
                    slot_y = 126;
                }
                case 7 -> {
                    slot_x = 61 - 32 - 8;
                    slot_y = 60 + 32 + 4;
                }
                case 8 -> {
                    slot_x = 33;
                    slot_y = 70;
                }
                case 9 -> {
                    slot_x = 61;
                    slot_y = 60 + 32 + 4;
                }
            }

            IngredientComponent itemComponent = new IngredientComponent(lIngredient);
            this.child(itemComponent
                    .positioning(Positioning.absolute(slot_x+ finalDx, slot_y)));
            itemComponent.blink();
        }));
    }
}
