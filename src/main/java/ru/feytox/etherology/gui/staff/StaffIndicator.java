package ru.feytox.etherology.gui.staff;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.val;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.item.LensItem;
import ru.feytox.etherology.item.StaffItem;
import ru.feytox.etherology.magic.lens.LensComponent;
import ru.feytox.etherology.util.misc.EIdentifier;

@Environment(EnvType.CLIENT)
public class StaffIndicator {

    private static final Identifier BACKGROUND_TEXTURE = EIdentifier.of("hud/staff/crosshair_cast_indicator_background");
    private static final Identifier PROGRESS_TEXTURE = EIdentifier.of("hud/staff/crosshair_cast_indicator_progress");
    private static final Identifier FULL_TEXTURE = EIdentifier.of("hud/staff/crosshair_cast_indicator_full");

    @Nullable
    private static Float prevIndicatorProgress = null;

    @Nullable
    private static Float indicatorProgress = null;

    /**
     * @see InGameHud#renderCrosshair(DrawContext, float)
     */
    public static void renderHud(DrawContext context, float tickDelta) {
        if (prevIndicatorProgress == null || indicatorProgress == null) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        if (!StaffItem.isStaffInHand(client.player)) return;

        float progress = MathHelper.lerp(tickDelta, prevIndicatorProgress, indicatorProgress);
        int x = context.getScaledWindowWidth() / 2 - 8;
        int y = context.getScaledWindowHeight() / 2 - 7 + 16;

        context.push();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);

        if (progress >= 1.0f) {
            context.drawGuiTexture(FULL_TEXTURE, x, y, 16, 16);
        }
        else {
            int width = (int) (17.0f * progress);
            context.drawGuiTexture(BACKGROUND_TEXTURE, x, y, 16, 4);
            context.drawGuiTexture(PROGRESS_TEXTURE, 16, 4, 0, 0, x, y, width, 4);
        }

        RenderSystem.defaultBlendFunc();
        context.pop();
    }

    public static void tickHudData(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        ClientWorld world = client.world;
        if (player == null || world == null) return;

        prevIndicatorProgress = indicatorProgress;
        indicatorProgress = refreshProgress(player, world);
        if (prevIndicatorProgress == null || indicatorProgress == null || prevIndicatorProgress == 1.0f) prevIndicatorProgress = indicatorProgress;
    }

    private static Float refreshProgress(ClientPlayerEntity player, ClientWorld world) {
        ItemStack staffStack = StaffItem.getStaffStackFromHand(player);
        if (staffStack == null) return null;

        ItemStack lensStack = LensItem.getStaffLens(staffStack);
        if (lensStack == null || !(lensStack.getItem() instanceof LensItem lensItem)) return null;

        val lensData = LensComponent.get(lensStack).orElse(null);
        if (lensData == null) return null;

        return switch (lensData.mode()) {
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
