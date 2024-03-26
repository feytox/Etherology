package ru.feytox.etherology.particle.info;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.particle.LightParticle;
import ru.feytox.etherology.particle.effects.LightParticleEffect;
import ru.feytox.etherology.particle.utility.ParticleInfo;
import ru.feytox.etherology.util.feyapi.RGBColor;

public class LightMatrixInfo extends ParticleInfo<LightParticle, LightParticleEffect> {

    private final Vec3d moveVec;

    public LightMatrixInfo(ClientWorld clientWorld, double x, double y, double z, LightParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);
        moveVec = parameters.getMoveVec();
    }

    @Override
    public @Nullable RGBColor getStartColor(Random random) {
        return new RGBColor(244, 194, 133);
    }

    @Override
    public float getScale(Random random) {
        return 0.3f;
    }

    @Override
    public void tick(LightParticle particle) {
        particle.simpleMovingTickOnVec(0.25f, moveVec);
        particle.setSpriteForAge();
    }

    @Override
    public int getMaxAge(Random random) {
        return 5;
    }
}
