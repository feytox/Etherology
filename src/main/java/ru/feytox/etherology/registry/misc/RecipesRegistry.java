package ru.feytox.etherology.registry.misc;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.recipes.FeyRecipeSerializer;
import ru.feytox.etherology.recipes.armillary.ArmillaryRecipeSerializer;
import ru.feytox.etherology.recipes.brewingCauldron.CauldronRecipeSerializer;
import ru.feytox.etherology.recipes.empower.EmpowerRecipeSerializer;
import ru.feytox.etherology.recipes.jewelry.JewelryRecipeSerializer;
import ru.feytox.etherology.recipes.staff.StaffCarpetCuttingRecipe;
import ru.feytox.etherology.recipes.staff.StaffCarpetingRecipe;
import ru.feytox.etherology.util.misc.EIdentifier;

@UtilityClass
public class RecipesRegistry {

    public static RecipeSerializer<StaffCarpetingRecipe> STAFF_CARPET = registerSerializer("staff_carpeting", new SpecialRecipeSerializer<>(StaffCarpetingRecipe::new));
    public static RecipeSerializer<StaffCarpetCuttingRecipe> STAFF_CARPET_CUT = registerSerializer("staff_carpet_cutting", new SpecialRecipeSerializer<>(StaffCarpetCuttingRecipe::new));

    public static void registerAll() {
        register(JewelryRecipeSerializer.INSTANCE);
        register(CauldronRecipeSerializer.INSTANCE);
        register(EmpowerRecipeSerializer.INSTANCE);
        register(ArmillaryRecipeSerializer.INSTANCE);
    }

    private static <S extends RecipeSerializer<T>, T extends Recipe<?>> S registerSerializer(String id, S serializer) {
        return Registry.register(Registries.RECIPE_SERIALIZER, new EIdentifier(id), serializer);
    }

    private static <T extends Recipe<?>> void register(FeyRecipeSerializer<T> serializer) {
        Registry.register(Registries.RECIPE_SERIALIZER, serializer.getId(), serializer);
        Registry.register(Registries.RECIPE_TYPE, serializer.getId(), serializer.getRecipeType());
    }

    @Nullable
    public static <T extends Recipe<M>, M extends Inventory> T getFirstMatch(World world, M inventory, FeyRecipeSerializer<T> serializer) {
        return world.getRecipeManager().getFirstMatch(serializer.getRecipeType(), inventory, world).orElse(null);
    }

    @Nullable
    public static Recipe<?> get(World world, @NonNull Identifier id) {
        return world.getRecipeManager().get(id).orElse(null);
    }
}