package ru.feytox.etherology.item.revelationView;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.api.client.TrinketRenderer;
import lombok.val;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Wearable;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import ru.feytox.etherology.model.custom.RevelationViewModel;
import ru.feytox.etherology.registry.item.ArmorItems;
import ru.feytox.etherology.util.feyapi.EIdentifier;

public class RevelationViewItem extends TrinketItem implements Wearable, TrinketRenderer {

    private static final Identifier TEXTURE = new EIdentifier("textures/entity/trinket/revelation_view.png");
    private BipedEntityModel<LivingEntity> model;

    public RevelationViewItem() {
        super(new Settings().maxCount(1));
    }

    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        World world = entity.getWorld();
        if (world == null || !world.isClient) return;
        if (!(entity instanceof PlayerEntity player)) return;
        RevelationViewRenderer.tickData(world, player);
    }

    public static boolean isEquipped(LivingEntity entity) {
        val trinket = TrinketsApi.getTrinketComponent(entity).orElse(null);
        if (trinket == null) return false;
        return trinket.isEquipped(ArmorItems.REVELATION_VIEW);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        BipedEntityModel<LivingEntity> model = getModel();
        model.setAngles(entity, limbAngle, limbDistance, animationProgress, animationProgress, headPitch);
        model.animateModel(entity, limbAngle, limbDistance, tickDelta);
        TrinketRenderer.followBodyRotations(entity, model);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(model.getLayer(TEXTURE));
        model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
    }

    @Environment(EnvType.CLIENT)
    private BipedEntityModel<LivingEntity> getModel() {
        if (model != null) return model;
        model = new RevelationViewModel(RevelationViewModel.getTexturedModelData().createModel());
        return model;
    }
}
