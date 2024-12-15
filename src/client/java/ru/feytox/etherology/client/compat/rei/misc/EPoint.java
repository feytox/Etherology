package ru.feytox.etherology.client.compat.rei.misc;

import me.shedaniel.math.Point;

public class EPoint extends Point {

    public EPoint(int x, int y) {
        super(x, y);
    }

    public EPoint add(int dx, int dy) {
        return new EPoint(x+dx, y+dy);
    }
}
