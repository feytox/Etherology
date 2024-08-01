package ru.feytox.etherology.gui.teldecore.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.NoSuchElementException;

public class ChapterGrid {

    private static final Codec<ChapterInfo> INFO_CODEC;
    public static final Codec<ChapterGrid> CODEC;
    private final List<ChapterInfo> info;
    @Nullable
    private final ChapterNode rootNode;

    private ChapterGrid(List<ChapterInfo> info) {
        this.info = info;
        this.rootNode = createRoot(info);
    }

    @Nullable
    private static ChapterNode createRoot(List<ChapterInfo> info) {
        // we don't need to create nodes on server
        if (FabricLoader.getInstance().getEnvironmentType().equals(EnvType.SERVER)) return null;

        List<ChapterInfo> roots = info.stream().filter(data -> data.after().isEmpty()).toList();
        if (roots.isEmpty()) throw new NoSuchElementException("Could not found root chapter for chapter grid.");
        if (roots.size() > 1) throw new IllegalArgumentException("Expected 1 root for chapter grid, found: " + roots.size());

        ChapterNode root = ChapterNode.of(roots.getFirst());
        info.stream().filter(data -> !data.after().isEmpty())
                .forEach(data -> {
                    ChapterNode node = ChapterNode.of(data);
                    data.after.forEach(id -> {
                        if (root.pushChild(id, node)) return;
                        throw new IllegalArgumentException("Failed to find parent-chapter %s for chapter %s. Try to define children below their parents.".formatted(id, node.id));
                    });
                });
        return root;
    }

    private record ChapterInfo(Identifier id, List<Identifier> after, boolean arrow) {}

    private record ChapterNode(Identifier id, List<ChapterNode> children, boolean arrow) {

        static ChapterNode of(ChapterInfo info) {
            return new ChapterNode(info.id, new ObjectArrayList<>(), info.arrow);
        }

        boolean pushChild(Identifier target, ChapterNode child) {
            if (id.equals(target)) {
                children.add(child);
                return true;
            }
            for (ChapterNode node : children) {
                if (node.pushChild(target, child)) return true;
            }
            return false;
        }
    }

    static {
        INFO_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("id").forGetter(ChapterInfo::id),
                Identifier.CODEC.listOf().optionalFieldOf("after", new ObjectArrayList<>()).forGetter(ChapterInfo::after),
                Codec.BOOL.optionalFieldOf("arrow", true).forGetter(ChapterInfo::arrow)
        ).apply(instance, ChapterInfo::new));
        CODEC = INFO_CODEC.listOf().xmap(ChapterGrid::new, grid -> grid.info);
    }
}
