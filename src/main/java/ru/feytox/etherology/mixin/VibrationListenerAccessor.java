package ru.feytox.etherology.mixin;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.listener.VibrationListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(VibrationListener.class)
public interface VibrationListenerAccessor {
    @Invoker
    static boolean callIsOccluded(World world, Vec3d start, Vec3d end) {
        throw new UnsupportedOperationException();
    }
}
