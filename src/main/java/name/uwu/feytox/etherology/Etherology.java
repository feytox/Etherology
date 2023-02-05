package name.uwu.feytox.etherology;

import name.uwu.feytox.etherology.blocks.etherWorkbench.EtherWorkbenchScreenHandler;
import name.uwu.feytox.etherology.commands.DevCommands;
import name.uwu.feytox.etherology.enums.MixTypes;
import name.uwu.feytox.etherology.recipes.alchemy.AlchemyRecipe;
import name.uwu.feytox.etherology.recipes.alchemy.AlchemyRecipeSerializer;
import name.uwu.feytox.etherology.recipes.armillary.ArmillaryRecipe;
import name.uwu.feytox.etherology.recipes.armillary.ArmillaryRecipeSerializer;
import name.uwu.feytox.etherology.recipes.ether.EtherRecipe;
import name.uwu.feytox.etherology.recipes.ether.EtherRecipeSerializer;
import name.uwu.feytox.etherology.util.feyapi.EIdentifier;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static name.uwu.feytox.etherology.BlocksRegistry.*;
import static name.uwu.feytox.etherology.ItemsRegistry.*;

public class Etherology implements ModInitializer {

    public static final String MOD_ID = "etherology";
    public static final ExtendedScreenHandlerType<EtherWorkbenchScreenHandler> ETHER_SCREEN_HANDLER =
            new ExtendedScreenHandlerType<>(((syncId, inventory, buf) ->
                    new EtherWorkbenchScreenHandler(syncId, inventory)));

    public static final DefaultParticleType ELECTRICITY1 = FabricParticleTypes.simple();
    public static final DefaultParticleType ELECTRICITY2 = FabricParticleTypes.simple();
    public static final DefaultParticleType SPARK = FabricParticleTypes.simple();
    public static final DefaultParticleType STEAM = FabricParticleTypes.simple();
    public static final DefaultParticleType LIGHT = FabricParticleTypes.simple();
    public static final DefaultParticleType LIGHT_VITAL = FabricParticleTypes.simple();
    public static final DefaultParticleType LIGHT_SPARK = FabricParticleTypes.simple();
    public static final DefaultParticleType VITAL_ENERGY = FabricParticleTypes.simple();
    public static final DefaultParticleType ZONE_PARTICLE = FabricParticleTypes.simple();

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
        MixTypes.registerMixes();
        DevCommands.register();

        // TODO: move somewhere else
        Registry.register(Registries.RECIPE_SERIALIZER, AlchemyRecipeSerializer.ID,
                AlchemyRecipeSerializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, new EIdentifier(AlchemyRecipe.Type.ID), AlchemyRecipe.Type.INSTANCE);

        Registry.register(Registries.RECIPE_SERIALIZER, EtherRecipeSerializer.ID,
                EtherRecipeSerializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, new EIdentifier(EtherRecipe.Type.ID), EtherRecipe.Type.INSTANCE);

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
        Registry.register(Registries.PARTICLE_TYPE, new EIdentifier("zone_particle"), ZONE_PARTICLE);

        ItemGroupEvents.modifyEntriesEvent(ETHER_GROUP).register(content -> {
            content.add(ARMILLARY_MATRIX_BASE);
            content.add(CRUCIBLE);
            content.add(ETHER_WORKBENCH);
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
    }

    static {
        Registry.register(Registries.SCREEN_HANDLER, new EIdentifier("ether_screen_handler"), ETHER_SCREEN_HANDLER);
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