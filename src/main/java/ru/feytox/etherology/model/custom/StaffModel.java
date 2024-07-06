package ru.feytox.etherology.model.custom;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext.BakedModelConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import ru.feytox.etherology.magic.staff.StaffPart;
import ru.feytox.etherology.magic.staff.StaffPartInfo;
import ru.feytox.etherology.model.MultiItemModel;
import ru.feytox.etherology.registry.misc.EtherologyComponents;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
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
        var modelConsumer = context.bakedModelConsumer();

        val staff = EtherologyComponents.STAFF.get(stack);
        Map<StaffPart, StaffPartInfo> parts = staff.getParts();

        if (parts == null) return;
        for (StaffPartInfo partInfo : parts.values()) {
            ModelIdentifier modelId = partInfo.toModelId();
            BakedModel model = modelManager.getModel(modelId);
            modelConsumer.accept(model);
        }
    }

    public static void loadPartModels(Consumer<Identifier> idConsumer) {
        StaffPartInfo.generateAll().stream().map(StaffPartInfo::toModelId).forEach(idConsumer);
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
