package name.uwu.feytox.etherology.blocks.closet;

import io.wispforest.owo.util.ImplementedInventory;
import name.uwu.feytox.etherology.enums.FurnitureType;
import name.uwu.feytox.etherology.furniture.AbstractFurSlabBlock;
import name.uwu.feytox.etherology.furniture.FurSlabBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ClosetSlabBlock extends AbstractFurSlabBlock {
    public ClosetSlabBlock() {
        super("closet_slab", FabricBlockSettings.copyOf(Blocks.CHEST), FurnitureType.CLOSET);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof FurSlabBlockEntity furniture) || !(newState.getBlock() instanceof AbstractFurSlabBlock)) {
            super.onStateReplaced(state, world, pos, newState, moved);
            return;
        }

        FurnitureType bottom1 = state.get(BOTTOM_TYPE);
        FurnitureType bottom2 = newState.get(BOTTOM_TYPE);
        FurnitureType top1 = state.get(TOP_TYPE);
        FurnitureType top2 = newState.get(TOP_TYPE);

        if ((top1.equals(FurnitureType.CLOSET) && !top2.equals(FurnitureType.CLOSET))) {
            ImplementedInventory inv = furniture.getInventory(false);
            if (inv != null) {
                ItemScatterer.spawn(world, pos, inv);
            }
        } else if (bottom1.equals(FurnitureType.CLOSET) && !bottom2.equals(FurnitureType.CLOSET)) {
            ImplementedInventory inv = furniture.getInventory(true);
            if (inv != null) {
                ItemScatterer.spawn(world, pos, inv);
            }
        }

        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
