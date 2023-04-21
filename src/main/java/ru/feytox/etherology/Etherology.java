package ru.feytox.etherology;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import ru.feytox.etherology.blocks.closet.ClosetScreenHandler;
import ru.feytox.etherology.blocks.crate.CrateScreenHandler;
import ru.feytox.etherology.blocks.empowerTable.EmpowerTableScreenHandler;
import ru.feytox.etherology.blocks.etherealFurnace.EtherealFurnaceScreenHandler;
import ru.feytox.etherology.blocks.etherealGenerators.EtherealGeneratorDispenserBehavior;
import ru.feytox.etherology.blocks.etherealStorage.EtherealStorageScreenHandler;
import ru.feytox.etherology.commands.DevCommands;
import ru.feytox.etherology.enums.MixTypes;
import ru.feytox.etherology.recipes.alchemy.AlchemyRecipe;
import ru.feytox.etherology.recipes.alchemy.AlchemyRecipeSerializer;
import ru.feytox.etherology.recipes.armillary.ArmillaryRecipe;
import ru.feytox.etherology.recipes.armillary.ArmillaryRecipeSerializer;
import ru.feytox.etherology.recipes.empower.EmpowerRecipe;
import ru.feytox.etherology.recipes.empower.EmpowerRecipeSerializer;
import ru.feytox.etherology.util.feyapi.EIdentifier;
import ru.feytox.etherology.world.gen.EWorldGeneration;

import static ru.feytox.etherology.BlocksRegistry.*;
import static ru.feytox.etherology.ItemsRegistry.*;
import static ru.feytox.etherology.world.features.zones.EssenceZoneFeature.ESSENCE_ZONE_FEATURE;
import static ru.feytox.etherology.world.features.zones.EssenceZoneFeature.ESSENCE_ZONE_FEATURE_ID;

public class Etherology implements ModInitializer {

    public static final Logger ELOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "etherology";
    public static final ScreenHandlerType<ClosetScreenHandler> CLOSET_SCREEN_HANDLER = new ScreenHandlerType<>(ClosetScreenHandler::new);
    public static final ScreenHandlerType<EtherealStorageScreenHandler> ETHEREAL_STORAGE_SCREEN_HANDLER = new ScreenHandlerType<>(EtherealStorageScreenHandler::new);
    public static final ScreenHandlerType<EtherealFurnaceScreenHandler> ETHEREAL_FURNACE_SCREEN_HANDLER = new ScreenHandlerType<>(EtherealFurnaceScreenHandler::new);
    public static final ScreenHandlerType<EmpowerTableScreenHandler> EMPOWER_TABLE_SCREEN_HANDLER = new ScreenHandlerType<>(EmpowerTableScreenHandler::new);
    public static final ScreenHandlerType<CrateScreenHandler> CRATE_SCREEN_HANDLER = new ScreenHandlerType<>(CrateScreenHandler::new);

    public static final DefaultParticleType ELECTRICITY1 = FabricParticleTypes.simple();
    public static final DefaultParticleType ELECTRICITY2 = FabricParticleTypes.simple();
    public static final DefaultParticleType SPARK = FabricParticleTypes.simple();
    public static final DefaultParticleType STEAM = FabricParticleTypes.simple();
    public static final DefaultParticleType LIGHT = FabricParticleTypes.simple();
    public static final DefaultParticleType LIGHT_VITAL = FabricParticleTypes.simple();
    public static final DefaultParticleType LIGHT_SPARK = FabricParticleTypes.simple();
    public static final DefaultParticleType VITAL_ENERGY = FabricParticleTypes.simple();
    public static final DefaultParticleType KETA_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType RELA_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType CLOS_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType VIA_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType GLINT_PARTICLE = FabricParticleTypes.simple();

    public static final ItemGroup ETHER_GROUP = FabricItemGroup.builder(new EIdentifier("ether_group"))
            .icon(() -> new ItemStack(TELDECORE))
            .displayName(Text.of("Etherology"))
            .build();

    public static final Identifier ELECTRICITY_SOUND_ID = new EIdentifier("electricity_sound");
    public static SoundEvent ELECTRICITY_SOUND_EVENT = SoundEvent.of(ELECTRICITY_SOUND_ID);
    public static final Identifier MATRIX_WORK_SOUND_ID = new EIdentifier("matrix_work_sound");
    public static SoundEvent MATRIX_WORK_SOUND_EVENT = SoundEvent.of(MATRIX_WORK_SOUND_ID);

