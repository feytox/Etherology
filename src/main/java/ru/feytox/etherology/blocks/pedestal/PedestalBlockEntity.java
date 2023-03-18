package ru.feytox.etherology.blocks.pedestal;

import io.wispforest.owo.util.ImplementedInventory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.mixin.ItemEntityAccessor;
import ru.feytox.etherology.particle.ItemMovingParticle;
import ru.feytox.etherology.particle.MovingParticle;
import ru.feytox.etherology.util.nbt.NbtCoord;

import java.util.UUID;

import static ru.feytox.etherology.BlocksRegistry.PEDESTAL_BLOCK_ENTITY;
import static ru.feytox.etherology.Etherology.LIGHT_SPARK;
import static ru.feytox.etherology.Etherology.SPARK;

public class PedestalBlockEntity extends BlockEntity implements ImplementedInventory {
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private UUID displayedItemUUID = null;
    private int itemConsumingTicks = 0;
    private NbtCoord centerCoord = null;

    public PedestalBlockEntity(BlockPos pos, BlockState state) {
        super(PEDESTAL_BLOCK_ENTITY, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    public void interact(PlayerEntity player, Hand hand) {
        ItemStack handStack = player.getStackInHand(hand);
        if (this.isEmpty() && !handStack.isEmpty()) {
            ItemStack copyStack = handStack.copy();
            copyStack.setCount(1);
            this.setStack(0, copyStack);
            handStack.decrement(1);
            markDirty();
        } else if (!this.isEmpty() && (handStack.isEmpty() || handStack.isItemEqual(this.items.get(0)))) {
            ItemStack pedestalStack = this.items.get(0);
            this.clear();
            if (handStack.isItemEqual(pedestalStack)) {
                handStack.increment(1);
                return;
            }
            player.setStackInHand(hand, pedestalStack);
            markDirty();
        }
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, PedestalBlockEntity blockEntity) {
        if (!world.isClient) {
            blockEntity.tickItem((ServerWorld) world);
            blockEntity.tickConsuming((ServerWorld) world);
        }
    }

    public static void clientTick(World world, BlockPos pos, BlockState state, PedestalBlockEntity blockEntity) {
        if (world.isClient) {
            blockEntity.tickConsumingParticles((ClientWorld) world);
        }
    }

    public void tickConsumingParticles(ClientWorld world) {
        if (!isConsuming()) return;

        Random random = Random.create();
        NbtCoord center = getCenterCoord();

        if (itemConsumingTicks % 5 != 0) return;

        for (int i = 0; i < 5; i++) {
            double x = pos.getX() + 0.5 + random.nextDouble() * 0.2f * random.nextBetween(-1, 1);
            double y = pos.getY() + 1.5 + random.nextDouble() * 0.2f * random.nextBetween(-1, 1);
            double z = pos.getZ() + 0.5 + random.nextDouble() * 0.2f * random.nextBetween(-1, 1);
            ItemMovingParticle particle = new ItemMovingParticle(world, x, y, z, center.x, center.y, center.z,
                    getItems().get(0).copy());
            MinecraftClient.getInstance().particleManager.addParticle(particle);
        }

        MovingParticle.spawnParticles(world, LIGHT_SPARK, random.nextBetween(10, 25), 0.35,
                pos.getX()+0.5, pos.getY()+1.5, pos.getZ()+0.5, center.x, center.y, center.z, random);
        MovingParticle.spawnParticles(world, SPARK, random.nextBetween(1, 5), 0.35,
                pos.getX()+0.5, pos.getY()+1.5, pos.getZ()+0.5, center.x, center.y, center.z, random);
    }

    public void consumeItem(int ticks, Vec3d centerPos) {
        itemConsumingTicks = ticks;
        centerCoord = new NbtCoord("centerCoord", centerPos.x, centerPos.y+0.3, centerPos.z);
        markDirty();
    }

    public boolean isConsuming() {
        return itemConsumingTicks > 0;
    }

    @Override
    public void markDirty() {
        super.markDirty();

        if (world != null) world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
    }

    public NbtCoord getCenterCoord() {
        return centerCoord;
    }

    public void tickConsuming(ServerWorld world) {
        if (itemConsumingTicks == 0) return;

        itemConsumingTicks--;
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 1.0;
        double z = pos.getZ() + 0.5;
        if (itemConsumingTicks > 0 && this.world != null) {
//            world.spawnParticles(Etherology.SPARK, x, y, z, 1,
//                    centerCoord.x-x, centerCoord.y-y, centerCoord.z-z, 1);
        } else if(itemConsumingTicks == 0) {
//            world.spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, items.get(0)), x, y, z,
//                    10, 0, 2, 0, 0.01);

            centerCoord = null;
            clear();
        }

        markDirty();
    }


    public void tickItem(ServerWorld world) {
        ItemEntity displayedItem = getDisplayedItemEntity(world);

        if (displayedItem != null && displayedItem.isAlive()) {
            displayedItem.setVelocity(0.0, 0.0, 0.0);
            if (displayedItem.getX() != this.pos.getX()+0.5 ||
            displayedItem.getY() != this.pos.getY()+1.0 ||
            displayedItem.getZ() != this.pos.getZ()+0.5) {
                displayedItem.setPos(this.pos.getX()+0.5, this.pos.getY()+1.0, this.pos.getZ()+0.5);
            }
        }

        if (!this.isEmpty() && (displayedItem == null || !displayedItem.isAlive())) {
            ItemStack displayedItemStack = items.get(0).copy();
            displayedItemStack.setCount(1);
            displayedItem = new ItemEntity(world, this.pos.getX()+0.5, this.pos.getY()+1.0, this.pos.getZ()+0.5,
                    displayedItemStack, 0.0, 0.0, 0.0);
            ((ItemEntityAccessor) displayedItem).setItemAge(-32768);
            ((ItemEntityAccessor) displayedItem).setPickupDelay(32767);
            displayedItem.setInvulnerable(true);
            world.spawnEntity(displayedItem);
            displayedItemUUID = displayedItem.getUuid();
        } else if (this.isEmpty() && displayedItem != null) {
            if (displayedItem.isAlive()) displayedItem.kill();
            displayedItemUUID = null;
        }
    }

    public void onBreak(World world) {
        ItemEntity displayedItem = getDisplayedItemEntity((ServerWorld) world);
        if (displayedItem != null && displayedItem.isAlive()) displayedItem.kill();
    }

    @Nullable
    public ItemEntity getDisplayedItemEntity(ServerWorld world) {
        Entity searchedEntity = world.getEntity(displayedItemUUID);
        return searchedEntity instanceof ItemEntity ? (ItemEntity) searchedEntity : null;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, items);

        nbt.putInt("itemConsumingTicks", itemConsumingTicks);
        displayedItemUUID = displayedItemUUID == null ? UUID.randomUUID() : displayedItemUUID;
        nbt.putUuid("displayedItemUUID", displayedItemUUID);

        if (centerCoord != null) centerCoord.writeNbt(nbt);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        displayedItemUUID = nbt.getUuid("displayedItemUUID");
        itemConsumingTicks = nbt.getInt("itemConsumingTicks");
        Inventories.readNbt(nbt, items);

        centerCoord = itemConsumingTicks != 0 ? NbtCoord.readNbt("centerCoord", nbt) : null;
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}
