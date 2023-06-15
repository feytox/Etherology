package ru.feytox.etherology.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.data.family.BlockFamily;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.data.client.EtherologyModels;
import ru.feytox.etherology.item.glints.AbstractGlintItem;
import ru.feytox.etherology.mixin.ModelAccessor;
import ru.feytox.etherology.registry.block.DecoBlocks;
import ru.feytox.etherology.registry.item.EItems;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static ru.feytox.etherology.registry.block.EBlockFamilies.*;
import static ru.feytox.etherology.registry.item.DecoBlockItems.*;
import static ru.feytox.etherology.registry.item.ToolItems.*;

public class ModelGeneration extends FabricModelProvider {
    public ModelGeneration(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {
        // ethereal stones
        registerBlockFamilies(generator, ETHEREAL_STONE, COBBLED_ETHEREAL_STONE, CRACKED_ETHEREAL_STONE_BRICKS, ETHEREAL_STONE_BRICKS, MOSSY_COBBLED_ETHEREAL_STONE, POLISHED_ETHEREAL_STONE, CHISELED_ETHEREAL_STONE_BRICKS, MOSSY_ETHEREAL_STONE_BRICKS);
        // clay tiles
        registerBlockFamilies(generator, CLAY_TILE, BLUE_CLAY_TILE, GREEN_CLAY_TILE, RED_CLAY_TILE, YELLOW_CLAY_TILE);
        // all simple blocks
        registerSimpleBlock(generator, DecoBlocks.ATTRAHITE_BLOCK, DecoBlocks.ETHRIL_BLOCK, DecoBlocks.TELDER_STEEL_BLOCK);

        generator.registerSingleton(DecoBlocks.PEACH_LEAVES, TexturedModel.LEAVES);
        generator.registerTintableCross(DecoBlocks.PEACH_SAPLING, BlockStateModelGenerator.TintType.NOT_TINTED);
    }

    @Override
    public void generateItemModels(ItemModelGenerator generator) {
        // glint
        registerGlint(EItems.GLINT, generator);
        // all simple items
        registerItems(generator, Models.GENERATED, ATTRAHITE_INGOT, ATTRAHITE_NUGGET, TELDER_STEEL_INGOT, TELDER_STEEL_NUGGET, ETHRIL_INGOT, ETHRIL_NUGGET, BEAM_FRUIT, BEAMER_SEEDS);
        // all handheld (swords, pickaxe and etc)
        registerItems(generator, Models.HANDHELD, ETHRIL_AXE, ETHRIL_PICKAXE, ETHRIL_HOE, ETHRIL_SHOVEL, ETHRIL_SWORD, TELDER_STEEL_AXE, TELDER_STEEL_PICKAXE, TELDER_STEEL_HOE, TELDER_STEEL_SHOVEL, TELDER_STEEL_SWORD);
        registerItems(generator, Models.HANDHELD, WOODEN_BATTLE_PICKAXE, STONE_BATTLE_PICKAXE, IRON_BATTLE_PICKAXE, GOLDEN_BATTLE_PICKAXE, DIAMOND_BATTLE_PICKAXE, NETHERITE_BATTLE_PICKAXE, ETHRIL_BATTLE_PICKAXE, TELDER_STEEL_BATTLE_PICKAXE);
        // all glaives
        registerGlaives(generator);
        registerGlaivesHandle(generator);
        registerItems(generator, Models.HANDHELD, GLAIVES);
    }

    private static void registerBlockFamilies(BlockStateModelGenerator generator, BlockFamily... blockFamilies) {
        Arrays.stream(blockFamilies).forEach(family -> generator.registerCubeAllModelTexturePool(family.getBaseBlock()).family(family));
    }

    private static void registerItems(ItemModelGenerator generator, Model model, Item... items) {
        Arrays.stream(items).forEach(item -> generator.register(item, model));
    }

    private static void registerItems(ItemModelGenerator generator, Model model, String suffix, Item... items) {
        Arrays.stream(items).forEach(item -> generator.register(item, suffix, model));
    }

    private static void registerGlaivesHandle(ItemModelGenerator generator) {
        Arrays.stream(GLAIVES).forEach(item -> {
            EtherologyModels.GLAIVE_IN_HAND_HANDLE.upload(ModelIds.getItemSubModelId(item, "_in_hand_handle"), TextureMap.layer0(TextureMap.getSubId(item, "_in_hand")), generator.writer);
        });
    }

    private static void registerGlaives(ItemModelGenerator generator) {
        var modelCollector = generator.writer;
        Model model = EtherologyModels.GLAIVE_IN_HAND;

        List<Item> glaives = Arrays.stream(GLAIVES).toList();
        for (Item item : glaives) {
            TextureMap textures = TextureMap.layer0(TextureMap.getSubId(item, "_in_hand"));
            Identifier id = ModelIds.getItemSubModelId(item, "_in_hand");
            Identifier handleId = ModelIds.getItemSubModelId(item, "_in_hand_handle");

            Map<TextureKey, Identifier> map = model.createTextureMap(textures);
            modelCollector.accept(id, () -> {
                JsonObject jsonObject = new JsonObject();
                ((ModelAccessor) model).getParent().ifPresent((parentId) ->
                        jsonObject.addProperty("parent", parentId.toString()));
                if (!map.isEmpty()) {
                    JsonObject jsonObject2 = new JsonObject();
                    map.forEach((textureKey, textureId) ->
                            jsonObject2.addProperty(textureKey.getName(), textureId.toString()));
                    jsonObject.add("textures", jsonObject2);
                }

                JsonArray overrides = new JsonArray();
                JsonObject glaive_override = new JsonObject();
                JsonObject predicate = new JsonObject();

                predicate.addProperty("glaive_handle", 1);
                glaive_override.add("predicate", predicate);
                glaive_override.addProperty("model", handleId.toString());
                overrides.add(glaive_override);
                jsonObject.add("overrides", overrides);

                return jsonObject;
            });
        }
    }

    private static void registerSimpleBlock(BlockStateModelGenerator generator, Block... blocks) {
        Arrays.stream(blocks).forEach(generator::registerSimpleCubeAll);
    }

    private static void registerGlint(AbstractGlintItem glint, ItemModelGenerator itemModelGenerator) {
        for (int i = 1; i < 17; i++) {
            itemModelGenerator.register(glint, "_"+i, Models.GENERATED);
        }
    }
}
