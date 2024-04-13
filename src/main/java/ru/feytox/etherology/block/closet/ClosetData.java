package ru.feytox.etherology.block.closet;

import io.wispforest.owo.util.ImplementedInventory;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.world.World;
import ru.feytox.etherology.block.furniture.FurnitureData;
import ru.feytox.etherology.util.misc.Nbtable;

import static ru.feytox.etherology.block.furniture.AbstractFurSlabBlock.BOTTOM_ACTIVE;
import static ru.feytox.etherology.block.furniture.AbstractFurSlabBlock.TOP_ACTIVE;
import static ru.feytox.etherology.registry.block.EBlocks.CLOSET_SLAB;

public class ClosetData extends FurnitureData implements ImplementedInventory, NamedScreenHandlerFactory {
    private World cachedWorld = null;
    private BlockState cachedState = null;
    private BlockPos cachedPos = null;
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(10, ItemStack.EMPTY);

    public ClosetData(boolean isBottom) {
        super(isBottom);
    }

    @Override
    public void clientUse(ClientWorld world, BlockState state, BlockPos pos, PlayerEntity player, Vec2f hitPos, Hand hand) {
        cache(world, state, pos);
    }

    @Override
    public void serverUse(ServerWorld world, BlockState state, BlockPos pos, PlayerEntity player, Vec2f hitPos, Hand hand) {
        cache(world, state, pos);
        NamedScreenHandlerFactory factory = createScreenFactory(this, isBottom);
        player.openHandledScreen(factory);
    }

    @Override
    public void onOpen(PlayerEntity player) {
        if (!checkCache()) return;

        playSound(SoundEvents.BLOCK_BARREL_OPEN);
        BlockState oldState = cachedWorld.getBlockState(cachedPos);
        BooleanProperty property = isBottom ? BOTTOM_ACTIVE : TOP_ACTIVE;
        cachedWorld.setBlockState(cachedPos, oldState.with(property, true));
    }

    @Override
    public void onClose(PlayerEntity player) {
        if (!checkCache()) return;

        playSound(SoundEvents.BLOCK_BARREL_CLOSE);
        BlockState oldState = cachedWorld.getBlockState(cachedPos);
        BooleanProperty property = isBottom ? BOTTOM_ACTIVE : TOP_ACTIVE;
        cachedWorld.setBlockState(cachedPos, oldState.with(property, false));
    }

    public void playSound(SoundEvent soundEvent) {
        cachedWorld.playSound(null,
                cachedPos.getX(), cachedPos.getY(), cachedPos.getZ(),
                soundEvent, SoundCategory.BLOCKS, 0.5F,
                cachedWorld.random.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, this.inventory);
    }

    @Override
    public Nbtable readNbt(NbtCompound nbt) {
        inventory.clear();
        Inventories.readNbt(nbt, this.inventory);
        return this;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public Text getDisplayName() {
        return getTitle();
    }

    public static Text getTitle() {
        return Text.translatable(CLOSET_SLAB.getTranslationKey());
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new ClosetScreenHandler(syncId, inv, this);
    }

    public void cache(World world, BlockState state, BlockPos pos) {
        cachedWorld = world;
        cachedState = state;
        cachedPos = pos;
    }

    public boolean checkCache() {
        return cachedWorld != null && cachedState != null && cachedPos != null;
    }

    public static NamedScreenHandlerFactory createScreenFactory(Inventory blockInv, boolean isBottom) {
        return new SimpleNamedScreenHandlerFactory(((syncId, inv, player) ->
                new ClosetScreenHandler(syncId, inv, blockInv)), ClosetData.getTitle());
    }
}
