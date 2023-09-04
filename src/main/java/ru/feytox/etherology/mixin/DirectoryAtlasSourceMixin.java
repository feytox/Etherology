package ru.feytox.etherology.mixin;

import net.minecraft.client.texture.atlas.AtlasSource;
import net.minecraft.client.texture.atlas.DirectoryAtlasSource;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.feytox.etherology.util.future.EPalettePermutationsAtlasSource;

@Mixin(DirectoryAtlasSource.class)
public class DirectoryAtlasSourceMixin {

    @Shadow @Final private String source;

    @Inject(method = "load", at = @At("HEAD"))
    private void injectTrimsToItems(ResourceManager resourceManager, AtlasSource.SpriteRegions regions, CallbackInfo ci) {
        if (!source.equals("item")) return;
        EPalettePermutationsAtlasSource trimsSource = new EPalettePermutationsAtlasSource();
        trimsSource.load(resourceManager, regions);
    }
}
