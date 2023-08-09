package ru.feytox.etherology.particle.utility;

import lombok.NonNull;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.particle.effects.misc.FeyParticleEffect;

public class VerticalParticle<T extends FeyParticleEffect<T>> extends VertexParticle<T> {
    public VerticalParticle(ClientWorld clientWorld, @NonNull Vec3d startCenter, @NonNull Vec3d endCenter, double radius, T parameters, SpriteProvider spriteProvider) {
        super(clientWorld, startCenter.add(0, -radius, 0), startCenter.add(0, radius, 0), endCenter.add(0, radius, 0), endCenter.add(0, -radius, 0), parameters, spriteProvider);
    }
}
