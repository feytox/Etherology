package ru.feytox.etherology.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import lombok.val;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.feytox.etherology.item.LensItem;
import ru.feytox.etherology.item.StaffItem;
import ru.feytox.etherology.magic.lens.LensMode;
import ru.feytox.etherology.model.EtherologyModelProvider;
import ru.feytox.etherology.model.EtherologyModels;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Shadow @Final private ItemModels models;

    @Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z", ordinal = 0))
    private void injectGUIItemModelReplace(ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci, @Local(argsOnly = true) LocalRef<BakedModel> itemModel) {
        ModelIdentifier modelId = EtherologyModels.getReplacedModel(stack.getItem(), false);
        if (modelId == null) return;
        BakedModel newModel = models.getModelManager().getModel(modelId);
        itemModel.set(newModel);
    }

    @ModifyExpressionValue(method = "getModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemModels;getModel(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/client/render/model/BakedModel;"))
    private BakedModel injectInHandItemModelReplace(BakedModel original, @Local(argsOnly = true) ItemStack stack, @Local(argsOnly = true) LivingEntity entity) {
        ModelIdentifier modelId;

        if (entity != null && stack.getItem() instanceof StaffItem && stack.equals(entity.getActiveItem())) {
            val lensData = LensItem.getStaffLens(stack);
            if (lensData == null) return original;
            modelId = lensData.mode().equals(LensMode.STREAM) ? EtherologyModelProvider.STAFF_STREAM : EtherologyModelProvider.STAFF_CHARGE;
        }
        else modelId = EtherologyModels.getReplacedModel(stack.getItem(), true);

        if (modelId == null) return original;
        return models.getModelManager().getModel(modelId);
    }
}
