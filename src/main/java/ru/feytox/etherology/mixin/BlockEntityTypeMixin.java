package ru.feytox.etherology.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import ru.feytox.etherology.registry.block.DecoBlocks;

import java.util.Arrays;
import java.util.stream.Stream;

@Mixin(BlockEntityType.class)
public class BlockEntityTypeMixin {

    @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/BlockEntityType$Builder;create(Lnet/minecraft/block/entity/BlockEntityType$BlockEntityFactory;[Lnet/minecraft/block/Block;)Lnet/minecraft/block/entity/BlockEntityType$Builder;"))
    private static <T extends BlockEntity> BlockEntityType.Builder<T> injectSigns(BlockEntityType.BlockEntityFactory<? extends T> factory, Block[] blocks, Operation<BlockEntityType.Builder<T>> original) {
        if (!(blocks[0] instanceof AbstractSignBlock)) return original.call(factory, blocks);
        return switch (blocks[0]) {
            case SignBlock ignored -> addBlocks(factory, blocks, original, DecoBlocks.SIGNS);
            case WallSignBlock ignored -> addBlocks(factory, blocks, original, DecoBlocks.SIGNS);
            case HangingSignBlock ignored -> addBlocks(factory, blocks, original, DecoBlocks.HANGING_SIGNS);
            case WallHangingSignBlock ignored -> addBlocks(factory, blocks, original, DecoBlocks.HANGING_SIGNS);
            default -> original.call(factory, blocks);
        };
    }

    @Unique
    private static <T extends BlockEntity> BlockEntityType.Builder<T> addBlocks(BlockEntityType.BlockEntityFactory<? extends T> factory, Block[] blocks, Operation<BlockEntityType.Builder<T>> original, Block[] additionalBlocks) {
        Block[] newBlocks = Stream.concat(Arrays.stream(blocks), Arrays.stream(additionalBlocks)).toArray(Block[]::new);
        return original.call(factory, newBlocks);
    }
}
