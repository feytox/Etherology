package ru.feytox.etherology.client.block;


import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import ru.feytox.etherology.block.brewingCauldron.BrewingCauldronBlockEntity;
import ru.feytox.etherology.block.etherealChannel.EtherealChannelBlockEntity;
import ru.feytox.etherology.block.etherealSocket.EtherealSocketBlockEntity;
import ru.feytox.etherology.block.generators.AbstractGeneratorBlockEntity;
import ru.feytox.etherology.block.jewelryTable.JewelryBlockEntity;
import ru.feytox.etherology.block.levitator.LevitatorBlockEntity;
import ru.feytox.etherology.block.matrix.MatrixBlockEntity;
import ru.feytox.etherology.block.seal.SealBlockEntity;
import ru.feytox.etherology.block.tuningFork.TuningForkBlockEntity;
import ru.feytox.etherology.client.block.brewingCauldron.BrewingCauldronClient;
import ru.feytox.etherology.client.block.channel.EtherealChannelClient;
import ru.feytox.etherology.client.block.etherealSocket.EtherealSocketClient;
import ru.feytox.etherology.client.block.generators.AbstractGeneratorClient;
import ru.feytox.etherology.client.block.jewelryTable.JewelryTableClient;
import ru.feytox.etherology.client.block.levitator.LevitatorBlockClient;
import ru.feytox.etherology.client.block.matrix.MatrixBlockClient;
import ru.feytox.etherology.client.block.seal.SealBlockClient;
import ru.feytox.etherology.client.block.tuningFork.TuningForkClient;
import ru.feytox.etherology.client.util.ClientTickableBlock;
import ru.feytox.etherology.util.misc.TickableBlockEntity;

import java.util.Map;

public class ClientBlockTickers {

    private static final Map<Class<? extends TickableBlockEntity>, ClientFactory<? extends TickableBlockEntity>> blockToFactory = new Object2ObjectOpenHashMap<>();
    private static final Map<Class<? extends TickableBlockEntity>, ClientTicker<?>> blockToTicker = new Object2ObjectOpenHashMap<>();

    public static void addAll() {
        addFactory(AbstractGeneratorBlockEntity.class, AbstractGeneratorClient::new);
        addTicker(EtherealChannelBlockEntity.class, EtherealChannelClient::clientTick);
        addFactory(BrewingCauldronBlockEntity.class, BrewingCauldronClient::new);
        addFactory(EtherealSocketBlockEntity.class, EtherealSocketClient::new);
        addTicker(JewelryBlockEntity.class, JewelryTableClient::clientTick);
        addFactory(MatrixBlockEntity.class, MatrixBlockClient::new);
        addTicker(SealBlockEntity.class, SealBlockClient::clientTick);
        addTicker(TuningForkBlockEntity.class, TuningForkClient::clientTick);
        addTicker(LevitatorBlockEntity.class, LevitatorBlockClient::clientTick);
    }

    @SuppressWarnings("unchecked")
    public static <T extends TickableBlockEntity> boolean tryTick(T blockEntity, ClientWorld world, BlockPos blockPos, BlockState state) {
        var clazz = blockEntity.getClientTickerProvider().orElseGet(blockEntity::getClass);
        if (blockToFactory.containsKey(clazz)) {
            var clientSideBlock = ((ClientTickableBlock<T>) blockEntity.getClientTickableBlock());
            if (clientSideBlock == null) {
                clientSideBlock = ((ClientFactory<T>) blockToFactory.get(clazz)).create(blockEntity);
                blockEntity.setClientTickableBlock(clientSideBlock);
            }

            clientSideBlock.clientTick(world, blockPos, state);
            return true;
        }

        if (!blockToTicker.containsKey(clazz))
            return false;
        ((ClientTicker<T>) blockToTicker.get(clazz)).clientTick(blockEntity, world, blockPos, state);
        return true;
    }

    // for ticks without client fields
    private static <T extends TickableBlockEntity> void addTicker(Class<T> blockEntityClass, ClientTicker<T> ticker) {
        blockToTicker.put(blockEntityClass, ticker);
    }

    // for ticks with fields
    private static <T extends TickableBlockEntity> void addFactory(Class<T> blockEntityClass, ClientFactory<T> factory) {
        blockToFactory.put(blockEntityClass, factory);
    }

    @FunctionalInterface
    interface ClientFactory<T extends TickableBlockEntity> {
        ClientTickableBlock<T> create(T blockEntity);
    }

    @FunctionalInterface
    interface ClientTicker<T extends TickableBlockEntity> {
        void clientTick(T blockEntity, ClientWorld world, BlockPos blockPos, BlockState state);
    }
}


