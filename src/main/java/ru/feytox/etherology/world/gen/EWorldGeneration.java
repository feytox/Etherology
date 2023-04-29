package ru.feytox.etherology.world.gen;

public class EWorldGeneration {
    public static void generateWorldGen() {
        EssenceZonesGeneration.generateZones();
        EtherTreesGeneration.generateTrees();
    }
}
