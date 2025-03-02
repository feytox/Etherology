package ru.feytox.etherology.client.item;

import lombok.experimental.UtilityClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;
import ru.feytox.etherology.client.block.seal.SealBlockRenderer;
import ru.feytox.etherology.client.gui.OcularOverlay;

@UtilityClass
public class OcularItemClient {

    public static void tickOcular(World world) {
        var client = MinecraftClient.getInstance();

        if (client.options.getPerspective().isFirstPerson()) {
            SealBlockRenderer.refreshSeeSealsAbility(world.getTime(), true, true);
            OcularOverlay.enableShader();
        } else
            OcularOverlay.disableShader();
    }

}
