package ru.feytox.etherology.blocks.jug;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import ru.feytox.etherology.BlocksRegistry;
import ru.feytox.etherology.util.registry.RegistrableBlock;

public class AbstractJugBlock extends Block implements RegistrableBlock, BlockEntityProvider {
    private final String blockId;
    private final JugType jugType;
    private static final VoxelShape OUTLINE_SHAPE;
    private static final VoxelShape TOP_SHAPE;

    public AbstractJugBlock(String blockId, JugType jugType) {
        super(FabricBlockSettings.copyOf(Blocks.TERRACOTTA).nonOpaque());
        this.blockId = blockId;
        this.jugType = jugType;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock() && !newState.isOf(BlocksRegistry.SEALED_JUG) && !moved) {
            if (world.getBlockEntity(pos) instanceof JugBlockEntity jug) {
                ItemScatterer.spawn(world, pos, jug);
                world.updateComparators(pos, this);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player.isSneaking() && player.getMainHandStack().isEmpty() && jugType.equals(JugType.SEALED) && !world.isClient) {
            if (world.getBlockEntity(pos) instanceof JugBlockEntity jug) {
                ItemStack jugStack = BlocksRegistry.SEALED_JUG.getItem().getDefaultStack();
                jug.setStackNbt(jugStack);
                jug.clear();
                jug.markDirty();
                player.setStackInHand(Hand.MAIN_HAND, jugStack);
                world.removeBlock(pos, true);
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE;
    }

    public JugType getJugType() {
        return jugType;
    }

    @Override
    public Block getBlockInstance() {
        return this;
    }

    @Override
    public String getBlockId() {
        return blockId;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new JugBlockEntity(pos, state);
    }

    static {
        TOP_SHAPE = VoxelShapes.combineAndSimplify(
                createCuboidShape(3, 12, 3, 13, 16, 13),
                createCuboidShape(5, 12, 5, 11, 16, 11),
                BooleanBiFunction.ONLY_FIRST
        );
        OUTLINE_SHAPE = VoxelShapes.combineAndSimplify(
                createCuboidShape(1, 0, 1, 15, 12, 15),
                TOP_SHAPE,
                BooleanBiFunction.OR
        );
    }
}
