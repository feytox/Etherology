package ru.feytox.etherology.block.jewelryTable;

import io.wispforest.owo.util.ImplementedInventory;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.magic.ether.EtherStorage;
import ru.feytox.etherology.particle.effects.ElectricityParticleEffect;
import ru.feytox.etherology.particle.effects.SparkParticleEffect;
import ru.feytox.etherology.particle.subtypes.ElectricitySubtype;
import ru.feytox.etherology.particle.subtypes.SparkSubtype;
import ru.feytox.etherology.recipes.jewelry.JewelryRecipe;
import ru.feytox.etherology.registry.particle.EtherParticleTypes;
import ru.feytox.etherology.util.misc.TickableBlockEntity;
import ru.feytox.etherology.util.misc.UniqueProvider;

import static ru.feytox.etherology.registry.block.EBlocks.JEWELRY_TABLE_BLOCK_ENTITY;

public class JewelryBlockEntity extends TickableBlockEntity
        implements EtherStorage, ImplementedInventory, UniqueProvider, NamedScreenHandlerFactory, SidedInventory {

    private final JewelryTableInventory inventory;
    private float storedEther = 0;
    @Getter
    @Setter
    private Float cachedUniqueOffset = null;

    public JewelryBlockEntity(BlockPos pos, BlockState state) {
        super(JEWELRY_TABLE_BLOCK_ENTITY, pos, state);
        inventory = new JewelryTableInventory(this);
    }

    @Override
    public void serverTick(ServerWorld world, BlockPos blockPos, BlockState state) {
        int tickRate = 10;
        if (inventory.isEmpty() || !inventory.hasRecipe()) {
            inventory.resetRecipe();
            tickRate = 7;
        }
        if (world.getTime() % tickRate == 0) decrement(1);
        if (!inventory.hasRecipe() || world.getTime() % 5 != 0) return;

        inventory.updateRecipe(world);
        JewelryRecipe recipe = inventory.getRecipe(world);
        if (recipe == null) return;
        if (storedEther < recipe.getEther()) return;

        storedEther = 0.0f;
        inventory.tryCraft(world);
        decrement(recipe.getEther());
        inventory.resetRecipe();

        Vec3d particlePos = blockPos.toCenterPos().add(0, 0.75d, 0);
        val effect = new SparkParticleEffect(EtherParticleTypes.SPARK, new Vec3d(0, 2.0d, 0), SparkSubtype.JEWELRY);
        effect.spawnParticles(world, 6, 0.25d, particlePos);
    }

    @Override
    public void clientTick(ClientWorld world, BlockPos blockPos, BlockState state) {
        if (inventory.isEmpty()) return;
        if (world.getTime() % 4 != 0) return;

        val effect = ElectricityParticleEffect.of(world.getRandom(), ElectricitySubtype.JEWELRY);
        effect.spawnParticles(world, 2, 0.2d, blockPos.toCenterPos().add(0, 0.75d, 0));
    }

    @Override
    public boolean isCrossEvaporate(Direction fromSide) {
        return inventory.isEmpty();
    }

    @Override
    public float getMaxEther() {
        return inventory.hasRecipe() ? Integer.MAX_VALUE : 4.0f;
    }

    @Override
    public float getStoredEther() {
        return storedEther;
    }

    @Override
    public float getTransferSize() {
        return 1.0f;
    }

    @Override
    public void setStoredEther(float value) {
        storedEther = value;
    }

    @Override
    public boolean isInputSide(Direction side) {
        return side.equals(Direction.DOWN);
    }

    @Override
    public @Nullable Direction getOutputSide() {
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
    public DefaultedList<ItemStack> getItems() {
        return inventory.getItems();
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        inventory.writeNbt(nbt);
        nbt.putFloat("ether", storedEther);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        inventory.readNbt(nbt);
        storedEther = nbt.getFloat("ether");
    }

    public void trySyncData() {
        markDirty();
        if (world instanceof ServerWorld serverWorld) syncData(serverWorld);
    }

    @Override
    public Text getDisplayName() {
        return Text.empty();
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new JewelryTableScreenHandler(syncId, inv, inventory);
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
