package ru.feytox.etherology.mixin;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TreeDecoratorType.class)
public interface TreeDecoratorTypeAccessor {
    @Invoker
    static <P extends TreeDecorator> TreeDecoratorType<P> callRegister(String id, Codec<P> codec) {
        throw new UnsupportedOperationException();
    }
}
