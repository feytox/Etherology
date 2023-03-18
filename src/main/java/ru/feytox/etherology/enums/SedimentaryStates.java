package ru.feytox.etherology.enums;

import net.minecraft.util.StringIdentifiable;
import ru.feytox.etherology.magic.zones.EssenceZones;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum SedimentaryStates implements StringIdentifiable {
    EMPTY(EssenceZones.NULL),
    KETA(EssenceZones.KETA),
    RELA(EssenceZones.RELA),
    CLOS(EssenceZones.CLOS),
    VIA(EssenceZones.VIA);

    private final EssenceZones zoneType;

    SedimentaryStates(EssenceZones zoneType) {
        this.zoneType = zoneType;
    }

    public EssenceZones getZoneType() {
        return zoneType;
    }

    @Override
    public String asString() {
        return this.name().toLowerCase();
    }

    public static SedimentaryStates getFromZone(EssenceZones zoneType) {
        Map<EssenceZones, SedimentaryStates> states = Arrays.stream(SedimentaryStates.values())
                .collect(Collectors.toMap(SedimentaryStates::getZoneType, Function.identity()));

        return states.getOrDefault(zoneType, EMPTY);
    }
}
