package ru.feytox.etherology.util.future;

public class ColorHelper {
    /**
     * from Minecraft 1.20.1 code. Replace after update
     */
    public static class Abgr {
        public static int getAlpha(int abgr) {
            return abgr >>> 24;
        }

        public static int getRed(int abgr) {
            return abgr & 255;
        }

        public static int getGreen(int abgr) {
            return abgr >> 8 & 255;
        }

        public static int getBlue(int abgr) {
            return abgr >> 16 & 255;
        }

        public static int getBgr(int abgr) {
            return abgr & 16777215;
        }

        public static int toOpaque(int abgr) {
            return abgr | -16777216;
        }

        public static int getAbgr(int a, int b, int g, int r) {
            return a << 24 | b << 16 | g << 8 | r;
        }

        public static int withAlpha(int alpha, int bgr) {
            return alpha << 24 | bgr & 16777215;
        }
    }
}
