package ru.feytox.etherology.block.furniture;

import io.wispforest.owo.util.ImplementedInventory;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.enums.FurnitureType;
import ru.feytox.etherology.util.feyapi.TickableBlockEntity;

import static ru.feytox.etherology.block.furniture.AbstractFurSlabBlock.BOTTOM_TYPE;
import static ru.feytox.etherology.block.furniture.AbstractFurSlabBlock.TOP_TYPE;
import static ru.feytox.etherology.registry.block.EBlocks.FURNITURE_BLOCK_ENTITY;

public class FurSlabBlockEntity extends TickableBlockEntity {
    private FurnitureType bottomType = FurnitureType.EMPTY;
    private FurnitureType topType = FurnitureType.EMPTY;
    @Nullable
    private FurnitureData bottomData = null;
    @Nullable
    private FurnitureData topData = null;

    public FurSlabBlockEntity(BlockPos pos, BlockState state) {
        super(FURNITURE_BLOCK_ENTITY, pos, state);
    }

    public FurSlabBlockEntity(BlockPos pos, BlockState state, FurnitureType bottomType, FurnitureType topType) {
        this(pos, state);
        this.bottomType = bottomType;
        this.topType = topType;
        this.bottomData = bottomType.createDataInstance(true);
        this.topData = topType.createDataInstance(false);
    }

    @Nullable
    public ImplementedInventory getInventory(boolean isBottom) {
        FurnitureData data = isBottom ? bottomData : topData;
        return data instanceof ImplementedInventory inv ? inv : null;
    }

    public void onUpdateState(BlockState newState) {
        FurnitureType bottomType = newState.get(BOTTOM_TYPE);
        FurnitureType topType = newState.get(TOP_TYPE);

        if (!bottomType.equals(this.bottomType)) {
            this.bottomType = bottomType;
            bottomData = bottomType.createDataInstance(true);
        }

        if (!topType.equals(this.topType)) {
            this.topType = topType;
            topData = topType.createDataInstance(false);
        }

        markDirty();
    }

    @Nullable
    public FurnitureData getBottomData() {
        return bottomData;
    }

    @Nullable
    public FurnitureData getTopData() {
        return topData;
    }

    public void bottomUse(World world, BlockState state, PlayerEntity player, Vec2f hitPos, Hand hand) {
        onUse(bottomData, world, state, player, hitPos, hand);
    }

    public void topUse(World world, BlockState state, PlayerEntity player, Vec2f hitPos, Hand hand) {
        onUse(topData, world, state, player, hitPos, hand);
    }

    private void onUse(FurnitureData furData, World world, BlockState state, PlayerEntity player, Vec2f hitPos, Hand hand) {
        if (furData == null) return;

        if (world.isClient) {
            furData.clientUse((ClientWorld) world, state, pos, player, hitPos, hand);
        } else {
            furData.serverUse((ServerWorld) world, state, pos, player, hitPos, hand);
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        NbtCompound bottomCompound = nbt.getCompound("bottom");
        NbtCompound bottomDataCompound = bottomCompound.getCompound("data");
        NbtCompound topCompound = nbt.getCompound("top");
        NbtCompound topDataCompound = topCompound.getCompound("data");

        bottomType = FurnitureType.readFromNbt(bottomCompound);
        bottomData = bottomType.createDataInstance(true);
        if (bottomData != null && !bottomType.isEmpty() && !bottomDataCompound.isEmpty()) {
            bottomData.readNbt(bottomDataCompound);
        } else bottomData = null;

        topType = FurnitureType.readFromNbt(topCompound);
        topData = topType.createDataInstance(false);
        if (topData != null && !topType.isEmpty() && !topDataCompound.isEmpty()) {
            topData.readNbt(topDataCompound);
        } else topData = null;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        NbtCompound bottomCompound = new NbtCompound();
        NbtCompound bottomDataCompound = new NbtCompound();
        NbtCompound topCompound = new NbtCompound();
        NbtCompound topDataCompound = new NbtCompound();

        bottomType.writeNbt(bottomCompound);
        if (bottomData != null) bottomData.writeNbt(bottomDataCompound);
        bottomCompound.put("data", bottomDataCompound);
        nbt.put("bottom", bottomCompound);

        topType.writeNbt(topCompound);
        if (topData != null) topData.writeNbt(topDataCompound);
        topCompound.put("data", topDataCompound);
        nbt.put("top", topCompound);

        super.writeNbt(nbt);
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
}
