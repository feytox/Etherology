package ru.feytox.etherology.particle;

import lombok.val;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import ru.feytox.etherology.particle.effects.ElectricityParticleEffect;
import ru.feytox.etherology.particle.subtypes.ElectricitySubtype;
import ru.feytox.etherology.particle.utility.FeyParticle;
import ru.feytox.etherology.util.feyapi.FeyColor;
import ru.feytox.etherology.util.feyapi.RGBColor;

public class ElectricityParticle extends FeyParticle<ElectricityParticleEffect> {

    public ElectricityParticle(ClientWorld clientWorld, double x, double y, double z, ElectricityParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);

        maxAge = 10;
        age = random.nextBetween(0, maxAge - 5);
        setSpriteForAge();
        setAngle(random.nextFloat() * 360.0f);

        val electricityType = parameters.getElectricityType();
        if (electricityType.equals(ElectricitySubtype.JEWELRY)) {
            maxAge = 7;
            age = random.nextBetween(0, 3);
        }

        switch (electricityType) {
            case MATRIX -> scale(1.4f);
            case PEAL -> {
                setRGB(RGBColor.of(0x00ffe5));
                scale(2.5f);
            }
            case JEWELRY -> {
                setRGB(FeyColor.getRandomColor(RGBColor.of(0xA24CFF), RGBColor.of(0xD866FF), random));
                scale(1.1f);
            }
        }
        Float instability = parameters.getInstability();
        if (instability != null && instability != -1.0f) {
            setRGB(255 - (104 * instability / 33.33369d), 115, 255);
        }
    }

    @Override
    public void tick() {
        if (tickAge()) return;
        setSpriteForAge();
        modifyAngle(1f);
    }
}
