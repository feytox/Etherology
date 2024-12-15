package ru.feytox.etherology.client.block.generators;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import ru.feytox.etherology.block.generators.AbstractGenerator;
import ru.feytox.etherology.block.generators.AbstractGeneratorBlockEntity;
import ru.feytox.etherology.client.util.ClientTickableBlock;
import ru.feytox.etherology.item.OculusItem;
import ru.feytox.etherology.particle.effects.LightParticleEffect;
import ru.feytox.etherology.particle.subtype.LightSubtype;
import ru.feytox.etherology.registry.particle.EtherParticleTypes;

import static ru.feytox.etherology.block.generators.AbstractGenerator.STALLED;

public class AbstractGeneratorClient extends ClientTickableBlock<AbstractGeneratorBlockEntity> {

    private boolean launched;

    public AbstractGeneratorClient(AbstractGeneratorBlockEntity blockEntity) {
        super(blockEntity);
    }

    @Override
    public void clientTick(ClientWorld world, BlockPos blockPos, BlockState state) {
        if (!launched) {
            blockEntity.triggerAnim(state.get(STALLED) ? "stalled" : "spin");
            launched = true;
        }

        if (world.getTime() % 10 != 0) return;
        var player = MinecraftClient.getInstance().player;
        if (player == null) return;
        if (!OculusItem.isInHands(player)) return;
        if (state.get(STALLED)) return;

        var targetPos = blockPos.toCenterPos();
        var direction = state.get(AbstractGenerator.FACING).getOpposite();
        var particlePos = blockPos.add(direction.getVector()).toCenterPos();
        var effect = new LightParticleEffect(EtherParticleTypes.LIGHT, LightSubtype.GENERATOR, targetPos);
        effect.spawnParticles(world, 4, 1.0d, particlePos);
    }
}
