package ru.feytox.etherology.blocks.samovar;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.Material;
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
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.util.registry.RegistrableBlock;

public class SamovarBlock extends Block implements RegistrableBlock {
    public static final BooleanProperty IS_FILLED = BooleanProperty.of("is_filled");

    public SamovarBlock() {
        super(FabricBlockSettings.of(Material.METAL).strength(1f).nonOpaque());
        this.setDefaultState(this.getDefaultState()
                .with(HorizontalFacingBlock.FACING, Direction.NORTH)
                .with(IS_FILLED, false));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack handStack = player.getStackInHand(hand);
        if (handStack.isOf(Items.WATER_BUCKET) && !state.get(IS_FILLED)) {
            world.setBlockState(pos, state.with(IS_FILLED, true));
            player.setStackInHand(hand, ItemUsage.exchangeStack(handStack, player, Items.BUCKET.getDefaultStack()));
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
            return ActionResult.CONSUME;
        }

        if (handStack.isOf(Items.BUCKET) && state.get(IS_FILLED)) {
            world.setBlockState(pos, state.with(IS_FILLED, false));
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

        if (handStack.isOf(Items.GLASS_BOTTLE) && state.get(IS_FILLED)) {
            player.setStackInHand(hand, ItemUsage.exchangeStack(handStack, player, PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER)));
            world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);
            return ActionResult.CONSUME;
        }

        return ActionResult.PASS;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(IS_FILLED, HorizontalFacingBlock.FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(HorizontalFacingBlock.FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    public Block getBlockInstance() {
        return this;
    }

    @Override
    public String getBlockId() {
        return "samovar";
    }
}
