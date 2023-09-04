package ru.feytox.etherology.mixin;

import com.google.common.collect.HashBiMap;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.texture.atlas.AtlasSourceManager;
import net.minecraft.client.texture.atlas.AtlasSourceType;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.feytox.etherology.util.future.EtherAtlasSources;

@Mixin(AtlasSourceManager.class)
public class AtlasSourceManagerMixin {

    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/HashBiMap;create()Lcom/google/common/collect/HashBiMap;"))
    private static HashBiMap<Identifier, AtlasSourceType> injectEtherAtlasSource(HashBiMap<Identifier, AtlasSourceType> original) {
//        original.put(new Identifier("ether_palette_permutations"), EtherAtlasSources.PALETTED_PERMUTATIONS);
        return original;
    }
}
