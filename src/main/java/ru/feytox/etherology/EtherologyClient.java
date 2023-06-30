package ru.feytox.etherology;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import ru.feytox.etherology.block.closet.ClosetScreen;
import ru.feytox.etherology.block.crate.CrateBlockRenderer;
import ru.feytox.etherology.block.crate.CrateScreen;
import ru.feytox.etherology.block.empowerTable.EmpowerTableScreen;
import ru.feytox.etherology.block.etherealFurnace.EtherealFurnaceScreen;
import ru.feytox.etherology.block.etherealGenerators.metronome.EtherealMetronomeRenderer;
import ru.feytox.etherology.block.etherealGenerators.spinner.EtherealSpinnerRenderer;
import ru.feytox.etherology.block.etherealSocket.EtherealSocketRenderer;
import ru.feytox.etherology.block.etherealStorage.EtherealStorageRenderer;
import ru.feytox.etherology.block.etherealStorage.EtherealStorageScreen;
import ru.feytox.etherology.block.ringMatrix.RingMatrixBlockRenderer;
import ru.feytox.etherology.furniture.FurnitureBlockEntityRenderer;
import ru.feytox.etherology.gui.teldecore.Chapters;
import ru.feytox.etherology.gui.teldecore.chapters.ExampleChapter;
import ru.feytox.etherology.particle.*;
import ru.feytox.etherology.particle.utility.SmallLightning;
import ru.feytox.etherology.registry.item.ModelPredicates;
import ru.feytox.etherology.registry.particle.ClientParticleTypes;
import ru.feytox.etherology.util.delayedTask.ClientTaskManager;
import ru.feytox.etherology.util.feyapi.EtherNetwork;
import ru.feytox.etherology.util.gecko.EGeoNetwork;
import software.bernie.geckolib.network.GeckoLibNetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static ru.feytox.etherology.Etherology.*;
import static ru.feytox.etherology.registry.block.DecoBlocks.*;
import static ru.feytox.etherology.registry.block.EBlocks.*;

@Environment(EnvType.CLIENT)
public class EtherologyClient implements ClientModInitializer {

    public static Chapters chapters = null;
    public static Integer timer3 = -1;
    public static List<Supplier<Boolean>> timer3_supps = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        GeckoLibNetwork.registerClientReceiverPackets();
        ClientParticleTypes.registerAll();
        ModelPredicates.registerAll();

        BlockEntityRendererFactories.register(RING_MATRIX_BLOCK_ENTITY, RingMatrixBlockRenderer::new);
        BlockEntityRendererFactories.register(FURNITURE_BLOCK_ENTITY, FurnitureBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ETHEREAL_STORAGE_BLOCK_ENTITY, EtherealStorageRenderer::new);
        BlockEntityRendererFactories.register(ETHEREAL_SOCKET_BLOCK_ENTITY, EtherealSocketRenderer::new);
        BlockEntityRendererFactories.register(ETHEREAL_SPINNER_BLOCK_ENTITY, EtherealSpinnerRenderer::new);
        BlockEntityRendererFactories.register(ETHEREAL_METRONOME_BLOCK_ENTITY, EtherealMetronomeRenderer::new);
        BlockEntityRendererFactories.register(ETHEROLOGY_SIGN, SignBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(CRATE_BLOCK_ENTITY, CrateBlockRenderer::new);

        HandledScreens.register(CLOSET_SCREEN_HANDLER, ClosetScreen::new);
        HandledScreens.register(ETHEREAL_STORAGE_SCREEN_HANDLER, EtherealStorageScreen::new);
        HandledScreens.register(ETHEREAL_FURNACE_SCREEN_HANDLER, EtherealFurnaceScreen::new);
        HandledScreens.register(EMPOWER_TABLE_SCREEN_HANDLER, EmpowerTableScreen::new);
        HandledScreens.register(CRATE_SCREEN_HANDLER, CrateScreen::new);

        ParticleFactoryRegistry.getInstance().register(ELECTRICITY1, ElectricityParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ELECTRICITY2, ElectricityParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(SPARK, SparkParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(STEAM, SteamParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(LIGHT, OldLightParticle.SimpleFactory::new);
        ParticleFactoryRegistry.getInstance().register(LIGHT_VITAL, LightVitalParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(LIGHT_SPARK, OldLightParticle.SparkFactory::new);
        ParticleFactoryRegistry.getInstance().register(VITAL_ENERGY, VitalParticle.Factory::new);

        ParticleFactoryRegistry.getInstance().register(KETA_PARTICLE, ZoneParticle.KetaFactory::new);
        ParticleFactoryRegistry.getInstance().register(RELA_PARTICLE, ZoneParticle.RelaFactory::new);
        ParticleFactoryRegistry.getInstance().register(CLOS_PARTICLE, ZoneParticle.ClosFactory::new);
        ParticleFactoryRegistry.getInstance().register(VIA_PARTICLE, ZoneParticle.ViaFactory::new);

        ParticleFactoryRegistry.getInstance().register(GLINT_PARTICLE, GlintParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(SHOCKWAVE, ShockwaveParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(PUSHING_PARTICLE, OldLightParticle.PushingFactory::new);
        ParticleFactoryRegistry.getInstance().register(ATTRACT_PARTICLE, OldLightParticle.AttractFactory::new);

        BlockRenderLayerMap.INSTANCE.putBlock(ETHEREAL_SOCKET, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(PEACH_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(PEACH_TRAPDOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(SAMOVAR_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(SPILL_BARREL, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BEAMER, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(PEACH_SAPLING, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(PEACH_LEAVES, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ETHEREAL_METRONOME, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ETHEREAL_SPINNER, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ETHEREAL_STORAGE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BREWING_CAULDRON, RenderLayer.getCutout());

        SmallLightning.registerPacket();
        EGeoNetwork.registerPackets();
        EtherNetwork.registerPackets();

        ClientTickEvents.END_CLIENT_TICK.register((client -> {
            ClientTaskManager.INSTANCE.tickTasks();

            // TODO: 24.06.2023 use... something better than this garbage
            if (chapters == null && client.textRenderer != null) {
                chapters = new Chapters();
                chapters.add(new ExampleChapter());
            }

            timer3 += 1;
            if (timer3 >= 20) {
                timer3 = 0;
                List<Supplier<Boolean>> temp_timer3_supps = new ArrayList<>();
                for (Supplier<Boolean> supp: timer3_supps) {
                    Boolean result = supp.get();
                    if (result) {
                        temp_timer3_supps.add(supp);
                    }
                }
                timer3_supps = temp_timer3_supps;
            }
        }));
    }
}
