package ru.feytox.etherology.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.server.DataPackContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.feytox.etherology.data.ethersource.EtherSourceLoader;
import ru.feytox.etherology.data.feyperms.FeyPermissionLoader;

import java.util.ArrayList;
import java.util.List;

@Mixin(DataPackContents.class)
public class DataPackContentsMixin {

    @ModifyExpressionValue(method = "reload", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/DataPackContents;getContents()Ljava/util/List;"))
    private static List<ResourceReloader> injectContents(List<ResourceReloader> original) {
        original = new ArrayList<>(original);
        original.add(FeyPermissionLoader.INSTANCE);
        original.add(EtherSourceLoader.INSTANCE);
        return original;
    }
}
