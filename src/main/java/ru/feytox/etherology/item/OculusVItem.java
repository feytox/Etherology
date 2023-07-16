package ru.feytox.etherology.item;

import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import lombok.NonNull;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.block.brewingCauldron.BrewingCauldronBlockEntity;
import ru.feytox.etherology.data.item_aspects.ItemAspectsLoader;
import ru.feytox.etherology.gui.oculus.AspectComponent;
import ru.feytox.etherology.magic.aspects.EtherAspectsContainer;
import ru.feytox.etherology.registry.block.EBlocks;

import java.util.concurrent.CompletableFuture;

public class OculusVItem extends Item {
    private static final FlowLayout displayedHud = createRoot();
    @Nullable
    private static CompletableFuture<Void> componentFuture = null;

    public OculusVItem() {
        super(new FabricItemSettings().maxCount(1));
    }

    @NonNull
    public static Component initHud() {
        return displayedHud;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (!world.isClient || !entity.isPlayer()) return;
        if (!selected) {
            displayedHud.clearChildren();
            return;
        }

        tickHud((ClientWorld) world);
    }

    private static void tickHud(@NonNull final ClientWorld world) {
        final MinecraftClient client = MinecraftClient.getInstance();
        final HitResult hitResult = client.crosshairTarget;

        if (hitResult == null || hitResult.getType().equals(HitResult.Type.MISS)) {
            componentFuture = null;
            displayedHud.clearChildren();
            return;
        }

        if (componentFuture != null && !componentFuture.isDone()) return;

        componentFuture = CompletableFuture
                .supplyAsync(() -> createHud(world, hitResult))
                .thenAcceptAsync(component -> {
                    displayedHud.clearChildren();
                    displayedHud.child(component);
                });
    }

    private static FlowLayout createRoot() {
        FlowLayout root = Containers.horizontalFlow(Sizing.content(), Sizing.content());
        root.positioning(Positioning.relative(50, 60));
        return root;
    }

    private static Component createHud(ClientWorld world, HitResult hitResult) {
        FlowLayout root = Containers.horizontalFlow(Sizing.content(), Sizing.content());

        EtherAspectsContainer aspects = getAspects(world, hitResult);
        if (aspects == null || aspects.isEmpty()) return root;

        aspects.getAspects().forEach((aspect, value) -> {
            AspectComponent aspectComponent = new AspectComponent(aspect, value);
            root.child(aspectComponent);
        });

        return root;
    }

    @Nullable
    private static EtherAspectsContainer getAspects(ClientWorld world, HitResult hitResult) {
        if (hitResult instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof ItemEntity itemEntity) {
            return ItemAspectsLoader.getAspectsOf(itemEntity.getStack()).orElse(null);
        }

        if (!(hitResult instanceof BlockHitResult blockHitResult)) return null;

        BlockPos pos = blockHitResult.getBlockPos();
        BlockState state = world.getBlockState(pos);
        if (!state.isOf(EBlocks.BREWING_CAULDRON)) {
            return ItemAspectsLoader.getAspectsOf(state.getBlock().asItem()).orElse(null);
        }

        // TODO: 15.07.2023 replace with EtherAspectsBlockContainer
        return world.getBlockEntity(pos) instanceof BrewingCauldronBlockEntity cauldron
                ? cauldron.getAspects() : ItemAspectsLoader.getAspectsOf(state.getBlock().asItem()).orElse(null);

    }
}
