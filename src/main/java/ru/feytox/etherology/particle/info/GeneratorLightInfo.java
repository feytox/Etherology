package ru.feytox.etherology.particle.info;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import ru.feytox.etherology.item.OculusItem;
import ru.feytox.etherology.particle.LightParticle;
import ru.feytox.etherology.particle.effects.LightParticleEffect;
import ru.feytox.etherology.particle.utility.ParticleInfo;
import ru.feytox.etherology.util.feyapi.RGBColor;

public class GeneratorLightInfo extends ParticleInfo<LightParticle, LightParticleEffect> {

    private final Vec3d endPos;

    public GeneratorLightInfo(ClientWorld clientWorld, double x, double y, double z, LightParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);

        endPos = parameters.getMoveVec();
    }

    @Override
    public void extraInit(LightParticle particle) {
        particle.setAlpha(0);
        super.extraInit(particle);
    }

    @Override
    public RGBColor getStartColor(Random random) {
        return new RGBColor(244, 194, 133);
    }

    @Override
    public float getScale(Random random) {
        return 0.3f;
    }

    @Override
    public void tick(LightParticle particle) {
        particle.acceleratedMovingTick(0.05f, 0.5f, true, endPos);
        particle.setSpriteForAge();

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        float alpha = MathHelper.lerp(0.25f, particle.getAlpha(), OculusItem.isUsingOculus(player) ? 1.0f : 0.0f);
        particle.setAlpha(alpha);
    }

    @Override
    public int getMaxAge(Random random) {
        return 80;
    }
}
