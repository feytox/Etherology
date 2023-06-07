package ru.feytox.etherology.particle.utility;

import lombok.NonNull;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;

public abstract class VerticalParticle extends VertexParticle {
    protected VerticalParticle(ClientWorld clientWorld, @NonNull Vec3d startCenter, @NonNull Vec3d endCenter, double radius) {
        super(clientWorld, startCenter.add(0, -radius, 0), startCenter.add(0, radius, 0), endCenter.add(0, radius, 0), endCenter.add(0, -radius, 0));
    }
}
