package ru.feytox.etherology.magic.aspects;

import it.unimi.dsi.fastutil.Pair;
import lombok.val;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.data.item_aspects.AspectsLoader;

import java.util.List;

public interface RevelationAspectProvider {

    @Nullable
    AspectContainer getRevelationAspects();

    default int getRevelationAspectsLimit() {
        return -1;
    }

    @Nullable
    static List<Pair<Aspect, Integer>> getSortedAspects(World world, HitResult hitResult) {
        val data = getData(world, hitResult);
        if (data == null) return null;
        val aspects = data.key();
        Integer limit = data.value();
        if (aspects == null || limit == null) return null;

        return aspects.sorted(true, limit);
    }

    @Nullable
    static Pair<AspectContainer, Integer> getData(World world, HitResult hitResult) {
        if (hitResult instanceof BlockHitResult blockHitResult) {
            BlockPos pos = blockHitResult.getBlockPos();
            if (world.getBlockEntity(pos) instanceof RevelationAspectProvider provider) {
                return Pair.of(provider.getRevelationAspects(), provider.getRevelationAspectsLimit());
            }
        }

        if (!(hitResult instanceof EntityHitResult entityHitResult)) return null;
        Entity entity = entityHitResult.getEntity();
        if (!(entity instanceof ItemFrameEntity itemFrame)) return null;

        return Pair.of(AspectsLoader.getAspects(itemFrame.getHeldItemStack(), false).orElse(null), -1);
    }
}
