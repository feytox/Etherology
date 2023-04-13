package ru.feytox.etherology.recipes.empower;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import ru.feytox.etherology.util.feyapi.EIdentifier;

import java.util.Map;
import java.util.Set;

public class EmpowerRecipeSerializer implements RecipeSerializer<EmpowerRecipe> {
    private EmpowerRecipeSerializer() {}

    public static final EmpowerRecipeSerializer INSTANCE = new EmpowerRecipeSerializer();
    public static final Identifier ID = new EIdentifier("empower_recipe");

    @Override
    public EmpowerRecipe read(Identifier id, JsonObject json) {
        Map<String, Ingredient> map = readSymbols(JsonHelper.getObject(json, "key"));
        String[] strings = removePadding(getPattern(JsonHelper.getArray(json, "pattern")));
        DefaultedList<Ingredient> gridInput = createPatternMatrix(strings, map);
        ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "result"));
        int heavenlyCount = JsonHelper.getInt(json, "relaCount", 0);
        int aquaticCount = JsonHelper.getInt(json, "viaCount", 0);
        int deepCount = JsonHelper.getInt(json, "closCount", 0);
        int terrestrialCount = JsonHelper.getInt(json, "ketaCount", 0);
        return new EmpowerRecipe(gridInput, heavenlyCount, aquaticCount, deepCount, terrestrialCount, output, id);
    }

    @Override
    public EmpowerRecipe read(Identifier id, PacketByteBuf buf) {
        DefaultedList<Ingredient> gridInput = DefaultedList.ofSize(9, Ingredient.EMPTY);
        gridInput.replaceAll(ignored -> Ingredient.fromPacket(buf));
        int relaCount = buf.readInt();
        int viaCount = buf.readInt();
        int closCount = buf.readInt();
        int ketaCount = buf.readInt();
        ItemStack output = buf.readItemStack();
        return new EmpowerRecipe(gridInput, relaCount, viaCount, closCount, ketaCount, output, id);
    }

    @Override
    public void write(PacketByteBuf buf, EmpowerRecipe recipe) {
        buf.writeCollection(recipe.gridInput(), (packetByteBuf, ingredient) -> ingredient.write(packetByteBuf));
        buf.writeInt(recipe.relaCount());
        buf.writeInt(recipe.viaCount());
        buf.writeInt(recipe.closCount());
        buf.writeInt(recipe.ketaCount());
        buf.writeItemStack(recipe.getOutput());
    }

    /**
     * from minecraft sources
     * but simplified
     */
    private static Map<String, Ingredient> readSymbols(JsonObject json) {
        Map<String, Ingredient> map = Maps.newHashMap();

        for (Map.Entry<String, JsonElement> stringJsonElementEntry : json.entrySet()) {
            String symbol = stringJsonElementEntry.getKey();
            if (symbol.length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + stringJsonElementEntry.getKey() + "' is an invalid symbol (must be 1 character only).");
            }

            if (" ".equals(symbol)) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }

            map.put(symbol, Ingredient.fromJson(stringJsonElementEntry.getValue()));
        }

        map.put(" ", Ingredient.EMPTY);
        return map;
    }

    private static String[] getPattern(JsonArray json) {
        String[] strings = new String[json.size()];
        if (strings.length > 3) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
        } else if (strings.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        } else {
            for(int i = 0; i < strings.length; ++i) {
                String string = JsonHelper.asString(json.get(i), "pattern[" + i + "]");
                if (string.length() > 3) {
                    throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
                }

                if (i > 0 && strings[0].length() != string.length()) {
                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                }

                strings[i] = string;
            }

            return strings;
        }
    }

    private static String[] removePadding(String... pattern) {
        int i = Integer.MAX_VALUE;
        int j = 0;
        int k = 0;
        int l = 0;

        for(int m = 0; m < pattern.length; ++m) {
            String string = pattern[m];
            i = Math.min(i, findFirstSymbol(string));
            int n = findLastSymbol(string);
            j = Math.max(j, n);
            if (n < 0) {
                if (k == m) {
                    ++k;
                }

                ++l;
            } else {
                l = 0;
            }
        }

        if (pattern.length == l) {
            return new String[0];
        } else {
            String[] strings = new String[pattern.length - l - k];

            for(int o = 0; o < strings.length; ++o) {
                strings[o] = pattern[o + k].substring(i, j + 1);
            }

            return strings;
        }
    }

    private static int findFirstSymbol(String line) {
        int i;
        for(i = 0; i < line.length() && line.charAt(i) == ' '; ++i) {
        }

        return i;
    }

    private static int findLastSymbol(String pattern) {
        int i;
        for(i = pattern.length() - 1; i >= 0 && pattern.charAt(i) == ' '; --i) {
        }

        return i;
    }

    private static DefaultedList<Ingredient> createPatternMatrix(String[] pattern, Map<String, Ingredient> symbols) {
        DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(3 * 3, Ingredient.EMPTY);
        Set<String> set = Sets.newHashSet((Iterable)symbols.keySet());
        set.remove(" ");

        for(int i = 0; i < pattern.length; ++i) {
            for(int j = 0; j < pattern[i].length(); ++j) {
                String string = pattern[i].substring(j, j + 1);
                Ingredient ingredient = symbols.get(string);
                if (ingredient == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + string + "' but it's not defined in the key");
                }

                set.remove(string);
                defaultedList.set(j + 3 * i, ingredient);
            }
        }

        if (!set.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
        } else {
            return defaultedList;
        }
    }
}