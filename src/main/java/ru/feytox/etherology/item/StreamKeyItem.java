package ru.feytox.etherology.item;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ToolItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.block.etherealChannel.EtherealChannel;
import ru.feytox.etherology.registry.block.EBlocks;
import ru.feytox.etherology.registry.misc.EtherSounds;
import ru.feytox.etherology.registry.misc.EtherToolMaterials;

public class StreamKeyItem extends ToolItem {

    public StreamKeyItem() {
        super(EtherToolMaterials.EBONY, new Settings());
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ActionResult result = tryUseOnChannel(context);
        if (result == null) return super.useOnBlock(context);

        Random random = context.getWorld().getRandom();
        context.getWorld().playSound(null, context.getBlockPos(), EtherSounds.RATCHET, SoundCategory.BLOCKS, 0.8f, random.nextFloat()*0.2f+0.9f);
        return result;
    }

    @Nullable
    private ActionResult tryUseOnChannel(ItemUsageContext context) {
        World world = context.getWorld();
        if (world.isClient) return null;

        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof EtherealChannel)) return null;

        PlayerEntity player = context.getPlayer();
        boolean isPlayer = player != null;
        boolean isVertical = isPlayer && player.isSneaking();
        ItemStack stack = context.getStack();

        Direction pipeFacing = state.get(EtherealChannel.FACING);
        Direction playerFacing = context.getHorizontalPlayerFacing();
        if (isVertical) {
            pipeFacing = pipeFacing.getOffsetY() == 0 ? Direction.UP : pipeFacing.getOpposite();
        }
        else {
            pipeFacing = pipeFacing.getOffsetY() == 0 ? pipeFacing.rotateYClockwise() : playerFacing;
        }

        state = EBlocks.ETHEREAL_CHANNEL.getChannelState(world, state.with(EtherealChannel.FACING, pipeFacing), pos);
        world.setBlockState(pos, state, EtherealChannel.NOTIFY_ALL);

        Hand hand = context.getHand();
        if (isPlayer) stack.damage(1, player, playerx -> playerx.sendToolBreakStatus(hand));
        else if (stack.damage(1, world.random, null)) {
            stack.decrement(1);
        }

        return ActionResult.SUCCESS;
    }
}
