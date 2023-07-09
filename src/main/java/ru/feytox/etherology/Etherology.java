package ru.feytox.etherology;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import org.slf4j.Logger;
import ru.feytox.etherology.animation.PredicateAnimations;
import ru.feytox.etherology.animation.TriggerAnimations;
import ru.feytox.etherology.block.closet.ClosetScreenHandler;
import ru.feytox.etherology.block.crate.CrateScreenHandler;
import ru.feytox.etherology.block.empowerTable.EmpowerTableScreenHandler;
import ru.feytox.etherology.block.etherealFurnace.EtherealFurnaceScreenHandler;
import ru.feytox.etherology.block.etherealGenerators.EtherealGeneratorDispenserBehavior;
import ru.feytox.etherology.block.etherealStorage.EtherealStorageScreenHandler;
import ru.feytox.etherology.commands.DevCommands;
import ru.feytox.etherology.enums.MixTypes;
import ru.feytox.etherology.network.EtherologyNetwork;
import ru.feytox.etherology.recipes.alchemy.AlchemyRecipe;
import ru.feytox.etherology.recipes.alchemy.AlchemyRecipeSerializer;
import ru.feytox.etherology.recipes.armillary.ArmillaryRecipe;
import ru.feytox.etherology.recipes.armillary.ArmillaryRecipeSerializer;
import ru.feytox.etherology.recipes.brewingCauldron.CauldronRecipe;
import ru.feytox.etherology.recipes.brewingCauldron.CauldronRecipeSerializer;
import ru.feytox.etherology.recipes.empower.EmpowerRecipe;
import ru.feytox.etherology.recipes.empower.EmpowerRecipeSerializer;
import ru.feytox.etherology.registry.block.EBlockFamilies;
import ru.feytox.etherology.registry.block.EBlocks;
import ru.feytox.etherology.registry.custom.EtherologyRegistry;
import ru.feytox.etherology.registry.item.EItems;
import ru.feytox.etherology.registry.item.EtherEnchantments;
import ru.feytox.etherology.registry.util.EtherSounds;
import ru.feytox.etherology.registry.util.ResourceReloaders;
import ru.feytox.etherology.util.delayedTask.ServerTaskManager;
import ru.feytox.etherology.util.feyapi.EIdentifier;
import ru.feytox.etherology.world.gen.EWorldGeneration;

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
    public static final DefaultParticleType OLD_STEAM = FabricParticleTypes.simple();
    public static final DefaultParticleType LIGHT = FabricParticleTypes.simple();
    public static final DefaultParticleType LIGHT_VITAL = FabricParticleTypes.simple();
    public static final DefaultParticleType LIGHT_SPARK = FabricParticleTypes.simple();
    public static final DefaultParticleType VITAL_ENERGY = FabricParticleTypes.simple();
    public static final DefaultParticleType KETA_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType RELA_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType CLOS_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType VIA_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType GLINT_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType SHOCKWAVE = FabricParticleTypes.simple();

    @Override
    public void onInitialize() {
        // TODO: 19/05/2023 убрать лишнее
        ResourceReloaders.registerServerData();
        EtherologyNetwork.registerPackets();
        EItems.registerItems();
        EBlocks.registerAll();
        EBlockFamilies.registerFamilies();
        MixTypes.registerMixes();
        DevCommands.register();
        EtherSounds.registerAll();
        PredicateAnimations.registerAll();
        TriggerAnimations.registerAll();
        EtherEnchantments.registerAll();

        // TODO: move somewhere else
        Registry.register(Registries.RECIPE_SERIALIZER, CauldronRecipeSerializer.ID,
                CauldronRecipeSerializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, new EIdentifier(CauldronRecipe.Type.ID), CauldronRecipe.Type.INSTANCE);

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
        Registry.register(Registries.PARTICLE_TYPE, new EIdentifier("old_steam"), OLD_STEAM);
        Registry.register(Registries.PARTICLE_TYPE, new EIdentifier("light"), LIGHT);
        Registry.register(Registries.PARTICLE_TYPE, new EIdentifier("light_vital"), LIGHT_VITAL);
        Registry.register(Registries.PARTICLE_TYPE, new EIdentifier("light_spark"), LIGHT_SPARK);
        Registry.register(Registries.PARTICLE_TYPE, new EIdentifier("vital_energy"), VITAL_ENERGY);

        Registry.register(Registries.PARTICLE_TYPE, new EIdentifier("keta_particle"), KETA_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, new EIdentifier("rela_particle"), RELA_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, new EIdentifier("clos_particle"), CLOS_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, new EIdentifier("via_particle"), VIA_PARTICLE);

        Registry.register(Registries.PARTICLE_TYPE, new EIdentifier("glint_particle"), GLINT_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, new EIdentifier("shockwave"), SHOCKWAVE);

        Registry.register(Registries.FEATURE, ESSENCE_ZONE_FEATURE_ID, ESSENCE_ZONE_FEATURE);

        Registry.register(Registries.SCREEN_HANDLER, new EIdentifier("closet_screen_handler"), CLOSET_SCREEN_HANDLER);
        Registry.register(Registries.SCREEN_HANDLER, new EIdentifier("ethereal_storage_screen_handler"), ETHEREAL_STORAGE_SCREEN_HANDLER);
        Registry.register(Registries.SCREEN_HANDLER, new EIdentifier("ethereal_furnace_screen_handler"), ETHEREAL_FURNACE_SCREEN_HANDLER);
        Registry.register(Registries.SCREEN_HANDLER, new EIdentifier("empower_table_screen_handler"), EMPOWER_TABLE_SCREEN_HANDLER);
        Registry.register(Registries.SCREEN_HANDLER, new EIdentifier("crate_screen_handler"), CRATE_SCREEN_HANDLER);

        EtherealGeneratorDispenserBehavior.register();

        EWorldGeneration.generateWorldGen();

        EtherologyRegistry.buildRegistry();

        ServerTickEvents.END_SERVER_TICK.register(server -> ServerTaskManager.INSTANCE.tickTasks());
    }
}