    @Override
    public void onInitialize() {
        ItemsRegistry.registerItems();
        BlocksRegistry.register();
        DecoBlocks.registerAll();
        DecoBlockItems.registerAll();
        EBlockFamilies.registerFamilies();
        MixTypes.registerMixes();
        DevCommands.register();

        // TODO: move somewhere else
        Registry.register(Registries.RECIPE_SERIALIZER, AlchemyRecipeSerializer.ID,
                AlchemyRecipeSerializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, new EIdentifier(AlchemyRecipe.Type.ID), AlchemyRecipe.Type.INSTANCE);

        Registry.register(Registries.RECIPE_SERIALIZER, EmpowerRecipeSerializer.ID,
                EmpowerRecipeSerializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, new EIdentifier(EmpowerRecipe.Type.ID), EmpowerRecipe.Type.INSTANCE);

        Registry.register(Registries.RECIPE_SERIALIZER, ArmillaryRecipeSerializer.ID,
                ArmillaryRecipeSerializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, new EIdentifier(ArmillaryRecipe.Type.ID), ArmillaryRecipe.Type.INSTANCE);

        Registry.register(Registries.PARTICLE_TYPE, new EIdentifier("electricity1"), ELECTRICITY1);
        Registry.register(Registries.PARTICLE_TYPE, new EIdentifier("electricity2"), ELECTRICITY2);
        Registry.register(Registries.PARTICLE_TYPE, new EIdentifier("spark"), SPARK);
        Registry.register(Registries.PARTICLE_TYPE, new EIdentifier("steam"), STEAM);
        Registry.register(Registries.PARTICLE_TYPE, new EIdentifier("light"), LIGHT);
        Registry.register(Registries.PARTICLE_TYPE, new EIdentifier("light_vital"), LIGHT_VITAL);
        Registry.register(Registries.PARTICLE_TYPE, new EIdentifier("light_spark"), LIGHT_SPARK);
        Registry.register(Registries.PARTICLE_TYPE, new EIdentifier("vital_energy"), VITAL_ENERGY);

        Registry.register(Registries.PARTICLE_TYPE, new EIdentifier("keta_particle"), KETA_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, new EIdentifier("rela_particle"), RELA_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, new EIdentifier("clos_particle"), CLOS_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, new EIdentifier("via_particle"), VIA_PARTICLE);

        Registry.register(Registries.PARTICLE_TYPE, new EIdentifier("glint_particle"), GLINT_PARTICLE);

        ItemGroupEvents.modifyEntriesEvent(ETHER_GROUP).register(content -> {
            content.add(ARMILLARY_MATRIX_BASE);
            content.add(CRUCIBLE);
            content.add(PEDESTAL_BLOCK);
            content.add(AQUATIC_SHARD);
            content.add(DEEP_SHARD);
            content.add(HEAVENLY_SHARD);
            content.add(TELDER_STEEL_RING);
            content.add(ETHRIL_RING);
            content.add(NETHERITE_RING);
            content.add(TELDECORE);
            content.add(TERRESTRIAL_SHARD);
        });

        Registry.register(Registries.SOUND_EVENT, ELECTRICITY_SOUND_ID, ELECTRICITY_SOUND_EVENT);
        Registry.register(Registries.SOUND_EVENT, MATRIX_WORK_SOUND_ID, MATRIX_WORK_SOUND_EVENT);

        Registry.register(Registries.FEATURE, ESSENCE_ZONE_FEATURE_ID, ESSENCE_ZONE_FEATURE);

        Registry.register(Registries.SCREEN_HANDLER, new EIdentifier("closet_screen_handler"), CLOSET_SCREEN_HANDLER);
        Registry.register(Registries.SCREEN_HANDLER, new EIdentifier("ethereal_storage_screen_handler"), ETHEREAL_STORAGE_SCREEN_HANDLER);
        Registry.register(Registries.SCREEN_HANDLER, new EIdentifier("ethereal_furnace_screen_handler"), ETHEREAL_FURNACE_SCREEN_HANDLER);
        Registry.register(Registries.SCREEN_HANDLER, new EIdentifier("empower_table_screen_handler"), EMPOWER_TABLE_SCREEN_HANDLER);

        EtherealGeneratorDispenserBehavior.register();

        EWorldGeneration.generateWorldGen();
    }
}

//     СТРАНИЦЫ ТЕЛДЕКОРА:
//    TODO: базовая страница +-
//     TODO: главный экран +-
//    TODO: базовая страница исследований
//    TODO: генератор страниц
//    TODO: страница с описанием +-
//    TODO: страница с верстак крафтом +-
//    TODO: страница с ether крафтом +-
//    TODO: страница с печным крафтом +-
//    TODO: страница с тигель крафтом +-
//    TODO: страница с трансформация крафтом
//    TODO: страница с квестом