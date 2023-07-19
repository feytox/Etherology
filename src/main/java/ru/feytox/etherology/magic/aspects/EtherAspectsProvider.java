package ru.feytox.etherology.magic.aspects;

import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.data.item_aspects.ItemAspectsLoader;

public interface EtherAspectsProvider {

    @Nullable
    EtherAspectsContainer getStoredAspects();

    Text getAspectsSourceName();

    @Nullable
    static EtherAspectsContainer getAspects(ClientWorld world, HitResult hitResult) {
        if (hitResult instanceof BlockHitResult blockHitResult) {
            BlockPos pos = blockHitResult.getBlockPos();
            if (world.getBlockEntity(pos) instanceof EtherAspectsProvider provider) {
                return provider.getStoredAspects();
            }

            BlockState state = world.getBlockState(pos);
            return ItemAspectsLoader.getAspectsOf(state.getBlock().asItem()).orElse(null);
        }

        if (!(hitResult instanceof EntityHitResult entityHitResult)) return null;
        if (entityHitResult.getEntity() instanceof ItemEntity itemEntity) {
            return ItemAspectsLoader.getAspectsOf(itemEntity.getStack().getItem()).orElse(null);
        }

        if (entityHitResult.getEntity() instanceof ItemFrameEntity itemFrame) {
            return ItemAspectsLoader.getAspectsOf(itemFrame.getHeldItemStack().getItem()).orElse(null);
        }

        return null;
    }

    @Nullable
    static Text getTargetName(ClientWorld world, HitResult hitResult) {
        if (hitResult instanceof BlockHitResult blockHitResult) {
            BlockPos pos = blockHitResult.getBlockPos();
            if (world.getBlockEntity(pos) instanceof EtherAspectsProvider provider) {
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
