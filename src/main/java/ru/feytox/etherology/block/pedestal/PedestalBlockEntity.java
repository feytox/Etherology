package ru.feytox.etherology.block.pedestal;

import io.wispforest.owo.util.ImplementedInventory;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.data.item_aspects.ItemAspectsLoader;
import ru.feytox.etherology.enums.LightParticleType;
import ru.feytox.etherology.magic.aspects.EtherAspectsContainer;
import ru.feytox.etherology.magic.aspects.EtherAspectsProvider;
import ru.feytox.etherology.particle.ItemMovingParticle;
import ru.feytox.etherology.particle.OldMovingParticle;
import ru.feytox.etherology.particle.types.LightParticleEffect;
import ru.feytox.etherology.registry.particle.ServerParticleTypes;
import ru.feytox.etherology.util.nbt.NbtPos;

import static ru.feytox.etherology.Etherology.SPARK;
import static ru.feytox.etherology.registry.block.EBlocks.PEDESTAL_BLOCK_ENTITY;

public class PedestalBlockEntity extends BlockEntity implements ImplementedInventory, EtherAspectsProvider {
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
    @Getter
    @Setter
    private Float cachedUniqueOffset = null;
    private int itemConsumingTicks = 0;
    private NbtPos centerCoord = null;

    public PedestalBlockEntity(BlockPos pos, BlockState state) {
        super(PEDESTAL_BLOCK_ENTITY, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    public void interact(ServerWorld world, PlayerEntity player, Hand hand) {
        ItemStack handStack = player.getStackInHand(hand);
        if (this.isEmpty() && !handStack.isEmpty()) {
            ItemStack copyStack = handStack.copy();
            copyStack.setCount(1);
            this.setStack(0, copyStack);
            handStack.decrement(1);
        } else if (!this.isEmpty() && (handStack.isEmpty() || handStack.isItemEqual(this.items.get(0)))) {
            ItemStack pedestalStack = this.items.get(0);
            this.clear();
            if (handStack.isItemEqual(pedestalStack)) {
                handStack.increment(1);
                syncData(world);
                return;
            }
            player.setStackInHand(hand, pedestalStack);
        }
        syncData(world);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, PedestalBlockEntity blockEntity) {
        if (!world.isClient) {
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
        NbtPos center = getCenterCoord();

        if (itemConsumingTicks % 5 != 0) return;

        for (int i = 0; i < 5; i++) {
            double x = pos.getX() + 0.5 + random.nextDouble() * 0.2f * random.nextBetween(-1, 1);
            double y = pos.getY() + 1.5 + random.nextDouble() * 0.2f * random.nextBetween(-1, 1);
            double z = pos.getZ() + 0.5 + random.nextDouble() * 0.2f * random.nextBetween(-1, 1);
            ItemMovingParticle particle = new ItemMovingParticle(world, x, y, z, center.x, center.y, center.z,
                    getItems().get(0).copy());
            MinecraftClient.getInstance().particleManager.addParticle(particle);
        }

        LightParticleEffect sparkEffect = new LightParticleEffect(ServerParticleTypes.LIGHT, LightParticleType.SPARK, center.asVector());
        sparkEffect.spawnParticles(world, random.nextBetween(10, 25), 0.35, pos.toCenterPos().add(0, 1, 0));

        OldMovingParticle.spawnParticles(world, SPARK, random.nextBetween(1, 5), 0.35,
                pos.getX()+0.5, pos.getY()+1.5, pos.getZ()+0.5, center.x, center.y, center.z, random);
    }

    public void consumeItem(ServerWorld world, int ticks, Vec3d centerPos) {
        itemConsumingTicks = ticks;
        centerCoord = new NbtPos("centerCoord", centerPos.x, centerPos.y+0.3, centerPos.z);
        syncData(world);
    }

    public boolean isConsuming() {
        return itemConsumingTicks > 0;
    }

    public NbtPos getCenterCoord() {
        return centerCoord;
    }

    public void tickConsuming(ServerWorld world) {
        if (itemConsumingTicks == 0) return;

        itemConsumingTicks--;
        if ((itemConsumingTicks <= 0 || this.world == null) && itemConsumingTicks == 0) {
            centerCoord = null;
            clear();
        }

        syncData(world);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, items);

        nbt.putInt("itemConsumingTicks", itemConsumingTicks);
        if (centerCoord != null) centerCoord.writeNbt(nbt);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        itemConsumingTicks = nbt.getInt("itemConsumingTicks");
        items.clear();
        Inventories.readNbt(nbt, items);

        centerCoord = itemConsumingTicks != 0 ? NbtPos.readNbt("centerCoord", nbt) : null;
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

    @Override
    public @Nullable EtherAspectsContainer getStoredAspects() {
        return ItemAspectsLoader.getAspectsOf(items.get(0).getItem()).orElse(null);
    }

    @Override
    public Text getAspectsSourceName() {
        String pedestalText = Text.translatable(getCachedState().getBlock().getTranslationKey()).getString();
        String itemText = items.get(0).getName().getString();
        return Text.of(pedestalText + " (" + itemText + ")");
    }

    private void syncData(ServerWorld world) {
        markDirty();
        world.getChunkManager().markForUpdate(pos);
    }
}
