package ru.feytox.etherology.block.ringMatrix;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ru.feytox.etherology.block.armillar.ArmillaryMatrixBlockEntity;
import ru.feytox.etherology.registry.block.EBlocks;
import ru.feytox.etherology.util.gecko.EGeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class RingMatrixBlockEntity extends BlockEntity implements EGeoBlockEntity {
    private static final RawAnimation BASE_ANIM = RawAnimation.begin()
            .thenLoop("animation.ring_matrix.base");
    private static final RawAnimation INACTIVELY_ANIM = RawAnimation.begin()
            .thenLoop("animation.ring_matrix.inactively_loop");
    private static final RawAnimation FLYING_ANIM = RawAnimation.begin()
            .thenLoop("animation.ring_matrix.flying_loop");
    private static final RawAnimation ACCEPTED_ANIM = RawAnimation.begin()
            .thenPlay("animation.ring_matrix.work_accepted");
    private static final RawAnimation STARTLOOP_ANIM = RawAnimation.begin()
            .thenPlay("animation.ring_matrix.work_accepted")
            .thenLoop("animation.ring_matrix.work_startloop");
    private static final RawAnimation START_ANIM = RawAnimation.begin()
            .thenPlay("animation.ring_matrix.work_start");
    private static final RawAnimation WORK_ANIM = RawAnimation.begin()
            .thenLoop("animation.ring_matrix.work_loop");
    private static final RawAnimation END_ANIM = RawAnimation.begin()
            .thenPlay("animation.ring_matrix.work_end");
    private static final RawAnimation INSTABILITY_ANIM = RawAnimation.begin()
            .thenLoop("animation.ring_matrix.instability_loop");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);


    public RingMatrixBlockEntity(BlockPos pos, BlockState state) {
        super(EBlocks.RING_MATRIX_BLOCK_ENTITY, pos, state);
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

    public static void serverTick(World world, BlockPos blockPos, BlockState state, BlockEntity blockEntity) {
        ServerWorld serverWorld = (ServerWorld) world;
        RingMatrixBlockEntity ringMatrix = (RingMatrixBlockEntity) blockEntity;

        ringMatrix.checkBase(serverWorld);
    }

    public void checkBase(ServerWorld world) {
        BlockEntity be = world.getBlockEntity(this.pos.down(1));

        if (!(be instanceof ArmillaryMatrixBlockEntity)) world.breakBlock(this.pos, false);
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
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(getController(BASE_ANIM));
        controllers.add(getTriggerController("inactively", INACTIVELY_ANIM));
        controllers.add(getTriggerController("flying", FLYING_ANIM));
        controllers.add(getTriggerController("startloop", STARTLOOP_ANIM));
        controllers.add(getTriggerController("start", START_ANIM));
        controllers.add(getTriggerController("work", WORK_ANIM));
        controllers.add(getTriggerController("end", END_ANIM));
        controllers.add(getTriggerController("accepted", ACCEPTED_ANIM));
        controllers.add(getTriggerController("instability", INSTABILITY_ANIM));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
