package ru.feytox.etherology.client.mixin;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.BlockStatesLoader;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.feytox.etherology.client.model.EtherologyModelProvider;
import ru.feytox.etherology.client.model.EtherologyModels;
import ru.feytox.etherology.registry.item.ToolItems;

import java.util.List;
import java.util.Map;

@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin {

    @Shadow protected abstract void loadItemModel(ModelIdentifier id);

    @Inject(method = "<init>(Lnet/minecraft/client/color/block/BlockColors;Lnet/minecraft/util/profiler/Profiler;Ljava/util/Map;Ljava/util/Map;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelLoader;loadItemModel(Lnet/minecraft/client/util/ModelIdentifier;)V", ordinal = 0))
    private void injectInHandModels(BlockColors blockColors, Profiler profiler, Map<Identifier, JsonUnbakedModel> jsonUnbakedModels, Map<Identifier, List<BlockStatesLoader.SourceTrackedData>> blockStates, CallbackInfo ci) {
        // TODO: 02.01.2024 simplify
        loadItemModel(EtherologyModels.getReplacedModel(ToolItems.BROADSWORD, true));
        loadItemModel(EtherologyModels.getReplacedModel(ToolItems.OCULUS, true));
        loadItemModel(EtherologyModelProvider.STAFF);
        loadItemModel(EtherologyModelProvider.STAFF_STREAM);
        loadItemModel(EtherologyModelProvider.STAFF_CHARGE);
    }
}
