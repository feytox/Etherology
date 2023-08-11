package ru.feytox.etherology.util.deprecated;

import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.util.Drawer;
import net.minecraft.util.Identifier;

// TODO: 05/05/2023 в разные места отправить
@Deprecated
public class UwuLib {

    public static Surface tiled(Identifier texture, int u1, int v1, int u2, int v2, int textureWidth, int textureHeight) {
        return (matrices, component) -> {
            RenderSystem.setShaderTexture(0, texture);
            Drawer.drawTexture(matrices, component.x(), component.y(), u1, v1, u2-u1+1, v2-v1+1, textureWidth, textureHeight);
        };
    }

    public static ButtonComponent.Renderer texture(Identifier texture, int u1, int v1, int u2, int v2, int textureWidth, int textureHeight) {
        return (matrices, button, delta) -> {
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderTexture(0, texture);
            Drawer.drawTexture(matrices, button.getX(), button.getY(), u1, v1, u2-u1+1, v2-v1+1, textureWidth, textureHeight);
        };
    }

    public static ButtonComponent.Renderer bigTexture(Identifier texture, int u1, int v1, int u2, int v2, int textureWidth, int textureHeight) {
        return (matrices, button, delta) -> {
            int renderV1 = v1;
            int renderV2 = v2;
            // TODO: прочекать на более простой вариант
            if (!button.isHovered()) {
                renderV1 += 200;
                renderV2 += 200;
            }
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderTexture(0, texture);
            Drawer.drawTexture(matrices, button.getX(), button.getY(), u1, renderV1, u2 - u1 + 1, renderV2 - renderV1 + 1, textureWidth, textureHeight);
        };
    }
}
