package ru.feytox.etherology.magic.ether;

public interface EtherPipe extends EtherStorage {
    @Override
    default boolean canOutputTo(EtherStorage consumer) {
        return true;
    }
}
