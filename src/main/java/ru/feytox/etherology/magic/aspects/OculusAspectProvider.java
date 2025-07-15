package ru.feytox.etherology.magic.aspects;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.data.aspectContainer.AspectContainerLoader;
import ru.feytox.etherology.magic.aspectContainer.AspectContainer;

public interface OculusAspectProvider {

    @Nullable
    static AspectContainer getAspects(World world, HitResult hitResult) {
        if (hitResult instanceof BlockHitResult blockHitResult) {
            var pos = blockHitResult.getBlockPos();
            var state = world.getBlockState(pos);
            return AspectContainerLoader.getAspects(world, state.getBlock().asItem().getDefaultStack(), false, false).orElse(null);
        }

        if (!(hitResult instanceof EntityHitResult entityHitResult)) return null;

        var entity = entityHitResult.getEntity();
        if (entity instanceof ItemEntity itemEntity) {
            return AspectContainerLoader.getAspects(world, itemEntity.getStack(), false, false).orElse(null);
        }

        return AspectContainerLoader.getEntityAspects(world, entity, false).orElse(null);
    }

    @Nullable
    static Text getTargetName(World world, HitResult hitResult) {
        if (hitResult instanceof BlockHitResult blockHitResult) {
            var pos = blockHitResult.getBlockPos();
            var state = world.getBlockState(pos);
            return Text.translatable(state.getBlock().getTranslationKey());
        }

        if (!(hitResult instanceof EntityHitResult entityHitResult)) return null;
        if (entityHitResult.getEntity() instanceof ItemFrameEntity itemFrame) return itemFrame.getHeldItemStack().getName();

        return entityHitResult.getEntity().getName();
    }
}
