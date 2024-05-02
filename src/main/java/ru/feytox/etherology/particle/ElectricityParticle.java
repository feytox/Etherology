package ru.feytox.etherology.particle;

import lombok.val;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import ru.feytox.etherology.particle.effects.ElectricityParticleEffect;
import ru.feytox.etherology.particle.utility.FeyParticle;
import ru.feytox.etherology.util.misc.FeyColor;
import ru.feytox.etherology.util.misc.RGBColor;

public class ElectricityParticle extends FeyParticle<ElectricityParticleEffect> {

    private float deltaAngle;

    public ElectricityParticle(ClientWorld clientWorld, double x, double y, double z, ElectricityParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);

        // TODO: 26.03.2024 default -> ParticleInfos

        deltaAngle = 1.0f;
        maxAge = 10;
        age = random.nextBetween(0, maxAge - 5);
        setAngle(random.nextFloat() * 360.0f);
        prevAngle = angle;
        setSpriteForAge();

        val electricityType = parameters.getElectricityType();
        switch (electricityType) {
            case MATRIX -> {
                maxAge = 6;
                age = random.nextBetween(0, 2);
                setRGB(RGBColor.of(0xF965FF));
                scale(1.75f);
                deltaAngle = 0.0f;
            }
            case JEWELRY -> {
                maxAge = 7;
                age = random.nextBetween(0, 3);
                setRGB(FeyColor.getRandomColor(RGBColor.of(0xA24CFF), RGBColor.of(0xD866FF), random));
                scale(1.1f);
            }
        }
    }

    @Override
    public void tick() {
        if (tickAge()) return;
        setSpriteForAge();
        if (deltaAngle != 0.0f) {
            prevAngle = angle;
            modifyAngle(deltaAngle);
        }
    }
}
