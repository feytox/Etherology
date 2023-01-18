package name.uwu.feytox.etherology.util;

import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;
import java.util.List;

public class FeyNbtList<T extends Nbtable> {
    private final List<T> list;
    private String name = "feyNbtList";

    public FeyNbtList(List<T> list) {
        this.list = list;
    }

    public FeyNbtList(String name, List<T> list) {
        this(list);
        this.name = name;
    }

    public List<T> getList() {
        return list;
    }

    public void writeNbt(NbtCompound nbt) {
        NbtCompound subNbt = new NbtCompound();

        for (int i = 0; i < list.size(); i++) {
            T element = list.get(i);
            NbtCompound elementNbt = new NbtCompound();

            element.writeNbt(elementNbt);
            subNbt.put("element_" + i, elementNbt);
        }

        nbt.put(name, subNbt);
    }

    public static <M extends Nbtable> FeyNbtList<M> readFromNbt(String name, Class<M> cls, NbtCompound nbt) {
        try {
            M reader = cls.getConstructor().newInstance();

            NbtCompound subNbt = nbt.getCompound(name);

            List<M> resultList = new ArrayList<>();
            for (int i = 0; i < subNbt.getKeys().size(); i++) {
                NbtCompound elementNbt = subNbt.getCompound("element_" + i);

                try {
                    M element = (M) reader.readNbt(elementNbt);
                    resultList.add(element);
                } catch (Exception ignored) {
                }
            }

            return new FeyNbtList<>(name, resultList);
        } catch (Exception e) {
            return null;
        }
    }

    public static <M extends Nbtable> FeyNbtList<M> readFromNbt(Class<M> cls, NbtCompound nbt) {
        return readFromNbt("feyNbtList", cls, nbt);
    }
}
