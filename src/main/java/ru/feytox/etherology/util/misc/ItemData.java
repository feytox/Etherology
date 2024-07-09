package ru.feytox.etherology.util.misc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.component.DataComponentType;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.function.TriFunction;
import org.slf4j.helpers.CheckReturnValue;

import java.util.function.BiFunction;

@AllArgsConstructor
public class ItemData<C> {

    private final ItemStack stack;
    private final DataComponentType<C> componentType;
    @Getter
    private C component;

    public static <C> ItemData<C> of(ItemStack stack, DataComponentType<C> componentType) {
        return new ItemData<>(stack, componentType, stack.get(componentType));
    }

    @CheckReturnValue
    public <T> ItemData<C> set(T value, BiFunction<C, T, C> withFunc) {
        component = withFunc.apply(component, value);
        return this;
    }

    @CheckReturnValue
    public <T, M> ItemData<C> set(T value1, M value2, TriFunction<C, T, M, C> func) {
        component = func.apply(component, value1, value2);
        return this;
    }

    public ItemData<C> save() {
        stack.set(componentType, component);
        return this;
    }
}
