package name.uwu.feytox.etherology.blocks.etherealStorage;

import name.uwu.feytox.etherology.magic.ether.EtherStorage;
import name.uwu.feytox.etherology.util.feyapi.TickableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import static name.uwu.feytox.etherology.BlocksRegistry.ETHEREAL_STORAGE_BLOCK_ENTITY;

public class EtherealStorageBlockEntity extends TickableBlockEntity implements EtherStorage {
    // TODO: 15/03/2023 change after test
    private float storedEther = 64;

    public EtherealStorageBlockEntity(BlockPos pos, BlockState state) {
        super(ETHEREAL_STORAGE_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void serverTick(ServerWorld world, BlockPos blockPos, BlockState state) {
        transferTick(world);
    }

    @Override
    public float getMaxEther() {
        return 64;
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
        return !side.equals(Direction.DOWN);
    }

    @Override
    public Direction getOutputSide() {
        return Direction.DOWN;
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
    protected void writeNbt(NbtCompound nbt) {
        nbt.putFloat("stored_ether", storedEther);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        storedEther = nbt.getFloat("stored_ether");
    }

    // TODO: 15/03/2023 delete after test
    public void onUse(ServerWorld world) {
        world.getPlayers().forEach(player -> player.sendMessage(Text.of("Stored Ether: " + storedEther)));
    }
}
