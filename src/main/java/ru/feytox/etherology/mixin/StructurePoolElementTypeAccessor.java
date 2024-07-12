package ru.feytox.etherology.mixin;

import com.mojang.serialization.MapCodec;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePoolElementType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(StructurePoolElementType.class)
public interface StructurePoolElementTypeAccessor {

    @Invoker
    static <P extends StructurePoolElement> StructurePoolElementType<P> callRegister(String id, MapCodec<P> codec) {
        throw new UnsupportedOperationException();
    }
}
