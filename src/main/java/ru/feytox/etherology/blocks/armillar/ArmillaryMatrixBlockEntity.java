package ru.feytox.etherology.blocks.armillar;

import io.wispforest.owo.util.ImplementedInventory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.blocks.pedestal.PedestalBlockEntity;
import ru.feytox.etherology.blocks.ringMatrix.RingMatrixBlockEntity;
import ru.feytox.etherology.components.IFloatComponent;
import ru.feytox.etherology.enums.ArmillarStateType;
import ru.feytox.etherology.enums.InstabTypes;
import ru.feytox.etherology.enums.RingType;
import ru.feytox.etherology.items.MatrixRing;
import ru.feytox.etherology.mixin.ItemEntityAccessor;
import ru.feytox.etherology.particle.ElectricityParticle;
import ru.feytox.etherology.particle.MovingParticle;
import ru.feytox.etherology.recipes.armillary.ArmillaryRecipe;
import ru.feytox.etherology.recipes.armillary.EArmillaryRecipe;
import ru.feytox.etherology.tickers.ITicker;
import ru.feytox.etherology.tickers.Ticker;
import ru.feytox.etherology.tickers.Tickers;
import ru.feytox.etherology.util.feyapi.UwuLib;
import ru.feytox.etherology.util.gecko.EGeoBlockEntity;
import ru.feytox.etherology.util.gecko.EGeoNetwork;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ru.feytox.etherology.BlocksRegistry.*;
import static ru.feytox.etherology.Etherology.LIGHT_VITAL;
import static ru.feytox.etherology.Etherology.STEAM;
import static ru.feytox.etherology.EtherologyComponents.ETHER_POINTS;
import static ru.feytox.etherology.enums.ArmillarStateType.*;

public class ArmillaryMatrixBlockEntity extends BlockEntity implements ImplementedInventory, ITicker, EGeoBlockEntity {
    List<Ticker> tickers = new ArrayList<>();
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(6, ItemStack.EMPTY);
    private UUID displayedItemUUID = null;
    private boolean activated = false;
    // TODO: replace all isDamaging -> ArmillarStateType.DAMAGING
    private boolean isDamaging = false;
    private InstabTypes instability = InstabTypes.NULL;
    private float instabilityMulti = 0.0f;
    private float storedEther = 0.0f;
    private int storingTicks = 0;
    private int craftStepTicks = 0;
    private int randomTicks = -1;
    private EArmillaryRecipe currentRecipe = null;
    private ArmillarStateType armillarStateType = ArmillarStateType.OFF;
    private MatrixWorkSoundInstance soundInstance = null;


