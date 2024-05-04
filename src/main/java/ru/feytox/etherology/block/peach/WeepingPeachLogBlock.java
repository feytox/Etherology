package ru.feytox.etherology.block.peach;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import ru.feytox.etherology.registry.item.DecoBlockItems;

public class WeepingPeachLogBlock extends PillarBlock {

    public WeepingPeachLogBlock(MapColor topMapColor, MapColor sideMapColor) {
        super(AbstractBlock.Settings.of(Material.WOOD, (state) -> state.get(PillarBlock.AXIS) == Direction.Axis.Y ? topMapColor : sideMapColor).strength(2.0F).sounds(BlockSoundGroup.WOOD));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        boolean iterResult = false;
        for (ItemStack itemStack : player.getHandItems()) {
            if (itemStack.getItem() instanceof AxeItem) {
                iterResult = true;
                break;
            }
        }
        if (!iterResult) return super.onUse(state, world, pos, player, hand, hit);

        ItemScatterer.spawn(world, pos.getX(), pos.getY() + 1, pos.getZ(), DecoBlockItems.EBONY.getDefaultStack());
        return super.onUse(state, world, pos, player, hand, hit);
    }
}
