package ru.feytox.etherology.gui.teldecore.pages;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.container.VerticalFlowLayout;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import ru.feytox.etherology.gui.teldecore.IngredientComponent;
import ru.feytox.etherology.gui.teldecore.TitleComponent;
import ru.feytox.etherology.recipes.visual.TAlchemyRecipe;
import ru.feytox.etherology.util.feyapi.EIdentifier;

public class AlchemyPage extends EmptyPage {
    public AlchemyPage(boolean is_left, TAlchemyRecipe recipe) {
        super(is_left, new EIdentifier("textures/gui/teldecore/teldecore_alchemy.png"));
        String title = "Тигель"; // TODO: lang file

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
                        .positioning(Positioning.absolute(51+dx, 71))
        );

        // ingredients
        //        int dy = Math.min(18, Math.floorDiv(122, recipe.size()-1));
        int dy = 18;
        VerticalFlowLayout flowLayout = Containers.verticalFlow(Sizing.fixed(16), Sizing.fixed(dy * recipe.size()));
        flowLayout.allowOverflow(true);
        recipe.getMatchingItems().forEach((slotNum, lIngredient) -> {
            IngredientComponent itemComponent = new IngredientComponent(lIngredient);
            flowLayout.child(
                    itemComponent.positioning(Positioning.absolute(0, dy*(slotNum-1)))
            );
            itemComponent.blink().margins(Insets.bottom(1));
        });
        recipe.getMixes().forEach((slotNum, mixType) -> {
            flowLayout.child(
                    Components.texture(mixType.getCraftTexture(), 0, 0, 16, 16, 16, 16)
                            .positioning(Positioning.absolute(0, dy*(slotNum-1)))
                            .tooltip(mixType.getTooltip())
            );
        });
        this.child(
                Containers.verticalScroll(Sizing.fixed(18), Sizing.fixed(134), flowLayout)
                        .scrollbar(ScrollContainer.Scrollbar.flat(Color.ofRgb(6768690)))
                        .scrollbarThiccness(1)
                        .positioning(Positioning.absolute(114+dx, 35))
        );
    }
}
