package ru.feytox.etherology.model.custom;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;
import ru.feytox.etherology.magic.staff.StaffComponent;
import ru.feytox.etherology.magic.staff.StaffPartInfo;
import ru.feytox.etherology.model.MultiItemModel;

import java.util.List;
import java.util.function.Supplier;

import static ru.feytox.etherology.model.EtherologyModelProvider.STAFF_CORE;

@RequiredArgsConstructor
public class StaffModel extends MultiItemModel {

    private final ModelTransformation transform;

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier,
                              RenderContext context) {
        BakedModelManager modelManager =
                MinecraftClient.getInstance().getBakedModelManager();

        val staffData = StaffComponent.get(stack).orElse(null);
        if (staffData == null) return;

        staffData.parts().values().forEach(partInfo -> {
            ModelIdentifier modelId = partInfo.toModelId();
            modelManager.getModel(modelId).emitItemQuads(stack, randomSupplier, context);
        });
    }

    public static void loadPartModels(ModelLoadingPlugin.Context context) {
        StaffPartInfo.generateAll().stream().map(StaffPartInfo::toModelId).forEach(context::addModels);
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
