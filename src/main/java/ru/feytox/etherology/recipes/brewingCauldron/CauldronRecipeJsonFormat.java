package ru.feytox.etherology.recipes.brewingCauldron;

import com.google.gson.JsonObject;
import ru.feytox.etherology.magic.aspects.EtherAspectsContainer;

public class CauldronRecipeJsonFormat {
    JsonObject inputItem;
    int inputAmount;
    EtherAspectsContainer inputAspects;
    String outputItem;
    int outputAmount;
}
