package ru.feytox.etherology.tickers;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class Ticker {
    protected final String name;
    protected final Consumer<World> action;
    protected int ticks;
    protected final int momentTicks;
    protected boolean isActive;
    protected boolean isLoop;

    public Ticker(String name, Consumer<World> action, int ticks, int momentTicks, boolean isLoop) {
        this.name = name;
        this.action = action;
        this.ticks = ticks;
        this.momentTicks = momentTicks;
        this.isActive = ticks != -1337;
        this.isLoop = isLoop;
    }

    public Ticker(String name, Consumer<World> action, int momentTicks, boolean isLoop) {
        this(name, action, 0, momentTicks, isLoop);
    }

    public void tick(World world) {
        if (!isActive) return;

        ticks++;
        if (ticks < momentTicks) return;
        action.accept(world);

        if (!isLoop) isActive = false;
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putInt(name + "_ticks", isActive ? ticks : -1337);
    }

    @Nullable
    public static Ticker readNbt(String name, Tickers tickers, NbtCompound nbt) {
        int ticks = nbt.getInt(name + "_ticks");
        Ticker base = tickers.get("name");
        return base == null ? null : base.copy(ticks);
    }

    public Ticker copy(int ticks) {
        return new Ticker(name, action, ticks, momentTicks, isLoop);
    }

    public String getName() {
        return name;
    }
}
