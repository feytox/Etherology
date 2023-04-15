package ru.feytox.etherology.blocks.spill_barrel;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.BlocksRegistry;
import ru.feytox.etherology.util.registry.RegistrableBlock;

public class SpillBarrelBlock extends Block implements RegistrableBlock, BlockEntityProvider {
    public SpillBarrelBlock() {
        super(FabricBlockSettings.copyOf(Blocks.BARREL).nonOpaque());
        this.setDefaultState(this.getDefaultState().with(HorizontalFacingBlock.FACING, Direction.NORTH));
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return !world.getBlockState(pos.down()).getBlock().equals(BlocksRegistry.SPILL_BARREL)
                && !world.getBlockState(pos.up()).getBlock().equals(BlocksRegistry.SPILL_BARREL);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (hand != Hand.MAIN_HAND) return ActionResult.PASS;
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof SpillBarrelBlockEntity spillBarrel)) return ActionResult.PASS;

        ItemStack handStack = player.getStackInHand(hand);
        if (handStack.isEmpty()) return ActionResult.PASS;

        if (spillBarrel.tryFillBarrel(handStack.copy())) {
            player.setStackInHand(hand, ItemUsage.exchangeStack(handStack, player, Items.GLASS_BOTTLE.getDefaultStack()));
            world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
            return ActionResult.CONSUME;
        }

        if (!spillBarrel.isEmpty() && !handStack.isOf(Items.POTION)) {
            ItemStack outputStack = spillBarrel.tryEmptyBarrel(handStack);
            if (outputStack.isOf(Items.POTION)) {
                player.setStackInHand(hand, ItemUsage.exchangeStack(handStack, player, outputStack));
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
                world.emitGameEvent(null, GameEvent.FLUID_PICKUP, pos);
                return ActionResult.CONSUME;
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SpillBarrelBlockEntity spillBarrel) {
            if (!world.isClient && (!player.isCreative() || (player.isCreative() && !spillBarrel.isEmpty()))) {
                ItemStack barrelStack = BlocksRegistry.SPILL_BARREL.getItem().getDefaultStack();
                spillBarrel.setStackNbt(barrelStack);
                if (spillBarrel.hasCustomName()) {
                    barrelStack.setCustomName(spillBarrel.getCustomName());
                }
                ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, barrelStack);
                itemEntity.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity);
            }
        }

        super.onBreak(world, pos, state, player);
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
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (itemStack.hasCustomName() && blockEntity instanceof SpillBarrelBlockEntity spillBarrel) {
            spillBarrel.setCustomName(itemStack.getName());
        }
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SpillBarrelBlockEntity(pos, state);
    }

    @Override
    public Block getBlockInstance() {
        return this;
    }

    @Override
    public String getBlockId() {
        return "spill_barrel";
    }
}
