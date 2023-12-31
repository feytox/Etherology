package ru.feytox.etherology.item;

import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Positioning;
import io.wispforest.owo.ui.core.Sizing;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
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
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.enums.EArmPose;
import ru.feytox.etherology.gui.oculus.AspectComponent;
import ru.feytox.etherology.magic.aspects.EtherAspectsContainer;
import ru.feytox.etherology.magic.aspects.EtherAspectsProvider;
import ru.feytox.etherology.magic.zones.EssenceZone;
import ru.feytox.etherology.magic.zones.EssenceZoneType;
import ru.feytox.etherology.magic.zones.ZoneComponent;
import ru.feytox.etherology.particle.effects.ZoneParticleEffect;
import ru.feytox.etherology.registry.particle.ServerParticleTypes;
import ru.feytox.etherology.util.feyapi.ScaledLabelComponent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class OculusItem extends Item {
    @Getter
    private static final FlowLayout displayedHud = createRoot();
    @Nullable
    private static CompletableFuture<Void> componentFuture = null;

    public OculusItem() {
        super(new FabricItemSettings().maxCount(1));
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return EArmPose.OCULUS_ETHEROLOGY.getUseAction();
    }

    public static boolean isUsingOculus(LivingEntity entity) {
        val items = entity.getHandItems();
        for (ItemStack stack : items) {
            if (stack.getItem() instanceof OculusItem) return true;
        }
        return false;
    }

    @NonNull
    public static Component initHud() {
        return displayedHud;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (!entity.isPlayer() || !world.isClient) return;
        if (!selected) {
            displayedHud.clearChildren();
            return;
        }

        tickZoneParticles((ClientWorld) world, (ClientPlayerEntity) entity);
        tickHud((ClientWorld) world);
    }

    private void tickZoneParticles(ClientWorld world, ClientPlayerEntity player) {
        ChunkPos centerChunk = player.getChunkPos();
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                trySpawnZoneParticles(world, centerChunk, x, z);
            }
        }
    }

    private void trySpawnZoneParticles(ClientWorld world, ChunkPos centerChunk, int x, int z) {
        Chunk chunk = world.getChunk(x + centerChunk.x, z + centerChunk.z);
        ZoneComponent zone = ZoneComponent.getZone(chunk);
        if (zone == null || zone.isEmpty()) return;

        EssenceZoneType zoneType = zone.getZoneType();
        EssenceZone essenceZone = zone.getEssenceZone();
        Integer zoneY = zone.getZoneY();
        if (essenceZone == null || zoneY == null) return;

        spawnZoneParticles(world, chunk, zoneType, essenceZone, zoneY, zone.getZoneRadius());
    }

    private void spawnZoneParticles(ClientWorld world, Chunk chunk, EssenceZoneType zoneType, EssenceZone essenceZone, int zoneY, int zoneRadius) {
        float k = essenceZone.getValue() / 64.0f;

        int count = MathHelper.ceil(5 * k);
        ZoneParticleEffect effect = new ZoneParticleEffect(ServerParticleTypes.ZONE_PARTICLE, zoneType);
        ChunkPos chunkPos = chunk.getPos();
        BlockPos startPos = new BlockPos(chunkPos.getStartX(), zoneY - zoneRadius, chunkPos.getStartZ());
        BlockPos endPos = new BlockPos(chunkPos.getEndX(), zoneY + zoneRadius, chunkPos.getEndZ());

        BlockPos.iterate(startPos, endPos).forEach(particlePos -> {
            if (world.getRandom().nextDouble() > k * 1/300d) return;
            effect.spawnParticles(world, count, 0.5, particlePos.toCenterPos());
        });
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
        Text targetName = EtherAspectsProvider.getTargetName(world, hitResult);
        if (targetName == null) return null;

        return new ScaledLabelComponent(targetName, 1.5f).shadow(true)
                .positioning(Positioning.relative(50, 45));
    }

    private static List<Component> createAspectHud(ClientWorld world, HitResult hitResult) {
        List<Component> components = new ObjectArrayList<>();
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
