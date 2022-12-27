package name.uwu.feytox.lotyh.blocks.ringMatrix;

import name.uwu.feytox.lotyh.ItemsRegistry;
import name.uwu.feytox.lotyh.util.SimpleBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RingMatrixBlock extends SimpleBlock implements BlockEntityProvider {
    public RingMatrixBlock() {
        super("ring_matrix", FabricBlockSettings.of(Material.METAL).strength(10.0f).nonOpaque());
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RingMatrixBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world.isClient) {
            super.onPlaced(world, pos, state, placer, itemStack);
            return;
        }

        RingMatrixBlockEntity ringMatrix = (RingMatrixBlockEntity) world.getBlockEntity(pos);
        if (ringMatrix != null) ringMatrix.setWorld(world);

        super.onPlaced(world, pos, state, placer, itemStack);
    }
}
