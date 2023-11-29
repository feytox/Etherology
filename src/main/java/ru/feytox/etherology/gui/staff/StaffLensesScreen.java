package ru.feytox.etherology.gui.staff;

import com.mojang.blaze3d.systems.RenderSystem;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.ItemComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import ru.feytox.etherology.item.LensItem;
import ru.feytox.etherology.item.StaffItem;
import ru.feytox.etherology.magic.lense.LensMode;
import ru.feytox.etherology.mixin.MinecraftClientAccessor;
import ru.feytox.etherology.registry.util.EtherologyComponents;
import ru.feytox.etherology.util.feyapi.EIdentifier;
import ru.feytox.etherology.util.feyapi.RenderUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class StaffLensesScreen extends BaseOwoScreen<FlowLayout> {

    private static final Identifier STAFF_MENU_TEXTURE = new EIdentifier("textures/gui/lens_selection_bg.png");
    @Nullable
    private CompletableFuture<List<Component>> refreshedMenu = CompletableFuture.supplyAsync(MinecraftClient::getInstance).thenApplyAsync(StaffLensesScreen::getPlayerLenses).thenApplyAsync(this::createItemComponents).thenApplyAsync(this::createSelectionMenu);
    private final Screen parent;
    @Getter
    private int chosenItem = -1;
    @Getter
    private LensSelectionType selected = LensSelectionType.NONE;
    private final FlowLayout menuRootComponent = Containers.horizontalFlow(Sizing.fill(100), Sizing.fill(100));

    private static Surface backgroundSurface() {
        return (matrices, component) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            PlayerEntity player = client.player;
            if (player == null) return;
            World world = player.getWorld();
            if (world == null) return;
            Screen screen = client.currentScreen;
            if (screen == null) return;
            val tickCounter = ((MinecraftClientAccessor) client).getRenderTickCounter();
            float tickDelta = tickCounter.tickDelta;

            RenderSystem.setShaderTexture(0, STAFF_MENU_TEXTURE);
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();

            float scale = component.width() / 512.0f;
            float centerX = screen.width / 2.0f;
            float centerY = screen.height / 2.0f;
            float x = centerX - component.width() * scale / 2.0f;
            float y = centerY - component.height() * scale / 2.0f;

            float angle = 2.35619449019f + (world.getTime() + tickDelta) / 2400 * MathHelper.TAU; // 3pi/4

            matrices.push();
            matrices.translate(centerX, centerY, 0);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotation(angle));
            matrices.translate(-centerX, -centerY, 0);
            matrices.scale(scale, scale, 0);
            RenderUtils.renderTexture(matrices, x / scale, y / scale, 0, 0, component.width(), component.height(), 512, 512);
            matrices.pop();
        };
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent.clearChildren();
        rootComponent.child(menuRootComponent);
    }

    @Override
    public void tick() {
        super.tick();
        if (client == null || refreshedMenu == null || !refreshedMenu.isDone()) return;

        List<Component> components = refreshedMenu.join();
        menuRootComponent.clearChildren();
        menuRootComponent.children(components);
        refreshedMenu = null;
    }

    public static List<ItemStack> getPlayerLenses(@NonNull MinecraftClient client) {
        List<ItemStack> result = new ObjectArrayList<>();
        if (client.player == null) return result;

        PlayerInventory inventory = client.player.getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!(stack.getItem() instanceof LensItem lensItem)) continue;
            if (!lensItem.isAdjusted()) continue;
            result.add(stack);
        }

        return result;
    }

    private List<Component> createSelectionMenu(List<Component> itemComponents) {
        List<Component> result = new ObjectArrayList<>();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return result;
        ItemStack staffStack = StaffItem.getStaffStackFromHand(client.player);
        if (staffStack == null) return result;

        int circleSize = getLensesCircleSize(itemComponents.size());
        int backSize = Math.round(circleSize * 512.0f / 180.0f);
        FlowLayout stacksRoot = Containers.horizontalFlow(Sizing.fixed(circleSize), Sizing.fixed(circleSize));
        stacksRoot.children(itemComponents);

        FlowLayout circleBack = Containers.horizontalFlow(Sizing.fixed(backSize), Sizing.fixed(backSize));
        circleBack.surface(backgroundSurface());

        ItemStack staffCopy = staffStack.copy();
        ItemStack currentLenseStack = LensItem.takeLenseFromStaff(staffCopy);

        val lens = currentLenseStack == null ? null : EtherologyComponents.LENS.get(staffStack);

        if (lens != null && !lens.isEmpty()) {
            val lensMode = lens.getLensMode();
            boolean isUpActive = lensMode.equals(LensMode.STREAM);
            boolean isDownActive = lensMode.equals(LensMode.CHARGE);
            LensModeSelectionButton.create(true, isUpActive, circleBack, selected -> this.selected = selected, () -> this.selected, backSize);
            LensModeSelectionButton.create(false, isDownActive, circleBack, selected -> this.selected = selected, () -> this.selected, backSize);

            ItemComponent currentLens = Components.item(currentLenseStack);
            result.add(currentLens.positioning(Positioning.relative(50, 50)));
        }

        result.add(circleBack.positioning(Positioning.relative(50, 50)));
        result.add(stacksRoot.positioning(Positioning.relative(50, 50)));

        return result;
    }

    private List<Component> createItemComponents(List<ItemStack> stacks) {
        List<Component> result = new ObjectArrayList<>();
        if (stacks.isEmpty()) return result;

        int size = stacks.size();
        for (int i = 0; i < size; i++) {
            ItemStack stack = stacks.get(i);
            ItemComponent component = Components.item(stack);
            final int itemId = i;
            component.mouseEnter().subscribe(() -> {
                selected = LensSelectionType.ITEM;
                chosenItem = itemId;
            });
            component.mouseLeave().subscribe(() -> {
                if (chosenItem == itemId) chosenItem = -1;
                if (selected.equals(LensSelectionType.ITEM)) selected = LensSelectionType.NONE;
            });
            float itemAngle = MathHelper.PI * (-1 / 2f + 2f * i / size);
            int xPercent = Math.round(50 * (1 + MathHelper.cos(itemAngle)));
            int yPercent = Math.round(50 * (1 + MathHelper.sin(itemAngle)));
            component.positioning(Positioning.relative(xPercent, yPercent));
            result.add(component);
        }

        return result;
    }

    @Override
    public void close() {
        if (client != null) client.setScreen(parent);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public static int getLensesCircleSize(int itemCount) {
        // TODO: 15.11.2023 dynamic size
        return 150;
    }
}
