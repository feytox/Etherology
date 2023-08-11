package ru.feytox.etherology.particle;

import lombok.NonNull;
import lombok.val;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import ru.feytox.etherology.particle.effects.ElectricityParticleEffect;
import ru.feytox.etherology.particle.effects.MovingParticleEffect;
import ru.feytox.etherology.particle.subtypes.ElectricitySubtype;
import ru.feytox.etherology.particle.utility.VerticalParticle;

import static ru.feytox.etherology.registry.particle.ServerParticleTypes.THUNDER_ZAP;

public class PealWaveParticle extends VerticalParticle<MovingParticleEffect> {
    public PealWaveParticle(ClientWorld clientWorld, double x, double y, double z, MovingParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, new Vec3d(x, y, z), parameters.getMoveVec(), 0.5, parameters, spriteProvider);
        maxAge = 10;
        setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        if (tickAge()) return;
        setSpriteForAge(spriteProvider);
    }

    public static void spawnWave(ClientWorld world, @NonNull Vec3d start, @NonNull Vec3d end) {
        MovingParticleEffect effect = new MovingParticleEffect(THUNDER_ZAP, end);
        world.addImportantParticle(effect, start.x, start.y, start.z, 0, 0, 0);
        spawnElectricity(world, start);
        spawnElectricity(world, end);
    }

    private static void spawnElectricity(ClientWorld world, Vec3d entityCenter) {
        Random random = world.getRandom();
        val effect = ElectricityParticleEffect.of(random, ElectricitySubtype.PEAL);
        effect.spawnParticles(world, random.nextBetween(3, 6), 0.5, entityCenter);
    }
}
