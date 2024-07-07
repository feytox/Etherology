package ru.feytox.etherology.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.datagen.util.RuTranslationBuilder;
import ru.feytox.etherology.datagen.util.RuTranslationPart;
import ru.feytox.etherology.enchantment.EtherEnchantments;
import ru.feytox.etherology.enchantment.PealEnchantment;
import ru.feytox.etherology.enchantment.ReflectionEnchantment;
import ru.feytox.etherology.magic.lens.LensModifier;
import ru.feytox.etherology.registry.block.DecoBlocks;
import ru.feytox.etherology.registry.block.DevBlocks;
import ru.feytox.etherology.registry.block.EBlocks;
import ru.feytox.etherology.registry.misc.EffectsRegistry;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import static ru.feytox.etherology.registry.block.DecoBlocks.*;
import static ru.feytox.etherology.registry.block.EBlocks.SPILL_BARREL;
import static ru.feytox.etherology.registry.block.EBlocks.*;
import static ru.feytox.etherology.registry.item.ArmorItems.*;
import static ru.feytox.etherology.registry.item.DecoBlockItems.*;
import static ru.feytox.etherology.registry.item.EItems.*;
import static ru.feytox.etherology.registry.item.ToolItems.*;

public class RuLangGeneration extends FabricLanguageProvider {

    private final String langCode;

    protected RuLangGeneration(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        // TODO: 07.07.2024 replace with "ru_ru"
        super(dataOutput, "en_us", registryLookup);
        langCode = "en_us";
    }


    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        RuTranslationBuilder builder = new RuTranslationBuilder(translationBuilder);

        builder.add(DecoBlocks.SLITHERITE, "Слизерит");
        RuTranslationPart.of(builder, "Слизеритовая")
                .stairs(SLITHERITE_STAIRS)
                .slab(SLITHERITE_SLAB)
                .wall(SLITHERITE_WALL)
                .button(SLITHERITE_BUTTON)
                .plate(SLITHERITE_PRESSURE_PLATE);

        builder.add(SLITHERITE_BRICKS, "Слизеритовые кирпичи");
        RuTranslationPart.of(builder, "слизеритового кирпича")
                .stairs(SLITHERITE_BRICK_STAIRS)
                .slab(SLITHERITE_BRICK_SLAB)
                .wall(SLITHERITE_BRICK_WALL);

        builder.add(CHISELED_SLITHERITE_BRICKS, "Резные слизеритовые кирпичи");

        builder.add(CRACKED_SLITHERITE_BRICKS, "Потрескавшиеся слизеритовые кирпичи");
        RuTranslationPart.of(builder, "потрескавшегося слизеритового кирпича")
                .slab(CRACKED_SLITHERITE_BRICK_SLAB)
                .stairs(CRACKED_SLITHERITE_BRICK_STAIRS);

        builder.add(POLISHED_SLITHERITE, "Полированный слизерит");
        RuTranslationPart.of(builder, "полированного слизерита")
                .stairs(POLISHED_SLITHERITE_STAIRS)
                .wall(POLISHED_SLITHERITE_WALL)
                .slab(POLISHED_SLITHERITE_SLAB);

        // azel
        builder.add(AZEL_BLOCK, "Азельный блок");
        builder.add(AZEL_INGOT, "Азельный слиток");
        builder.add(AZEL_NUGGET, "Азельный самородок");

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
        builder.add(ETHRIL_NUGGET, "Эфриловый самородок");

        // ebony
        builder.add(EBONY_BLOCK, "Эбонитовый блок");
        builder.add(EBONY_INGOT, "Эбонитовый слиток");
        builder.add(EBONY_NUGGET, "Эбонитовый самородок");

        // beamer
        builder.add(BEAMER, "Лучевод");
        builder.add(BEAMER_SEEDS, "Семена лучевода");
        builder.add(BEAM_FRUIT, "Лучеплод");

        builder.add(CRATE, "Ящик");

        builder.add(PEACH_SAPLING, "Саженец персикового дерева");
        builder.add(PEACH_LEAVES, "Персиковая листва");
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
        builder.add(ARISTOCRAT_PATTERN_TABLET, "Скрижаль со Знатным Шаблоном");
        builder.add(ASTRONOMY_PATTERN_TABLET, "Скрижаль с Астрономическим Шаблоном");
        builder.add(HEAVENLY_PATTERN_TABLET, "Скрижаль с Вольным Шаблоном");
        builder.add(OCULAR_PATTERN_TABLET, "Скрижаль с Окулярным Шаблоном");
        builder.add(RITUAL_PATTERN_TABLET, "Скрижаль с Ритуальным Шаблоном");
        builder.add(ROYAL_PATTERN_TABLET, "Скрижаль с Королевским Шаблоном");
        builder.add(TRADITIONAL_PATTERN_TABLET, "Скрижаль с Традиционным Шаблоном");

