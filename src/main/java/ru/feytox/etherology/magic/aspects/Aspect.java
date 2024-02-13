package ru.feytox.etherology.magic.aspects;

public enum Aspect implements EtherologyAspect {
    RELLA(0, 0),
    ETHA(0, 1),
    DIZORD(0, 2),
    VACUO(0, 3),
    NETHA(0, 4),
    GRAVIA(0, 5),
    MOUNTA(0, 6),
    SOWORDA(0, 7),
    CLOS(1, 0),
    ENN(1, 1),
    ANEMA(1, 2),
    VIBRA(1, 3),
    MATERRA(1, 4),
    SOLISTA(1, 5),
    DEFENTA(1, 6),
    FELKA(1, 7),
    VIA(2, 0),
    FLIMA(2, 1),
    AREA(2, 2),
    CHAOS(2, 3),
    GEMA(2, 4),
    DOGMA(2, 5),
    HENDALL(2, 6),
    STRALFA(2, 7),
    KETA(3, 0),
    MORA(3, 1),
    MEMO(3, 2),
    DEVO(3, 3),
    SECRA(3, 4),
    ISKIL(3, 5),
    ALCHEMA(3, 6),
    GROSEAL(3, 7);

    private final int textureRow;
    private final int textureColumn;

    Aspect(int textureRow, int textureColumn) {
        this.textureRow = textureRow;
        this.textureColumn = textureColumn;
    }

    @Override
    public String getAspectName() {
        return this.name().toLowerCase();
    }

    @Override
    public int getTextureRow() {
        return textureRow;
    }

    @Override
    public int getTextureColumn() {
        return textureColumn;
    }
}
