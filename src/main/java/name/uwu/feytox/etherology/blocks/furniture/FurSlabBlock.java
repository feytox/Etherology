package name.uwu.feytox.etherology.blocks.furniture;

import name.uwu.feytox.etherology.enums.FurnitureType;
import name.uwu.feytox.etherology.furniture.AbstractFurSlabBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class FurSlabBlock extends AbstractFurSlabBlock {
    public FurSlabBlock() {
        super("furniture_slab", FabricBlockSettings.of(Material.WOOD), FurnitureType.FURNITURE);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }
}
