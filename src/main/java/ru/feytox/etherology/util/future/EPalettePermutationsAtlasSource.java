package ru.feytox.etherology.util.future;

import com.google.common.base.Suppliers;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.val;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.texture.SpriteDimensions;
import net.minecraft.client.texture.atlas.AtlasSource;
import net.minecraft.client.texture.atlas.AtlasSourceType;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import ru.feytox.etherology.mixin.NativeImageAccessor;
import ru.feytox.etherology.util.feyapi.EIdentifier;

import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;

@Getter
@Environment(EnvType.CLIENT)
public class EPalettePermutationsAtlasSource implements AtlasSource {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceFinder RESOURCE_FINDER = new ResourceFinder("textures", ".png");

    public static final Codec<EPalettePermutationsAtlasSource> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.list(Identifier.CODEC).fieldOf("textures").forGetter(EPalettePermutationsAtlasSource::getTextures),
                            Identifier.CODEC.fieldOf("palette_key").forGetter(EPalettePermutationsAtlasSource::getPaletteKey),
                            Codec.unboundedMap(Codec.STRING, Identifier.CODEC).fieldOf("permutations").forGetter(EPalettePermutationsAtlasSource::getPermutations))
            .apply(instance, EPalettePermutationsAtlasSource::new));

    private final List<Identifier> textures;
    private final Map<String, Identifier> permutations;
    private final Identifier paletteKey;

    private EPalettePermutationsAtlasSource(List<Identifier> textures, Identifier paletteKey, Map<String, Identifier> permutations) {
        this.textures = textures;
        this.permutations = permutations;
        this.paletteKey = paletteKey;
    }

    public EPalettePermutationsAtlasSource() {
        // TODO: 04.09.2023 load from json
        this.textures = ObjectArrayList.of(
                new EIdentifier("trims/textures/staff_style_aristocrat"),
                new EIdentifier("trims/textures/staff_style_astronomy"),
                new EIdentifier("trims/textures/staff_style_heavenly"),
                new EIdentifier("trims/textures/staff_style_ocular"),
                new EIdentifier("trims/textures/staff_style_ritual"),
                new EIdentifier("trims/textures/staff_style_royal"),
                new EIdentifier("trims/textures/staff_style_traditional")
        );
        this.permutations = Map.of("iron", new EIdentifier("trims/palettes/iron"),
                "gold", new EIdentifier("trims/palettes/gold"),
                "netherite", new EIdentifier("trims/palettes/netherite"),
                "copper", new EIdentifier("trims/palettes/copper"),
                "azel", new EIdentifier("trims/palettes/azel"),
                "telder", new EIdentifier("trims/palettes/telder"),
                "ethril", new EIdentifier("trims/palettes/ethril"));
        this.paletteKey = new EIdentifier("trims/palettes/trim_palette");
    }

    public void load(ResourceManager resourceManager, AtlasSource.SpriteRegions regions) {
        Supplier<int[]> paletteKeyGetter = Suppliers.memoize(() -> loadPalette(resourceManager, paletteKey));
        Map<String, Supplier<IntUnaryOperator>> palettes = new Object2ObjectOpenHashMap<>();

        permutations.forEach((name, permutationId) -> palettes.put(name, Suppliers.memoize(() -> combinePalette(paletteKeyGetter.get(), loadPalette(resourceManager, permutationId)))));

        textures.forEach(textureId -> {
            Identifier texturePath = RESOURCE_FINDER.toResourcePath(textureId);
            Optional<Resource> optionalResource = resourceManager.getResource(texturePath);
            if (optionalResource.isEmpty()) {
                LOGGER.warn("Unable to find texture {}", texturePath);
                return;
            }

            ESprite sprite = new ESprite(texturePath, optionalResource.get(), palettes.size());
            palettes.forEach((name, operator) -> {
                Identifier coloredTextureId = textureId.withPath(textureId.getPath() + "_" + name);
                regions.add(coloredTextureId, new PalettedSpriteRegion(sprite, operator, coloredTextureId));
            });
        });

        LOGGER.info("Successfully combined {} textures with {} permutations", textures.size(), permutations.size());
    }

    private static IntUnaryOperator combinePalette(int[] is, int[] js) {
        if (js.length != is.length) {
            LOGGER.warn("Palette mapping has different sizes: {} and {}", is.length, js.length);
            throw new IllegalArgumentException();
        } else {
            Int2IntMap int2IntMap = new Int2IntOpenHashMap(js.length);

            for(int i = 0; i < is.length; ++i) {
                int j = is[i];
                if (ColorHelper.Abgr.getAlpha(j) != 0) {
                    int2IntMap.put(ColorHelper.Abgr.getBgr(j), js[i]);
                }
            }

            return (ix) -> {
                int j = ColorHelper.Abgr.getAlpha(ix);
                if (j == 0) {
                    return ix;
                } else {
                    int k = ColorHelper.Abgr.getBgr(ix);
                    int l = int2IntMap.getOrDefault(k, ColorHelper.Abgr.toOpaque(k));
                    int m = ColorHelper.Abgr.getAlpha(l);
                    return ColorHelper.Abgr.withAlpha(j * m / 255, l);
                }
            };
        }
    }

    public static int[] loadPalette(ResourceManager resourceManager, Identifier identifier) {
        Optional<Resource> optional = resourceManager.getResource(RESOURCE_FINDER.toResourcePath(identifier));
        if (optional.isEmpty()) {
            LOGGER.error("Failed to load palette image {}", identifier);
            throw new IllegalArgumentException();
        }

        try (InputStream inputStream = optional.get().getInputStream()) {
            try (NativeImage image = NativeImage.read(inputStream)) {
                return copyPixelsRgba(image);
            }

        } catch (Exception e) {
            LOGGER.error("Couldn't load texture {}", identifier, e);
            throw new IllegalArgumentException();
        }
    }

    public AtlasSourceType getType() {
        return EtherAtlasSources.PALETTED_PERMUTATIONS;
    }

    private static int[] copyPixelsRgba(NativeImage image) throws IllegalArgumentException {
        if (image.getFormat() != NativeImage.Format.RGBA) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "getPixelsRGBA only works on RGBA images; have %s", image.getFormat()));
        } else {
            val accessor = ((NativeImageAccessor) (Object) image);
            accessor.callCheckAllocated();
            int size = image.getWidth() * image.getHeight();
            int[] pixels = new int[size];
            MemoryUtil.memIntBuffer(accessor.getPointer(), size).get(pixels);
            return pixels;
        }
    }

    private static NativeImage applyToCopy(NativeImage image, IntUnaryOperator operator) {
        if (image.getFormat() != NativeImage.Format.RGBA) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "function application only works on RGBA images; have %s", image.getFormat()));
        }

        val accessor = ((NativeImageAccessor) (Object) image);
        accessor.callCheckAllocated();
        NativeImage result = new NativeImage(image.getWidth(), image.getHeight(), false);
        int size = image.getWidth() * image.getHeight();

        IntBuffer intBuffer = MemoryUtil.memIntBuffer(accessor.getPointer(), size);
        IntBuffer intBuffer2 = MemoryUtil.memIntBuffer(((NativeImageAccessor) (Object) result).getPointer(), size);

        for(int j = 0; j < size; ++j) {
            intBuffer2.put(j, operator.applyAsInt(intBuffer.get(j)));
        }

        return result;
    }

    @Environment(EnvType.CLIENT)
    record PalettedSpriteRegion(ESprite baseImage, Supplier<IntUnaryOperator> palette, Identifier permutationLocation) implements AtlasSource.SpriteRegion {


        @Nullable
        public SpriteContents get() {
            SpriteContents result;
            try {
                NativeImage image = applyToCopy(baseImage.read(), palette.get());
                result = new SpriteContents(permutationLocation, new SpriteDimensions(image.getWidth(), image.getHeight()), image, AnimationResourceMetadata.EMPTY);
                return result;

            } catch (IllegalArgumentException | IOException e) {
                LOGGER.error("unable to apply palette to {}", this.permutationLocation, e);
                result = null;
            } finally {
                baseImage.close();
            }

            return result;
        }

        public void close() {
            this.baseImage.close();
        }
    }
}
