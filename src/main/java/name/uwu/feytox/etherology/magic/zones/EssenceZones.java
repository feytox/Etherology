package name.uwu.feytox.etherology.magic.zones;

import name.uwu.feytox.etherology.blocks.zone_blocks.ZoneBlock;
import name.uwu.feytox.etherology.util.nbt.Nbtable;
import net.minecraft.nbt.NbtCompound;

import static name.uwu.feytox.etherology.BlocksRegistry.*;

public enum EssenceZones implements Nbtable {
    KETA(KETA_ZONE_BLOCK),
    RELA(RELA_ZONE_BLOCK),
    CLOS(CLOS_ZONE_BLOCK),
    VIA(VIA_ZONE_BLOCK),
    NULL(null);

    private final ZoneBlock fillBlock;

    EssenceZones(ZoneBlock fillBlock) {
        this.fillBlock = fillBlock;
    }

    public ZoneBlock getFillBlock() {
        return fillBlock;
    }

    public static EssenceZones getByName(String name) {
        return EssenceZones.valueOf(name.toUpperCase());
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
