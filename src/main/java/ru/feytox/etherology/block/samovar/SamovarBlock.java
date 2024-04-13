package ru.feytox.etherology.block.samovar;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.util.misc.RegistrableBlock;

public class SamovarBlock extends Block implements RegistrableBlock {
    private static final VoxelShape OUTLINE_SHAPE;


    public SamovarBlock() {
        super(FabricBlockSettings.of(Material.METAL).strength(1f).nonOpaque());
        this.setDefaultState(this.getDefaultState()
                .with(HorizontalFacingBlock.FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack handStack = player.getStackInHand(hand);

        if (handStack.isOf(Items.BUCKET)) {
            if (handStack.getCount() > 1) {
                handStack.decrement(1);
                player.giveItemStack(Items.WATER_BUCKET.getDefaultStack());
            } else {
                player.setStackInHand(hand, ItemUsage.exchangeStack(handStack, player, Items.WATER_BUCKET.getDefaultStack()));
            }
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);
            return ActionResult.CONSUME;
        }

        if (handStack.isOf(Items.GLASS_BOTTLE)) {
            player.setStackInHand(hand, ItemUsage.exchangeStack(handStack, player, PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER)));
            world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);
            return ActionResult.CONSUME;
        }

        return ActionResult.PASS;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HorizontalFacingBlock.FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(HorizontalFacingBlock.FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    public String getBlockId() {
        return "samovar";
    }

    static {
        OUTLINE_SHAPE = Block.createCuboidShape(4.0d, 0.0d, 4.0d, 12.0d, 15.0d, 12.0d);
    }
}
