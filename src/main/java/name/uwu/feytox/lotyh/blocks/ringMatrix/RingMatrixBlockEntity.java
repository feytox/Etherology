package name.uwu.feytox.lotyh.blocks.ringMatrix;

import io.wispforest.owo.util.ImplementedInventory;
import name.uwu.feytox.lotyh.BlocksRegistry;
import name.uwu.feytox.lotyh.blocks.armillar.ArmillaryMatrixBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.RawAnimation;
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

        BlockPos armPos = this.pos.add(0, -1, 0);
        return (ArmillaryMatrixBlockEntity) world.getBlockEntity(armPos);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>
                (this, "controller", 1, this::predicate));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        ArmillaryMatrixBlockEntity armBlock = getBaseBlock();
        if (armBlock == null || !armBlock.isActivated()) {
//            event.getController().setAnimation(new AnimationBuilder()
//                    .loop("animation.ring_matrix.base")
//                    .loop("animation.ring_matrix.flying_loop"));
            return PlayState.CONTINUE;
        }

        event.getController().setAnimation(new AnimationBuilder()
                .loop("animation.ring_matrix.base")
                .loop("animation.ring_matrix.flying_loop")
                .loop("animation.ring_matrix.work_1"));
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
