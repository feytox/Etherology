package ru.feytox.etherology.block.empowerTable;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.feytox.etherology.util.feyapi.RegistrableBlock;

public class EmpowerTableBlock extends Block implements RegistrableBlock, BlockEntityProvider {
    public EmpowerTableBlock() {
        super(FabricBlockSettings.copyOf(Blocks.CRAFTING_TABLE));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory factory = (NamedScreenHandlerFactory) world.getBlockEntity(pos);
            if (factory != null) {
                player.openHandledScreen(factory);
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public Block getBlockInstance() {
        return this;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EmpowerTableBlockEntity(pos, state);
    }

    @Override
    public String getBlockId() {
        return "empowerment_table";
    }
}
