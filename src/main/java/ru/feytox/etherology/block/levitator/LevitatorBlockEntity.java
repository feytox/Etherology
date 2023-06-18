package ru.feytox.etherology.block.levitator;

import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.magic.ether.EtherStorage;
import ru.feytox.etherology.util.feyapi.TickableBlockEntity;

import java.util.List;

import static net.minecraft.state.property.Properties.FACING;
import static net.minecraft.state.property.Properties.POWER;
import static ru.feytox.etherology.block.levitator.LevitatorBlock.PUSHING;
import static ru.feytox.etherology.registry.block.EBlocks.LEVITATOR_BLOCK_ENTITY;

public class LevitatorBlockEntity extends TickableBlockEntity implements EtherStorage {
    private static final int FUEL_TIME = 600;
    private int fuel = 0;
    private float storedEther = 0;

    public LevitatorBlockEntity(BlockPos pos, BlockState state) {
        super(LEVITATOR_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void serverTick(ServerWorld world, BlockPos blockPos, BlockState state) {
        if (tickFuel(world, blockPos, state)) {
            tickLevitation(world, blockPos, state);
        }
        markDirty();
    }

    @Override
    public void clientTick(ClientWorld world, BlockPos blockPos, BlockState state) {
        // TODO: 18.06.2023 tickParticles (or do it using another ticker)
        tickLevitation(world, pos, state);
    }

    private boolean tickFuel(ServerWorld world, BlockPos blockPos, BlockState state) {
        if (fuel-- > 0) return true;

        boolean wasFueled = state.get(LevitatorBlock.WITH_FUEL);
        if (storedEther > 0) {
            decrement(1);
            fuel = FUEL_TIME;
            if (!wasFueled) world.setBlockState(blockPos, state.with(LevitatorBlock.WITH_FUEL, true));
            return true;
        }

        world.setBlockState(blockPos, state.with(LevitatorBlock.WITH_FUEL, false));
        return false;
    }

    private void tickLevitation(World world, BlockPos pos, BlockState state) {
        boolean hasFuel = state.get(LevitatorBlock.WITH_FUEL);
        if (!hasFuel) return;
        int power = state.get(POWER);
        if (power <= 0) return;

        Vec3i directionVec = state.get(FACING).getVector().multiply(-1);
        int distance = MathHelper.ceil(power / 2f);
        BlockBox levitationBlockBox = BlockBox.create(pos.add(directionVec), pos.add(directionVec.multiply(distance)));
        Box levitationBox = Box.from(levitationBlockBox);

        List<Entity> entities = world.getNonSpectatingEntities(Entity.class, levitationBox);
        if (entities.isEmpty()) return;

        boolean isPushing = state.get(PUSHING);
        directionVec = isPushing ? directionVec : directionVec.multiply(-1);
        Vec3d levitationVec = Vec3d.of(directionVec).multiply(0.1);
        for (Entity entity : entities) {
            Vec3d oldVelocity = entity.getVelocity();
            Vec3d newVelocity = oldVelocity.add(levitationVec);
            entity.setVelocity(newVelocity);

            // TODO: 18.06.2023 clamping

            if (entity instanceof ServerPlayerEntity serverPlayerTarget && entity.velocityModified) {
                serverPlayerTarget.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(entity));
                entity.velocityModified = false;
                entity.setVelocity(oldVelocity);
            }
        }
    }

    @Override
    public float getMaxEther() {
        return 1;
    }

    @Override
    public float getStoredEther() {
        return storedEther;
    }

    @Override
    public float getTransferSize() {
        return 1;
    }

    @Override
    public void setStoredEther(float value) {
        storedEther = value;
    }

    @Override
    public boolean isInputSide(Direction side) {
        return true;
    }

    @Nullable
    @Override
    public Direction getOutputSide() {
        return null;
    }

    @Override
    public BlockPos getStoragePos() {
        return pos;
    }

    @Override
    public void transferTick(ServerWorld world) {}

    @Override
    public boolean isActivated() {
        return false;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putInt("fuel", fuel);
        nbt.putFloat("stored_ether", storedEther);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        fuel = nbt.getInt("fuel");
        storedEther = nbt.getFloat("stored_ether");
    }
}
