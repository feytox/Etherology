package ru.feytox.etherology.recipes.brewingCauldron;

import com.google.gson.JsonObject;
import ru.feytox.etherology.magic.aspects.AspectContainer;

public class CauldronRecipeJsonFormat {
    JsonObject inputItem;
    int inputAmount;
    AspectContainer inputAspects;
    String outputItem;
    int outputAmount;
}
