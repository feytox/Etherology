package name.uwu.feytox.lotyh;

import name.uwu.feytox.lotyh.blocks.etherWorkbench.EtherWorkbenchScreenHandler;
import name.uwu.feytox.lotyh.commands.DevCommands;
import name.uwu.feytox.lotyh.enums.MixTypes;
import name.uwu.feytox.lotyh.recipes.alchemy.AlchemyRecipe;
import name.uwu.feytox.lotyh.recipes.alchemy.AlchemyRecipeSerializer;
import name.uwu.feytox.lotyh.recipes.armillary.ArmillaryRecipe;
import name.uwu.feytox.lotyh.recipes.armillary.ArmillaryRecipeSerializer;
import name.uwu.feytox.lotyh.recipes.ether.EtherRecipe;
import name.uwu.feytox.lotyh.recipes.ether.EtherRecipeSerializer;
import name.uwu.feytox.lotyh.util.LIdentifier;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.registry.Registry;

public class Lotyh implements ModInitializer {

    public static final String MOD_ID = "lotyh";
    public static final ExtendedScreenHandlerType<EtherWorkbenchScreenHandler> ETHER_SCREEN_HANDLER =
            new ExtendedScreenHandlerType<>(((syncId, inventory, buf) ->
                    new EtherWorkbenchScreenHandler(syncId, inventory)));
    public static final DefaultParticleType CONSUMING = FabricParticleTypes.simple();


    @Override
    public void onInitialize() {
        ItemsRegistry.registerItems();
        BlocksRegistry.register();
        MixTypes.registerMixes();
        DevCommands.register();

        // TODO: move somewhere else
        Registry.register(Registry.RECIPE_SERIALIZER, AlchemyRecipeSerializer.ID,
                AlchemyRecipeSerializer.INSTANCE);
        Registry.register(Registry.RECIPE_TYPE, new LIdentifier(AlchemyRecipe.Type.ID), AlchemyRecipe.Type.INSTANCE);

        Registry.register(Registry.RECIPE_SERIALIZER, EtherRecipeSerializer.ID,
                EtherRecipeSerializer.INSTANCE);
        Registry.register(Registry.RECIPE_TYPE, new LIdentifier(EtherRecipe.Type.ID), EtherRecipe.Type.INSTANCE);

        Registry.register(Registry.RECIPE_SERIALIZER, ArmillaryRecipeSerializer.ID,
                ArmillaryRecipeSerializer.INSTANCE);
        Registry.register(Registry.RECIPE_TYPE, new LIdentifier(ArmillaryRecipe.Type.ID), ArmillaryRecipe.Type.INSTANCE);

        Registry.register(Registry.PARTICLE_TYPE, new LIdentifier("consuming"), CONSUMING);
    }

    static {
        Registry.register(Registry.SCREEN_HANDLER, new LIdentifier("ether_screen_handler"), ETHER_SCREEN_HANDLER);
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