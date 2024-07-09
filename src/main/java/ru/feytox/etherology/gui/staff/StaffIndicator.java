package ru.feytox.etherology.gui.staff;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.val;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.item.LensItem;
import ru.feytox.etherology.item.StaffItem;
import ru.feytox.etherology.magic.lens.LensComponent;
import ru.feytox.etherology.mixin.InGameHudAccessor;

@Environment(EnvType.CLIENT)
public class StaffIndicator {

    private static final Identifier CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_TEXTURE = new Identifier("hud/crosshair_attack_indicator_background");
    private static final Identifier CROSSHAIR_ATTACK_INDICATOR_PROGRESS_TEXTURE = new Identifier("hud/crosshair_attack_indicator_progress");

    @Nullable
    private static Float prevHudData = null;

    @Nullable
    private static Float hudData = null;

    public static void renderHud(DrawContext context, float tickDelta) {
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

        context.push();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);

        context.drawGuiTexture(CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_TEXTURE, x, y, 16, 4);
        context.drawGuiTexture(CROSSHAIR_ATTACK_INDICATOR_PROGRESS_TEXTURE, 16, 4, 0, 0, x, y, width, 4);

        RenderSystem.defaultBlendFunc();
        context.pop();
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
