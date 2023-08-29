package ru.feytox.etherology.model.custom;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.val;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import ru.feytox.etherology.magic.staff.StaffPart;
import ru.feytox.etherology.magic.staff.StaffPartInfo;
import ru.feytox.etherology.magic.staff.StaffStyle;
import ru.feytox.etherology.model.EtherologyModels;
import ru.feytox.etherology.model.ModelTransformations;
import ru.feytox.etherology.model.MultiItemModel;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class StaffModel extends MultiItemModel {

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        BakedModelManager modelManager = MinecraftClient.getInstance().getBakedModelManager();
        var modelConsumer = context.bakedModelConsumer();

        NbtCompound stackNbt = stack.getNbt();
        if (stackNbt == null) return;
        NbtList nbtList = stackNbt.getOr(StaffPartInfo.LIST_KEY, new NbtList());
        nbtList.stream().map(Optional::of)
                .map(nbtOptional -> (NbtCompound) nbtOptional.filter(nbt -> nbt instanceof NbtCompound).orElse(null))
                .map(nbt -> {
                    StaffPartInfo staffPartInfo = StaffPartInfo.of(null, null);
                    return staffPartInfo.readNbt(nbt);
                })
                .map(StaffPartInfo::toModelId)
                .map(modelManager::getModel)
                .forEach(modelConsumer);


    }

    public static void loadPartModels(Consumer<Identifier> idConsumer) {
        // TODO: 29.08.2023 replace hard code to better models loading
        val styles = Arrays.stream(StaffStyle.values()).filter(style -> !style.equals(StaffStyle.NULL)).collect(Collectors.toCollection(ObjectArrayList::new));

        Arrays.stream(StaffPart.values()).filter(part -> !part.equals(StaffPart.NULL) && !part.isHardCode())
                .flatMap(part -> styles.stream().map(style -> StaffPartInfo.of(part, style)))
                .map(StaffPartInfo::toModelId)
                .forEach(idConsumer);
        idConsumer.accept(StaffPartInfo.of(StaffPart.CORE, StaffStyle.NULL).toModelId());
        idConsumer.accept(StaffPartInfo.of(StaffPart.LENSE, StaffStyle.NULL).toModelId());
    }

    @Override
    public ModelTransformation getTransformation() {
        return ModelTransformations.DEFAULT_ITEM_TRANSFORMS;
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
