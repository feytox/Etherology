package ru.feytox.etherology.model.custom;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.screen.PlayerScreenHandler;
import ru.feytox.etherology.model.EtherologyModels;
import ru.feytox.etherology.model.ModelTransformations;
import ru.feytox.etherology.model.MultiItemModel;
import ru.feytox.etherology.util.feyapi.EIdentifier;

public class OculusModel extends MultiItemModel {
    public static final ModelIdentifier OCULUS_BASE = EtherologyModels.createItemModelId("oculus_base");
    public static final ModelIdentifier OCULUS_LENS = EtherologyModels.createItemModelId("oculus_lens");

    public OculusModel() {
        super(OCULUS_BASE, OCULUS_LENS);
    }

    @Override
    public Sprite getParticleSprite() {
        return MinecraftClient.getInstance().getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(new EIdentifier("item/oculus_in_hand"));
    }

    @Override
    public ModelTransformation getTransformation() {
        return ModelTransformations.loadTransformFromJson(new EIdentifier("models/item/oculus_base"));
    }
}
