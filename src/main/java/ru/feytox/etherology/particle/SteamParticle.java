package ru.feytox.etherology.particle;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.particle.types.MovingParticleEffect;
import ru.feytox.etherology.particle.utility.MovingParticle;
import ru.feytox.etherology.util.feyapi.RGBColor;

public class SteamParticle extends MovingParticle<MovingParticleEffect> {

    private final Vec3d endPos;

    public SteamParticle(ClientWorld clientWorld, double x, double y, double z, MovingParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);
        endPos = parameters.getMoveVec().add(startPos);

        scale(0.66f);
        maxAge = 15 + random.nextBetween(0, 15);
        setRandomColor(RGBColor.of(0xB668FF), RGBColor.of(0xEC49D9));
        setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        setSpriteForAge(spriteProvider);
        timeAcceleratedMovingTick(0.01f, 0.015f, endPos, false);
    }
}
