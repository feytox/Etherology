package ru.feytox.etherology.magic.zones;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DefaultParticleType;
import ru.feytox.etherology.util.feyapi.EEquality;
import ru.feytox.etherology.util.feyapi.RGBColor;
import ru.feytox.etherology.util.nbt.Nbtable;

import java.util.Arrays;
import java.util.List;

import static ru.feytox.etherology.Etherology.*;

public enum EssenceZones implements Nbtable, EEquality {
    KETA(KETA_PARTICLE, new RGBColor(128, 205, 247), new RGBColor(105, 128, 231)),
    RELA(RELA_PARTICLE, new RGBColor(177, 229,106), new RGBColor(106, 182, 81)),
    CLOS(CLOS_PARTICLE, new RGBColor(106, 182, 81), new RGBColor(208, 158, 89)),
    VIA(VIA_PARTICLE, new RGBColor(248, 122, 95), new RGBColor(205, 58, 76)),
    NULL(null, null, null);

    private final RGBColor firstColor;
    private final RGBColor secondColor;
    private final DefaultParticleType particleType;


    EssenceZones(DefaultParticleType particleType, RGBColor firstColor, RGBColor secondColor) {
        this.particleType = particleType;
        this.firstColor = firstColor;
        this.secondColor = secondColor;
    }

    public RGBColor getFirstColor() {
        return firstColor;
    }

    public RGBColor getSecondColor() {
        return secondColor;
    }

    public DefaultParticleType getParticleType() {
        return particleType;
    }

    public static EssenceZones getByName(String name) {
        return EssenceZones.valueOf(name.toUpperCase());
    }

    public static EssenceZones getByNum(int zoneNum) {
        List<EssenceZones> zoneList = Arrays.stream(EssenceZones.values()).toList();
        return zoneNum >= zoneList.size() || zoneNum == 621 ? null : zoneList.get(zoneNum);
    }

    public int getZoneNum() {
        List<EssenceZones> zoneList = Arrays.stream(EssenceZones.values()).toList();
        for (int i = 0; i < zoneList.size(); i++) {
            if (zoneList.get(i).equals(this)) return i;
        }
        return 621;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        NbtCompound subNbt = new NbtCompound();
        subNbt.putString("zone_id", this.name());
        nbt.put("essence_zone", subNbt);
    }

    public static EssenceZones readFromNbt(NbtCompound nbt) {
        NbtCompound subNbt = nbt.getCompound("essence_zone");
        return EssenceZones.valueOf(subNbt.getString("zone_id"));
    }

    @Override
    public Nbtable readNbt(NbtCompound nbt) {
        return readFromNbt(nbt);
    }
}
