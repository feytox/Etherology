package ru.feytox.etherology.magic.lense;

import dev.onyxstudios.cca.api.v3.component.CopyableComponent;
import dev.onyxstudios.cca.api.v3.item.CcaNbtType;
import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import lombok.val;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.apache.commons.lang3.EnumUtils;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.item.LensItem;
import ru.feytox.etherology.item.StaffItem;
import ru.feytox.etherology.magic.staff.StaffPart;
import ru.feytox.etherology.registry.util.EtherologyComponents;

public class LensComponent extends ItemComponent implements CopyableComponent<LensComponent> {

    @Nullable
    private Long cachedEndTick;

    @Nullable
    private Integer cachedCharge;

    @Nullable
    private LensMode cachedMode;

    @Nullable
    private Integer cachedGameId;

    @Nullable
    private Boolean cachedEmpty;

    @Nullable
    private LensPattern cachedPattern;

    public LensComponent(ItemStack stack) {
        super(stack);
    }

    public boolean checkCooldown(ServerWorld world) {
        if (getEndTick() != -1) return getEndTick() <= world.getTime();
        setEndTick(world.getTime());
        return true;
    }

    private long getEndTick() {
        if (cachedEndTick != null) return cachedEndTick;
        if (!hasTag("end_tick", CcaNbtType.LONG)) putLong("end_tick", -1);
        cachedEndTick = getLong("end_tick");
        return cachedEndTick;
    }

    public int getCharge() {
        if (cachedCharge != null) return cachedCharge;
        if (!hasTag("charge", CcaNbtType.INT)) putInt("charge", 0);
        cachedCharge = getInt("charge");
        return cachedCharge;
    }

    public void setCharge(int amount) {
        putInt("charge", amount);
        resetCache();
    }

    public LensMode getLensMode() {
        if (cachedMode != null) return cachedMode;
        if (!hasTag("mode", CcaNbtType.STRING)) putString("mode", LensMode.STREAM.name());
        cachedMode = EnumUtils.getEnumIgnoreCase(LensMode.class, getString("mode"));
        return cachedMode;
    }

    public int getGameId() {
        if (cachedGameId != null) return cachedGameId;
        if (!hasTag("game_id", CcaNbtType.INT)) putInt("game_id", Etherology.GAME_ID);
        cachedGameId = getInt("game_id");
        return cachedGameId;
    }

    public boolean isCurrentGameId() {
        return getGameId() == Etherology.GAME_ID;
    }

    public void refreshGameId() {
        if (isCurrentGameId()) return;
        putInt("game_id", Etherology.GAME_ID);
        resetCache();
    }

    public LensPattern getPattern() {
        if (cachedPattern != null) return cachedPattern.copy();
        if (!hasTag("pattern", CcaNbtType.COMPOUND)) putCompound("pattern", LensPattern.empty().writeNbt());
        cachedPattern = LensPattern.readNbt(getCompound("pattern"));
        if (cachedPattern == null) throw new IllegalStateException("Failed to read lens pattern");
        return cachedPattern.copy();
    }

    public void setPattern(LensPattern pattern) {
        if (pattern.equals(getPattern())) return;
        putCompound("pattern", pattern.writeNbt());
        resetCache();
    }

    public void incrementCooldown(ServerWorld world, long cooldown) {
        if (checkCooldown(world)) putLong("end_tick", world.getTime() + cooldown);
        else putLong("end_tick", getEndTick() + cooldown);
        resetCache();
    }

    private void setEndTick(long value) {
        if (value == getEndTick()) return;
        putLong("end_tick", value);
        resetCache();
    }

    public void setLensMode(LensMode newMode) {
        if (newMode.equals(LensMode.STREAM)) setEndTick(-1);
        putString("mode", newMode.name());
        resetCache();
    }

    @Override
    public void onTagInvalidated() {
        super.onTagInvalidated();
        resetCache();
    }

    private void resetCache() {
        // TODO: 29.11.2023 рассмотреть раздельный ресет кэша, если необходимо
        cachedMode = null;
        cachedEndTick = null;
        cachedEmpty = null;
        cachedGameId = null;
        cachedCharge = null;
        cachedPattern = null;
    }

    public boolean isEmpty() {
        if (cachedEmpty != null) return cachedEmpty;
        cachedEmpty = checkEmpty();
        return cachedEmpty;
    }

    private boolean checkEmpty() {
        if (stack.getItem() instanceof LensItem) return false;
        if (!(stack.getItem() instanceof StaffItem)) return false;
        val staff = EtherologyComponents.STAFF.get(stack);
        val lensInfo = staff.getPartInfo(StaffPart.LENS);
        return lensInfo == null;
    }

    @Override
    public void copyFrom(LensComponent other) {
        setEndTick(other.getEndTick());
        setLensMode(other.getLensMode());
        setCharge(other.getCharge());
        refreshGameId();
    }
}
