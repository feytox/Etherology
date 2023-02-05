package name.uwu.feytox.etherology.client;

import name.uwu.feytox.etherology.Etherology;
import name.uwu.feytox.etherology.blocks.crucible.CrucibleBlockRenderer;
import name.uwu.feytox.etherology.blocks.etherWorkbench.EtherWorkbenchScreen;
import name.uwu.feytox.etherology.blocks.ringMatrix.RingMatrixBlockRenderer;
import name.uwu.feytox.etherology.gui.teldecore.Chapters;
import name.uwu.feytox.etherology.gui.teldecore.chapters.ExampleChapter;
import name.uwu.feytox.etherology.particle.*;
import name.uwu.feytox.etherology.particle.utility.SmallLightning;
import name.uwu.feytox.etherology.util.gecko.EGeoNetwork;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static name.uwu.feytox.etherology.BlocksRegistry.CRUCIBLE_BLOCK_ENTITY;
import static name.uwu.feytox.etherology.BlocksRegistry.RING_MATRIX_BLOCK_ENTITY;
import static name.uwu.feytox.etherology.Etherology.*;

@Environment(EnvType.CLIENT)
public class EtherologyClient implements ClientModInitializer {

    public static Chapters chapters = null;
    public static Integer timer3 = -1;
    public static List<Supplier<Boolean>> timer3_supps = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(CRUCIBLE_BLOCK_ENTITY, CrucibleBlockRenderer::new);
        BlockEntityRendererFactories.register(RING_MATRIX_BLOCK_ENTITY, RingMatrixBlockRenderer::new);

        HandledScreens.register(Etherology.ETHER_SCREEN_HANDLER, EtherWorkbenchScreen::new);

        ParticleFactoryRegistry.getInstance().register(ELECTRICITY1, ElectricityParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ELECTRICITY2, ElectricityParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(SPARK, SparkParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(STEAM, SteamParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(LIGHT, LightParticle.SimpleFactory::new);
        ParticleFactoryRegistry.getInstance().register(LIGHT_VITAL, LightVitalParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(LIGHT_SPARK, LightParticle.SparkFactory::new);
        ParticleFactoryRegistry.getInstance().register(VITAL_ENERGY, VitalParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ZONE_PARTICLE, ZoneParticle.Factory::new);

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
                List<Supplier<Boolean>> temp_timer3_supps = new ArrayList<>();;
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
