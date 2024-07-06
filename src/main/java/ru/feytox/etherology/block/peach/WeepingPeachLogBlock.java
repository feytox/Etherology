package ru.feytox.etherology.block.peach;

import net.minecraft.block.*;
import net.minecraft.block.enums.Instrument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ActionResult;
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
        super(Settings.create().mapColor((state) -> state.get(PillarBlock.AXIS) == Direction.Axis.Y ? topMapColor : sideMapColor).instrument(Instrument.BASS).strength(2.0F).sounds(BlockSoundGroup.WOOD).burnable());
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!(stack.getItem() instanceof AxeItem)) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        ItemScatterer.spawn(world, pos.getX(), pos.getY() + 1, pos.getZ(), DecoBlockItems.EBONY.getDefaultStack());
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
