package ru.feytox.etherology.item;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import lombok.NonNull;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.gui.oculus.AspectComponent;
import ru.feytox.etherology.magic.aspects.EtherAspectsContainer;
import ru.feytox.etherology.magic.aspects.EtherAspectsProvider;

import java.util.ArrayList;
import java.util.List;
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
                .supplyAsync(() -> getTrueCrosshairTarget(client))
                .thenApplyAsync(hitResult -> createAspectHud(world, hitResult))
                .thenAcceptAsync(component -> {
                    displayedHud.clearChildren();
                    displayedHud.children(component);
                });
    }

    private static FlowLayout createRoot() {
        FlowLayout root = Containers.horizontalFlow(Sizing.fill(100), Sizing.fill(100));
        root.positioning(Positioning.relative(0, 0));
        return root;
    }

    @Nullable
    private static Component createTargetNameHud(ClientWorld world, @NonNull HitResult hitResult) {
        Text targetName = EtherAspectsProvider.getTargetName(world, hitResult);
        if (targetName == null) return null;

        return Components.label(targetName).shadow(true)
                .verticalTextAlignment(VerticalAlignment.CENTER)
                .horizontalTextAlignment(HorizontalAlignment.CENTER)
                .positioning(Positioning.relative(50, 45));
    }

    private static List<Component> createAspectHud(ClientWorld world, HitResult hitResult) {
        List<Component> components = new ArrayList<>();
        if (hitResult == null || hitResult.getType().equals(HitResult.Type.MISS)) return components;

        Component targetName = createTargetNameHud(world, hitResult);
        if (targetName != null) components.add(targetName);

        FlowLayout aspectsRoot = Containers.horizontalFlow(Sizing.content(), Sizing.content());
        aspectsRoot.positioning(Positioning.relative(50, 60));
        EtherAspectsContainer aspects = EtherAspectsProvider.getAspects(world, hitResult);
        if (aspects == null || aspects.isEmpty()) return components;

        aspects.getAspects().forEach((aspect, value) -> {
            AspectComponent aspectComponent = new AspectComponent(aspect, value);
            aspectsRoot.child(aspectComponent);
        });
        components.add(aspectsRoot);

        return components;
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
