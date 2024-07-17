package ru.feytox.etherology.entity.redstoneBlob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import ru.feytox.etherology.magic.lens.RedstoneLensEffects;
import ru.feytox.etherology.particle.effects.misc.FeyParticleEffect;
import ru.feytox.etherology.registry.entity.EntityRegistry;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class RedstoneChargeEntity extends ProjectileEntity implements GeoEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.redstone_charge.idle");

    private int power;
    private int delay;
    private float speed;
    private int maxAge;
    private Vec3d flyDirection;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public RedstoneChargeEntity(World world, double x, double y, double z, Vec3d flyDirection, int power, int delay, float speed, int maxAge) {
        this(EntityRegistry.REDSTONE_CHARGE, world, flyDirection.multiply(speed));
        refreshPositionAndAngles(x, y, z, getYaw(), getPitch());
        refreshPosition();

        this.power = power;
        this.delay = delay;
        this.speed = speed;
        this.maxAge = maxAge;
    }

    public RedstoneChargeEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        this(entityType, world, Vec3d.ZERO);
    }

    private RedstoneChargeEntity(EntityType<? extends ProjectileEntity> entityType, World world, Vec3d flyDirection) {
        super(entityType, world);
        this.flyDirection = flyDirection;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
    }

    /**
     * @see ExplosiveProjectileEntity#tick()
     */
    @Override
    public void tick() {
        World world = getWorld();
        if (!world.isClient && !world.isChunkLoaded(getBlockPos())) {
            discard();
            return;
        }

        if (!world.isClient && age++ >= maxAge) {
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
        tickParticles();
    }

    private void tickParticles() {
        World world = getWorld();
        if (!world.isClient) return;

        FeyParticleEffect.spawnParticles(new DustParticleEffect(DustParticleEffect.RED, 1.0f), world, 2, 0.35, getPos().add(0, 0.25, 0));
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
        if (!(getWorld() instanceof ServerWorld world)) return;

        if (power <= 0 || delay <= 0) return;

        RedstoneLensEffects.getServerState(world).addUsage(world, blockHitResult.getBlockPos(), power, delay);
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
        nbt.putFloat("speed", speed);
        nbt.putDouble("flyX", flyDirection.x);
        nbt.putDouble("flyY", flyDirection.y);
        nbt.putDouble("flyZ", flyDirection.z);
        nbt.putInt("maxAge", maxAge);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        power = nbt.getInt("power");
        delay = nbt.getInt("delay");
        speed = nbt.getFloat("speed");
        maxAge = nbt.getInt("maxAge");
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


    // TODO: 11.06.2024 replace this with more efficient way to create and receive packet
    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket(EntityTrackerEntry entityTrackerEntry) {
        return new EntitySpawnS2CPacket(getId(), getUuid(), getX(), getY(), getZ(), getPitch(), getYaw(), getType(), getId(), flyDirection, 0.0);
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        flyDirection = new Vec3d(packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityZ());
    }
}
