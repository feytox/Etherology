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
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
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

        if (componentFuture != null && !componentFuture.isDone()) return;

        componentFuture = CompletableFuture
                .supplyAsync(() -> createHud(client, world))
                .thenAcceptAsync(component -> {
                    displayedHud.clearChildren();
                    if (component != null) displayedHud.child(component);
                    else componentFuture = null;
                });
    }

    private static FlowLayout createRoot() {
        FlowLayout root = Containers.horizontalFlow(Sizing.content(), Sizing.content());
        root.positioning(Positioning.relative(50, 60));
        return root;
    }

    @Nullable
    private static Component createHud(MinecraftClient client, ClientWorld world) {
        FlowLayout root = Containers.horizontalFlow(Sizing.content(), Sizing.content());
        HitResult hitResult = getTrueCrosshairTarget(client);
        if (hitResult == null || hitResult.getType().equals(HitResult.Type.MISS)) return null;

        EtherAspectsContainer aspects = getAspects(world, hitResult);
        if (aspects == null || aspects.isEmpty()) return null;

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

    @Nullable
    private static HitResult getTrueCrosshairTarget(MinecraftClient client) {
        HitResult firstTarget = client.crosshairTarget;
        Entity camera = client.getCameraEntity();
        if (camera == null || client.interactionManager == null) return firstTarget;

        double reachDistance = client.interactionManager.getReachDistance();
        Vec3d cameraPos = camera.getCameraPosVec(1.0F);
        Vec3d cameraRotation = camera.getRotationVec(1.0F);
        Vec3d vec = cameraPos.add(cameraRotation.x * reachDistance, cameraRotation.y * reachDistance, cameraRotation.z * reachDistance);

        double maxSquaredDistance = reachDistance * reachDistance;
        if (firstTarget != null) {
            maxSquaredDistance = firstTarget.getPos().squaredDistanceTo(cameraPos);
        }

        Box box = camera.getBoundingBox().stretch(cameraRotation.multiply(reachDistance)).expand(1.0, 1.0, 1.0);
        EntityHitResult entityHitResult = ProjectileUtil.raycast(camera, cameraPos, vec, box,
                (entity1) -> !entity1.isSpectator(), maxSquaredDistance);
        if (entityHitResult == null) return firstTarget;
        if (firstTarget instanceof EntityHitResult oldTarget && oldTarget.getEntity().equals(entityHitResult.getEntity())) return firstTarget;

        double newTargetDistance = entityHitResult.getEntity().squaredDistanceTo(cameraPos);
        if (newTargetDistance > 9.0f) return firstTarget;
        return firstTarget == null || newTargetDistance < maxSquaredDistance ? entityHitResult : firstTarget;
    }
}
