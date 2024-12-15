package ru.feytox.etherology.client.item.revelationView;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.client.model.custom.RevelationViewModel;
import ru.feytox.etherology.util.misc.EIdentifier;

public class RevelationViewItemRenderer implements TrinketRenderer {

    private static final Identifier TEXTURE = EIdentifier.of("textures/entity/trinket/revelation_view.png");
    private BipedEntityModel<LivingEntity> model;

    @Override
    public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        var model = getModel();
        model.setAngles(entity, limbAngle, limbDistance, animationProgress, animationProgress, headPitch);
        model.animateModel(entity, limbAngle, limbDistance, tickDelta);
        TrinketRenderer.followBodyRotations(entity, model);
        var vertexConsumer = vertexConsumers.getBuffer(model.getLayer(TEXTURE));
        model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 0xFFFFFFFF);
    }

    private BipedEntityModel<LivingEntity> getModel() {
        if (model != null) return model;
        model = new RevelationViewModel(RevelationViewModel.getTexturedModelData().createModel());
        return model;
    }
}
