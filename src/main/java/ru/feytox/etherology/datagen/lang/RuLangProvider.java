package ru.feytox.etherology.datagen.lang;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.registry.block.DecoBlocks;

import java.nio.file.Path;

import static ru.feytox.etherology.registry.block.DecoBlocks.*;
import static ru.feytox.etherology.registry.block.EBlocks.CRATE;
import static ru.feytox.etherology.registry.item.DecoBlockItems.*;
import static ru.feytox.etherology.registry.item.ToolItems.*;

public class RuLangProvider extends FabricLanguageProvider {
    private final String langCode;

    public RuLangProvider(FabricDataOutput dataOutput) {
        // TODO: 12/04/2023 rename on release or smth
        super(dataOutput, "en_us");
        langCode = "en_us";
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        BlockRuTranslationBuilder builder = new BlockRuTranslationBuilder(translationBuilder);

        builder.add(DecoBlocks.ETHEREAL_STONE, "Эфирный камень");
        new TranslationPart(builder, "эфирного камня")
                .stairs(ETHEREAL_STONE_STAIRS)
                .slab(ETHEREAL_STONE_SLAB)
                .wall(ETHEREAL_STONE_WALL)
                .button(ETHEREAL_STONE_BUTTON)
                .plate(ETHEREAL_STONE_PRESSURE_PLATE);

        builder.add(COBBLED_ETHEREAL_STONE, "Эфирный булыжник");
        new TranslationPart(builder, "эфирного булыжника")
                .stairs(COBBLED_ETHEREAL_STONE_STAIRS)
                .slab(COBBLED_ETHEREAL_STONE_SLAB)
                .wall(COBBLED_ETHEREAL_STONE_WALL);

        builder.add(ETHEREAL_STONE_BRICKS, "Эфирные каменные кирпичи");
        new TranslationPart(builder, "эфирного каменного кирпича")
                .stairs(ETHEREAL_STONE_BRICK_STAIRS)
                .slab(ETHEREAL_STONE_BRICK_SLAB)
                .wall(ETHEREAL_STONE_BRICK_WALL);

        builder.add(CHISELED_ETHEREAL_STONE_BRICKS, "Резные эфирные каменные кирпичи");
        new TranslationPart(builder, "резного эфирного каменного кирпича")
                .stairs(CHISELED_ETHEREAL_STONE_BRICK_STAIRS);

        builder.add(CRACKED_ETHEREAL_STONE_BRICKS, "Потрескавшиеся эфирные каменные кирпичи");
        new TranslationPart(builder, "потрескавшегося эфирного каменного кирпича")
                .slab(CRACKED_ETHEREAL_STONE_BRICK_SLAB)
                .stairs(CRACKED_ETHEREAL_STONE_BRICK_STAIRS);

        builder.add(MOSSY_COBBLED_ETHEREAL_STONE, "Замшелый эфирный булыжник");
        new TranslationPart(builder, "замшелого эфирного булыжника")
                .slab(MOSSY_COBBLED_ETHEREAL_STONE_SLAB)
                .stairs(MOSSY_COBBLED_ETHEREAL_STONE_STAIRS);

        builder.add(MOSSY_ETHEREAL_STONE_BRICKS, "Замшелые эфирные каменные кирпичи");
        new TranslationPart(builder, "замшелого эфирного каменного кирпича")
                .slab(MOSSY_ETHEREAL_STONE_BRICK_SLAB)
                .stairs(MOSSY_ETHEREAL_STONE_BRICK_STAIRS);

        builder.add(POLISHED_ETHEREAL_STONE, "Полированный эфирный камень");
        new TranslationPart(builder, "полированного эфирного камня")
                .stairs(POLISHED_ETHEREAL_STONE_STAIRS)
                .slab(POLISHED_ETHEREAL_STONE_SLAB);

        builder.add(CLAY_TILES, "Керамическая плитка");
        new TranslationPart(builder, "керамической плитки")
                .stairs(CLAY_TILES_STAIRS)
                .slab(CLAY_TILES_SLAB)
                .wall(CLAY_TILES_WALL);

        builder.add(BLUE_CLAY_TILES, "Синяя керамическая плитка");
        new TranslationPart(builder, "синей керамической плитки")
                .stairs(BLUE_CLAY_TILES_STAIRS)
                .slab(BLUE_CLAY_TILES_SLAB)
                .wall(BLUE_CLAY_TILES_WALL);

        builder.add(GREEN_CLAY_TILES, "Зелёная керамическая плитка");
        new TranslationPart(builder, "зелёной керамической плитки")
                .stairs(GREEN_CLAY_TILES_STAIRS)
                .slab(GREEN_CLAY_TILES_SLAB)
                .wall(GREEN_CLAY_TILES_WALL);

        builder.add(RED_CLAY_TILES, "Красная керамическая плитка");
        new TranslationPart(builder, "красной керамической плитки")
                .stairs(RED_CLAY_TILES_STAIRS)
                .slab(RED_CLAY_TILES_SLAB)
                .wall(RED_CLAY_TILES_WALL);

        builder.add(YELLOW_CLAY_TILES, "Жёлтая керамическая плитка");
        new TranslationPart(builder, "жёлтой керамической плитки")
                .stairs(YELLOW_CLAY_TILES_STAIRS)
                .slab(YELLOW_CLAY_TILES_SLAB)
                .wall(YELLOW_CLAY_TILES_WALL);

        // attrahite
        builder.add(ATTRAHITE_BLOCK, "Аттрахитовый блок");
        builder.add(ATTRAHITE_INGOT, "Аттрахитовый слиток");
        builder.add(ATTRAHITE_NUGGET, "Аттрахитовый самородок");

        // ethril
        builder.add(ETHRIL_BLOCK, "Эфриловый блок");
        builder.add(ETHRIL_INGOT, "Эфриловый слиток");
        builder.add(ETHRIL_NUGGET, "Эфриловый самородок");

        // telder steel
        builder.add(TELDER_STEEL_BLOCK, "Телдер-стальной блок");
        builder.add(TELDER_STEEL_INGOT, "Телдер-стальной слиток");
        builder.add(TELDER_STEEL_NUGGET, "Телдер-стальной самородок");

        // beamer
        builder.add(BEAMER, "Лучевод");
        builder.add(BEAMER_SEEDS, "Семена лучевода");
        builder.add(BEAM_FRUIT, "Лучеплод");

        builder.add(CRATE, "Ящик");

        builder.add(PEACH_SAPLING, "Саженец персикового дерева");
        builder.add(PEACH_LEAVES, "Персиковая листва");

        builder.add(ETHRIL_AXE, "Эфриловый топор");
        builder.add(ETHRIL_HOE, "Эфриловая мотыга");
        builder.add(ETHRIL_PICKAXE, "Эфриловая кирка");
        builder.add(ETHRIL_SHOVEL, "Эфриловая лопата");
        builder.add(ETHRIL_SWORD, "Эфриловый меч");

        builder.add(TELDER_STEEL_AXE, "Телдер-стальной меч");
        builder.add(TELDER_STEEL_HOE, "Телдер-стальная мотыга");
        builder.add(TELDER_STEEL_PICKAXE, "Телдер-стальная кирка");
        builder.add(TELDER_STEEL_SHOVEL, "Телдер-стальная лопата");
        builder.add(TELDER_STEEL_SWORD, "Телдер-стальный меч");

        try {
            Path existingFilePath = dataOutput.getModContainer().findPath("assets/" + Etherology.MOD_ID + "/lang/" + langCode + ".existing.json").get();
            translationBuilder.add(existingFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add existing language file!", e);
        }
    }
}
