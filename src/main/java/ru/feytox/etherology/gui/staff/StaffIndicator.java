package ru.feytox.etherology.gui.staff;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.val;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.item.LensItem;
import ru.feytox.etherology.item.StaffItem;
import ru.feytox.etherology.mixin.InGameHudAccessor;
import ru.feytox.etherology.registry.misc.EtherologyComponents;

import static net.minecraft.client.gui.DrawContext.GUI_ICONS_TEXTURE;

@Environment(EnvType.CLIENT)
public class StaffIndicator {

    @Nullable
    private static Float prevHudData = null;

    @Nullable
    private static Float hudData = null;

    public static void renderHud(MatrixStack matrices, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null) return;

        ItemStack staffStack = StaffItem.getStaffStackFromHand(player);
        if (staffStack == null || hudData == null || prevHudData == null) return;

        float progress = MathHelper.lerp(tickDelta, prevHudData, hudData);
        if (progress >= 1.0f) return;

        val accessor = ((InGameHudAccessor) client.inGameHud);
        int x = accessor.getScaledWidth() / 2 - 8;
        int y = accessor.getScaledHeight() / 2 - 7 + 16;
        int width = (int) (17.0f * progress);

        matrices.push();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);

        client.inGameHud.drawTexture(matrices, x, y, 36, 94, 16, 4);
        client.inGameHud.drawTexture(matrices, x, y, 52, 94, width, 4);

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.defaultBlendFunc();
        matrices.pop();
    }

    public static void tickHudData(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        ClientWorld world = client.world;
        if (player == null || world == null) return;

        prevHudData = hudData;
        hudData = refreshData(player, world);
        if (prevHudData == null || hudData == null || prevHudData == 1.0f) prevHudData = hudData;
    }

    private static Float refreshData(ClientPlayerEntity player, ClientWorld world) {
        ItemStack staffStack = StaffItem.getStaffStackFromHand(player);
        if (staffStack == null) return null;

        ItemStack lensStack = LensItem.getLensStack(staffStack);
        if (lensStack == null || !(lensStack.getItem() instanceof LensItem lensItem)) return null;

        val lensOptional = EtherologyComponents.LENS.maybeGet(lensStack);
        if (lensOptional.isEmpty()) return null;

        val lensData = lensOptional.get();
        return switch (lensData.getLensMode()) {
            case CHARGE -> {
                int chargeTime = lensItem.getChargeTime(lensData, player.getItemUseTime());
                float result = (float) chargeTime / LensItem.CHARGE_LIMIT;
                yield result > 0.0f ? result : null;
            }
            case STREAM -> {
                int maxCooldown = lensItem.getStreamCooldown(lensData);
                long remainTicks = Math.min(maxCooldown, maxCooldown - lensData.getCooldown(world));
                yield (float) remainTicks / maxCooldown;
            }
        };
    }
}
