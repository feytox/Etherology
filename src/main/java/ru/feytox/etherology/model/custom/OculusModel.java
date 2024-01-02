package ru.feytox.etherology.model.custom;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import ru.feytox.etherology.model.EtherologyModels;
import ru.feytox.etherology.model.ModelComponents;
import ru.feytox.etherology.model.MultiItemModel;
import ru.feytox.etherology.util.feyapi.EIdentifier;

import java.util.List;

public class OculusModel extends MultiItemModel {
    public static final ModelIdentifier OCULUS_BASE = EtherologyModels.createItemModelId("oculus_base");
    public static final ModelIdentifier OCULUS_LENS = EtherologyModels.createItemModelId("oculus_lens");


    @Override
    public ModelIdentifier getModelForParticles() {
        return OCULUS_BASE;
    }

    @Override
    protected List<ModelIdentifier> getModels() {
        return ObjectArrayList.of(OCULUS_BASE, OCULUS_LENS);
    }

    @Override
    public ModelTransformation getTransformation() {
        return ModelComponents.loadTransformFromJson(new EIdentifier("models/item/oculus_base"));
    }
}
