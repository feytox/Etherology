package name.uwu.feytox.etherology.magic.zones;

import name.uwu.feytox.etherology.blocks.zone_blocks.ZoneBlock;
import name.uwu.feytox.etherology.util.feyapi.EEquality;
import name.uwu.feytox.etherology.util.feyapi.RGBColor;
import name.uwu.feytox.etherology.util.nbt.Nbtable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DefaultParticleType;

import java.util.Arrays;
import java.util.List;

import static name.uwu.feytox.etherology.BlocksRegistry.*;
import static name.uwu.feytox.etherology.Etherology.*;

public enum EssenceZones implements Nbtable, EEquality {
    KETA(KETA_ZONE_BLOCK, KETA_PARTICLE, new RGBColor(183, 0, 244), new RGBColor(15, 161, 207)),
    RELA(RELA_ZONE_BLOCK, RELA_PARTICLE, new RGBColor(83, 221,10), new RGBColor(240, 240, 20)),
    CLOS(CLOS_ZONE_BLOCK, CLOS_PARTICLE, new RGBColor(240, 240, 20), new RGBColor(255, 170, 170)),
    VIA(VIA_ZONE_BLOCK, VIA_PARTICLE, new RGBColor(182, 27, 21), new RGBColor(255, 170, 170)),
    NULL(null, null, null, null);

    private final ZoneBlock fillBlock;
    private final RGBColor firstColor;
    private final RGBColor secondColor;
    private final DefaultParticleType particleType;


    EssenceZones(ZoneBlock fillBlock, DefaultParticleType particleType, RGBColor firstColor, RGBColor secondColor) {
        this.fillBlock = fillBlock;
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

    public ZoneBlock getFillBlock() {
        return fillBlock;
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
