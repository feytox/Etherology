package name.uwu.feytox.lotyh.blocks.ringMatrix;

import io.wispforest.owo.util.ImplementedInventory;
import name.uwu.feytox.lotyh.BlocksRegistry;
import name.uwu.feytox.lotyh.blocks.armillar.ArmillaryMatrixBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class RingMatrixBlockEntity extends BlockEntity implements IAnimatable, ImplementedInventory {
    private AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private DefaultedList<ItemStack> items = DefaultedList.ofSize(5, ItemStack.EMPTY);

    public RingMatrixBlockEntity(BlockPos pos, BlockState state) {
        super(BlocksRegistry.RING_MATRIX_BLOCK_ENTITY, pos, state);
    }

    public ArmillaryMatrixBlockEntity getBaseBlock() {
        if (this.world == null) return null;

        BlockPos armPos = this.pos.add(0, -3, 0);
        BlockEntity blockEntity = world.getBlockEntity(armPos);
        if (blockEntity instanceof ArmillaryMatrixBlockEntity) return (ArmillaryMatrixBlockEntity) blockEntity;

        armPos = this.pos.add(0, -1, 0);
        return (ArmillaryMatrixBlockEntity) world.getBlockEntity(armPos);
    }

    public void moveRings(ServerWorld world, boolean isActivating) {
        int dy = isActivating ? 2 : -2;
        BlockPos newPos = this.pos.add(0, dy, 0);
        world.setBlockState(newPos, this.getCachedState());
        world.setBlockState(this.pos, Blocks.AIR.getDefaultState());
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>
                (this, "base", 1, event -> {
                    event.getController().setAnimation(new AnimationBuilder()
                            .loop("animation.ring_matrix.base"));
                    return PlayState.CONTINUE;
                }));
        for (int i = 0; i < 3; i++) {
            animationData.addAnimationController(new AnimationController<>
                    (this, String.valueOf(i), 1, this::predicate));
        }
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        ArmillaryMatrixBlockEntity armBlock = getBaseBlock();
        if (armBlock == null || !armBlock.isActivated()) return PlayState.STOP;

        String animationName = null;
        switch (event.getController().getName()) {
            case "0" -> animationName = "animation.ring_matrix.flying_loop";
            case "1" -> animationName = "animation.ring_matrix.work_1";
            case "2" -> {
                // TODO: add instability anim
            }
        }

        if (animationName != null) event.getController().setAnimation(new AnimationBuilder()
                .loop(animationName));

        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, items);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        Inventories.readNbt(nbt, items);
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}
