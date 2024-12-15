package ru.feytox.etherology.client.gui.teldecore.misc;

import org.jetbrains.annotations.Nullable;

public interface FocusedIngredientProvider {

    @Nullable
    FeyIngredient getFocusedIngredient(int mouseX, int mouseY);
}
