package ru.feytox.etherology.recipes.armillary;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import ru.feytox.etherology.block.armillar.ArmillaryMatrixBlockEntity;
import ru.feytox.etherology.magic.aspects.Aspect;
import ru.feytox.etherology.recipes.FeyRecipe;
import ru.feytox.etherology.recipes.FeyRecipeSerializer;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ArmillaryRecipe implements FeyRecipe<ArmillaryMatrixBlockEntity> {

    private final Ingredient centerInput;
    private final List<Aspect> aspects;
    private final float etherPoints;
    @Getter(value = AccessLevel.PRIVATE)
    private final ItemStack outputStack;
    private final Identifier id;

    @Override
    public boolean matches(ArmillaryMatrixBlockEntity inventory, World world) {
        if (!centerInput.test(inventory.getStack(0))) return false;

        List<Aspect> aspects = inventory.getCurrentAspects();
        if (aspects == null) return false;
        return aspects.equals(this.aspects);
    }

    @Override
    public ItemStack craft(ArmillaryMatrixBlockEntity inventory) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput() {
        return outputStack.copy();
    }

    @Override
    public FeyRecipeSerializer<?> getSerializer() {
        return ArmillaryRecipeSerializer.INSTANCE;
    }
}
