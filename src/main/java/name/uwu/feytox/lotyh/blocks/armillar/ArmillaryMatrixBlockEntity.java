package name.uwu.feytox.lotyh.blocks.armillar;

import io.wispforest.owo.util.ImplementedInventory;
import name.uwu.feytox.lotyh.blocks.pedestal.PedestalBlockEntity;
import name.uwu.feytox.lotyh.blocks.ringMatrix.RingMatrixBlockEntity;
import name.uwu.feytox.lotyh.components.IFloatComponent;
import name.uwu.feytox.lotyh.enums.InstabTypes;
import name.uwu.feytox.lotyh.mixin.ItemEntityAccessor;
import name.uwu.feytox.lotyh.recipes.armillary.ArmillaryRecipe;
import name.uwu.feytox.lotyh.recipes.armillary.LArmillaryRecipe;
import name.uwu.feytox.lotyh.util.UwuLib;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static name.uwu.feytox.lotyh.BlocksRegistry.ARMILLARY_MATRIX_BLOCK_ENTITY;
import static name.uwu.feytox.lotyh.BlocksRegistry.PEDESTAL_BLOCK_ENTITY;
import static name.uwu.feytox.lotyh.LotyhComponents.ETHER_POINTS;

public class ArmillaryMatrixBlockEntity extends BlockEntity implements ImplementedInventory {
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private UUID displayedItemUUID = null;
    private boolean activated = false;
    private boolean isDamaging = false;
    private InstabTypes instability = InstabTypes.NULL;
    private float instabilityMulti = 0.0f;
    private float storedEther = 0.0f;
    private int storingTicks = 0;
    private int craftStepTicks = 0;
    private int randomTicks = 0;
    private LArmillaryRecipe currentRecipe = null;


