package ru.feytox.etherology.item.glints;

import lombok.Getter;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.math.Fraction;
import ru.feytox.etherology.mixin.BundleContentsComponentAccessor;
import ru.feytox.etherology.registry.misc.ComponentTypes;

import java.util.List;
import java.util.Optional;

import static ru.feytox.etherology.registry.item.EItems.ETHER_SHARD;

@Getter
public class GlintItem extends Item {

    private final float maxEther;

    public GlintItem(float maxEther) {
        super(new Settings().maxCount(1).component(ComponentTypes.STORED_ETHER, 0f));
        this.maxEther = maxEther;
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        float storedEther = getStoredEther(stack);
        int etherValue = MathHelper.floor(storedEther);

        int slots = MathHelper.floor(maxEther / 64);
        DefaultedList<ItemStack> defaultedList = DefaultedList.of();
        for (int i = 0; i < slots && etherValue > 0; i++) {
            int count = Math.min(64, etherValue);
            etherValue -= count;
            ItemStack etherStack = ETHER_SHARD.getDefaultStack();
            etherStack.setCount(count);
            defaultedList.add(etherStack);
        }

        BundleContentsComponent component = BundleContentsComponentAccessor.createBundleContentsComponent(defaultedList, Fraction.getFraction(MathHelper.floor(storedEther), MathHelper.floor(maxEther)));
        return Optional.of(new GlintTooltipData(component, MathHelper.floor(maxEther)));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        float storedEther = getStoredEther(stack);
        int etherValue = MathHelper.floor(storedEther);

        tooltip.add(Text.translatable("item.etherology.glint.fullness", etherValue, MathHelper.floor(getMaxEther())).formatted(Formatting.GRAY));
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        return MathHelper.clamp(Math.round(13.0f * getStoredEther(stack) / maxEther), 0, 13);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        float percent = Math.max(0.0F, (getStoredEther(stack) / maxEther));
        return MathHelper.hsvToRgb(percent / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        float ether = getStoredEther(stack);
        return ether > 0 && ether < maxEther;
    }

    public static Float getStoredEther(ItemStack stack) {
        return stack.getOrDefault(ComponentTypes.STORED_ETHER, 0.0f);
    }

    private static void setStoredEther(ItemStack stack, float value) {
        stack.set(ComponentTypes.STORED_ETHER, value);
    }

    /**
     * @return излишек, который не поместился в глинт
     */
    public static float increment(ItemStack stack, float maxEther, float value) {
        float storedEther = getStoredEther(stack);
        float newEther = Math.min(storedEther + value, maxEther);
        setStoredEther(stack, newEther);

        return value + storedEther - newEther;
    }

    /**
     * @return количество забранного эфира
     */
    public static float decrement(ItemStack stack, float value) {
        float storedEther = getStoredEther(stack);
        float newEther = Math.max(storedEther - value, 0);
        setStoredEther(stack, newEther);

        return storedEther - newEther;
    }
}
