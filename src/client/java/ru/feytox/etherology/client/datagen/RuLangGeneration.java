package ru.feytox.etherology.client.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.client.datagen.util.RuTranslationBuilder;
import ru.feytox.etherology.client.datagen.util.RuTranslationPart;
import ru.feytox.etherology.data.EBlockTags;
import ru.feytox.etherology.data.EItemTags;
import ru.feytox.etherology.magic.lens.LensModifier;
import ru.feytox.etherology.registry.block.DecoBlocks;
import ru.feytox.etherology.registry.block.DevBlocks;
import ru.feytox.etherology.registry.block.EBlocks;
import ru.feytox.etherology.registry.block.ExtraBlocksRegistry;
import ru.feytox.etherology.registry.misc.EffectsRegistry;
import ru.feytox.etherology.registry.misc.EtherEnchantments;
import ru.feytox.etherology.registry.world.WorldGenRegistry;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import static ru.feytox.etherology.registry.block.DecoBlocks.*;
import static ru.feytox.etherology.registry.block.EBlocks.*;
import static ru.feytox.etherology.registry.item.ArmorItems.*;
import static ru.feytox.etherology.registry.item.DecoBlockItems.*;
import static ru.feytox.etherology.registry.item.EItems.*;
import static ru.feytox.etherology.registry.item.ToolItems.*;

public class RuLangGeneration extends FabricLanguageProvider {

    private final String langCode;

    protected RuLangGeneration(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "ru_ru", registryLookup);
        langCode = "ru_ru";
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        RuTranslationBuilder builder = new RuTranslationBuilder(translationBuilder);

        builder.add(DecoBlocks.SLITHERITE, "Слизерит");
        RuTranslationPart.of(builder, "Слизеритовая")
                .slab(SLITHERITE_SLAB)
                .wall(SLITHERITE_WALL);
        builder.add(SLITHERITE_STAIRS, "Слизеритовые ступеньки");

        builder.add(POLISHED_SLITHERITE, "Полированный слизерит");
        RuTranslationPart.of(builder, "полированного слизерита")
                .stairs(POLISHED_SLITHERITE_STAIRS)
                .wall(POLISHED_SLITHERITE_WALL)
                .slab(POLISHED_SLITHERITE_SLAB)
                .button(POLISHED_SLITHERITE_BUTTON)
                .plate(POLISHED_SLITHERITE_PRESSURE_PLATE);

        builder.add(POLISHED_SLITHERITE_BRICKS, "Полированные слизеритовые кирпичи");
        RuTranslationPart.of(builder, "полированного слизеритового кирпича")
                .stairs(POLISHED_SLITHERITE_BRICK_STAIRS)
                .slab(POLISHED_SLITHERITE_BRICK_SLAB)
                .wall(POLISHED_SLITHERITE_BRICK_WALL);

        builder.add(CHISELED_POLISHED_SLITHERITE, "Резной полированный слизерит");
        builder.add(CHISELED_POLISHED_SLITHERITE_BRICKS, "Резные полированные слизеритовые кирпичи");
        builder.add(CRACKED_POLISHED_SLITHERITE_BRICKS, "Потрескавшиеся полированные слизеритовые кирпичи");

        // azel
        builder.add(AZEL_BLOCK, "Блок азеля");
        builder.add(AZEL_INGOT, "Слиток азеля");
        builder.add(AZEL_NUGGET, "Кусочек азеля");

        // attrahite
        builder.add(ATTRAHITE, "Аттрахит");
        builder.add(ATTRAHITE_BRICKS, "Аттрахитовые кирпичи");
        RuTranslationPart.of(builder, "аттрахитовых кирпичей")
                .slab(ATTRAHITE_BRICK_SLAB)
                .stairs(ATTRAHITE_BRICK_STAIRS);
        builder.add(ATTRAHITE_BRICK, "Аттрахитовый кирпич");
        builder.add(RAW_AZEL, "Сырой азель");
        builder.add(ENRICHED_ATTRAHITE, "Обогащённый аттрахит");

        // ethril
        builder.add(ETHRIL_BLOCK, "Эфриловый блок");
        builder.add(ETHRIL_INGOT, "Эфриловый слиток");
        builder.add(ETHRIL_NUGGET, "Эфриловый кусочек");

        // ebony
        builder.add(EBONY_BLOCK, "Эбонитовый блок");
        builder.add(EBONY_INGOT, "Эбонитовый слиток");
        builder.add(EBONY_NUGGET, "Эбонитовый кусочек");

        // beamer
        builder.add(BEAMER, "Лучевод");
        builder.add(BEAMER_SEEDS, "Семена лучевода");
        builder.add(BEAM_FRUIT, "Лучеплод");

        builder.add(CRATE, "Ящик");

        builder.add(PEACH_SAPLING, "Саженец персика");
        builder.add(PEACH_LEAVES, "Персиковые листья");
        builder.add(WEEPING_PEACH_LOG, "Плакучее персиковое бревно");

