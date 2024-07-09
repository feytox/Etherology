package ru.feytox.etherology.item;

import lombok.val;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.magic.corruption.Corruption;
import ru.feytox.etherology.magic.corruption.CorruptionComponent;
import ru.feytox.etherology.registry.misc.EtherSounds;
import ru.feytox.etherology.registry.misc.EtherologyComponents;
import ru.feytox.etherology.util.misc.ItemStackData;
import ru.feytox.etherology.util.misc.PseudoLivingEntity;

public class WarpCounter extends Item {

    private static final int TICK_RATE = 4;
    private static final float SOUND_MIN_CHANCE = 1f / (3*20);
    private static final float SOUND_MAX_CHANCE = 1f / 2;

    public WarpCounter() {
        super(new Settings().maxCount(1));
    }

    public static float getWarpLevel(ItemStack stack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity entity) {
        ItemStackData data = ItemStackData.get(stack);
        float targetWarpLevel = getTargetWarpLevel(stack, clientWorld, entity, data);
        Float lastWarpLevel = data.getLastWarpLevel();
        if (lastWarpLevel == null) lastWarpLevel = targetWarpLevel;
        else lastWarpLevel = MathHelper.lerp(0.01f, lastWarpLevel, targetWarpLevel);
        data.setLastWarpLevel(lastWarpLevel);
        return lastWarpLevel;
    }

    public static float getTargetWarpLevel(ItemStack stack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity entity, ItemStackData data) {
        Entity holder = entity != null ? entity : stack.getHolder();
        if (holder == null) return 0.0f;

        World world = clientWorld != null ? clientWorld : holder.getWorld();
        if (world == null) return 0.0f;

        Float warpLevel = data.getTargetWarpLevel();
        long currentTick = world.getTime();
        if (data.getLastWarpTick() == currentTick && warpLevel != null) return warpLevel;

        data.setLastWarpTick(currentTick);
        tickSound(world, holder, stack, warpLevel);
        if (currentTick % TICK_RATE != 0 && warpLevel != null) return warpLevel;

        warpLevel = getLevel(world, holder.getBlockPos());
        data.setTargetWarpLevel(warpLevel);
        return warpLevel;
    }

    private static void tickSound(World world, Entity holder, ItemStack stack, Float warpLevel) {
        if (warpLevel == null || warpLevel == 0) return;
        if (holder instanceof LivingEntity entity && !(holder instanceof PseudoLivingEntity)) {
            if (!entity.getStackInHand(Hand.MAIN_HAND).equals(stack) && !entity.getStackInHand(Hand.OFF_HAND).equals(stack)) return;
        }

        float soundChance = MathHelper.lerp(warpLevel, SOUND_MIN_CHANCE, SOUND_MAX_CHANCE);
        if (world.getRandom().nextFloat() > soundChance) return;

        world.playSound(MinecraftClient.getInstance().player, holder.getBlockPos(), EtherSounds.WARP_COUNTER, SoundCategory.PLAYERS, 1.0f, 1.0f);
    }

    private static float getLevel(World world, BlockPos pos) {
        val data = world.getChunk(pos).getComponent(EtherologyComponents.CORRUPTION);
        Corruption corruption = data.getCorruption();
        if (corruption == null) return 0.0f;
        return Math.min(corruption.corruptionValue() / CorruptionComponent.MAX_CHUNK_CORRUPTION, 1.0f);
    }
}
