package ru.feytox.etherology.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import ru.feytox.etherology.item.GlaiveItem;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Shadow @Final private MinecraftClient client;

    @ModifyReturnValue(method = "hasExtendedReach", at = @At("RETURN"))
    private boolean extendReachWithGlaive(boolean original) {
        ClientPlayerEntity player = client.player;
        return original || GlaiveItem.checkGlaive(player);
    }
}
