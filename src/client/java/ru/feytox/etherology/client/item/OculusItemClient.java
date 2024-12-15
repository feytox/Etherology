package ru.feytox.etherology.client.item;

import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.client.block.seal.SealBlockRenderer;
import ru.feytox.etherology.client.gui.oculus.AspectComponent;
import ru.feytox.etherology.client.util.ScaledLabelComponent;
import ru.feytox.etherology.magic.aspects.OculusAspectProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@UtilityClass
public class OculusItemClient {

    @Getter
    private static final FlowLayout displayedHud = createRoot();
    @Nullable
    private static CompletableFuture<Void> componentFuture = null;

    public static void tickOculus(World world, boolean selected) {
        if (!selected) {
            displayedHud.clearChildren();
            return;
        }

        SealBlockRenderer.refreshOculus(world.getTime());
        tickHud((ClientWorld) world);
    }

    private static void tickHud(@NonNull final ClientWorld world) {
        var client = MinecraftClient.getInstance();
        if (!client.options.getPerspective().isFirstPerson()) {
            displayedHud.clearChildren();
            return;
        }

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
        var root = Containers.horizontalFlow(Sizing.fill(100), Sizing.fill(100));
        root.positioning(Positioning.relative(0, 0));
        return root;
    }

    @Nullable
    private static Component createTargetNameHud(ClientWorld world, @NonNull HitResult hitResult) {
        var targetName = OculusAspectProvider.getTargetName(world, hitResult);
        if (targetName == null) return null;

        return new ScaledLabelComponent(targetName, 1.2f).shadow(true)
                .positioning(Positioning.relative(50, 45));
    }

    private static List<Component> createAspectHud(ClientWorld world, HitResult hitResult) {
        var components = new ObjectArrayList<Component>();
        if (hitResult == null || hitResult.getType().equals(HitResult.Type.MISS)) return components;

        var targetName = createTargetNameHud(world, hitResult);
        if (targetName != null) components.add(targetName);

        var aspectsRoot = Containers.horizontalFlow(Sizing.content(), Sizing.content());
        aspectsRoot.positioning(Positioning.relative(50, 60));
        var aspects = OculusAspectProvider.getAspects(world, hitResult);
        if (aspects == null || aspects.isEmpty()) return components;

        var sortedAspects = aspects.sorted(true, -1);
        sortedAspects.forEach(pair -> {
            var aspectComponent = new AspectComponent(pair.getFirst(), pair.getSecond());
            aspectsRoot.child(aspectComponent);
        });
        components.add(aspectsRoot);

        return components;
    }

    @Nullable
    private static HitResult getTrueCrosshairTarget(MinecraftClient client) {
        var firstTarget = client.crosshairTarget;
        var camera = client.getCameraEntity();
        if (camera == null || client.player == null) return firstTarget;

        var reachDistance = client.player.getEntityInteractionRange();
        var cameraPos = camera.getCameraPosVec(1.0F);
        var cameraRotation = camera.getRotationVec(1.0F);
        var vec = cameraPos.add(cameraRotation.x * reachDistance, cameraRotation.y * reachDistance, cameraRotation.z * reachDistance);

        var maxSquaredDistance = reachDistance * reachDistance;
        if (firstTarget != null) {
            maxSquaredDistance = firstTarget.getPos().squaredDistanceTo(cameraPos);
        }

        var box = camera.getBoundingBox().stretch(cameraRotation.multiply(reachDistance)).expand(1.0, 1.0, 1.0);
        var entityHitResult = ProjectileUtil.raycast(camera, cameraPos, vec, box,
                (entity1) -> !entity1.isSpectator(), maxSquaredDistance);
        if (entityHitResult == null) return firstTarget;
        if (firstTarget instanceof EntityHitResult oldTarget && oldTarget.getEntity().equals(entityHitResult.getEntity())) return firstTarget;

        var newTargetDistance = entityHitResult.getEntity().squaredDistanceTo(cameraPos);
        if (newTargetDistance > 9.0f) return firstTarget;
        return firstTarget == null || newTargetDistance < maxSquaredDistance ? entityHitResult : firstTarget;
    }

    // TODO: 21.02.2024 move to another class
    @NonNull
    public static Component initHud() {
        return displayedHud;
    }
}
