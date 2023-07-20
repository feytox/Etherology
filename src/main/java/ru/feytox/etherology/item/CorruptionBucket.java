package ru.feytox.etherology.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.magic.aspects.EtherAspectsContainer;
import ru.feytox.etherology.magic.corruption.Corruption;
import ru.feytox.etherology.registry.item.EItems;

public class CorruptionBucket extends Item {
    
    public CorruptionBucket() {
        super(new FabricItemSettings());
    }

    @Nullable
    public static ItemStack createBucketStack(EtherAspectsContainer aspects) {
        ItemStack stack = EItems.CORRUPTION_BUCKET.getDefaultStack();
        Corruption corruption = Corruption.of(aspects);
        if (corruption == null) return null;

        NbtCompound corruptionCompound = new NbtCompound();
        corruption.writeNbt(corruptionCompound);
        stack.setSubNbt("Corruption", corruptionCompound);
        return stack;
    }

    @Nullable
    public static Corruption getCorruptionFromBucket(ItemStack bucketStack) {
        if (!bucketStack.isOf(EItems.CORRUPTION_BUCKET)) return null;
        NbtCompound nbt = bucketStack.getSubNbt("Corruption");
        return nbt == null ? null : Corruption.readFromNbt(nbt);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        if (world.isClient || player == null) return super.useOnBlock(context);

        BlockPos usePos = context.getBlockPos();
        ItemStack handStack = context.getStack();

        Corruption corruption = getCorruptionFromBucket(handStack);
        if (corruption != null) corruption.injectInChunk((ServerWorld) world, usePos);
        ItemStack newStack = ItemUsage.exchangeStack(handStack, player, Items.BUCKET.getDefaultStack());
        player.setStackInHand(context.getHand(), newStack);

        world.playSound(null, usePos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
        return ActionResult.CONSUME;
    }
}
