package ru.feytox.etherology.util.misc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.component.DataComponentType;
import net.minecraft.item.ItemStack;

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

    public <T> ItemData<C> set(T value, BiFunction<C, T, C> withFunc) {
        component = withFunc.apply(component, value);
        return this;
    }

    public void save() {
        stack.set(componentType, component);
    }
}
