package ru.feytox.etherology.block.shelf;

import io.wispforest.owo.util.ImplementedInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import ru.feytox.etherology.block.furniture.FurSlabBlockEntity;
import ru.feytox.etherology.block.furniture.FurnitureData;
import ru.feytox.etherology.util.feyapi.Nbtable;

import static net.minecraft.client.render.model.json.ModelTransformation.Mode.FIXED;

public class ShelfData extends FurnitureData implements ImplementedInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);

    public ShelfData(boolean isBottom) {
        super(isBottom);
    }

    @Override
    public void serverUse(ServerWorld world, BlockState state, BlockPos pos, PlayerEntity player, Vec2f hitPos, Hand hand) {
        boolean isLeft = hitPos.x < 0.5;
        int slot = isLeft ? 0 : 1;
        ItemStack currentStack = getStack(slot);
        hand = player.getActiveHand();
        ItemStack playerStack = player.getStackInHand(hand);
        boolean isSameItem = playerStack.isOf(currentStack.getItem());

        if (!isSameItem && !playerStack.isEmpty()) {
            // замена предмета на предмет
            ItemStack takingStack = playerStack.copy();
            playerStack = currentStack;
            currentStack = takingStack;

            player.setStackInHand(hand, playerStack);
            setStack(slot, currentStack);
            world.markDirty(pos);
            updateData(world, pos);

        } else if (!currentStack.isEmpty() && !playerStack.isEmpty()) {
            // кладём предмет на НЕПУСТУЮ полку
            ItemStack takingStack = playerStack.copy();
            takingStack.setCount(currentStack.getMaxCount() - currentStack.getCount());

            playerStack.decrement(takingStack.getCount());
            currentStack.increment(takingStack.getCount());

            world.markDirty(pos);
            updateData(world, pos);

        } else if (!currentStack.isEmpty()) {
            // берём предмет ПУСТОЙ рукой с НЕПУСТОЙ полки
            ItemStack takingStack = currentStack.copy();
            currentStack.setCount(0);

            player.setStackInHand(hand, takingStack);
            world.markDirty(pos);
            updateData(world, pos);
        }
    }

    @Override
    public void render(BlockEntityRendererFactory.Context ctx, FurSlabBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = entity.getWorld();
        if (world == null) return;

        ItemRenderer itemRenderer = ctx.getItemRenderer();
        ItemStack leftStack = getStack(0);
        ItemStack rightStack = getStack(1);

        Direction facing = entity.getCachedState().get(HorizontalFacingBlock.FACING);
        light = WorldRenderer.getLightmapCoordinates(world, entity.getPos().add(facing.getVector()));
        float degrees = 180 - facing.asRotation();
        float rads = degrees * MathHelper.PI / 180;

        // нахождение "уголка"
        Vec3d northPoint = new Vec3d(-0.5, 0, -0.5);
        Vec3d diffVec = northPoint.rotateY(rads).subtract(northPoint);

        double y = isBottom ? 0.25 : 0.75;
        Vec3d leftItemVec = new Vec3d(0.75, y, 0.05).rotateY(rads).add(diffVec);
        Vec3d rightItemVec = new Vec3d(0.25, y, 0.05).rotateY(rads).add(diffVec);

        matrices.push();
        matrices.translate(leftItemVec.x, leftItemVec.y, leftItemVec.z);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(degrees));
        matrices.scale(0.25f, 0.25f, 0.25f);
        itemRenderer.renderItem(leftStack, FIXED, light, overlay, matrices, vertexConsumers, 621);
        matrices.pop();

        matrices.push();
        matrices.translate(rightItemVec.x, rightItemVec.y, rightItemVec.z);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(degrees));
        matrices.scale(0.25f, 0.25f, 0.25f);
        itemRenderer.renderItem(rightStack, FIXED, light, overlay, matrices, vertexConsumers, 621);
        matrices.pop();
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
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
}
