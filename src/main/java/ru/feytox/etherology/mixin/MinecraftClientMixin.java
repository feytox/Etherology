package ru.feytox.etherology.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.feytox.etherology.animation.TriggerAnimations;
import ru.feytox.etherology.animation.TriggerPlayerAnimation;
import ru.feytox.etherology.item.HammerItem;
import ru.feytox.etherology.item.TwoHandheldSword;
import ru.feytox.etherology.network.EtherologyNetwork;
import ru.feytox.etherology.network.interaction.TwoHandHeldAttackC2S;
import ru.feytox.etherology.registry.util.EtherSounds;
import ru.feytox.etherology.util.compatibility.CompatFlags;
import ru.feytox.etherology.util.misc.EtherologyPlayer;
import ru.feytox.etherology.util.misc.ShockwaveUtil;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow @Nullable public ClientPlayerEntity player;
    @Shadow @Nullable public ClientWorld world;

    @Inject(method = "doAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/HitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;"))
    private void hammerFullAttack(CallbackInfoReturnable<Boolean> cir) {
        if (player == null) return;
        if (!TwoHandheldSword.check(player, TwoHandheldSword.class)) return;
        MinecraftClient client = ((MinecraftClient) (Object) this);

        float cooldown = player.getAttackCooldownProgress(0.0F);
        HitResult hitResult = client.crosshairTarget;
        boolean attackGround = hitResult != null && !hitResult.getType().equals(HitResult.Type.ENTITY);
        boolean isStrongAttack = cooldown > 0.9f && !attackGround;
        boolean isHammer = HammerItem.checkHammer(player);

        TwoHandHeldAttackC2S packet = new TwoHandHeldAttackC2S(attackGround, isHammer);
        EtherologyNetwork.sendToServer(packet);

        if (!(player instanceof EtherologyPlayer animatedPlayer)) return;
        ClientWorld world = player.clientWorld;

        if (!CompatFlags.BETTER_COMBAT.isEnable()) {
            TriggerPlayerAnimation animation = TriggerAnimations.getTwohandheldAnim(player.getMainArm(), isHammer, isStrongAttack);
            animation.play(animatedPlayer, 0, null);
        }
        if (!isStrongAttack || !isHammer) return;

        player.playSound(EtherSounds.HAMMER_SWING, SoundCategory.PLAYERS, 0.5f, 0.9f);
        ShockwaveUtil.spawnShockParticles(world, player);
    }
}