    public ArmillaryMatrixBlockEntity(BlockPos pos, BlockState state) {
        super(ARMILLARY_MATRIX_BLOCK_ENTITY, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    public void interact(PlayerEntity player, Hand hand) {
        ItemStack handStack = player.getStackInHand(hand);

        if (handStack.isItemEqual(Items.ARROW.getDefaultStack())) {
            boolean result = startCrafting(player);
            player.sendMessage(Text.of(result ? "началось" : "не началось"));
            return;
        }

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

    public boolean isActivated() {
        return activated;
    }

    public RingMatrixBlockEntity getRingMatrix() {
        if (this.world == null || this.world.isClient) return null;

        BlockPos ringPos = this.pos.add(0, 1, 0);
        return (RingMatrixBlockEntity) world.getBlockEntity(ringPos);
    }

    public boolean startCrafting(PlayerEntity player) {
        // TODO: check player stats and etc.
        if (this.world == null || this.world.isClient) return false;
        List<ItemStack> allItems = getAllItems(this.world, this.pos, this.world.getBlockState(this.pos));
        SimpleInventory fakeInventory = new SimpleInventory(allItems.size());
        for (int i = 0; i < allItems.size(); i++) {
            fakeInventory.setStack(i, allItems.get(i));
        }

        Optional<ArmillaryRecipe> match = this.world.getRecipeManager()
                .getFirstMatch(ArmillaryRecipe.Type.INSTANCE, fakeInventory, this.world);

        if (match.isEmpty()) return false;

        currentRecipe = new LArmillaryRecipe(match.get());
        instability = currentRecipe.getInstability();
        instabilityMulti = 1.0f;
        activated = true;

        markDirty();
        return true;
    }

    public boolean craft(ServerWorld world) {
        if (currentRecipe == null) return false;


        UwuLib.drawParticleLine(world, getCenterPos(), getCenterPos().add(0, 0.2, 0), ParticleTypes.LARGE_SMOKE, 0.1f);

        ItemEntity displayedItem = getDisplayedItemEntity(world);
        if (displayedItem != null) displayedItem.kill();
        this.setStack(0, currentRecipe.getOutputStack().copy());
        tickItem(world);

        activated = false;
        currentRecipe = null;
        storedEther = 0.0f;
        instability = InstabTypes.NULL;
        instabilityMulti = 0.0f;

        markDirty();
        return true;
    }

    public boolean craftStep(ServerWorld world) {
        if (currentRecipe == null) return false;

        if (storedEther < currentRecipe.getEtherPoints()) return false;

        craftStepTicks++;
        if (craftStepTicks < 5*20) return false;
        craftStepTicks = 0;

        if (currentRecipe.isFinished()) {
            return craft(world);
        }

        List<ItemStack> pedestalItems = getPedestalItems(world, pos, world.getBlockState(pos));
        int result = currentRecipe.findMatchAndRemove(pedestalItems, this.items.get(0));

        if (result == -621) {
            this.instabilityMulti += this.instabilityMulti * 0.1f;
            this.isDamaging = true;
            return false;
        }
        this.isDamaging = false;
        // TODO: добавить какие-то эффекты для забирания предмета
        if (result == -1) {
            this.clear();
            if (currentRecipe.isFinished()) return craft(world);
        } else {
            List<PedestalBlockEntity> pedestals = getNotEmptyPedestals(world, pos, world.getBlockState(pos));
            PedestalBlockEntity pedestal = pedestals.get(result);
            ItemEntity itemEntity = pedestal.getDisplayedItemEntity(world);
            if (itemEntity != null) UwuLib.drawParticleLine(world, getCenterPos(), itemEntity, ParticleTypes.EFFECT, 0.1f);
            pedestal.clear();
        }

        return true;
    }

    // TODO: static -> class method
    public static void tick(World world, BlockPos pos, BlockState state, ArmillaryMatrixBlockEntity blockEntity) {
        blockEntity.tickItem((ServerWorld) world);
        blockEntity.tickStore((ServerWorld) world, pos);
        blockEntity.craftStep((ServerWorld) world);
        blockEntity.tickInstability((ServerWorld) world, pos, state);
    }

    public void tickInstability(ServerWorld world, BlockPos pos, BlockState state) {
        if (!activated || currentRecipe == null) return;
        randomTicks++;
        if (randomTicks < 10) return;
        randomTicks = 0;

        if (instability.event1(instabilityMulti, world)) return;
        if (instability.event2(instabilityMulti, world, pos, state)) return;
        if (instability.event3(instabilityMulti, world, this)) return;
        if (instability.event4(instabilityMulti, world, pos)) return;
        instability.event5(instabilityMulti, world, this);
    }

    public void tickStore(ServerWorld world, BlockPos pos) {
        if (!activated || currentRecipe == null) return;
        storingTicks += 1;
        if (storingTicks < 20) return;
        storingTicks = 0;
        if (storedEther < currentRecipe.getEtherPoints()) {
            List<? extends LivingEntity> allEntities = world
                    .getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class), livingEntity -> !livingEntity.isPlayer());
            List<LivingEntity> nearestEntities = new ArrayList<>();
            allEntities.forEach(livingEntity -> {
                if (livingEntity.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()) <= 64) nearestEntities.add(livingEntity);
            });
            if (nearestEntities.isEmpty()) {
                // player ether damage
                PlayerEntity player = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 100, false);
                if (player == null) return;
                UwuLib.drawParticleLine(world, getCenterPos(), player, ParticleTypes.GLOW, 0.1f);
                IFloatComponent ether_points = ETHER_POINTS.get(player);
                if (ether_points.getValue() >= 0.75f) {
                    ether_points.decrement(0.75f);
                    storedEther += 0.5f;
                }
                return;
            }
            UwuLib.drawParticleLine(world, getCenterPos(), nearestEntities.get(0), ParticleTypes.CRIT, 0.1f);
            nearestEntities.get(0).damage(DamageSource.MAGIC, 0.5f);
            storedEther += 0.5f;
        } else if (isDamaging) {
            PlayerEntity player = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 8, false);
            if (player == null) return;
            UwuLib.drawParticleLine(world, getCenterPos(), player, ParticleTypes.GLOW, 0.1f);
            ETHER_POINTS.get(player).decrement(0.3f);
        }
    }

    public void tickItem(ServerWorld world) {
        ItemEntity displayedItem = getDisplayedItemEntity(world);

        if (displayedItem != null && displayedItem.isAlive()) {
            displayedItem.setVelocity(0.0, 0.0, 0.0);
            if (displayedItem.getX() != this.pos.getX()+0.5 ||
                    displayedItem.getY() != this.pos.getY()+0.75 ||
                    displayedItem.getZ() != this.pos.getZ()+0.5) {
                displayedItem.setPos(this.pos.getX()+0.5, this.pos.getY()+0.75, this.pos.getZ()+0.5);
            }
        }

        if (!this.isEmpty() && (displayedItem == null || !displayedItem.isAlive())) {
            ItemStack displayedItemStack = items.get(0).copy();
            displayedItemStack.setCount(1);
            displayedItem = new ItemEntity(world, this.pos.getX()+0.5, this.pos.getY()+0.75, this.pos.getZ()+0.5,
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

    public boolean checkStructure(World world, BlockPos pos, BlockState state) {
        return getPedestals(world, pos, state).size() >= 9;
    }

    @Nullable
    private ItemEntity getDisplayedItemEntity(ServerWorld world) {
        Entity searchedEntity = world.getEntity(displayedItemUUID);
        return searchedEntity instanceof ItemEntity ? (ItemEntity) searchedEntity : null;
    }

    public List<PedestalBlockEntity> getPedestals(World world, BlockPos pos, BlockState state) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        List<PedestalBlockEntity> pedestals = new ArrayList<>();
        for (int iy = y-1; iy < y+2; iy++) {
            for (int ix = x-8; ix < x+9; ix++) {
                for (int iz = z-8; iz < z+9; iz++) {
                    Optional<PedestalBlockEntity> matches =
                            world.getBlockEntity(new BlockPos(ix, iy, iz), PEDESTAL_BLOCK_ENTITY);
                    matches.ifPresent(pedestals::add);
                }
            }
        }

        return pedestals;
    }

    public List<PedestalBlockEntity> getNotEmptyPedestals(World world, BlockPos pos, BlockState state) {
        List<PedestalBlockEntity> pedestals = getPedestals(world, pos, state);
        return pedestals.stream().filter(pedestal -> !pedestal.isEmpty()).toList();
    }

    public List<ItemStack> getAllItems(World world, BlockPos pos, BlockState state) {
        List<ItemStack> itemStacks = getPedestalItems(world, pos, state);
        if (!this.items.isEmpty()) itemStacks.add(this.items.get(0));
        return itemStacks;
    }

    public List<ItemStack> getPedestalItems(World world, BlockPos pos, BlockState state) {
        List<PedestalBlockEntity> pedestals = getPedestals(world, pos, state);
        List<ItemStack> pedestalItems = new ArrayList<>();
        for (PedestalBlockEntity pedestal: pedestals) {
            ItemStack itemStack = pedestal.getStack(0);
            if (!itemStack.isEmpty()) pedestalItems.add(itemStack);
        }

        return pedestalItems;
    }

    public void onBreak(World world) {
        ItemEntity displayedItem = getDisplayedItemEntity((ServerWorld) world);
        if (displayedItem != null && displayedItem.isAlive()) displayedItem.kill();
        markRemoved();
    }

    public Vec3d getCenterPos() {
        return new Vec3d(this.pos.getX()+0.5, this.pos.getY()+1.0, this.pos.getZ()+0.5);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, items);

        displayedItemUUID = displayedItemUUID == null ? UUID.randomUUID() : displayedItemUUID;
        nbt.putUuid("displayedItemUUID", displayedItemUUID);

        nbt.putFloat("storedEther", storedEther);
        nbt.putFloat("instabilityMulti", instabilityMulti);
        instability.writeNbt(nbt);
        LArmillaryRecipe.writeNbt(currentRecipe, nbt);
        nbt.putInt("storingTicks", storingTicks);
        nbt.putInt("randomTicks", randomTicks);
        nbt.putInt("craftStepTicks", craftStepTicks);
        nbt.putBoolean("activated", activated);
        nbt.putBoolean("isDamaging", isDamaging);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        displayedItemUUID = nbt.getUuid("displayedItemUUID");
        storedEther = nbt.getFloat("storedEther");
        instabilityMulti = nbt.getFloat("instabilityMulti");
        instability = InstabTypes.readNbt(nbt);
        currentRecipe = LArmillaryRecipe.readNbt(nbt, this.world);
        storingTicks = nbt.getInt("storingTicks");
        randomTicks = nbt.getInt("randomTicks");
        craftStepTicks = nbt.getInt("craftStepTicks");
        isDamaging = nbt.getBoolean("isDamaging");
        activated = nbt.getBoolean("activated");
        Inventories.readNbt(nbt, items);
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
