package ru.feytox.etherology.magic.ether;

/**
 * Blocks, that should display ether points in Revelation View, should implement this.
 */
public interface EtherDisplay extends EtherStorage {

    float getDisplayEther();
    float getDisplayMaxEther();
}
