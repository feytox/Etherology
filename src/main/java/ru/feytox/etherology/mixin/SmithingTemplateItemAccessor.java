package ru.feytox.etherology.mixin;

import net.minecraft.item.SmithingTemplateItem;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SmithingTemplateItem.class)
public interface SmithingTemplateItemAccessor {

    @Accessor("INGREDIENTS_TEXT")
    static Text getIngredientsText() {
        throw new UnsupportedOperationException();
    }

    @Accessor("APPLIES_TO_TEXT")
    static Text getAppliesToText() {
        throw new UnsupportedOperationException();
    }

    @Accessor("DESCRIPTION_FORMATTING")
    static Formatting getDescriptionFormatting() {
        throw new UnsupportedOperationException();
    }

    @Accessor("TITLE_FORMATTING")
    static Formatting getTitleFormatting() {
        throw new UnsupportedOperationException();
    }
}
