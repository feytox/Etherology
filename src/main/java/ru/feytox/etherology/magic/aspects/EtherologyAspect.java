package ru.feytox.etherology.magic.aspects;

import org.apache.commons.lang3.StringUtils;

public interface EtherologyAspect {

    int TEXTURE_WIDTH = 320;
    int TEXTURE_HEIGHT = 192;

    String getAspectName();
    int getTextureRow();
    int getTextureColumn();

    default String getDisplayName() {
        return StringUtils.capitalize(getAspectName());
    }

    default int getTextureMinX() {
        return 32 + 32 * getTextureColumn();
    }

    default int getTextureMinY() {
        return 32 + 32 * getTextureRow();
    }

    default int getWidth() {
        return 32;
    }

    default int getHeight() {
        return 32;
    }
}
