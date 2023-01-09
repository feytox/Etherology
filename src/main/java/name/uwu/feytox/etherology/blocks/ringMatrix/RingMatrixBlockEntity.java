package name.uwu.feytox.etherology.blocks.ringMatrix;

import name.uwu.feytox.etherology.BlocksRegistry;
import name.uwu.feytox.etherology.blocks.armillar.ArmillaryMatrixBlockEntity;
import name.uwu.feytox.etherology.enums.ArmillarStateType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import static name.uwu.feytox.etherology.enums.ArmillarStateType.*;

public class RingMatrixBlockEntity extends BlockEntity implements IAnimatable, IAnimationTickable {
    private AnimationFactory factory = GeckoLibUtil.createFactory(this);

    public RingMatrixBlockEntity(BlockPos pos, BlockState state) {
        super(BlocksRegistry.RING_MATRIX_BLOCK_ENTITY, pos, state);
    }

    public ArmillaryMatrixBlockEntity getBaseBlock() {
        if (this.world == null) return null;

        BlockPos armPos = this.pos.add(0, -1, 0);
        return (ArmillaryMatrixBlockEntity) world.getBlockEntity(armPos);
    }

    // TODO: delete
    @Deprecated
    public void moveRings(ServerWorld world, boolean isActivating) {
        int dy = isActivating ? 2 : -2;
        BlockPos newPos = this.pos.add(0, dy, 0);
        world.setBlockState(newPos, this.getCachedState());
        world.setBlockState(this.pos, Blocks.AIR.getDefaultState());
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>
                (this, "base", 1, event -> {
                    event.getController().setAnimation(new AnimationBuilder()
                            .loop("animation.ring_matrix.base"));
                    return PlayState.CONTINUE;
                }));
        for (int i = 0; i < 5; i++) {
            animationData.addAnimationController(new AnimationController<>
                    (this, String.valueOf(i), 1, this::predicate));
        }
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        ArmillaryMatrixBlockEntity armBlock = getBaseBlock();
        if (armBlock == null) return PlayState.CONTINUE;

        ArmillarStateType stateType = armBlock.getArmillarStateType();

        String animationName = null;
        switch (event.getController().getName()) {
            case "0" -> {
//                if (!stateType.equals(OFF)) animationName = "animation.ring_matrix.flying_loop";
            }
            case "1" -> {
                if (stateType.equals(OFF)) animationName = "animation.ring_matrix.inactively_loop";
            }
            case "2" -> {
                if (!stateType.equalsAny(LOWERING, OFF)) animationName = "animation.ring_matrix.work_startloop";
            }
            case "3" -> {
                if (stateType.equals(DAMAGING)) animationName = "animation.ring_matrix.instability_loop";
            }
            case "4" -> {
                if (stateType.equals(LOWERING)) animationName = "animation.ring_matrix.work_end";
            }
        }

        if (animationName == null) return PlayState.STOP;

        AnimationState animationState = event.getController().getAnimationState();
        event.getController().setAnimation(new AnimationBuilder().loop(animationName));

        if (animationName == "animation.ring_matrix.work_startloop") {
            int x = 1 + 2;
        }

        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public void tick() {
    }

    @Override
    public int tickTimer() {
        return 0;
    }
}
