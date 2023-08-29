package ru.feytox.etherology.model;

import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelVariantProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.model.custom.OculusModel;
import ru.feytox.etherology.model.custom.StaffModel;
import ru.feytox.etherology.registry.item.ToolItems;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Supplier;

import static ru.feytox.etherology.model.custom.OculusModel.OCULUS_BASE;
import static ru.feytox.etherology.model.custom.OculusModel.OCULUS_LENS;

public class EtherologyModelProvider implements ModelVariantProvider {

    private static final ModelIdentifier OCULUS_IN_HAND = EtherologyModels.createItemModelId("oculus_in_hand");
    public static final ModelIdentifier ETHER_STAFF = EtherologyModels.createItemModelId(ToolItems.ETHER_STAFF.toString());

    public static void register() {
        ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, identifierConsumer) -> {
            identifierConsumer.accept(OCULUS_BASE);
            identifierConsumer.accept(OCULUS_LENS);
            StaffModel.loadPartModels(identifierConsumer);
        });

        ModelLoadingRegistry.INSTANCE.registerVariantProvider(resourceManager -> new EtherologyModelProvider());
    }

    @Override
    public @Nullable UnbakedModel loadModelVariant(ModelIdentifier modelId, ModelProviderContext context) {
        if (modelId.equals(OCULUS_IN_HAND)) return new UnbakedMultiItemModel(OculusModel::new);
        if (modelId.equals(ETHER_STAFF)) return new UnbakedMultiItemModel(StaffModel::new);
        return null;

    }

    private record UnbakedMultiItemModel(Supplier<MultiItemModel> modelSupplier) implements UnbakedModel {

        @Override
        public Collection<Identifier> getModelDependencies() {
            return Collections.emptyList();
        }

        @Override
        public void setParents(Function<Identifier, UnbakedModel> modelLoader) {}

        @Nullable
        @Override
        public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
            return modelSupplier.get();
        }
    }
}
