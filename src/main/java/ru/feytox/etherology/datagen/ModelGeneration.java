package ru.feytox.etherology.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.data.family.BlockFamily;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.item.glints.AbstractGlintItem;
import ru.feytox.etherology.magic.staff.StaffPartInfo;
import ru.feytox.etherology.model.EtherologyModels;
import ru.feytox.etherology.registry.block.DecoBlocks;
import ru.feytox.etherology.registry.block.DevBlocks;
import ru.feytox.etherology.registry.block.EBlocks;
import ru.feytox.etherology.registry.item.EItems;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.Arrays;
import java.util.List;

import static ru.feytox.etherology.registry.block.EBlockFamilies.FAMILIES;
import static ru.feytox.etherology.registry.block.EBlocks.ETHEREAL_CHANNEL;
import static ru.feytox.etherology.registry.block.EBlocks.ETHEREAL_FORK;
import static ru.feytox.etherology.registry.item.ArmorItems.*;
import static ru.feytox.etherology.registry.item.DecoBlockItems.*;
import static ru.feytox.etherology.registry.item.EItems.*;
import static ru.feytox.etherology.registry.item.ToolItems.*;

@SuppressWarnings("SameParameterValue")
public class ModelGeneration extends FabricModelProvider {
    public ModelGeneration(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {
        // block families
        registerBlockFamilies(generator, FAMILIES);
        // all simple blocks
        registerSimpleBlock(generator, DecoBlocks.AZEL_BLOCK, DecoBlocks.ETHRIL_BLOCK, DecoBlocks.EBONY_BLOCK, EBlocks.ETHEREAL_CHANNEL_CASE, DecoBlocks.ATTRAHITE);
        // peach models
        generator.registerSingleton(DecoBlocks.PEACH_LEAVES, TexturedModel.LEAVES);
        generator.registerTintableCross(DecoBlocks.PEACH_SAPLING, BlockStateModelGenerator.TintType.NOT_TINTED);
        // dev blocks
        registerSimpleBlock(generator, DevBlocks.UNLIMITED_ETHER_STORAGE_BLOCK);
        // plants
        registerOnlyPottedPlant(generator, DecoBlocks.BEAMER, DecoBlocks.POTTED_BEAMER, BlockStateModelGenerator.TintType.NOT_TINTED);

    }

    @Override
    public void generateItemModels(ItemModelGenerator generator) {
        // glint
        registerGlint(EItems.GLINT, generator);
        // simple items
        registerItems(generator, Models.GENERATED, AZEL_INGOT, AZEL_NUGGET, EBONY_INGOT, EBONY_NUGGET, ETHRIL_INGOT, ETHRIL_NUGGET, BEAM_FRUIT, BEAMER_SEEDS, OCULUS, CORRUPTION_BUCKET, REDSTONE_LENS, UNADJUSTED_LENS, THUJA_OIL, THUJA_SEEDS, ETHEROSCOPE, RAW_AZEL, ATTRAHITE_BRICK, ENRICHED_ATTRAHITE, BINDER, ETHEREAL_CHANNEL, ETHEREAL_FORK, REVELATION_VIEW);
        // handheld (swords, pickaxe etc.)
        registerItems(generator, Models.HANDHELD, ETHRIL_AXE, ETHRIL_PICKAXE, ETHRIL_HOE, ETHRIL_SHOVEL, ETHRIL_SWORD, EBONY_AXE, EBONY_PICKAXE, EBONY_HOE, EBONY_SHOVEL, EBONY_SWORD, STREAM_KEY, BROADSWORD);
        registerItems(generator, Models.HANDHELD, BATTLE_PICKAXES);
        // armor
        registerItems(generator, Models.GENERATED, ETHRIL_HELMET, ETHRIL_CHESTPLATE, ETHRIL_LEGGINGS, ETHRIL_BOOTS, EBONY_HELMET, EBONY_CHESTPLATE, EBONY_LEGGINGS, EBONY_BOOTS);
        // staff parts
        registerStaffParts(generator);
        // pattern tablets
        registerItems(generator, Models.GENERATED, PATTERN_TABLETS);
    }

    private static void registerBlockFamilies(BlockStateModelGenerator generator, List<BlockFamily> blockFamilies) {
        blockFamilies.forEach(family -> generator.registerCubeAllModelTexturePool(family.getBaseBlock()).family(family));
    }

    private static void registerItems(ItemModelGenerator generator, Model model, ItemConvertible... items) {
        Arrays.stream(items).map(ItemConvertible::asItem).forEach(item -> generator.register(item, model));
    }

    private static void registerSimpleBlock(BlockStateModelGenerator generator, Block... blocks) {
        Arrays.stream(blocks).forEach(generator::registerSimpleCubeAll);
    }

    private static void registerGlint(AbstractGlintItem glint, ItemModelGenerator itemModelGenerator) {
        for (int i = 1; i < 17; i++) {
            itemModelGenerator.register(glint, "_"+i, Models.GENERATED);
        }
    }

    private static void registerStaffParts(ItemModelGenerator generator) {
        StaffPartInfo.generateAll().forEach(partInfo -> {
            Identifier fileId = partInfo.toModelId().withPrefixedPath("item/");
            TextureMap textures = TextureMap.particle(new EIdentifier("item/staff_core_oak")).put(EtherologyModels.STYLE, partInfo.toTextureId());

            EtherologyModels.getStaffPartModel(partInfo).upload(fileId, textures, generator.writer);
        });
    }

    private static void registerOnlyPottedPlant(BlockStateModelGenerator generator, Block plantBlock, Block flowerPotBlock, BlockStateModelGenerator.TintType tintType) {
        TextureMap textureMap = TextureMap.plant(plantBlock);
        Identifier identifier = tintType.getFlowerPotCrossModel().upload(flowerPotBlock, textureMap, generator.modelCollector);
        generator.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(flowerPotBlock, identifier));
    }
}
