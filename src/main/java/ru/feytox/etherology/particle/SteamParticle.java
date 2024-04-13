package ru.feytox.etherology.particle;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.particle.effects.MovingParticleEffect;
import ru.feytox.etherology.particle.utility.MovingParticle;
import ru.feytox.etherology.util.feyapi.RGBColor;

public class SteamParticle extends MovingParticle<MovingParticleEffect> {

    private final Vec3d moveVec;

    public SteamParticle(ClientWorld clientWorld, double x, double y, double z, MovingParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);
        moveVec = parameters.getMoveVec();

        scale(0.66f);
        maxAge = 20 + random.nextBetween(0, 15);
        setRandomColor(RGBColor.of(0xB668FF), RGBColor.of(0xEC49D9));
        setSpriteForAgeCycle(5);
    }

    @Override
    public void tick() {
        setSpriteForAgeCycle(5);
        timeAcceleratedMovingTickOnVec(0.0078f, 0.010f, moveVec);
    }
}
