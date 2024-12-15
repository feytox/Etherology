package ru.feytox.etherology.client.model;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.client.model.custom.OculusModel;
import ru.feytox.etherology.client.model.custom.StaffModel;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Supplier;

import static ru.feytox.etherology.client.model.custom.OculusModel.OCULUS_BASE;
import static ru.feytox.etherology.client.model.custom.OculusModel.OCULUS_LENS;

public class EtherologyModelProvider {

    private static final ModelIdentifier OCULUS_IN_HAND = EtherologyModels.createItemModelId("oculus_in_hand");
    public static final ModelIdentifier STAFF = EtherologyModels.createItemModelId("staff");
    public static final ModelIdentifier STAFF_STREAM = EtherologyModels.createItemModelId(STAFF.id().getPath() + "_stream");
    public static final ModelIdentifier STAFF_CHARGE = EtherologyModels.createItemModelId(STAFF.id().getPath() + "_charge");
    public static final ModelIdentifier STAFF_CORE = EtherologyModels.createItemModelId("item/staff_core_oak");

    public static void register() {
        ModelLoadingPlugin.register(context -> {
            context.addModels(OCULUS_BASE.id(), OCULUS_LENS.id(), STAFF_CORE.id());
            context.addModels(OCULUS_IN_HAND.id(), STAFF.id(), STAFF_CHARGE.id(), STAFF_STREAM.id());
            StaffModel.loadPartModels(context);

            context.resolveModel().register(resolver -> {
                Identifier modelId = resolver.id();
                if (!modelId.getNamespace().equals(Etherology.MOD_ID)) return null;

                modelId = modelId.withPath(path -> path.replace("item/", ""));

                if (modelId.equals(OCULUS_IN_HAND.id())) return new UnbakedMultiItemModel(OculusModel::new);
                if (modelId.equals(STAFF.id())) return new UnbakedMultiItemModel(() -> new StaffModel(ModelComponents.STAFF_ITEM));
                if (modelId.equals(STAFF_CHARGE.id())) return new UnbakedMultiItemModel(() -> new StaffModel(ModelComponents.STAFF_ITEM_CHARGE));
                if (modelId.equals(STAFF_STREAM.id())) return new UnbakedMultiItemModel(() -> new StaffModel(ModelComponents.STAFF_ITEM_STREAM));
                return null;
            });
        });
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
        public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer) {
            return modelSupplier.get();
        }
    }
}