        builder.add(ETHRIL_AXE, "Эфриловый топор");
        builder.add(ETHRIL_HOE, "Эфриловая мотыга");
        builder.add(ETHRIL_PICKAXE, "Эфриловая кирка");
        builder.add(ETHRIL_SHOVEL, "Эфриловая лопата");
        builder.add(ETHRIL_SWORD, "Эфриловый меч");

        builder.add(EBONY_AXE, "Эбонитовый топор");
        builder.add(EBONY_HOE, "Эбонитовая мотыга");
        builder.add(EBONY_PICKAXE, "Эбонитовая кирка");
        builder.add(EBONY_SHOVEL, "Эбонитовая лопата");
        builder.add(EBONY_SWORD, "Эбонитовый меч");

        builder.add(WOODEN_BATTLE_PICKAXE, "Деревянное кайло");
        builder.add(STONE_BATTLE_PICKAXE, "Каменное кайло");
        builder.add(IRON_BATTLE_PICKAXE, "Железное кайло");
        builder.add(GOLDEN_BATTLE_PICKAXE, "Золотое кайло");
        builder.add(DIAMOND_BATTLE_PICKAXE, "Алмазное кайло");
        builder.add(NETHERITE_BATTLE_PICKAXE, "Незеритовое кайло");
        builder.add(ETHRIL_BATTLE_PICKAXE, "Эфриловое кайло");
        builder.add(EBONY_BATTLE_PICKAXE, "Эбонитовое кайло");

        builder.add(TUNING_MACE, "Тональная булава");
        builder.add(BROADSWORD, "Палаш");

        builder.add(ETHRIL_HELMET, "Эфриловый шлем");
        builder.add(ETHRIL_CHESTPLATE, "Эфриловый нагрудник");
        builder.add(ETHRIL_LEGGINGS, "Эфриловые поножи");
        builder.add(ETHRIL_BOOTS, "Эфриловые ботинки");
        builder.add(EBONY_HELMET, "Эбонитовый шлем");
        builder.add(EBONY_CHESTPLATE, "Эбонитовый нагрудник");
        builder.add(EBONY_LEGGINGS, "Эбонитовые поножи");
        builder.add(EBONY_BOOTS, "Эбонитовые ботинки");

        builder.add(DevBlocks.UNLIMITED_ETHER_STORAGE_BLOCK, "Творческий бесконечный источник эфира");
        builder.add(LEVITATOR, "Левитатор");

        builder.add(STAFF, "Посох");
        builder.add(EBlocks.INVENTOR_TABLE, "Стол изобретателя");
        builder.add(EBlocks.JEWELRY_TABLE, "Стол ювелира");
        builder.add(ARISTOCRAT_PATTERN_TABLET, "Стиль \"Знатный\"");
        builder.add(ASTRONOMY_PATTERN_TABLET, "Стиль \"Астрономический\"");
        builder.add(HEAVENLY_PATTERN_TABLET, "Стиль \"Вольный\"");
        builder.add(OCULAR_PATTERN_TABLET, "Стиль \"Окулярный\"");
        builder.add(RITUAL_PATTERN_TABLET, "Стиль \"Ритуальный\"");
        builder.add(ROYAL_PATTERN_TABLET, "Стиль \"Королевский\"");
        builder.add(TRADITIONAL_PATTERN_TABLET, "Стиль \"Традиционный\"");

        builder.add(UNADJUSTED_LENS, "Ненастроенная линза");
        builder.add(REDSTONE_LENS, "Линза красного камня");

        builder.add(EBlocks.ARCANELIGHT_DETECTOR_BLOCK, "Датчик незримого света");

        builder.add(ETHEROSCOPE, "Эфироскоп");
        builder.add(THUJA_OIL, "Хвойное масло");
        builder.add(THUJA_SEEDS, "Семена туи");
        builder.add(BINDER, "Связующий элемент");
        builder.add(EBONY, "Эбен");
        builder.add(RESONATING_WAND, "Резонирующая палочка");

        builder.add(EBlocks.CHANNEL_CASE, "Обшивка канала");
        builder.add(STREAM_KEY, "Ключ трансляции");
        builder.add(REVELATION_VIEW, "Окуляр");

        builder.add(TUNING_FORK, "Камертон");

        builder.add(LensModifier.STREAM, "Поток");
        builder.add(LensModifier.CHARGE, "Заряд");
        builder.add(LensModifier.FILTERING, "Фильтрация");
        builder.add(LensModifier.CONCENTRATION, "Концентрация");
        builder.add(LensModifier.REINFORCEMENT, "Усиление");
        builder.add(LensModifier.AREA, "Область");
        builder.add(LensModifier.SAVING, "Сбережение");

        builder.add(WARP_COUNTER, "Варп счётчик");

