package name.uwu.feytox.lotyh.gui.teldecore;

import io.wispforest.owo.ui.component.ItemComponent;
import io.wispforest.owo.ui.core.Sizing;
import name.uwu.feytox.lotyh.client.LotyhClient;
import name.uwu.feytox.lotyh.util.LIngredient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;

public class IngredientComponent extends ItemComponent {
    LIngredient lIngredient;

    public IngredientComponent(LIngredient lIngredient) {
        super(lIngredient.getNextStack());
        this.showOverlay(true);
        this.sizing(Sizing.content());
        this.lIngredient = lIngredient;
        this.ingredient(lIngredient.current());
    }

    public IngredientComponent ingredient(ItemStack itemStack) {
        this.stack(itemStack);
        this.tooltip(itemStack
                .getTooltip(MinecraftClient.getInstance().player, TooltipContext.Default.NORMAL));
        return this;
    }

    public IngredientComponent ingredient(LIngredient lIngredient) {
        return this.ingredient(lIngredient.getNextStack());
    }

    public IngredientComponent ingredient() {
        return this.ingredient(this.lIngredient);
    }

    public IngredientComponent blink() {
        LotyhClient.timer3_supps.add(() -> {
            if (this.hasParent()) {
                this.ingredient();
                return true;
            }
            return false;
        });
        return this;
    }
}
