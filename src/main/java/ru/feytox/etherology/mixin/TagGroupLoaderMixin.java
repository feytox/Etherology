package ru.feytox.etherology.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import ru.feytox.etherology.util.misc.TagExcludeUtil;

import java.util.List;
import java.util.Map;

@Mixin(TagGroupLoader.class)
public class TagGroupLoaderMixin {

    @Shadow @Final private String dataType;

    @ModifyReturnValue(method = "loadTags", at = @At("RETURN"))
    private Map<Identifier, List<TagGroupLoader.TrackedEntry>> excludeTags(Map<Identifier, List<TagGroupLoader.TrackedEntry>> original) {
        return TagExcludeUtil.applyExcluding(original, dataType);
    }
}
