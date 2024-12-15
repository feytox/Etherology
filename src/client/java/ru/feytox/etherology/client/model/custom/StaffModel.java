package ru.feytox.etherology.client.model.custom;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import ru.feytox.etherology.client.model.EtherologyModels;
import ru.feytox.etherology.client.model.MultiItemModel;
import ru.feytox.etherology.magic.staff.StaffComponent;
import ru.feytox.etherology.magic.staff.StaffPartInfo;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.List;
import java.util.function.Supplier;

import static ru.feytox.etherology.client.model.EtherologyModelProvider.STAFF_CORE;

@RequiredArgsConstructor
public class StaffModel extends MultiItemModel {

    private final ModelTransformation transform;

    public static ModelIdentifier toModelId(StaffPartInfo partInfo) {
        String suffix = partInfo.part().getName();
        if (!partInfo.firstPattern().isEmpty()) suffix += "_" + partInfo.firstPattern().getName();
        if (!partInfo.secondPattern().isEmpty()) suffix += "_" + partInfo.secondPattern().getName();
        return EtherologyModels.createItemModelId("item/staff_" + suffix);
    }

    public static Identifier toTextureId(StaffPartInfo partInfo) {
        var prefix = partInfo.part().isStyled() ? "trims/textures/" : "item/";
        var suffix = partInfo.part().isStyled() ? "style" : partInfo.part().getName();
        if (!partInfo.firstPattern().isEmpty()) suffix += "_" + partInfo.firstPattern().getName();
        if (!partInfo.secondPattern().isEmpty()) suffix += "_" + partInfo.secondPattern().getName();
        return EIdentifier.of(prefix + "staff_" + suffix);
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier,
                              RenderContext context) {
        BakedModelManager modelManager =
                MinecraftClient.getInstance().getBakedModelManager();

        val staffData = StaffComponent.get(stack).orElse(null);
        if (staffData == null) return;

        staffData.parts().values().forEach(partInfo -> {
            ModelIdentifier modelId = toModelId(partInfo);
            BakedModel model = modelManager.getModel(modelId.id());
            model.emitItemQuads(stack, randomSupplier, context);
        });
    }

    public static void loadPartModels(ModelLoadingPlugin.Context context) {
        for (StaffPartInfo staffPartInfo : StaffPartInfo.generateAll()) {
            ModelIdentifier modelId = toModelId(staffPartInfo);
            Identifier id = modelId.id();
            context.addModels(id);
        }
    }

    @Override
    public ModelTransformation getTransformation() {
        return transform;
    }

    @Override
    public ModelIdentifier getModelForParticles() {
        return STAFF_CORE;
    }

    @Override
    protected List<ModelIdentifier> getModels() {
        return ObjectArrayList.of();
    }
}
