package name.uwu.feytox.etherology.client;

import name.uwu.feytox.etherology.Etherology;
import name.uwu.feytox.etherology.blocks.closet.ClosetScreen;
import name.uwu.feytox.etherology.blocks.crucible.CrucibleBlockRenderer;
import name.uwu.feytox.etherology.blocks.etherWorkbench.EtherWorkbenchScreen;
import name.uwu.feytox.etherology.blocks.etherealStorage.EtherealStorageRenderer;
import name.uwu.feytox.etherology.blocks.etherealStorage.EtherealStorageScreen;
import name.uwu.feytox.etherology.blocks.ringMatrix.RingMatrixBlockRenderer;
import name.uwu.feytox.etherology.furniture.FurnitureBlockEntityRenderer;
import name.uwu.feytox.etherology.gui.teldecore.Chapters;
import name.uwu.feytox.etherology.gui.teldecore.chapters.ExampleChapter;
import name.uwu.feytox.etherology.magic.ether.EtherGlint;
import name.uwu.feytox.etherology.particle.*;
import name.uwu.feytox.etherology.particle.utility.SmallLightning;
import name.uwu.feytox.etherology.util.gecko.EGeoNetwork;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static name.uwu.feytox.etherology.BlocksRegistry.*;
import static name.uwu.feytox.etherology.Etherology.*;
import static name.uwu.feytox.etherology.ItemsRegistry.GLINT;

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

        HandledScreens.register(Etherology.ETHER_SCREEN_HANDLER, EtherWorkbenchScreen::new);
        HandledScreens.register(Etherology.CLOSET_SCREEN_HANDLER, ClosetScreen::new);
        HandledScreens.register(ETHEREAL_STORAGE_SCREEN_HANDLER, EtherealStorageScreen::new);

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

        ModelPredicateProviderRegistry.register(GLINT, new Identifier("ether_percentage"), ((stack, world, entity, seed) -> {
            EtherGlint glint = new EtherGlint(stack);
            return glint.getStoredEther() / glint.getMaxEther();
        }));

        SmallLightning.registerPacket();
        EGeoNetwork.registerPackets();

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
