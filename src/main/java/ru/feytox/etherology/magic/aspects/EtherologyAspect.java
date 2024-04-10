package ru.feytox.etherology.magic.aspects;

public interface EtherologyAspect {

    int TEXTURE_WIDTH = 320;
    int TEXTURE_HEIGHT = 192;

    String getAspectName();
    int getTextureRow();
    int getTextureColumn();

    default int getTextureMinX() {
        return 32 + 32 * getTextureColumn();
    }

    default int getTextureMaxX() {
        return getTextureMinX() + 31;
    }

    default int getTextureMinY() {
        return 32 + 32 * getTextureRow();
    }

    default int getTextureMaxY() {
        return getTextureMinY() + 31;
    }
}
