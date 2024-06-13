package ru.feytox.etherology.magic.lens;

import dev.onyxstudios.cca.api.v3.item.CcaNbtType;
import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import lombok.val;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.apache.commons.lang3.EnumUtils;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.Etherology;
import ru.feytox.etherology.item.LensItem;
import ru.feytox.etherology.item.StaffItem;
import ru.feytox.etherology.magic.staff.StaffPart;
import ru.feytox.etherology.registry.misc.EtherologyComponents;

public class LensComponent extends ItemComponent {

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

    @Nullable
    private LensModifiersData cachedModifiers;

    public LensComponent(ItemStack stack) {
        super(stack);
    }

    public long getCooldown(World world) {
        return getEndTick() - world.getTime();
    }

    public boolean checkCooldown(World world) {
        if (getEndTick() != -1) return getCooldown(world) <= 0;
        setEndTick(world.getTime());
        return true;
    }

    private long getEndTick() {
        if (cachedEndTick != null) return cachedEndTick;
        if (!hasTag("end_tick", CcaNbtType.LONG)) putLong("end_tick", -1);
        cachedEndTick = getLong("end_tick");
        return cachedEndTick;
    }

    @Deprecated // huh?
    public int getCharge() {
        if (cachedCharge != null) return cachedCharge;
        if (!hasTag("charge", CcaNbtType.INT)) putInt("charge", 0);
        cachedCharge = getInt("charge");
        return cachedCharge;
    }

    @Deprecated
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

    public int getLevel(LensModifier lensModifier) {
        return getModifiers().getLevel(lensModifier);
    }

    /**
     * @param lensModifier The lens modifier, used to calculate the value.
     * @param start The value at level 0.
     * @param end The value at the highest level.
     * @param modifier The modifier from 0 to 1, representing how fast the value grows with level. A smaller number means faster growth.
     * @return The calculated value rounded to the nearest integer.
     */
    public int calcRoundValue(LensModifier lensModifier, int start, int end, float modifier) {
        return Math.round(calcValue(lensModifier, start, end, modifier));
    }

    /**
     * @param lensModifier The lens modifier, used to calculate the value.
     * @param start The value at level 0.
     * @param end The value at the highest level.
     * @param modifier The modifier from 0 to 1, representing how fast the value grows with level. A smaller number means faster growth.
     * @return The calculated value.
     */
    public float calcValue(LensModifier lensModifier, float start, float end, float modifier) {
        int level = getLevel(lensModifier);
        return (float) (end - (end - start) * Math.pow(modifier, level));
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

    @Deprecated
    public void refreshGameId() {
        if (isCurrentGameId()) return;
        putInt("game_id", Etherology.GAME_ID);
        resetCache();
    }

    public LensModifiersData getModifiers() {
        if (cachedModifiers != null) return cachedModifiers;
        if (!hasTag("modifiers", CcaNbtType.COMPOUND)) putCompound("modifiers", LensModifiersData.empty().writeNbt());
        cachedModifiers = LensModifiersData.readNbt(getCompound("modifiers"));
        return cachedModifiers;
    }

    public void setModifiers(LensModifiersData modifiers) {
        putCompound("modifiers", modifiers.writeNbt());
        resetCache();
    }

    public LensPattern getPattern() {
        if (cachedPattern != null) return cachedPattern.clone();
        if (!hasTag("pattern", CcaNbtType.COMPOUND)) putCompound("pattern", LensPattern.empty().writeNbt());
        cachedPattern = LensPattern.readNbt(getCompound("pattern"));
        if (cachedPattern == null) throw new IllegalStateException("Failed to read lens pattern");
        return cachedPattern.clone();
    }

    public void setPattern(LensPattern pattern) {
        if (pattern.equals(getPattern())) return;
        putCompound("pattern", pattern.writeNbt());
        resetCache();
    }

    public void incrementCooldown(World world, long cooldown) {
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
        // TODO: 29.11.2023 consider to split cache resetting
        cachedMode = null;
        cachedEndTick = null;
        cachedEmpty = null;
        cachedGameId = null;
        cachedCharge = null;
        cachedPattern = null;
        cachedModifiers = null;
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
}
