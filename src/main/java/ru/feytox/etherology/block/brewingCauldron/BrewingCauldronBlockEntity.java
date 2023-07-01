package ru.feytox.etherology.block.brewingCauldron;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.data.item_aspects.ItemAspectsLoader;
import ru.feytox.etherology.magic.aspects.EtherAspectsContainer;
import ru.feytox.etherology.recipes.brewingCauldron.CauldronRecipe;
import ru.feytox.etherology.recipes.brewingCauldron.CauldronRecipeInventory;
import ru.feytox.etherology.util.feyapi.TickableBlockEntity;

import java.util.Optional;

import static ru.feytox.etherology.registry.block.EBlocks.BREWING_CAULDRON_BLOCK_ENTITY;

public class BrewingCauldronBlockEntity extends TickableBlockEntity {
    private EtherAspectsContainer aspects = new EtherAspectsContainer(new Object2ObjectOpenHashMap<>());

    public BrewingCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(BREWING_CAULDRON_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void serverTick(ServerWorld world, BlockPos blockPos, BlockState state) {
        if (!BrewingCauldronBlock.isFilled(state)) return;
        tickTemperature(world, blockPos, state);
    }

    private void tickAspects(ServerWorld world, BlockPos blockPos, BlockState state) {
        // TODO: 30.06.2023 сделать испарение со временем
    }

    private void tickTemperature(ServerWorld world, BlockPos blockPos, BlockState state) {
        if (world.getTime() % 5 != 0) return;

        BlockState downState = world.getBlockState(blockPos.down());
        if (!downState.isOf(Blocks.TORCH)) return;

        int newTemperature = Math.min(100, state.get(BrewingCauldronBlock.TEMPERATURE) + 1);
        world.setBlockState(blockPos, state.with(BrewingCauldronBlock.TEMPERATURE, newTemperature));
    }

    public void clearAspects() {
        aspects = new EtherAspectsContainer(new Object2ObjectOpenHashMap<>());
        markDirty();
    }

    public void consumeItem(ServerWorld world, ItemEntity itemEntity, BlockState state) {
        if (itemEntity instanceof CauldronItemEntity) return;

        ItemStack stack = itemEntity.getStack();
        if (checkForRecipe(world, stack, state)) {
            itemEntity.discard();
            return;
        }

        if (!BrewingCauldronBlock.isFilled(world, pos)) return;
        EtherAspectsContainer aspectContainer = ItemAspectsLoader.getAspectsOf(stack).orElse(null);
        if (aspectContainer == null) return;

        itemEntity.discard();
        aspects = aspects.add(aspectContainer);
        markDirty();
    }

    private boolean checkForRecipe(ServerWorld world, ItemStack inputStack, BlockState state) {
        CauldronRecipeInventory inventory = new CauldronRecipeInventory(aspects, inputStack);
        Optional<CauldronRecipe> match = world.getRecipeManager()
                .getFirstMatch(CauldronRecipe.Type.INSTANCE, inventory, world);
        if (match.isEmpty()) return false;

        ItemStack resultStack = craft(world, inputStack, match.get(), state);
        CauldronItemEntity.spawn(world, pos.up().toCenterPos(), resultStack);
        markDirty();
        return inputStack.isEmpty();
    }

    @NotNull
    private ItemStack craft(ServerWorld world, ItemStack itemStack, CauldronRecipe recipe, BlockState state) {
        CauldronRecipeInventory inventory;
        Item outputItem = recipe.getOutput().getItem();
        int count = 0;

        do {
            itemStack.decrement(recipe.getInputAmount());
            aspects = aspects.subtract(recipe.getInputAspects());
            count += recipe.getOutput().getCount();

            int oldLevel = state.get(BrewingCauldronBlock.LEVEL);
            state = state.with(BrewingCauldronBlock.LEVEL, oldLevel-1);
            inventory = new CauldronRecipeInventory(aspects, itemStack);
        } while (recipe.matches(inventory, world) && BrewingCauldronBlock.isFilled(state));

        world.setBlockState(pos, state);
        return new ItemStack(outputItem, count);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        aspects.writeNbt(nbt);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        aspects = (EtherAspectsContainer) aspects.readNbt(nbt);
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
