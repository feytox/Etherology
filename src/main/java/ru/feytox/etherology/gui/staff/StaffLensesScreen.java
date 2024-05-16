package ru.feytox.etherology.gui.staff;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.val;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.StickyKeyBinding;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.item.LensItem;
import ru.feytox.etherology.item.StaffItem;
import ru.feytox.etherology.magic.lens.LensMode;
import ru.feytox.etherology.mixin.KeyBindingAccessor;
import ru.feytox.etherology.mixin.StickyKeyBindingAccessor;
import ru.feytox.etherology.network.interaction.StaffMenuSelectionC2S;
import ru.feytox.etherology.registry.misc.EtherologyComponents;
import ru.feytox.etherology.util.misc.EIdentifier;
import ru.feytox.etherology.util.misc.RenderUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StaffLensesScreen extends Screen {

    private static final Identifier TEXTURE = new EIdentifier("textures/gui/lens_selection_bg.png");
    private static final Identifier BUTTON_TEXTURE = new EIdentifier("textures/gui/staff_mode_selection.png");
    private static Boolean invMoveLoaded = null;
    private static final int LENSES_REFRESH_RATE = 10;
    private static final float MENU_OPEN_DELAY = 7.5f;
    private static final float BUTTON_OFFSET = 40.0f;
    private static final float BUTTON_SCALE = 0.75f;
    private static final float BUTTON_WIDTH = 29.0f;
    private static final float BUTTON_HEIGHT = 47.0f;
    public static final float ITEM_RADIUS = 74.0f;
    public static final float LENS_OPEN_DELAY = 3.0f;

    private final Map<String, Boolean> wasToggleKeyDown = new Object2BooleanOpenHashMap<>();
    private final Screen parent;
    private final VertexConsumerProvider.Immediate immediate;

    @Nullable
    private ItemStack selectedStack = null;
    @Getter
    private LensSelectionType selected = LensSelectionType.NONE;
    @Nullable
    private List<LensWidget> lensWidgets = null;
    @Nullable
    private LensWidget mainLensWidget = null;
    @Nullable
    private LensMode lensMode = null;
    private float ticks; // TODO: 12.05.2024 consider to replace with smth else
    private float progressTicks; // I use two variables to ensure that the circle rotation does not reset when the ticks are reset.
    private boolean isClosing = false;

    public StaffLensesScreen(Screen parent) {
        super(Text.empty());
        this.parent = parent;
        MinecraftClient client = MinecraftClient.getInstance();
        immediate = client.getBufferBuilders().getEntityVertexConsumers();

        if (client.player == null) return;
        refreshData(client, client.player);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        if (client == null || client.world == null) return;
        renderTick(delta);

        float percent = Math.min(progressTicks / MENU_OPEN_DELAY, 1.0f);
        float progress = getProgress(percent);
        float circleScale = 0.75f;
        float centerX = width / 2.0f;
        float centerY = height / 2.0f;

        if (percent == 1.0f) updateMouse(centerX, centerY, mouseX, mouseY, progress, circleScale);
        renderCircle(matrices, centerX, centerY, progress, circleScale);
        renderLenses(matrices, centerX, centerY, progress, circleScale);
        renderButtons(matrices, centerX, centerY, progress, circleScale);
    }

    private void renderTick(float delta) {
        ticks += delta;
        progressTicks += delta;
        if (lensWidgets != null) lensWidgets.forEach(widget -> widget.tick(delta));
    }

    private float getProgress(float percent) {
        return isClosing ? 1 - percent * percent : 1 - (1 - percent) * (1 - percent);
    }

    private void updateMouse(float centerX, float centerY, int mouseX, int mouseY, float progress, float circleScale) {
        if (isClosing) return;
        if (updateButtons(centerX, centerY, mouseX, mouseY, progress, circleScale)) return;

        updateChosenItem(centerX, centerY, mouseX, mouseY, progress, circleScale);
    }

    private boolean updateButtons(float centerX, float centerY, int mouseX, int mouseY, float progress, float circleScale) {
        if (lensMode == null) return false;

        float scale = progress * BUTTON_SCALE;
        float y0 = centerY - 23.5f * scale * circleScale;
        float leftX = centerX + (-BUTTON_OFFSET - 14.5f) * scale * circleScale;
        float rightX = centerX + (BUTTON_OFFSET - 14.5f) * scale * circleScale;

        if (isInBox(mouseX, mouseY, leftX, y0, BUTTON_WIDTH*scale, BUTTON_HEIGHT*scale)) {
            selected = LensSelectionType.UP_ARROW;
            selectedStack = null;
            return true;
        }

        if (isInBox(mouseX, mouseY, rightX, y0, BUTTON_WIDTH*scale, BUTTON_HEIGHT*scale)) {
            selected = LensSelectionType.DOWN_ARROW;
            selectedStack = null;
            return true;
        }

        selected = LensSelectionType.NONE;
        return false;
    }

    private void updateChosenItem(float centerX, float centerY, int mouseX, int mouseY, float progress, float circleScale) {
        if (lensWidgets == null || lensWidgets.isEmpty()) return;

        for (val widget : lensWidgets) {
            selectedStack = widget.updateMouse(mouseX, mouseY, centerX, centerY, progress, circleScale);
            if (selectedStack != null) break;
        }

        if (selectedStack == null) {
            selected = LensSelectionType.NONE;
            return;
        }

        selected = LensSelectionType.ITEM;
    }

    public static boolean isInBox(int mouseX, int mouseY, float x0, float y0, float width, float height) {
        return mouseX >= x0 && mouseX <= x0+width && mouseY >= y0 && mouseY <= y0+height;
    }

    private void renderCircle(MatrixStack matrices, float centerX, float centerY, float progress, float circleScale) {
        float scale = circleScale * progress;
        float circleWidth = 512.0f;
        float circleHeight = 512.0f;
        float x = -circleWidth * scale / 2.0f;
        float y = -circleHeight * scale / 2.0f;

        float angle = 2.35619449019f + ticks / 2400 * MathHelper.TAU; // 3pi/4

        matrices.push();

        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1f, 1f, 1f, progress);

        matrices.translate(centerX, centerY, 0);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotation(angle));
        matrices.translate(x, y, 0);
        matrices.scale(scale, scale, scale);
        RenderUtils.renderTexture(matrices, 0, 0, 0, 0, circleWidth, circleHeight, 512, 512);

        matrices.pop();
    }

    private void renderLenses(MatrixStack matrices, float centerX, float centerY, float progress, float circleScale) {
        if (mainLensWidget != null) {
            mainLensWidget.render(this, immediate, matrices, itemRenderer, centerX, centerY, progress, circleScale);
        }

        if (lensWidgets == null || lensWidgets.isEmpty()) return;

        lensWidgets.forEach(widget -> widget.render(this, immediate, matrices, itemRenderer, centerX, centerY, progress, circleScale));
    }

    private static float getItemAngle(int index, int size) {
        return MathHelper.PI * (-0.5f + 2f * index / size);
    }

    private void renderButtons(MatrixStack matrices, float centerX, float centerY, float progress, float circleScale) {
        if (lensMode == null) return;

        boolean leftActive = lensMode.equals(LensMode.STREAM) || selected.equals(LensSelectionType.UP_ARROW);
        boolean rightActive = lensMode.equals(LensMode.CHARGE) || selected.equals(LensSelectionType.DOWN_ARROW);

        renderButton(matrices, true, false, centerX, centerY, progress, circleScale);
        renderButton(matrices, false, false, centerX, centerY, progress, circleScale);

        if (leftActive) renderButton(matrices, true, true, centerX, centerY, progress, circleScale);
        if (rightActive) renderButton(matrices, false, true, centerX, centerY, progress, circleScale);
    }

    private void renderButton(MatrixStack matrices, boolean isLeft, boolean isActive, float centerX, float centerY, float progress, float circleScale) {
        centerX += (isLeft ? -1 : 1) * BUTTON_OFFSET * circleScale * progress;
        int u = isLeft ? 11 : 60;
        int v = isActive ? 52 : 2;

        matrices.push();

        RenderSystem.setShaderTexture(0, BUTTON_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1f, 1f, 1f, progress);

        matrices.translate(centerX, centerY, 0);
        matrices.scale(progress, progress, progress);
        matrices.scale(BUTTON_SCALE, BUTTON_SCALE, BUTTON_SCALE);
        matrices.translate(-BUTTON_WIDTH / 2, -BUTTON_HEIGHT / 2, 0);

        RenderUtils.renderTexture(matrices, 0, 0, u, v, 29, 47, 29, 47, 128, 128);

        matrices.pop();
    }

    @Override
    public void tick() {
        super.tick();
        if (client == null || client.player == null || client.world == null) return;

        if (isClosing && progressTicks >= MENU_OPEN_DELAY) {
            close();
            return;
        }

        tickMovement(client.player);
        tickLensRefreshing(client, client.player, client.world);
    }

    private void tickLensRefreshing(MinecraftClient client, ClientPlayerEntity player, World world) {
        if (world.getTime() % LENSES_REFRESH_RATE != 0 && lensWidgets == null) return;
        refreshData(client, player);
    }

    private void refreshData(MinecraftClient client, ClientPlayerEntity player) {
        lensWidgets = createLensWidgets(client);

        ItemStack staffStack = StaffItem.getStaffStackFromHand(player);
        ItemStack mainLens;
        if (staffStack == null) {
            mainLensWidget = null;
            lensMode = null;
            return;
        }

        mainLens = LensItem.takeLensFromStaff(staffStack, false);
        if (mainLens == null) {
            lensMode = null;
            return;
        }

        mainLensWidget = new LensWidget(mainLens, null);
        val lens = EtherologyComponents.LENS.maybeGet(mainLens).orElse(null);
        if (lens == null) return;

        lensMode = lens.getLensMode();
    }

    private List<LensWidget> createLensWidgets(MinecraftClient client) {
        List<ItemStack> stacks = StaffItem.getPlayerLenses(client);
        if (stacks.isEmpty()) return new ObjectArrayList<>();
        if (lensWidgets != null && stacks.size() == lensWidgets.size()) return lensWidgets;

        int size = stacks.size();

        return IntStream.range(0, size)
                .mapToObj(i -> new LensWidget(stacks.get(i), getItemAngle(i, size)))
                .collect(Collectors.toCollection(ObjectArrayList::new));
    }

    /**
     * <a href="https://github.com/PieKing1215/InvMove/blob/master/crossversion/common/src/main/java/me/pieking1215/invmove/InvMove.java#L165">source</a>
     */
    private void tickMovement(ClientPlayerEntity player) {
        if (invMoveLoaded == null) invMoveLoaded = FabricLoader.getInstance().isModLoaded("invmove");
        if (invMoveLoaded) return;
        Input input = player.input;

        if (input.getClass() != KeyboardInput.class) return;

        // TODO: 22.02.2024 maybe fix toggle sneak
        KeyBindingAccessor.getKEYS_BY_ID().forEach((id, key) -> {
            if (!key.boundKey.getCategory().equals(InputUtil.Type.KEYSYM) || key.boundKey.getCode() == InputUtil.UNKNOWN_KEY.getCode()) return;

            boolean pressed = InputUtil.isKeyPressed(client.getWindow().getHandle(), key.boundKey.getCode());

            if (key instanceof StickyKeyBinding stickyKey && ((StickyKeyBindingAccessor) stickyKey).getToggleGetter().getAsBoolean()) {
                if (wasToggleKeyDown.containsKey(id)) {
                    if (!wasToggleKeyDown.get(id) && pressed) key.setPressed(true);
                }
                wasToggleKeyDown.put(id, pressed);
                key.setPressed(wasToggleKeyDown.get(id));
            } else {
                key.setPressed(pressed);
            }

            client.options.dropKey.setPressed(false);
        });
    }

    public void tryClose() {
        if (isClosing) return;
        isClosing = true;
        progressTicks = Math.max(MENU_OPEN_DELAY - progressTicks, 0.0f);
        sendSelectionPacket();
    }

    public void tryOpen() {
        if (!isClosing) return;
        isClosing = false;
        progressTicks = Math.max(MENU_OPEN_DELAY - progressTicks, 0.0f);
    }

    @Override
    public void close() {
        if (client != null) client.setScreen(parent);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public void sendSelectionPacket() {
        if (selected.equals(LensSelectionType.NONE)) return;
        if (!selected.isEmptySelectedItem() && (lensWidgets == null || lensWidgets.isEmpty())) return;

        ItemStack stack = selectedStack == null ? ItemStack.EMPTY : selectedStack;
        val packet = new StaffMenuSelectionC2S(selected, stack);
        packet.sendToServer();
    }
}
