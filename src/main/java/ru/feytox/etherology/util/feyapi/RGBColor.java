package ru.feytox.etherology.util.feyapi;

public record RGBColor(int r, int g, int b) {
    public int asHex() {
        return r << 16 | g << 8 | b;
    }

    public static RGBColor of(int hex) {
        int r = (hex >> 16) & 0xFF;
        int g = (hex >> 8) & 0xFF;
        int b = hex & 0xFF;
        return new RGBColor(r, g, b);
    }
}
