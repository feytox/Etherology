package ru.feytox.etherology.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Arm;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.feytox.etherology.animation.TriggerablePlayerAnimation;
import ru.feytox.etherology.item.HammerItem;
import ru.feytox.etherology.network.EtherologyNetwork;
import ru.feytox.etherology.network.animation.HammerSwingC2S;
import ru.feytox.etherology.particle.ShockwaveParticle;
import ru.feytox.etherology.registry.util.EtherSounds;
import ru.feytox.etherology.util.feyapi.IAnimatedPlayer;

import static ru.feytox.etherology.animation.TriggerAnimations.*;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow @Nullable public ClientPlayerEntity player;
    @Shadow @Nullable public ClientWorld world;

    @Inject(method = "doAttack", at = @At("HEAD"))
    private void hammerFullAttack(CallbackInfoReturnable<Boolean> cir) {
        if (player == null) return;
        if (!HammerItem.checkHammer(player)) return;

        float cooldown = player.getAttackCooldownProgress(0.0F);

        HammerSwingC2S packet = new HammerSwingC2S(cooldown);
        EtherologyNetwork.sendToServer(packet);

        if (!(player instanceof IAnimatedPlayer animatedPlayer)) return;
        ClientWorld world = player.clientWorld;

        TriggerablePlayerAnimation animation = player.getMainArm().equals(Arm.LEFT) ? LEFT_HAMMER_HIT : RIGHT_HAMMER_HIT;
        if (cooldown != 1.0F) {
            animation = player.getMainArm().equals(Arm.LEFT) ? LEFT_HAMMER_HIT_WEAK : RIGHT_HAMMER_HIT_WEAK;
            animation.play(animatedPlayer, 0, null);
            return;
        }
        animation.play(animatedPlayer, 0, null);

        float pitchVal = 0.9f + world.random.nextFloat() * 0.2f;
        player.playSound(EtherSounds.HAMMER_SWING, SoundCategory.PLAYERS, 0.5f, pitchVal);

        ShockwaveParticle.spawnParticle(world, player);
    }
}
