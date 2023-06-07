package ru.feytox.etherology.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Arm;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.feytox.etherology.animation.TriggerablePlayerAnimation;
import ru.feytox.etherology.item.HammerItem;
import ru.feytox.etherology.network.EtherologyNetwork;
import ru.feytox.etherology.network.interaction.HammerAttackC2S;
import ru.feytox.etherology.particle.ShockwaveParticle;
import ru.feytox.etherology.registry.util.EtherSounds;
import ru.feytox.etherology.util.feyapi.IAnimatedPlayer;
import ru.feytox.etherology.util.feyapi.ShockwaveUtil;

import static ru.feytox.etherology.animation.TriggerAnimations.*;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow @Nullable public ClientPlayerEntity player;
    @Shadow @Nullable public ClientWorld world;

    @Inject(method = "doAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/HitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;"))
    private void hammerFullAttack(CallbackInfoReturnable<Boolean> cir) {
        if (player == null) return;
        if (!HammerItem.checkHammer(player)) return;
        MinecraftClient client = ((MinecraftClient) (Object) this);

        float cooldown = player.getAttackCooldownProgress(0.0F);
        HitResult hitResult = client.crosshairTarget;
        boolean attackGround = hitResult != null && !hitResult.getType().equals(HitResult.Type.ENTITY);

        HammerAttackC2S packet = new HammerAttackC2S(attackGround);
        EtherologyNetwork.sendToServer(packet);

        if (cooldown > 0.9f && attackGround) {
            ShockwaveUtil.onFullAttack(player);
            if (hitResult.getType().equals(HitResult.Type.BLOCK)) player.resetLastAttackedTicks();
        }

        if (!(player instanceof IAnimatedPlayer animatedPlayer)) return;
        ClientWorld world = player.clientWorld;

        TriggerablePlayerAnimation animation = player.getMainArm().equals(Arm.LEFT) ? LEFT_HAMMER_HIT : RIGHT_HAMMER_HIT;
        if (cooldown <= 0.9F) {
            animation = player.getMainArm().equals(Arm.LEFT) ? LEFT_HAMMER_HIT_WEAK : RIGHT_HAMMER_HIT_WEAK;
            animation.play(animatedPlayer, 0, null);
            return;
        }
        animation.play(animatedPlayer, 0, null);

        float pitchVal = 0.9f + world.random.nextFloat() * 0.2f;
        player.playSound(EtherSounds.HAMMER_SWING, SoundCategory.PLAYERS, 0.5f, pitchVal);

        ShockwaveParticle.spawnShockParticles(world, player);
    }
}