        builder.add(UNADJUSTED_LENS, "Ненастроенная линза");
        builder.add(REDSTONE_LENS, "Линза красного камня");

        builder.add(EBlocks.ESSENCE_DETECTOR_BLOCK, "Датчик незримого света");

        builder.add(ETHEROSCOPE, "Эфироскоп");
        builder.add(THUJA_OIL, "Хвойное масло");
        builder.add(THUJA_SEEDS, "Семена туи");
        builder.add(BINDER, "Связующий элемент");
        builder.add(EBONY, "Эбен");
        builder.add(RESONATING_WAND, "Резонирующая палочка");

        builder.add(EBlocks.ETHEREAL_CHANNEL_CASE, "Обшивка эфирного канала");
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

        builder.add(TELDECORE, "Телдекор");
        builder.add(ARMILLARY_MATRIX, "Армиллярная сфера");
        builder.add(CLAY_JUG, "Форма сосуда для хранения");
        builder.add(PRIMOSHARD_KETA, "Первичный осколок");
        builder.add(PRIMOSHARD_RELLA, "Первичный осколок");
        builder.add(PRIMOSHARD_CLOS, "Первичный осколок");
        builder.add(PRIMOSHARD_VIA, "Первичный осколок");
        builder.add(GLINT, "Огонёк");
        builder.add(ETHER_SHARD, "Эфир");
        builder.add(IRON_SHIELD, "Железный щит");
        builder.add(OCULUS, "Окулус");
        builder.add(CORRUPTION_BUCKET, "Ведро с мутной водой");
        builder.add(CLOSET_SLAB, "Шкаф");
        builder.add(SHELF_SLAB, "Полка");
        builder.add(FURNITURE_SLAB, "Мебель");
        builder.add(PEDESTAL_BLOCK, "Пьедестал");
        builder.add(ETHEREAL_CHANNEL, "Эфирный канал");
        builder.add(ETHEREAL_FORK, "Эфирная развилка");
        builder.add(ETHEREAL_STORAGE, "Эфирное хранилище");
        builder.add(ETHEREAL_SOCKET, "Разъём питания");
        builder.add(ETHEREAL_FURNACE, "Эфирный горн");
        builder.add(ETHEREAL_SPINNER, "Волчок");
        builder.add(ETHEREAL_METRONOME, "Метроном");
        builder.add(EMPOWERMENT_TABLE, "Стол Великоестествия");
        builder.add(SAMOVAR_BLOCK, "Самовар");
        builder.add(SPILL_BARREL, "Разливная бочка");
        builder.add(JUG, "Сосуд для хранения");
        builder.add(PEACH_LOG, "Персиковое дерево");
        builder.add(STRIPPED_PEACH_LOG, "Обтёсанное персиковое дерево");
        builder.add(PEACH_WOOD, "Персиковая древесина");
        builder.add(STRIPPED_PEACH_WOOD, "Обтёсанная персиковая древесина");
        builder.add(PEACH_PLANKS, "Персиковые доски");
        builder.add(PEACH_STAIRS, "Персиковые ступеньки");
        builder.add(PEACH_SLAB, "Персиковый полублок");
        builder.add(PEACH_BUTTON, "Персиковая кнопка");
        builder.add(DecoBlocks.PEACH_DOOR, "Персиковая дверь");
        builder.add(PEACH_FENCE, "Персиковый забор");
        builder.add(PEACH_FENCE_GATE, "Персиковая калитка");
        builder.add(PEACH_PRESSURE_PLATE, "Персиковая нажимная плита");
        builder.add(DecoBlocks.PEACH_SIGN, "Персиковая табличка");
        builder.add(PEACH_TRAPDOOR, "Персиковый люк");
        builder.add(BREWING_CAULDRON, "Варочный тигель");
        builder.add(SEDIMENTARY_BLOCK, "Осадочный камень");
        builder.add(EtherEnchantments.PEAL, "Раскат");
        builder.add(EtherEnchantments.REFLECTION, "Отражение");

        try {
            Path existingFilePath = dataOutput.getModContainer().findPath("assets/" + Etherology.MOD_ID + "/lang/" + langCode + ".existing.json").get();
            translationBuilder.add(existingFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add existing language file!", e);
        }
    }
}
