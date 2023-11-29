package ru.feytox.etherology.magic.lense;

import dev.onyxstudios.cca.api.v3.component.CopyableComponent;
import dev.onyxstudios.cca.api.v3.item.CcaNbtType;
import dev.onyxstudios.cca.api.v3.item.ItemComponent;
import lombok.val;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.EnumUtils;
import org.jetbrains.annotations.Nullable;
import ru.feytox.etherology.item.LensItem;
import ru.feytox.etherology.item.StaffItem;
import ru.feytox.etherology.magic.staff.StaffPart;
import ru.feytox.etherology.registry.util.EtherologyComponents;

public class LensComponent extends ItemComponent implements CopyableComponent<LensComponent> {

    @Nullable
    // TODO: 28.11.2023 do not save
    // TODO: 29.11.2023 or save???
    private Integer cachedCooldown;

    @Nullable
    private LensMode cachedMode;

    @Nullable
    private Boolean cachedEmpty;

    public LensComponent(ItemStack stack) {
        super(stack);
    }

    public int getCooldown() {
        if (cachedCooldown != null) return cachedCooldown;
        if (!hasTag("cooldown", CcaNbtType.INT)) putInt("cooldown", 0);
        cachedCooldown = getInt("cooldown");
        return cachedCooldown;
    }

    public LensMode getLensMode() {
        if (cachedMode != null) return cachedMode;
        if (!hasTag("mode", CcaNbtType.STRING)) putString("mode", LensMode.STREAM.name());
        cachedMode = EnumUtils.getEnumIgnoreCase(LensMode.class, getString("mode"));
        return cachedMode;
    }

    public void incrementCooldown(int value, int maxCooldown) {
        putInt("cooldown", Math.min(maxCooldown, getCooldown() + value));
        resetCache();
    }

    public void decrementCooldown(int value) {
        putInt("cooldown", Math.max(0, getCooldown() - value));
        resetCache();
    }

    public void setCooldown(int value) {
        if (value == getCooldown()) return;
        putInt("cooldown", value);
        resetCache();
    }

    public void setLensMode(LensMode newMode) {
        if (newMode.equals(LensMode.STREAM)) setCooldown(0);
        putString("mode", newMode.name());
        resetCache();
    }

    @Override
    public void onTagInvalidated() {
        super.onTagInvalidated();
        resetCache();
    }

    public void resetCache() {
        // TODO: 29.11.2023 рассмотреть раздельный ресет кэша, если необходимо
        cachedMode = null;
        cachedCooldown = null;
        cachedEmpty = null;
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
    public void copyFrom(LensComponent source) {
        cachedCooldown = source.cachedCooldown;
        cachedMode = source.cachedMode;
        cachedEmpty = source.cachedEmpty;
    }
}
