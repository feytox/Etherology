package ru.feytox.etherology.tickers;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class Tickers {
    private Map<String, Ticker> tickers = new HashMap<>();

    public Tickers() {}

    public Tickers register(Ticker... tickers) {
        for (Ticker ticker: tickers) {
            this.tickers.put(ticker.getName(), ticker);
        }
        return this;
    }

    @Nullable
    public Ticker get(String name) {
        return tickers.getOrDefault(name, null);
    }

    public Map<String, Ticker> getTickers() {
        return tickers;
    }
}
