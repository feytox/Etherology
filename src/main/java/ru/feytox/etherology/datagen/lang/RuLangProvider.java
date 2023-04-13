package ru.feytox.etherology.datagen.lang;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import ru.feytox.etherology.DecoBlocks;
import ru.feytox.etherology.Etherology;

import java.nio.file.Path;

import static ru.feytox.etherology.DecoBlocks.*;

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

        builder.add(CLAY_TILES, "Глиняная черепица");
        new TranslationPart(builder, "глиняной черепицы")
                .stairs(CLAY_TILES_STAIRS)
                .slab(CLAY_TILES_SLAB)
                .wall(CLAY_TILES_WALL);

        builder.add(BLUE_CLAY_TILES, "Синяя глиняная черепица");
        new TranslationPart(builder, "синей глиняной черепицы")
                .stairs(BLUE_CLAY_TILES_STAIRS)
                .slab(BLUE_CLAY_TILES_SLAB)
                .wall(BLUE_CLAY_TILES_WALL);

        builder.add(GREEN_CLAY_TILES, "Зелёная глиняная черепица");
        new TranslationPart(builder, "зелёной глиняной черепицы")
                .stairs(GREEN_CLAY_TILES_STAIRS)
                .slab(GREEN_CLAY_TILES_SLAB)
                .wall(GREEN_CLAY_TILES_WALL);

        builder.add(RED_CLAY_TILES, "Красная глиняная черепица");
        new TranslationPart(builder, "красной глиняной черепицы")
                .stairs(RED_CLAY_TILES_STAIRS)
                .slab(RED_CLAY_TILES_SLAB)
                .wall(RED_CLAY_TILES_WALL);

        builder.add(YELLOW_CLAY_TILES, "Жёлтая глиняная черепица");
        new TranslationPart(builder, "жёлтой глиняной черепицы")
                .stairs(YELLOW_CLAY_TILES_STAIRS)
                .slab(YELLOW_CLAY_TILES_SLAB)
                .wall(YELLOW_CLAY_TILES_WALL);

        try {
            Path existingFilePath = dataOutput.getModContainer().findPath("assets/" + Etherology.MOD_ID + "/lang/" + langCode + ".existing.json").get();
            translationBuilder.add(existingFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add existing language file!", e);
        }
    }
}
