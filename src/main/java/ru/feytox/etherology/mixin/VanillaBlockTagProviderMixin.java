package ru.feytox.etherology.mixin;

import net.minecraft.data.server.tag.ValueLookupTagProvider;
import net.minecraft.data.server.tag.VanillaBlockTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.feytox.etherology.DecoBlocks;

@Mixin(VanillaBlockTagProvider.class)
public class VanillaBlockTagProviderMixin {

    @Inject(method = "configure", at = @At("HEAD"))
    public void onConfigure(RegistryWrapper.WrapperLookup lookup, CallbackInfo ci) {
        // TODO: 09/04/2023 mixin -> normal impl

        VanillaBlockTagProvider it = ((VanillaBlockTagProvider)(Object) this);
        ((ValueLookupTagProvider.ObjectBuilder) it.getOrCreateTagBuilder(BlockTags.PLANKS)).add(DecoBlocks.PEACH_PLANKS);
        ((ValueLookupTagProvider.ObjectBuilder) it.getOrCreateTagBuilder(BlockTags.FENCES)).add(DecoBlocks.PEACH_FENCE);
        ((ValueLookupTagProvider.ObjectBuilder) it.getOrCreateTagBuilder(BlockTags.WOODEN_FENCES)).add(DecoBlocks.PEACH_FENCE);
        ((ValueLookupTagProvider.ObjectBuilder) it.getOrCreateTagBuilder(BlockTags.FENCE_GATES)).add(DecoBlocks.PEACH_FENCE_GATE);
        ((ValueLookupTagProvider.ObjectBuilder) it.getOrCreateTagBuilder(BlockTags.ALL_SIGNS)).add(DecoBlocks.PEACH_SIGN);
        ((ValueLookupTagProvider.ObjectBuilder) it.getOrCreateTagBuilder(BlockTags.LOGS)).add(DecoBlocks.PEACH_LOG, DecoBlocks.STRIPPED_PEACH_LOG);

    }
}
