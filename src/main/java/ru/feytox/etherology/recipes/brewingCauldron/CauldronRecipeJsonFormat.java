package ru.feytox.etherology.recipes.brewingCauldron;

import com.google.gson.JsonObject;
import ru.feytox.etherology.data.item_aspects.ItemAspectsContainer;

public class CauldronRecipeJsonFormat {
    JsonObject inputItem;
    int inputAmount;
    ItemAspectsContainer inputAspects;
    String outputItem;
    int outputAmount;
}
