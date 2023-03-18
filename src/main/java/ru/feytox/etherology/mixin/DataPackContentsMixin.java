package ru.feytox.etherology.mixin;

import net.minecraft.resource.ResourceReloader;
import net.minecraft.server.DataPackContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.feytox.etherology.feyperms.FeyPermissionLoader;

import java.util.ArrayList;
import java.util.List;

@Mixin(DataPackContents.class)
public class DataPackContentsMixin {

    @Redirect(method = "reload", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/DataPackContents;getContents()Ljava/util/List;"))
    private static List<ResourceReloader> injectContents(DataPackContents instance) {
        List<ResourceReloader> contents = new ArrayList<>(instance.getContents());
        contents.add(FeyPermissionLoader.INSTANCE);
        return contents;
    }

}
