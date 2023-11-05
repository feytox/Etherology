package ru.feytox.etherology.registry.util;

import lombok.experimental.UtilityClass;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.recipes.alchemy.AlchemyRecipe;
import ru.feytox.etherology.recipes.alchemy.AlchemyRecipeSerializer;
import ru.feytox.etherology.recipes.armillary.ArmillaryRecipe;
import ru.feytox.etherology.recipes.armillary.ArmillaryRecipeSerializer;
import ru.feytox.etherology.recipes.brewingCauldron.CauldronRecipe;
import ru.feytox.etherology.recipes.brewingCauldron.CauldronRecipeSerializer;
import ru.feytox.etherology.recipes.empower.EmpowerRecipe;
import ru.feytox.etherology.recipes.empower.EmpowerRecipeSerializer;
import ru.feytox.etherology.recipes.staff.StaffCarpetingRecipe;
import ru.feytox.etherology.util.feyapi.EIdentifier;

@UtilityClass
public class RecipesRegistry {

    public static RecipeSerializer<StaffCarpetingRecipe> STAFF_CARPET = registerSerializer("staff_carpeting", new SpecialRecipeSerializer<>(StaffCarpetingRecipe::new));

    public static void registerAll() {
        register(CauldronRecipeSerializer.ID, CauldronRecipeSerializer.INSTANCE, CauldronRecipe.Type.ID, CauldronRecipe.Type.INSTANCE);
        register(AlchemyRecipeSerializer.ID, AlchemyRecipeSerializer.INSTANCE, AlchemyRecipe.Type.ID, AlchemyRecipe.Type.INSTANCE);
        register(EmpowerRecipeSerializer.ID, EmpowerRecipeSerializer.INSTANCE, EmpowerRecipe.Type.ID, EmpowerRecipe.Type.INSTANCE);
        register(ArmillaryRecipeSerializer.ID, ArmillaryRecipeSerializer.INSTANCE, ArmillaryRecipe.Type.ID, ArmillaryRecipe.Type.INSTANCE);
    }

    private static <T extends Recipe<?>> void register(Identifier serializerId, RecipeSerializer<T> serializerInstance, String recipeTypeId, RecipeType<T> recipeTypeInstance) {
        Registry.register(Registries.RECIPE_SERIALIZER, serializerId, serializerInstance);
        Registry.register(Registries.RECIPE_TYPE, new EIdentifier(recipeTypeId), recipeTypeInstance);
    }

    private static <S extends RecipeSerializer<T>, T extends Recipe<?>> S registerSerializer(String id, S serializer) {
        return Registry.register(Registries.RECIPE_SERIALIZER, new EIdentifier(id), serializer);
    }
}
