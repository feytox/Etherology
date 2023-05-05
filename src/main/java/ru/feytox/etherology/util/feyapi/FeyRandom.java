package ru.feytox.etherology.util.feyapi;

import net.minecraft.util.math.random.Random;

@Deprecated
public class FeyRandom {
    /**
     * @return рандомный знак (+1 или -1)
     */
    public static int getValueSign(Random random) {
        return random.nextBetween(0, 1) == 0 ? -1 : 1;
    }
}
