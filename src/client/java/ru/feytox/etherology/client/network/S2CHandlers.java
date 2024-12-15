package ru.feytox.etherology.client.network;

import lombok.val;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import ru.feytox.etherology.network.animation.StartBlockAnimS2C;
import ru.feytox.etherology.network.animation.StopBlockAnimS2C;
import ru.feytox.etherology.network.animation.SwitchBlockAnimS2C;
import ru.feytox.etherology.network.interaction.RedstoneLensStreamS2C;
import ru.feytox.etherology.network.interaction.RemoveBlockEntityS2C;
import ru.feytox.etherology.particle.effects.SimpleParticleEffect;
import ru.feytox.etherology.registry.particle.EtherParticleTypes;
import ru.feytox.etherology.util.deprecated.EVec3d;
import ru.feytox.etherology.util.gecko.EGeo2BlockEntity;
import ru.feytox.etherology.util.gecko.EGeoBlockEntity;

public class S2CHandlers {

    public static void receiveRemoveBlockEntity(RemoveBlockEntityS2C packet, ClientPlayNetworking.Context context) {
        MinecraftClient client = context.client();

        client.execute(() -> {
            if (client.world == null) return;
            client.world.removeBlockEntity(packet.pos());
        });
    }

    public static void receiveRedstoneStream(RedstoneLensStreamS2C packet, ClientPlayNetworking.Context context) {
        MinecraftClient client = context.client();

        client.execute(() -> {
            ClientWorld world = client.world;
            if (world == null) return;

            val effect = new SimpleParticleEffect(EtherParticleTypes.REDSTONE_FLASH);
            EVec3d.lineOf(packet.start(), packet.end(), 0.5d)
                    .forEach(pos -> effect.spawnParticles(world, 1, 0, pos));
            if (packet.isMiss()) return;

            val redstoneEffect = new SimpleParticleEffect(EtherParticleTypes.REDSTONE_STREAM);
            redstoneEffect.spawnParticles(world, world.random.nextBetween(8, 12), 0.1, packet.end());
        });
    }

    public static void receiveSwitchBlockAnim(SwitchBlockAnimS2C packet, ClientPlayNetworking.Context context) {
        MinecraftClient client = context.client();

        client.execute(() -> {
            if (client.world == null) return;
            if (!(client.world.getBlockEntity(packet.pos()) instanceof EGeo2BlockEntity animatable)) return;

            animatable.stopClientAnim(packet.stopAnim());
            animatable.triggerAnimByName(packet.startAnim());
        });
    }

    public static void receiveStopBlockAnim(StopBlockAnimS2C packet, ClientPlayNetworking.Context context) {
        MinecraftClient client = context.client();

        client.execute(() -> {
            if (client.world == null) return;
            var be = client.world.getBlockEntity(packet.pos());

            if (be instanceof EGeoBlockEntity geoBlock)
                geoBlock.stopClientAnim(packet.animName());
            else if (be instanceof EGeo2BlockEntity geoBlock)
                geoBlock.stopClientAnim(packet.animName());
        });
    }

    public static void receiveStartBlockAnim(StartBlockAnimS2C packet, ClientPlayNetworking.Context context) {
        MinecraftClient client = context.client();

        client.execute(() -> {
            if (client.world == null) return;
            var be = client.world.getBlockEntity(packet.pos());

            if (be instanceof EGeoBlockEntity eGeoBlock)
                eGeoBlock.triggerAnim(packet.animName());
        });
    }
}
