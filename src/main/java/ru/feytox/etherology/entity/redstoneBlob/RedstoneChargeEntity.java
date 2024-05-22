package ru.feytox.etherology.entity.redstoneBlob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import ru.feytox.etherology.magic.lens.RedstoneLensEffects;
import ru.feytox.etherology.registry.entity.EntityRegistry;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class RedstoneChargeEntity extends ProjectileEntity implements GeoEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.redstone_charge.idle");

    private int power;
    private int delay;
    private Vec3d flyDirection;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public RedstoneChargeEntity(World world, double x, double y, double z, Vec3d flyDirection, int power, int delay) {
        this(EntityRegistry.REDSTONE_CHARGE, world, flyDirection);
        refreshPositionAndAngles(x, y, z, getYaw(), getPitch());
        refreshPosition();

        this.power = power;
        this.delay = delay;
    }

    public RedstoneChargeEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        this(entityType, world, Vec3d.ZERO);
    }

    private RedstoneChargeEntity(EntityType<? extends ProjectileEntity> entityType, World world, Vec3d flyDirection) {
        super(entityType, world);
        this.flyDirection = flyDirection;
    }

    @Override
    protected void initDataTracker() {}

    /**
     * @see ExplosiveProjectileEntity#tick()
     */
    @Override
    public void tick() {
        if (!world.isClient && !world.isChunkLoaded(getBlockPos())) {
            discard();
            return;
        }

        super.tick();
        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
        if (hitResult.getType() != HitResult.Type.MISS) onCollision(hitResult);

        checkBlockCollision();
        Vec3d velocity = getVelocity();
        double newX = getX() + velocity.x;
        double newY = getY() + velocity.y;
        double newZ = getZ() + velocity.z;
        ProjectileUtil.setRotationFromVelocity(this, 0.2F);

        double speedModifier = 0.19d;
        if (isTouchingWater()) speedModifier *= 0.8f;

        setVelocity(velocity.add(flyDirection).multiply(speedModifier));
        setPosition(newX, newY, newZ);
    }

    @Override
    public boolean shouldRender(double distance) {
        return super.shouldRender(distance);
    }

    @Override
    protected boolean canHit(Entity entity) {
        return false;
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if (!(world instanceof ServerWorld serverWorld)) return;

        if (power <= 0 || delay <= 0) return;

        RedstoneLensEffects.getServerState(serverWorld).addUsage(serverWorld, blockHitResult.getBlockPos(), power, delay);
        discard();
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (hitResult.getType().equals(HitResult.Type.ENTITY)) return;
        super.onCollision(hitResult);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("power", power);
        nbt.putInt("delay", delay);
        nbt.putDouble("flyX", flyDirection.x);
        nbt.putDouble("flyY", flyDirection.y);
        nbt.putDouble("flyZ", flyDirection.z);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        power = nbt.getInt("power");
        delay = nbt.getInt("delay");
        flyDirection = new Vec3d(nbt.getDouble("flyX"), nbt.getDouble("flyY"), nbt.getDouble("flyZ"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this, "Flying", 0, event -> event.setAndContinue(IDLE)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(getId(), getUuid(), getX(), getY(), getZ(), getPitch(), getYaw(), getType(), getId(), flyDirection, 0.0);
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        flyDirection = new Vec3d(packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityZ());
    }
}
