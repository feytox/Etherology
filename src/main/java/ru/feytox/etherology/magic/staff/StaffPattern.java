package ru.feytox.etherology.magic.staff;

import com.google.common.base.Suppliers;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.function.Supplier;

// TODO: 26.07.2024 move from enums to data-driven patterns
public interface StaffPattern {

    EmptyPattern EMPTY = new EmptyPattern();

    String getName();

    default boolean isEmpty() {
        return this instanceof EmptyPattern;
    }

    @SafeVarargs
    static <T extends StaffPattern> Supplier<List<? extends StaffPattern>> memoize(T... values) {
        if (values.length == 0) return Suppliers.memoize(ObjectArrayList::of);
        return Suppliers.memoize(() -> ObjectArrayList.of(values));
    }

    class EmptyPattern implements StaffPattern {

        @Override
        public String getName() {
            return "empty";
        }
    }
}
