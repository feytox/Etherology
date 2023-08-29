package ru.feytox.etherology.model;

import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * partly taken from <a href="https://github.com/TechReborn/TechReborn/blob/1.19.3-port/src/client/java/techreborn/client/render/BaseDynamicFluidBakedModel.java">source</a>
 */
public abstract class MultiItemModel implements BakedModel, FabricBakedModel {

    public MultiItemModel() {}

    abstract public ModelIdentifier getModelForParticles();

    abstract protected List<ModelIdentifier> getModels();

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        BakedModelManager modelManager = MinecraftClient.getInstance().getBakedModelManager();
        var modelConsumer = context.bakedModelConsumer();

        getModels().forEach(modelId -> modelConsumer.accept(modelManager.getModel(modelId)));
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {}

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
        return Collections.emptyList();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean hasDepth() {
        return false;
    }

    @Override
    public boolean isSideLit() {
        return false;
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY;
    }

    @Override
    public Sprite getParticleSprite() {
        BakedModelManager modelManager = MinecraftClient.getInstance().getBakedModelManager();
        return modelManager.getModel(getModelForParticles()).getParticleSprite();
    }
}
