package ru.feytox.etherology.client.compat.rei.misc;

import me.shedaniel.rei.api.common.entry.EntryStack;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import ru.feytox.etherology.client.compat.rei.EtherREIPlugin;
import ru.feytox.etherology.magic.aspects.Aspect;

public record AspectPair(Aspect aspect, int value) {

    public static EntryStack<AspectPair> entry(Aspect aspect, int value) {
        return EntryStack.of(EtherREIPlugin.ASPECT_ENTRY, new AspectPair(aspect, value));
    }

    public AspectPair normalize() {
        return new AspectPair(aspect, 1);
    }

    public boolean aspectEquals(AspectPair that) {
        return aspect.equals(that.aspect);
    }

    public int aspectHash() {
        return new HashCodeBuilder(17, 37).append(aspect).toHashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AspectPair that)) return false;

        return new EqualsBuilder().append(value, that.value).append(aspect, that.aspect).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(aspect).append(value).toHashCode();
    }
}
