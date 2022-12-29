package name.uwu.feytox.etherology.client;

import name.uwu.feytox.etherology.BlocksRegistry;
import name.uwu.feytox.etherology.Etherology;
import name.uwu.feytox.etherology.ItemsRegistry;
import name.uwu.feytox.etherology.blocks.crucible.CrucibleBlockItemRenderer;
import name.uwu.feytox.etherology.blocks.crucible.CrucibleBlockRenderer;
import name.uwu.feytox.etherology.blocks.etherWorkbench.EtherWorkbenchScreen;
import name.uwu.feytox.etherology.blocks.ringMatrix.RingMatrixBlockRenderer;
import name.uwu.feytox.etherology.gui.teldecore.Chapters;
import name.uwu.feytox.etherology.gui.teldecore.chapters.ExampleChapter;
import name.uwu.feytox.etherology.particle.MovingParticle;
import name.uwu.feytox.etherology.util.EIdentifier;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.screen.PlayerScreenHandler;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static name.uwu.feytox.etherology.Etherology.CONSUMING;

@Environment(EnvType.CLIENT)
public class EtherologyClient implements ClientModInitializer {

    public static Chapters chapters = null;
    public static Integer timer3 = -1;
    public static List<Supplier<Boolean>> timer3_supps = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        GeoItemRenderer.registerItemRenderer(ItemsRegistry.CRUCIBLE_BLOCK_ITEM, new CrucibleBlockItemRenderer());
        BlockEntityRendererRegistry.register(BlocksRegistry.CRUCIBLE_BLOCK_ENTITY, CrucibleBlockRenderer::new);
        BlockEntityRendererRegistry.register(BlocksRegistry.RING_MATRIX_BLOCK_ENTITY, RingMatrixBlockRenderer::new);
        HandledScreens.register(Etherology.ETHER_SCREEN_HANDLER, EtherWorkbenchScreen::new);

        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(((atlasTexture, registry) ->
                registry.register(new EIdentifier("particle/consuming"))));


        ParticleFactoryRegistry.getInstance().register(CONSUMING, MovingParticle.Factory::new);

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
