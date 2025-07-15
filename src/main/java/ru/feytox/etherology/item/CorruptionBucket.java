package ru.feytox.etherology.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.magic.aspectContainer.AspectContainer;
import ru.feytox.etherology.magic.corruption.Corruption;
import ru.feytox.etherology.registry.item.EItems;
import ru.feytox.etherology.registry.misc.ComponentTypes;

import java.util.List;

public class CorruptionBucket extends Item {
    
    public CorruptionBucket() {
        super(new Settings().maxCount(1).component(ComponentTypes.CORRUPTION, new Corruption(0)));
    }

    @Nullable
    public static ItemStack createBucketStack(AspectContainer aspects) {
        ItemStack stack = EItems.CORRUPTION_BUCKET.getDefaultStack();
        Corruption corruption = Corruption.ofAspects(aspects);
        if (corruption == null) return null;

        stack.set(ComponentTypes.CORRUPTION, corruption);
        return stack;
    }

    @Nullable
    public static Corruption getCorruptionFromBucket(ItemStack bucketStack) {
        if (!bucketStack.isOf(EItems.CORRUPTION_BUCKET)) return null;
        Corruption corruption = bucketStack.get(ComponentTypes.CORRUPTION);
        return corruption == null || corruption.isEmpty() ? null : corruption;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        if (world.isClient || player == null) return super.useOnBlock(context);

        BlockPos usePos = context.getBlockPos();
        ItemStack handStack = context.getStack();

        Corruption corruption = getCorruptionFromBucket(handStack);
        if (corruption != null) corruption.placeInChunk((ServerWorld) world, usePos);
        ItemStack newStack = ItemUsage.exchangeStack(handStack, player, Items.BUCKET.getDefaultStack());
        player.setStackInHand(context.getHand(), newStack);

        world.playSound(null, usePos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
        return ActionResult.CONSUME;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        var corruption = stack.get(ComponentTypes.CORRUPTION);
        if (corruption == null)
            return;

        var level = corruption.corruptionValue();
        tooltip.add(Text.translatable("lore.etherology.corruption_bucket.emptying").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("lore.etherology.corruption_bucket.increment", level).formatted(Formatting.DARK_PURPLE));
    }
}
