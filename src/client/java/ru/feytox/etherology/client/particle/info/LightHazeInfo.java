package ru.feytox.etherology.client.particle.info;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.client.particle.LightParticle;
import ru.feytox.etherology.client.particle.utility.ParticleInfo;
import ru.feytox.etherology.particle.effects.LightParticleEffect;
import ru.feytox.etherology.util.misc.RGBColor;

@Deprecated
public class LightHazeInfo extends ParticleInfo<LightParticle, LightParticleEffect> {

    private Vec3d moveVec;

    public LightHazeInfo(ClientWorld clientWorld, double x, double y, double z, LightParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);

        Random random = clientWorld.getRandom();
        moveVec = new Vec3d(random.nextDouble()*14-7, random.nextDouble()*20+10, random.nextDouble()*14-7)
                .multiply(1/250f);
    }

    @Override
    public @Nullable RGBColor getStartColor(Random random) {
        return RGBColor.of(0xE16BFF);
    }

    @Override
    public float getScale(Random random) {
        return 0.5f;
    }

    @Override
    public void tick(LightParticle particle) {
        moveVec = moveVec.add(0, -0.0075, 0);
        particle.movingTick(moveVec);
        particle.setSpriteForAge();
    }

    @Override
    public int getMaxAge(Random random) {
        return random.nextBetween(7, 15);
    }
}
