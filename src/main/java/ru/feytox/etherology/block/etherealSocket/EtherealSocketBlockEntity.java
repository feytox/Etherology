package ru.feytox.etherology.block.etherealSocket;

import io.wispforest.owo.util.ImplementedInventory;
import lombok.Getter;
import lombok.val;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.item.glints.AbstractGlintItem;
import ru.feytox.etherology.magic.ether.EtherGlint;
import ru.feytox.etherology.magic.ether.EtherStorage;
import ru.feytox.etherology.particle.effects.MovingParticleEffect;
import ru.feytox.etherology.registry.particle.ServerParticleTypes;
import ru.feytox.etherology.util.deprecated.EVec3d;
import ru.feytox.etherology.util.feyapi.TickableBlockEntity;

import java.util.List;

import static net.minecraft.block.FacingBlock.FACING;
import static ru.feytox.etherology.block.etherealSocket.EtherealSocketBlock.WITH_GLINT;
import static ru.feytox.etherology.registry.block.EBlocks.ETHEREAL_SOCKET_BLOCK_ENTITY;

public class EtherealSocketBlockEntity extends TickableBlockEntity
        implements EtherStorage, ImplementedInventory, SidedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private Boolean wasWithGlint = null;
    // TODO: 09.08.2023 deprecate
    private boolean isUpdated = false;
    @Getter
    private float cachedPercent = 0;

    public EtherealSocketBlockEntity(BlockPos pos, BlockState state) {
        super(ETHEREAL_SOCKET_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void serverTick(ServerWorld world, BlockPos blockPos, BlockState state) {
        transferTick(world);

        if (isUpdated) {
            world.getChunkManager().markForUpdate(pos);
            isUpdated = false;
        }
    }

    @Override
    public void clientTick(ClientWorld world, BlockPos blockPos, BlockState state) {
        cachePercent();
        tickGlintParticles(world, state);
    }

    private void tickGlintParticles(ClientWorld world, BlockState state) {
        Boolean withGlint = state.getOrEmpty(WITH_GLINT).orElse(null);
        if (withGlint == null) return;

        if (wasWithGlint != null && !wasWithGlint && withGlint) {
            spawnGlintParticles(world, state.get(FACING));
        }
        wasWithGlint = withGlint;
    }

    private void spawnGlintParticles(ClientWorld world, Direction facing) {
        Random random = world.getRandom();
        Vec3d pos = Vec3d.of(this.pos);
        Vec3d centerPos = this.pos.toCenterPos().subtract(0, 0.3, 0);

        double dxz = 6/16d;
        double dy = 0.25;
        // down direction
        Vec3d start = new Vec3d(dxz, dy, dxz);
        Vec3d end = new Vec3d(0.25 + dxz, dy, 0.25 + dxz);
        switch (facing) {
            case UP -> {
                start = new Vec3d(dxz, 0.5 + dy, dxz);
                end = new Vec3d(0.25 + dxz, 0.5 + dy, 0.25 + dxz);
            }
            case NORTH -> {
                start = new Vec3d(dxz, dxz, dy);
                end = new Vec3d(0.25 + dxz, 0.25 + dxz, dy);
            }
            case SOUTH -> {
                start = new Vec3d(dxz, dxz, 0.5 + dy);
                end = new Vec3d(0.25 + dxz, 0.25 + dxz, 0.5 + dy);
            }
            case WEST -> {
                start = new Vec3d(dy, dxz, dxz);
                end = new Vec3d(dy, 0.25 + dxz, 0.25 + dxz);
            }
            case EAST -> {
                start = new Vec3d(0.5 + dy, dxz, dxz);
                end = new Vec3d(0.5 + dy, 0.25 + dxz, 0.25 + dxz);
            }
        }

        List<Vec3d> particlePoses = EVec3d.aroundSquareOf(pos.add(start), pos.add(end), 0.05d);

        int count = MathHelper.floor(25f * cachedPercent);
        for (int i = 0; i < count; i++) {
            int j = random.nextInt(particlePoses.size()-1);
            Vec3d particlePos = particlePoses.get(j);
            Vec3d path = particlePos
                    .subtract(centerPos)
                    .multiply(random.nextDouble() * 1);
            path = particlePos.add(path);
            val effect = new MovingParticleEffect(ServerParticleTypes.GLINT, path);
            effect.spawnParticles(world, 2, 0.01, particlePos);
        }
    }

    public void cachePercent() {
        // TODO: 24/03/2023 как-то изменить, чтобы не моргало
        float storedEther = getStoredEther();
        float maxEther = getMaxEther();
        cachedPercent = maxEther == 0 ? cachedPercent : storedEther / maxEther;
    }

    public ActionResult onUse(ServerWorld world, PlayerEntity player, BlockState state) {
        ItemStack glintStack = getStack(0);
        ItemStack useStack = player.getMainHandStack();

        if (glintStack.isEmpty() && useStack.getItem() instanceof AbstractGlintItem) {
            // вставка глинта
            ItemStack takingStack = useStack.copy();
            useStack = glintStack;
            glintStack = takingStack;
            player.setStackInHand(Hand.MAIN_HAND, useStack);
            setStack(0, glintStack);

            world.setBlockState(pos, state.with(WITH_GLINT, true));
            isUpdated = true;
            world.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.BLOCKS, 0.6f, 0.95f);
            syncData(world);
            return ActionResult.CONSUME;

        } else if (glintStack.getItem() instanceof AbstractGlintItem && useStack.isEmpty()) {
            // забирание глинта
            ItemStack takingStack = glintStack.copy();
            glintStack.setCount(0);
            player.setStackInHand(Hand.MAIN_HAND, takingStack);

            world.setBlockState(pos, state.with(WITH_GLINT, false));
            syncData(world);
            isUpdated = true;
            world.playSound(null, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.BLOCKS, 0.8f, 0.95f);
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    public float getMaxEther() {
        if (inventory.isEmpty()) return 0;

        ItemStack glintStack = getStack(0);
        if (!(glintStack.getItem() instanceof AbstractGlintItem glint)) return 0;

        return glint.getMaxEther();
    }

    @Override
    public float getStoredEther() {
        if (inventory.isEmpty()) return 0;

        ItemStack glintStack = getStack(0);
        if (!(glintStack.getItem() instanceof AbstractGlintItem)) return 0;

        return new EtherGlint(glintStack).getStoredEther();
    }

    @Override
    public float getTransferSize() {
        return 1;
    }

    @Override
    public float increment(float value) {
        if (inventory.isEmpty()) return value;

        ItemStack glintStack = getStack(0);
        if (!(glintStack.getItem() instanceof AbstractGlintItem)) return value;

        float exceed = new EtherGlint(glintStack).increment(value);
        isUpdated = true;
        return exceed;
    }

    @Override
    public float decrement(float value) {
        if (inventory.isEmpty()) return 0;

        ItemStack glintStack = getStack(0);
        if (!(glintStack.getItem() instanceof AbstractGlintItem)) return 0;

        float decremented = new EtherGlint(glintStack).decrement(value);
        isUpdated = true;
        return decremented;
    }

    @Override
    public boolean isInputSide(Direction side) {
        return false;
    }

    @Nullable
    @Override
    public Direction getOutputSide() {
        // FIXME: 22/03/2023 исправить баг с определением оутпут сайда
        Direction output = getCachedState().get(FacingBlock.FACING);
        return !output.equals(Direction.DOWN) && !output.equals(Direction.UP) ? output.getOpposite() : output;
    }

    @Override
    public BlockPos getStoragePos() {
        return pos;
    }

    @Override
    public void transferTick(ServerWorld world) {
        if (world.getTime() % 20 == 0) transfer(world);
    }

    @Override
    public boolean isActivated() {
        return false;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    /**
     * @deprecated use increment/decrement
     */
    @Deprecated
    @Override
    public void setStoredEther(float value) {}

    @Override
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, inventory);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        inventory.clear();
        Inventories.readNbt(nbt, inventory);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[0];
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }
}
