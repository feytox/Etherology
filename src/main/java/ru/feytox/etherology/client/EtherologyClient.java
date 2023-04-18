package ru.feytox.etherology.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.blocks.closet.ClosetScreen;
import ru.feytox.etherology.blocks.crucible.CrucibleBlockRenderer;
import ru.feytox.etherology.blocks.empowerTable.EmpowerTableScreen;
import ru.feytox.etherology.blocks.etherealFurnace.EtherealFurnaceScreen;
import ru.feytox.etherology.blocks.etherealGenerators.metronome.EtherealMetronomeRenderer;
import ru.feytox.etherology.blocks.etherealGenerators.spinner.EtherealSpinnerRenderer;
import ru.feytox.etherology.blocks.etherealSocket.EtherealSocketRenderer;
import ru.feytox.etherology.blocks.etherealStorage.EtherealStorageRenderer;
import ru.feytox.etherology.blocks.etherealStorage.EtherealStorageScreen;
import ru.feytox.etherology.blocks.ringMatrix.RingMatrixBlockRenderer;
import ru.feytox.etherology.furniture.FurnitureBlockEntityRenderer;
import ru.feytox.etherology.gui.teldecore.Chapters;
import ru.feytox.etherology.gui.teldecore.chapters.ExampleChapter;
import ru.feytox.etherology.magic.ether.EtherGlint;
import ru.feytox.etherology.particle.*;
import ru.feytox.etherology.particle.utility.SmallLightning;
import ru.feytox.etherology.util.feyapi.EtherNetwork;
import ru.feytox.etherology.util.gecko.EGeoNetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static ru.feytox.etherology.BlocksRegistry.*;
import static ru.feytox.etherology.DecoBlocks.*;
import static ru.feytox.etherology.Etherology.*;
import static ru.feytox.etherology.ItemsRegistry.GLINT;

@Environment(EnvType.CLIENT)
public class EtherologyClient implements ClientModInitializer {

    public static Chapters chapters = null;
    public static Integer timer3 = -1;
    public static List<Supplier<Boolean>> timer3_supps = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(CRUCIBLE_BLOCK_ENTITY, CrucibleBlockRenderer::new);
        BlockEntityRendererFactories.register(RING_MATRIX_BLOCK_ENTITY, RingMatrixBlockRenderer::new);
        BlockEntityRendererFactories.register(FURNITURE_BLOCK_ENTITY, FurnitureBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ETHEREAL_STORAGE_BLOCK_ENTITY, EtherealStorageRenderer::new);
        BlockEntityRendererFactories.register(ETHEREAL_SOCKET_BLOCK_ENTITY, EtherealSocketRenderer::new);
        BlockEntityRendererFactories.register(ETHEREAL_SPINNER_BLOCK_ENTITY, EtherealSpinnerRenderer::new);
        BlockEntityRendererFactories.register(ETHEREAL_METRONOME_BLOCK_ENTITY, EtherealMetronomeRenderer::new);
        BlockEntityRendererFactories.register(ETHEROLOGY_SIGN, SignBlockEntityRenderer::new);

        HandledScreens.register(Etherology.CLOSET_SCREEN_HANDLER, ClosetScreen::new);
        HandledScreens.register(ETHEREAL_STORAGE_SCREEN_HANDLER, EtherealStorageScreen::new);
        HandledScreens.register(ETHEREAL_FURNACE_SCREEN_HANDLER, EtherealFurnaceScreen::new);
        HandledScreens.register(EMPOWER_TABLE_SCREEN_HANDLER, EmpowerTableScreen::new);

        ParticleFactoryRegistry.getInstance().register(ELECTRICITY1, ElectricityParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ELECTRICITY2, ElectricityParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(SPARK, SparkParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(STEAM, SteamParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(LIGHT, LightParticle.SimpleFactory::new);
        ParticleFactoryRegistry.getInstance().register(LIGHT_VITAL, LightVitalParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(LIGHT_SPARK, LightParticle.SparkFactory::new);
        ParticleFactoryRegistry.getInstance().register(VITAL_ENERGY, VitalParticle.Factory::new);

        ParticleFactoryRegistry.getInstance().register(KETA_PARTICLE, ZoneParticle.KetaFactory::new);
        ParticleFactoryRegistry.getInstance().register(RELA_PARTICLE, ZoneParticle.RelaFactory::new);
        ParticleFactoryRegistry.getInstance().register(CLOS_PARTICLE, ZoneParticle.ClosFactory::new);
        ParticleFactoryRegistry.getInstance().register(VIA_PARTICLE, ZoneParticle.ViaFactory::new);

        ParticleFactoryRegistry.getInstance().register(GLINT_PARTICLE, GlintParticle.Factory::new);

        ModelPredicateProviderRegistry.register(GLINT, new Identifier("ether_percentage"), ((stack, world, entity, seed) -> {
            EtherGlint glint = new EtherGlint(stack);
            return glint.getStoredEther() / glint.getMaxEther();
        }));

        BlockRenderLayerMap.INSTANCE.putBlock(ETHEREAL_SOCKET, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(PEACH_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(PEACH_TRAPDOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(SAMOVAR_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(SPILL_BARREL, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(BEAMER, RenderLayer.getCutout());

        SmallLightning.registerPacket();
        EGeoNetwork.registerPackets();
        EtherNetwork.registerPackets();

        ClientTickEvents.END_CLIENT_TICK.register((client -> {
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
