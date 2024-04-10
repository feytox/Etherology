package ru.feytox.etherology.particle;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.feytox.etherology.item.OculusItem;
import ru.feytox.etherology.magic.zones.EssenceZoneType;
import ru.feytox.etherology.particle.effects.ZoneParticleEffect;
import ru.feytox.etherology.particle.utility.MovingParticle;

public class ZoneParticle extends MovingParticle<ZoneParticleEffect> {
    private final Vec3d endPos;

    public ZoneParticle(ClientWorld clientWorld, double x, double y, double z, ZoneParticleEffect parameters, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z, parameters, spriteProvider);
        endPos = parameters.getEndPos();
        setSpriteForAge();
        maxAge = 20 * random.nextBetween(1, 2);
        scale(0.15f);

        EssenceZoneType zoneType = parameters.getZoneType();
        setRandomColor(zoneType.getStartColor(), zoneType.getEndColor());
    }

    @Override
    public void tick() {
        setSpriteForAge();
        acceleratedMovingTick(0.2f, 0.3f, true, endPos);

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        float alpha = MathHelper.lerp(0.25f, getAlpha(), OculusItem.isUsing(player) ? 1.0f : 0.0f);
        setAlpha(alpha);
    }
}