    public ArmillaryMatrixBlockEntity(BlockPos pos, BlockState state) {
        super(ARMILLARY_MATRIX_BLOCK_ENTITY, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    public void interact(ServerWorld world, PlayerEntity player, Hand hand) {
        ItemStack handStack = player.getStackInHand(hand);

        if (handStack.isItemEqual(Items.ARROW.getDefaultStack()) && !activated) {
            boolean result = startCrafting(world, player);
            player.sendMessage(Text.of(result ? "началось" : "не началось"));
            return;
        }

        if (this.getRingsNum() < 5 && handStack.getItem() instanceof MatrixRing) {
            ItemStack copyStack = handStack.copy();
            copyStack.setCount(1);
            this.setStack(this.getRingsNum()+1, copyStack);
            handStack.decrement(1);
            markDirty();
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

    public List<RingType> getRingTypes() {
        List<RingType> ringTypes = new ArrayList<>();
        if (getRingsNum() == 0) return ringTypes;

        for (ItemStack itemStack : items) {
            if (itemStack.getItem() instanceof MatrixRing ring) ringTypes.add(ring.getRingType());
        }
        return ringTypes;
    }

    @Override
    public void markDirty() {
        super.markDirty();

        if (world != null) world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
    }

    public boolean isActivated() {
        return activated;
    }

    public ArmillarStateType getArmillarStateType() {
        return armillarStateType;
    }

    public RingMatrixBlockEntity getRingMatrix(World world) {
        BlockPos ringPos = this.pos.add(0, 1, 0);
        RingMatrixBlockEntity result = (RingMatrixBlockEntity) world.getBlockEntity(ringPos);
        if (world.isClient) return result;
        return result == null ? repairRingMatrix((ServerWorld) world, ringPos) : result;
    }

    public RingMatrixBlockEntity repairRingMatrix(ServerWorld world, BlockPos pos) {
        RingMatrixBlockEntity result = (RingMatrixBlockEntity) world.getBlockEntity(pos);
        if (result != null) return result;

        world.setBlockState(pos, RING_MATRIX_BLOCK.getDefaultState());
        return (RingMatrixBlockEntity) world.getBlockEntity(pos);
    }

    public boolean startCrafting(ServerWorld world, PlayerEntity player) {
        // TODO: check player stats etc.
        if (this.world == null || this.world.isClient) return false;
        List<ItemStack> allItems = getAllItems(this.world, this.pos, this.world.getBlockState(this.pos));
        SimpleInventory fakeInventory = new SimpleInventory(allItems.size());
        for (int i = 0; i < allItems.size(); i++) {
            fakeInventory.setStack(i, allItems.get(i));
        }

        Optional<ArmillaryRecipe> match = this.world.getRecipeManager()
                .getFirstMatch(ArmillaryRecipe.Type.INSTANCE, fakeInventory, this.world);

        if (match.isEmpty()) return false;

        currentRecipe = new EArmillaryRecipe(match.get());
        instability = currentRecipe.getInstability();
        instabilityMulti = 1.0f;
        activate(world);

        markDirty();
        return true;
    }

    public void tryActivate(ServerWorld world) {
        Ticker ticker = getDefaultTickers().get("starter");
        if (ticker == null) return;
        addTicker(ticker.copy(0));
        getRingMatrix(world).triggerAnim("accepted");
    }

    public void activate(ServerWorld world) {
        armillarStateType = RAISING;
        EGeoNetwork.sendStartMatrix(world, this);

        storingTicks = -42; // TODO: check raising time

        ItemEntity displayedItem = getDisplayedItemEntity(world);
        if (displayedItem != null) displayedItem.setVelocity(0, 1 / 16.9d, 0);

        activated = true;
        tickItem(world);
    }

    public void deactivate(ServerWorld world) {
        armillarStateType = LOWERING;
        EGeoNetwork.sendStopAnim(world, pos, "work");
        getRingMatrix(world).triggerAnim("end");
        getRingMatrix(world).triggerAnim("inactively");
        storingTicks = -40; // TODO: check raising time

        ItemEntity displayedItem = getDisplayedItemEntity(world);

        if (displayedItem != null) displayedItem.setVelocity(0, -1 / 16.9d, 0);

        activated = false;
    }

    public boolean craft(ServerWorld world) {
        if (currentRecipe == null) return false;

        UwuLib.drawParticleLine(world, getCenterPos(), getCenterPos().add(0, 0.2, 0), STEAM, 0.1f);

        ItemEntity displayedItem = getDisplayedItemEntity(world);
        if (displayedItem != null) displayedItem.kill();
        this.setStack(0, currentRecipe.getOutputStack().copy());
        tickItem(world);

        deactivate(world);
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

        armillarStateType = CRAFTING;

        craftStepTicks++;
        if (craftStepTicks < 6*20) return false;
        craftStepTicks = 0;

        if (currentRecipe.isFinished()) {
            return craft(world);
        }

        List<ItemStack> pedestalItems = getPedestalItems(world, pos, world.getBlockState(pos));
        int result = currentRecipe.findMatchAndRemove(pedestalItems, this.items.get(0));

        if (result == -621) {
            this.instabilityMulti += this.instabilityMulti * 0.42f;
            this.isDamaging = true;
            this.armillarStateType = DAMAGING;
            return false;
        }
        this.isDamaging = false;
        if (result == -1) {
            this.clear();
            if (currentRecipe.isFinished()) return craft(world);
        } else {
            List<PedestalBlockEntity> pedestals = getNotEmptyPedestals(world, pos, world.getBlockState(pos));
            PedestalBlockEntity pedestal = pedestals.get(result);
            pedestal.consumeItem(3*20, getCenterPos());
//            if (itemEntity != null) UwuLib.drawParticleLine(world, getCenterPos(), itemEntity, ParticleTypes.EFFECT, 0.1f);
        }

        return true;
    }

    // TODO: simplify getter method
    public static void serverTick(ServerWorld world, BlockPos pos, BlockState state, ArmillaryMatrixBlockEntity blockEntity) {
        blockEntity.tickItem(world);
        blockEntity.serverTickStore(world);
        blockEntity.craftStep(world);
        blockEntity.tickInstability(world, pos, state);

        blockEntity.plsDeleteMe(world);
        blockEntity.tickTickers(world);

        blockEntity.markDirty();
    }

    // FIXME: TODO: пожалуйста, сделай это без таких костылей, прошу тебя
    public void plsDeleteMe(ServerWorld world) {
        if (randomTicks == -1) getRingMatrix(world).triggerAnim("inactively");
    }

    public float getInstabilityMulti() {
        return instabilityMulti;
    }

    public static void clientTick(ClientWorld world, BlockPos pos, BlockState state, ArmillaryMatrixBlockEntity blockEntity) {
        blockEntity.clientTickStore(world);
        blockEntity.clientTickWorking(world);
        blockEntity.clientTickSound(world, pos);
    }

    public void clientTickSound(ClientWorld world, BlockPos pos) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null) return;

        if (soundInstance == null && !armillarStateType.equalsAny(OFF, RAISING, LOWERING)
                && client.player.squaredDistanceTo(getCenterPos()) < 36) {
            soundInstance = new MatrixWorkSoundInstance(this, client.player);
            client.getSoundManager().play(soundInstance);
        }

        if (soundInstance != null && (armillarStateType.equalsAny(OFF, RAISING, LOWERING)
                || soundInstance.isDone())) soundInstance = null;
    }

    public void clientTickWorking(ClientWorld world) {
        if (armillarStateType.equalsAny(OFF, RAISING, LOWERING)) return;

        Random rand = Random.create();
        if (rand.nextDouble() > 0.075) return;

        Vec3d center = getCenterPos();

        MovingParticle.spawnParticles(world, ElectricityParticle.getParticleType(rand), rand.nextBetween(1, 3), 1,
                center.x, center.y, center.z, instabilityMulti, 20, 0, rand);
    }

    public void tickInstability(ServerWorld world, BlockPos pos, BlockState state) {
        if (armillarStateType.equalsAny(OFF, RAISING, LOWERING)) return;
        randomTicks++;
        if (randomTicks < 10) return;
        randomTicks = 0;

        if (instability.event1(instabilityMulti, world)) return;
        if (instability.event2(instabilityMulti, world, pos, state)) return;
        if (instability.event3(instabilityMulti, world, this)) return;
        if (instability.event4(instabilityMulti, world, pos)) return;
        instability.event5(instabilityMulti, world, this);
    }

    public void serverTickStore(ServerWorld world) {
        if (storingTicks < 0 && armillarStateType.equals(LOWERING)) storingTicks++;
        if (armillarStateType.equals(LOWERING) && storingTicks == 0) {
            armillarStateType = OFF;
        }

        if (!activated || currentRecipe == null) return;
        storingTicks++;

        if (armillarStateType.equals(RAISING) && storingTicks == 0) {
            armillarStateType = STORING;
            getRingMatrix(world).triggerAnim("work");
        }

        if (storingTicks < 20) return;
        storingTicks = 0;
        if (storedEther < currentRecipe.getEtherPoints()) {
            Box entitiesBox = new Box(pos.getX()-8, pos.getY()-1, pos.getZ()-8, pos.getX()+8,
                    pos.getY()+8, pos.getZ()+8);
            List<? extends LivingEntity> nearestEntities = world
                    .getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class), entitiesBox,
                            livingEntity -> !livingEntity.isPlayer());

            if (nearestEntities.isEmpty()) {
                // player ether damage
                // TODO: replace 100 -> 8
                PlayerEntity player = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 100, false);
                if (player == null) return;
//                UwuLib.drawParticleLine(world, getCenterPos(), player, ParticleTypes.GLOW, 0.1f);
                IFloatComponent ether_points = ETHER_POINTS.get(player);
                if (ether_points.getValue() >= 0.75f) {
                    ether_points.decrement(0.75f);
                    storedEther += 0.75f;
                }
                return;
            }
//            UwuLib.drawParticleLine(world, getCenterPos(), nearestEntities.get(0), ParticleTypes.CRIT, 0.1f);
            nearestEntities.get(0).damage(DamageSource.MAGIC, 0.5f);
            storedEther += 0.5f;
        } else if (isDamaging) {
            PlayerEntity player = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 8, false);
            if (player == null) return;
//            UwuLib.drawParticleLine(world, getCenterPos(), player, ParticleTypes.GLOW, 0.1f);
            ETHER_POINTS.get(player).decrement(0.3f);
        }
    }

    public void clientTickStore(ClientWorld world) {
        if (!armillarStateType.equals(STORING)) return;

        Random rand = Random.create();

//        if (rand.nextDouble() > 0.2) return;

        Vec3d center = getCenterPos();

        if (!armillarStateType.equals(DAMAGING)) {
            Box entitiesBox = new Box(pos.getX()-8, pos.getY()-1, pos.getZ()-8, pos.getX()+8,
                    pos.getY()+8, pos.getZ()+8);
            List<? extends LivingEntity> nearestEntities = world
                    .getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class), entitiesBox,
                            livingEntity -> !livingEntity.isPlayer());

            if (nearestEntities.isEmpty()) {
                // TODO: replace 100 -> 8
                PlayerEntity player = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 100, false);
                if (player == null) return;

                MovingParticle.spawnParticles(world, LIGHT_VITAL, 5, 0.1, player,
                        center.x, center.y, center.z, rand);

                return;
            }

            Entity nearestEntity = nearestEntities.get(0);
            MovingParticle.spawnParticles(world, LIGHT_VITAL, 5, 0.1, nearestEntity,
                    center.x, center.y, center.z, rand);

        } else {
            PlayerEntity player = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 8, false);
            if (player == null) return;

            MovingParticle.spawnParticles(world, LIGHT_VITAL, 5, 0.1, player,
                    center.x, center.y, center.z, rand);
        }
    }

    public void tickItem(ServerWorld world) {
        if (armillarStateType.equalsAny(RAISING, LOWERING)) return;

        ItemEntity displayedItem = getDisplayedItemEntity(world);
        Vec3d mustPos = getCenterPos();

        if (displayedItem != null && displayedItem.isAlive()) {
            displayedItem.setVelocity(0.0, 0.0, 0.0);
            if (displayedItem.getX() != mustPos.getX() || displayedItem.getY() != mustPos.getY() ||
                    displayedItem.getZ() != mustPos.getZ()) {
                displayedItem.setPos(mustPos.x, mustPos.y, mustPos.z);
            }
        }

        if (!this.isEmpty() && (displayedItem == null || !displayedItem.isAlive())) {
            ItemStack displayedItemStack = items.get(0).copy();
            displayedItemStack.setCount(1);
            displayedItem = new ItemEntity(world, mustPos.x, mustPos.y, mustPos.z,
                    displayedItemStack, 0.0, 0.0, 0.0);
            ((ItemEntityAccessor) displayedItem).setItemAge(-32768);
            ((ItemEntityAccessor) displayedItem).setPickupDelay(32767);
            displayedItem.setInvulnerable(true);
            displayedItem.setNoGravity(true);
            world.spawnEntity(displayedItem);
            displayedItemUUID = displayedItem.getUuid();
        } else if (this.isEmpty() && displayedItem != null) {
            if (displayedItem.isAlive()) displayedItem.kill();
            displayedItemUUID = null;
        }
    }

    public boolean checkStructure(World world, BlockPos pos, BlockState state) {
        // TODO: как-то использовать проверку структуры
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

    public void onBreak(ServerWorld world) {
        world.setBlockState(getRingMatrix(world).getPos(), Blocks.AIR.getDefaultState());

        ItemEntity displayedItem = getDisplayedItemEntity(world);
        if (displayedItem != null && displayedItem.isAlive()) displayedItem.kill();
        markRemoved();
    }

    public Vec3d getCenterPos() {
        Vec3d notActivated = new Vec3d(this.pos.getX()+0.5, this.pos.getY()+0.75, this.pos.getZ()+0.5);
        if (!activated) return notActivated;

        return notActivated.add(0, 2, 0);
    }

    @Override
    public boolean isEmpty() {
        return items.get(0).isEmpty();
    }

    public int getRingsNum() {
        int i = 0;
        for (; i < 5; i++) {
            if (this.items.get(i+1).isEmpty()) break;
        }
        return i;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, items);

        displayedItemUUID = displayedItemUUID == null ? UUID.randomUUID() : displayedItemUUID;
        nbt.putUuid("displayedItemUUID", displayedItemUUID);

        nbt.putFloat("storedEther", storedEther);
        nbt.putFloat("instabilityMulti", instabilityMulti);
        instability.writeNbt(nbt);
        EArmillaryRecipe.writeNbt(currentRecipe, nbt);
        nbt.putInt("storingTicks", storingTicks);
        nbt.putInt("randomTicks", randomTicks);
        nbt.putInt("craftStepTicks", craftStepTicks);
        nbt.putBoolean("activated", activated);
        nbt.putBoolean("isDamaging", isDamaging);
        armillarStateType.writeNbt(nbt);

        writeTNbt(nbt);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        displayedItemUUID = nbt.getUuid("displayedItemUUID");
        storedEther = nbt.getFloat("storedEther");
        instabilityMulti = nbt.getFloat("instabilityMulti");
        instability = InstabTypes.readNbt(nbt);
        currentRecipe = EArmillaryRecipe.readNbt(nbt);
        storingTicks = nbt.getInt("storingTicks");
        randomTicks = nbt.getInt("randomTicks");
        craftStepTicks = nbt.getInt("craftStepTicks");
        isDamaging = nbt.getBoolean("isDamaging");
        activated = nbt.getBoolean("activated");
        armillarStateType = ArmillarStateType.readNbt(nbt);
        Inventories.readNbt(nbt, items);

        readTNbt(nbt);
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
    public List<Ticker> getTickers() {
        return tickers;
    }

    @Override
    public void setTickers(List<Ticker> tickers) {
        this.tickers = tickers;
    }

    @Override
    public Tickers getDefaultTickers() {
        // TODO: добавить функционал
        return new Tickers().register(
                new Ticker("ender", world1 -> deactivate((ServerWorld) world1), 15, false)
        );
    }

    @Override
    public void stopAnim(String animName) {
        if (this.world == null) return;
        getRingMatrix(this.world).stopAnim(animName);
    }

    @Deprecated
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Deprecated
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return null;
    }
}
