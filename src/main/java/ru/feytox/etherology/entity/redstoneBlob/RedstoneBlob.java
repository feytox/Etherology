package ru.feytox.etherology.entity.redstoneBlob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import ru.feytox.etherology.magic.lense.RedstoneLensEffects;
import ru.feytox.etherology.registry.entity.EntityRegistry;

public class RedstoneBlob extends ProjectileEntity {

    private static final TrackedData<Integer> POWER = DataTracker.registerData(RedstoneBlob.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> DELAY = DataTracker.registerData(RedstoneBlob.class, TrackedDataHandlerRegistry.INTEGER);

    private final Vec3d flyDirection;

    public RedstoneBlob(World world, double x, double y, double z, Vec3d flyDirection, int power, int delay) {
        this(EntityRegistry.REDSTONE_BLOB, world, flyDirection);
        setPosition(x, y, z);

        dataTracker.set(POWER, power);
        dataTracker.set(DELAY, delay);
    }

    public RedstoneBlob(EntityType<? extends ProjectileEntity> entityType, World world) {
        this(entityType, world, Vec3d.ZERO);
    }

    private RedstoneBlob(EntityType<? extends ProjectileEntity> entityType, World world, Vec3d flyDirection) {
        super(entityType, world);
        this.flyDirection = flyDirection;
    }

    @Override
    public void tick() {
        super.tick();
        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
        if (hitResult.getType() != HitResult.Type.MISS) this.onCollision(hitResult);

        checkBlockCollision();
        Vec3d velocity = this.getVelocity();
        prevX = getX();
        prevY = getY();
        prevZ = getZ();
        double newX = this.getX() + velocity.x;
        double newY = this.getY() + velocity.y;
        double newZ = this.getZ() + velocity.z;

        double speedModifier = 0.19d;
        if (isTouchingWater()) speedModifier *= 0.8f;

        setVelocity(velocity.add(flyDirection).multiply(speedModifier));
        setPosition(newX, newY, newZ);

        if (!world.isClient || !isTouchingWater()) return;
        for (int i = 0; i < 4; i++) {
            world.addParticle(ParticleTypes.BUBBLE, newX - velocity.x * 0.25, newY - velocity.y * 0.25, newZ - velocity.z * 0.25, velocity.x, velocity.y, velocity.z);
        }
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

        int power = getPower();
        int delay = getDelay();
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
        nbt.putInt("power", getPower());
        nbt.putInt("delay", getDelay());
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        setPower(nbt.getInt("power"));
        setDelay(nbt.getInt("delay"));
    }

    @Override
    protected void initDataTracker() {
        dataTracker.startTracking(POWER, -1);
        dataTracker.startTracking(DELAY, -1);
    }

    public int getDelay() {
        return dataTracker.get(DELAY);
    }

    public int getPower() {
        return dataTracker.get(POWER);
    }

    public void setPower(int power) {
        dataTracker.set(POWER, power);
    }

    public void setDelay(int delay) {
        dataTracker.set(DELAY, delay);
    }

}
