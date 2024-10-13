package ru.feytox.etherology.item;

import com.mojang.datafixers.util.Pair;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.block.zone.ZoneCoreRenderer;
import ru.feytox.etherology.enums.EArmPose;
import ru.feytox.etherology.gui.oculus.AspectComponent;
import ru.feytox.etherology.magic.aspects.Aspect;
import ru.feytox.etherology.magic.aspects.AspectContainer;
import ru.feytox.etherology.magic.aspects.OculusAspectProvider;
import ru.feytox.etherology.util.misc.DoubleModel;
import ru.feytox.etherology.util.misc.ItemUtils;
import ru.feytox.etherology.util.misc.ScaledLabelComponent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class OculusItem extends Item implements DoubleModel {

    @Getter
    private static final FlowLayout displayedHud = createRoot();
    @Nullable
    private static CompletableFuture<Void> componentFuture = null;

    public OculusItem() {
        super(new Settings().maxCount(1));
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return EArmPose.OCULUS_ETHEROLOGY.getUseAction();
    }

    public static boolean isInHands(LivingEntity entity) {
        return ItemUtils.isInHands(entity, OculusItem.class);
    }

    // TODO: 21.02.2024 move to another class
    @NonNull
    public static Component initHud() {
        return displayedHud;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (!world.isClient) return;
        if (!selected) {
            displayedHud.clearChildren();
            return;
        }

        ZoneCoreRenderer.refreshOculus(world.getTime());
        tickHud((ClientWorld) world);
    }

    private static void tickHud(@NonNull final ClientWorld world) {
        final MinecraftClient client = MinecraftClient.getInstance();
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
        FlowLayout root = Containers.horizontalFlow(Sizing.fill(100), Sizing.fill(100));
        root.positioning(Positioning.relative(0, 0));
        return root;
    }

    @Nullable
    private static Component createTargetNameHud(ClientWorld world, @NonNull HitResult hitResult) {
        Text targetName = OculusAspectProvider.getTargetName(world, hitResult);
        if (targetName == null) return null;

        return new ScaledLabelComponent(targetName, 1.2f).shadow(true)
                .positioning(Positioning.relative(50, 45));
    }

    private static List<Component> createAspectHud(ClientWorld world, HitResult hitResult) {
        List<Component> components = new ObjectArrayList<>();
        if (hitResult == null || hitResult.getType().equals(HitResult.Type.MISS)) return components;

        Component targetName = createTargetNameHud(world, hitResult);
        if (targetName != null) components.add(targetName);

        FlowLayout aspectsRoot = Containers.horizontalFlow(Sizing.content(), Sizing.content());
        aspectsRoot.positioning(Positioning.relative(50, 60));
        AspectContainer aspects = OculusAspectProvider.getAspects(world, hitResult);
        if (aspects == null || aspects.isEmpty()) return components;

        List<Pair<Aspect, Integer>> sortedAspects = aspects.sorted(true, -1);
        sortedAspects.forEach(pair -> {
            AspectComponent aspectComponent = new AspectComponent(pair.getFirst(), pair.getSecond());
            aspectsRoot.child(aspectComponent);
        });
        components.add(aspectsRoot);

        return components;
    }

    @Nullable
    private static HitResult getTrueCrosshairTarget(MinecraftClient client) {
        HitResult firstTarget = client.crosshairTarget;
        Entity camera = client.getCameraEntity();
        if (camera == null || client.player == null) return firstTarget;

        double reachDistance = client.player.getEntityInteractionRange();
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
