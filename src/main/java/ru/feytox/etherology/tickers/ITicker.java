package ru.feytox.etherology.tickers;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public interface ITicker {
    List<Ticker> getTickers();
    void setTickers(List<Ticker> tickers);
    Tickers getDefaultTickers();

    default void tickTickers(World world) {
        List<Ticker> tickers = getTickers();
        tickers.forEach(ticker -> ticker.tick(world));
    }

    default void addTicker(Ticker ticker) {
        List<Ticker> tickers = getTickers();
        tickers.add(ticker);
        setTickers(tickers);
    }

    default void writeTNbt(NbtCompound nbt) {
        getTickers().forEach(ticker -> ticker.writeNbt(nbt));
    }

    default void readTNbt(NbtCompound nbt) {
        List<String> tickersNames = getDefaultTickers().getTickers().keySet().stream().toList();

        List<Ticker> tickers = new ArrayList<>();
        for (String s : tickersNames) {
            Ticker ticker = Ticker.readNbt(s, getDefaultTickers(), nbt);
            if (ticker != null) tickers.add(ticker);
        }
        setTickers(tickers);
    }
}
