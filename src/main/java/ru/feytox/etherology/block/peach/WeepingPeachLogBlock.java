package ru.feytox.etherology.block.peach;

import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import ru.feytox.etherology.registry.item.DecoBlockItems;

public class WeepingPeachLogBlock extends PillarBlock {

    public WeepingPeachLogBlock(MapColor topMapColor, MapColor sideMapColor) {
        super(Settings.create().mapColor((state) -> state.get(PillarBlock.AXIS) == Direction.Axis.Y ? topMapColor : sideMapColor).instrument(NoteBlockInstrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable());
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!(stack.getItem() instanceof AxeItem)) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (hand.equals(Hand.MAIN_HAND) && player.getOffHandStack().isOf(Items.SHIELD) && !player.shouldCancelInteraction()) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        BlockPos itemPos = pos.add(hit.getSide().getVector());
        ItemScatterer.spawn(world, itemPos.getX(), itemPos.getY(), itemPos.getZ(), DecoBlockItems.EBONY.getDefaultStack());
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
