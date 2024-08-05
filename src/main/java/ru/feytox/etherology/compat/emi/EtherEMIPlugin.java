package ru.feytox.etherology.compat.emi;

import dev.emi.emi.api.EmiInitRegistry;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import ru.feytox.etherology.compat.emi.misc.AspectStack;
import ru.feytox.etherology.compat.emi.misc.FeyEmiCategory;
import ru.feytox.etherology.compat.emi.recipe.*;
import ru.feytox.etherology.magic.staff.StaffColors;
import ru.feytox.etherology.magic.staff.StaffComponent;
import ru.feytox.etherology.magic.staff.StaffPart;
import ru.feytox.etherology.magic.staff.StaffPartInfo;
import ru.feytox.etherology.recipes.FeyRecipeSerializer;
import ru.feytox.etherology.recipes.alchemy.AlchemyRecipeSerializer;
import ru.feytox.etherology.recipes.empower.EmpowerRecipeSerializer;
import ru.feytox.etherology.recipes.jewelry.LensRecipeSerializer;
import ru.feytox.etherology.recipes.jewelry.ModifierRecipeSerializer;
import ru.feytox.etherology.recipes.matrix.MatrixRecipeSerializer;
import ru.feytox.etherology.registry.block.EBlocks;
import ru.feytox.etherology.registry.item.ToolItems;
import ru.feytox.etherology.registry.misc.ComponentTypes;
import ru.feytox.etherology.util.misc.ItemUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class EtherEMIPlugin implements EmiPlugin {

    public static final FeyEmiCategory EMPOWERMENT = FeyEmiCategory.of("empowerment", EBlocks.EMPOWERMENT_TABLE, "block.etherology.empowerment_table.title");
    public static final FeyEmiCategory INVENTOR = FeyEmiCategory.of("inventor", EBlocks.INVENTOR_TABLE);
    public static final FeyEmiCategory ALCHEMY = FeyEmiCategory.of("alchemy", EBlocks.BREWING_CAULDRON);
    public static final FeyEmiCategory ASPECTION = FeyEmiCategory.of("aspection", ToolItems.OCULUS, "gui.etherology.aspects");
    public static final FeyEmiCategory MATRIX = FeyEmiCategory.of("matrix", EBlocks.ARMILLARY_MATRIX);
    public static final FeyEmiCategory JEWELRY_LENS = FeyEmiCategory.of("jewelry", EBlocks.JEWELRY_TABLE);
    public static final FeyEmiCategory JEWELRY_MODIFIER = FeyEmiCategory.of("jewelry", EBlocks.JEWELRY_TABLE);

    @Override
    public void initialize(EmiInitRegistry registry) {
        registry.addIngredientSerializer(AspectStack.class, new AspectStack.Serializer());
    }

    @Override
    public void register(EmiRegistry registry) {
        registerCategories(registry);
        registerRecipes(registry);
    }

    private void registerCategories(EmiRegistry registry) {
        registerCategory(registry, EMPOWERMENT);
        registerCategory(registry, INVENTOR);
        registerCategory(registry, ALCHEMY);
        registerCategory(registry, ASPECTION);
        registerCategory(registry, MATRIX);
        registerCategory(registry, JEWELRY_LENS);
        registerCategory(registry, JEWELRY_MODIFIER);
    }

    private void registerRecipes(EmiRegistry registry) {
        registerCarpetingRecipes(registry);
        registerCarpetCuttingRecipes(registry);
        InventorERecipe.addRecipes(registry);
        AspectionERecipe.registerRecipes(registry);

        registerRecipe(registry, EmpowerRecipeSerializer.INSTANCE, EmpowerERecipe::of);
        registerRecipe(registry, AlchemyRecipeSerializer.INSTANCE, AlchemyERecipe::of);
        registerRecipe(registry, MatrixRecipeSerializer.INSTANCE, MatrixERecipe::of);
        registerRecipe(registry, LensRecipeSerializer.INSTANCE, JewelryERecipe.Lens::of);
        registerRecipe(registry, ModifierRecipeSerializer.INSTANCE, JewelryERecipe.Modifier::of);
    }

    private <T extends Recipe<I>, I extends RecipeInput, R extends EmiRecipe> void registerRecipe(EmiRegistry registry, FeyRecipeSerializer<T> feySerializer, Function<RecipeEntry<T>, R> mapper) {
        getRecipes(registry, feySerializer.getRecipeType()).forEach(entry -> registry.addRecipe(mapper.apply(entry)));
    }

    private <T extends Recipe<I>, I extends RecipeInput> List<RecipeEntry<T>> getRecipes(EmiRegistry registry, RecipeType<T> recipeType) {
        return registry.getRecipeManager().listAllOfType(recipeType);
    }

    private void registerCarpetingRecipes(EmiRegistry registry) {
        EmiStack staff = EmiStack.of(ToolItems.STAFF);

        Arrays.stream(StaffColors.values()).map(color -> {
            ItemStack resultStaff = ToolItems.STAFF.getDefaultStack();
            resultStaff.apply(ComponentTypes.STAFF, StaffComponent.DEFAULT, component -> component.setPartInfo(StaffPartInfo.of(StaffPart.HANDLE, color)));
            return new EmiCraftingRecipe(List.of(staff, EmiStack.of(color.getCarpet())), EmiStack.of(resultStaff), ItemUtils.suffixId(ToolItems.STAFF, "_" + color.getName() + "_carpeting"), true);
        }).forEach(registry::addRecipe);
    }

    private void registerCarpetCuttingRecipes(EmiRegistry registry) {
        EmiStack resultStaff = EmiStack.of(ToolItems.STAFF);
        EmiStack shears = EmiStack.of(Items.SHEARS);

        Arrays.stream(StaffColors.values()).map(color -> {
            ItemStack inputStaff = ToolItems.STAFF.getDefaultStack();
            inputStaff.apply(ComponentTypes.STAFF, StaffComponent.DEFAULT, component -> component.setPartInfo(StaffPartInfo.of(StaffPart.HANDLE, color)));
            return new EmiCraftingRecipe(List.of(EmiStack.of(inputStaff), shears), resultStaff, ItemUtils.suffixId(ToolItems.STAFF, "_" + color.getName() + "_carpet_cutting"), true);
        }).forEach(registry::addRecipe);
    }

    private void registerCategory(EmiRegistry registry, FeyEmiCategory category) {
        registry.addCategory(category);
        registry.addWorkstation(category, category.getIcon());
    }

    // TODO: 05.08.2024 find a solution to add EMI support for Teldecore

    //    private void registerStackProviders(EmiRegistry registry) {
//        registry.addStackProvider(TeldecoreScreen.class, (screen, x, y) -> {
//            FeyIngredient focusedIngredient = screen.getFocusedIngredient(x, y);
//            if (focusedIngredient == null) return EmiStackInteraction.EMPTY;
//            EmiStack focusedStack = toEmiStack(focusedIngredient);
//            return focusedStack == null ? EmiStackInteraction.EMPTY : new EmiStackInteraction(focusedStack);
//        });
//    }

//    @Nullable
//    private static EmiStack toEmiStack(FeyIngredient ingredient) {
//        Object content = ingredient.getContent();
//        if (content == null) return null;
//        return switch (content) {
//            case ItemStack stack -> EmiStack.of(stack);
//            default -> null;
//        };
//    }
}
