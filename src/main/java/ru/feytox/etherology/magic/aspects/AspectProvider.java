package ru.feytox.etherology.magic.aspects;

import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.data.item_aspects.AspectsLoader;

public interface AspectProvider {

    @Nullable
    AspectContainer getStoredAspects();

    Text getAspectsSourceName();

    @Nullable
    static AspectContainer getAspects(ClientWorld world, HitResult hitResult) {
        if (hitResult instanceof BlockHitResult blockHitResult) {
            BlockPos pos = blockHitResult.getBlockPos();
            if (world.getBlockEntity(pos) instanceof AspectProvider provider) {
                return provider.getStoredAspects();
            }

            BlockState state = world.getBlockState(pos);
            return AspectsLoader.getAspects(state.getBlock().asItem().getDefaultStack(), false).orElse(null);
        }

        if (!(hitResult instanceof EntityHitResult entityHitResult)) return null;

        Entity entity = entityHitResult.getEntity();
        if (entity instanceof ItemEntity itemEntity) {
            return AspectsLoader.getAspects(itemEntity.getStack(), false).orElse(null);
        }

        if (entity instanceof ItemFrameEntity itemFrame) {
            return AspectsLoader.getAspects(itemFrame.getHeldItemStack(), false).orElse(null);
        }

        return AspectsLoader.getEntityAspects(entity).orElse(null);
    }

    @Nullable
    static Text getTargetName(ClientWorld world, HitResult hitResult) {
        if (hitResult instanceof BlockHitResult blockHitResult) {
            BlockPos pos = blockHitResult.getBlockPos();
            if (world.getBlockEntity(pos) instanceof AspectProvider provider) {
                return provider.getAspectsSourceName();
            }

            BlockState state = world.getBlockState(pos);
            return Text.translatable(state.getBlock().getTranslationKey());
        }

        if (!(hitResult instanceof EntityHitResult entityHitResult)) return null;
        if (entityHitResult.getEntity() instanceof ItemFrameEntity itemFrame) return itemFrame.getHeldItemStack().getName();

        return entityHitResult.getEntity().getName();
    }
}
