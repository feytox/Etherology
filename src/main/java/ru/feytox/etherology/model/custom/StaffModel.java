package ru.feytox.etherology.model.custom;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.val;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import ru.feytox.etherology.magic.staff.StaffPart;
import ru.feytox.etherology.magic.staff.StaffPartInfo;
import ru.feytox.etherology.model.EtherologyModels;
import ru.feytox.etherology.model.ModelTransformations;
import ru.feytox.etherology.model.MultiItemModel;
import ru.feytox.etherology.registry.util.EtherologyComponents;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class StaffModel extends MultiItemModel {

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        BakedModelManager modelManager = MinecraftClient.getInstance().getBakedModelManager();
        var modelConsumer = context.bakedModelConsumer();

        val staff = EtherologyComponents.STAFF.get(stack);
        Map<StaffPart, StaffPartInfo> parts = staff.getParts();
        if (parts == null) return;
        parts.values().stream()
                .map(StaffPartInfo::toModelId)
                .map(modelManager::getModel)
                .forEach(modelConsumer);
    }

    public static void loadPartModels(Consumer<Identifier> idConsumer) {
        StaffPartInfo.generateAll().stream().map(StaffPartInfo::toModelId).forEach(idConsumer);
    }

    @Override
    public ModelTransformation getTransformation() {
        return ModelTransformations.STAFF_ITEM_TRANSFORM;
    }

    @Override
    public ModelIdentifier getModelForParticles() {
        return EtherologyModels.createItemModelId("staff_core");
    }

    @Override
    protected List<ModelIdentifier> getModels() {
        return ObjectArrayList.of();
    }
}
