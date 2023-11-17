package ru.feytox.etherology.gui.staff;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.ItemComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.util.UIErrorToast;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SignItem;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Math;
import ru.feytox.etherology.Etherology;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class StaffLensesScreen extends BaseOwoScreen<FlowLayout> {

    @Nullable
    private CompletableFuture<List<Component>> refreshedMenu = null;
    private final Screen parent;
    @Getter
    private int chosenItem = -1;

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        List<Component> components = new ObjectArrayList<>();
        if (refreshedMenu != null) components = refreshedMenu.join();

        rootComponent.children(components);
        refreshedMenu = null;
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void tick() {
        super.tick();
        if (client == null) return;

        if (refreshedMenu == null) {

            refreshedMenu = CompletableFuture
                    .supplyAsync(() -> getPlayerLenses(client))
                    .thenApplyAsync(this::getItemComponents);
        }

        if (refreshedMenu != null && !refreshedMenu.isDone()) return;

        try {
            uiAdapter = createAdapter();
            build(uiAdapter.rootComponent);

            uiAdapter.inflateAndMount();
        } catch (Exception error) {
            Etherology.ELOGGER.warn("Could not refresh staff lenses screen", error);
            UIErrorToast.report(error);
            invalid = true;
        }
    }

    public static List<ItemStack> getPlayerLenses(MinecraftClient client) {
        List<ItemStack> result = new ObjectArrayList<>();
        if (client.player == null) return result;

        PlayerInventory inventory = client.player.getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.getItem() instanceof SignItem) result.add(stack);
        }

        return result;
    }

    private List<Component> getItemComponents(List<ItemStack> stacks) {
        List<Component> result = new ObjectArrayList<>();
        if (stacks.isEmpty()) return result;

        // TODO: 15.11.2023 dynamic size
        FlowLayout stacksRoot = Containers.horizontalFlow(Sizing.fixed(150), Sizing.fixed(150));

        int size = stacks.size();
        for (int i = 0; i < size; i++) {
            ItemStack stack = stacks.get(i);
            ItemComponent component = Components.item(stack);
            final int itemId = i;
            component.mouseEnter().subscribe(() -> chosenItem = itemId);
            component.mouseLeave().subscribe(() -> {
                if (chosenItem == itemId) chosenItem = -1;
            });
            float itemAngle = MathHelper.PI * (-1 / 2f + 2f * i / size);
            int xPercent = Math.round(50 * (1 + MathHelper.cos(itemAngle)));
            int yPercent = Math.round(50 * (1 + MathHelper.sin(itemAngle)));
            component.positioning(Positioning.relative(xPercent, yPercent));
            stacksRoot.child(component);
        }
        result.add(stacksRoot.positioning(Positioning.relative(50, 50)));

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
}
