package name.uwu.feytox.lotyh.gui.teldecore.pages;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import name.uwu.feytox.lotyh.gui.teldecore.IngredientComponent;
import name.uwu.feytox.lotyh.gui.teldecore.TitleComponent;
import name.uwu.feytox.lotyh.recipes.visual.TFurnaceRecipe;
import name.uwu.feytox.lotyh.util.LIdentifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class FurnacePage extends EmptyPage {
    public FurnacePage(boolean is_left, TFurnaceRecipe recipe) {
        super(is_left, new LIdentifier("textures/gui/teldecore/teldecore_furnace.png"));
        String title = "Печь";

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
                        .positioning(Positioning.absolute(90+dx, 89))
        );

        // ingredient
        IngredientComponent itemComponent = new IngredientComponent(recipe.getMatchingItems().get(1));
        this.child(itemComponent
                .positioning(Positioning.absolute(30+dx, 63)));
        itemComponent.blink();

        // time tooltip
        // TODO: lang file
        this.child(
                Containers.verticalFlow(Sizing.fixed(22), Sizing.fixed(50))
                        .tooltip(Text.of("Time: " + recipe.getTicks() + " ticks"))
                        .positioning(Positioning.absolute(27+dx, 84))
        );
    }
}
