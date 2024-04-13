package ru.feytox.etherology.util.misc;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SimpleHitResult {
    private final boolean isEntity;
    @Nullable
    private final BlockPos blockPos;
    @Nullable
    private final Integer entityId;

    public static SimpleHitResult of(HitResult hitResult) {
        boolean isEntity = false;
        BlockPos blockPos = null;
        Integer entityId = null;

        switch (hitResult.getType()) {
            case ENTITY -> {
                isEntity = true;
                entityId = ((EntityHitResult) hitResult).getEntity().getId();
            }
            case BLOCK -> blockPos = ((BlockHitResult) hitResult).getBlockPos();
        }

        return new SimpleHitResult(isEntity, blockPos, entityId);
    }
}
