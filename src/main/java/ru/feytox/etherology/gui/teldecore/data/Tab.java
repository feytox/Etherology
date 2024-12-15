package ru.feytox.etherology.gui.teldecore.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import ru.feytox.etherology.gui.teldecore.content.AbstractContent;
import ru.feytox.etherology.util.misc.Color;

import java.util.List;

public record Tab(int tabId, Identifier icon, String titleKey, Color color, boolean show,
                  List<AbstractContent> contents, ResearchTree tree) {

    public static final Codec<Tab> CODEC;

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("tab_id").forGetter(t -> t.tabId),
                Identifier.CODEC.fieldOf("icon").forGetter(t -> t.icon),
                Codec.STRING.fieldOf("title").forGetter(t -> t.titleKey),
                Color.CODEC.fieldOf("color").forGetter(t -> t.color),
                Codec.BOOL.optionalFieldOf("show", true).forGetter(t -> t.show),
                Chapter.CONTENT_CODEC.listOf().fieldOf("content").forGetter(t -> t.contents),
                ResearchTree.CODEC.fieldOf("chapters").forGetter(t -> t.tree)
        ).apply(instance, Tab::new));
    }
}
