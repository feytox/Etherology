package ru.feytox.etherology.util.feyapi;

public record RGBColor(int r, int g, int b) {
    public int asHex() {
        int rgb = r;
        rgb = (rgb << 8) + g;
        rgb = (rgb << 8) + b;
        return rgb;
    }
}