        builder.add(FOREST_LANTERN, "Лесной фонарь");
        builder.add(FOREST_LANTERN_CRUMB, "Грибной мякиш");
        builder.add(LIGHTELET, "Колосвет");

        builder.add(EffectsRegistry.DEVASTATION.value(), "Опустошение");
        builder.add(EffectsRegistry.VITAL_ENERGY.value(), "Духовное восстановление");
        builder.add(EffectsRegistry.VITAL_ENERGY_POTION, "духовного восстановления");

        builder.add(TELDECORE, "Телдекор [WIP]");
        builder.add(ARMILLARY_SPHERE, "Армиллярная сфера [WIP]");
        builder.add(CLAY_JUG, "Форма сосуда для хранения");
        builder.add(PRIMOSHARD_KETA, "Первичный осколок");
        builder.add(PRIMOSHARD_RELLA, "Первичный осколок");
        builder.add(PRIMOSHARD_CLOS, "Первичный осколок");
        builder.add(PRIMOSHARD_VIA, "Первичный осколок");
        builder.add(GLINT, "Огонёк");
        builder.add(ETHER, "Эфир");
        builder.add(IRON_SHIELD, "Железный щит");
        builder.add(OCULUS, "Окулус");
        builder.add(CORRUPTION_BUCKET, "Ведро с мутной водой");
        builder.add(CLOSET_SLAB, "Шкаф [WIP]");
        builder.add(SHELF_SLAB, "Полка [WIP]");
        builder.add(FURNITURE_SLAB, "Мебель [WIP]");
        builder.add(PEDESTAL_BLOCK, "Пьедестал");
        builder.add(ETHEREAL_CHANNEL, "Эфирный канал");
        builder.add(ETHEREAL_FORK, "Эфирная развилка");
        builder.add(ETHEREAL_STORAGE, "Эфирное хранилище");
        builder.add(ETHEREAL_SOCKET, "Разъём питания");
        builder.add(ETHEREAL_FURNACE, "Эфирный горн [WIP]");
        builder.add(SPINNER, "Волчок");
        builder.add(METRONOME, "Метроном");
        builder.add(EMPOWERMENT_TABLE, "Стол великоестествия");
        builder.add(SAMOVAR_BLOCK, "Самовар");
        builder.add(SPILL_BARREL, "Разливная бочка");
        builder.add(JUG, "Сосуд для хранения");
        builder.add(PEACH_LOG, "Персиковое бревно");
        builder.add(STRIPPED_PEACH_LOG, "Обтёсанное персиковое бревно");
        builder.add(PEACH_WOOD, "Персиковое дерево");
        builder.add(STRIPPED_PEACH_WOOD, "Обтёсанное персиковое дерево");
        builder.add(ExtraBlocksRegistry.PEACH_PLANKS, "Персиковые доски");
        builder.add(PEACH_STAIRS, "Персиковые ступеньки");
        builder.add(PEACH_SLAB, "Персиковый полублок");
        builder.add(PEACH_BUTTON, "Персиковая кнопка");
        builder.add(DecoBlocks.PEACH_DOOR, "Персиковая дверь");
        builder.add(PEACH_FENCE, "Персиковый забор");
        builder.add(PEACH_FENCE_GATE, "Персиковая калитка");
        builder.add(PEACH_PRESSURE_PLATE, "Персиковая плита");
        builder.add(DecoBlocks.PEACH_SIGN, "Персиковая табличка");
        builder.add(DecoBlocks.PEACH_HANGING_SIGN, "Персиковая подвесная табличка");
        builder.add(PEACH_TRAPDOOR, "Персиковый люк");
        builder.add(BREWING_CAULDRON, "Варочный тигель");
        builder.add(SEDIMENTARY_STONE, "Осадочный камень");
        builder.add(EtherEnchantments.PEAL, "Раскат");
        builder.add(EtherEnchantments.REFLECTION, "Отражение");

        builder.add(PEACH_BOAT, "Персиковая лодка");
        builder.add(PEACH_CHEST_BOAT, "Персиковая грузовая лодка");

        // tags
        builder.add(EBlockTags.PEACH_LOGS, "Персиковые брёвна");
        builder.add(EItemTags.PEACH_LOGS, "Персиковые брёвна");
        builder.add(EItemTags.IRON_SHIELDS, "Железные щиты");
        builder.add(EItemTags.TUNING_MACES, "Тональные булавы");
        builder.add(EItemTags.SEDIMENTARY_STONES, "Осадочные камни");
        builder.add(EBlockTags.SEDIMENTARY_STONES, "Осадочные камни");

        builder.add(WorldGenRegistry.GOLDEN_FOREST, "Златолесье");

        try {
            Path existingFilePath = dataOutput.getModContainer().findPath("assets/" + Etherology.MOD_ID + "/lang/" + langCode + ".existing.json").orElse(null);
            translationBuilder.add(existingFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add existing language file!", e);
        }
    }
}
