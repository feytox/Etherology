package ru.feytox.etherology.util.future;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Environment(EnvType.CLIENT)
public class ESprite {
    private final Identifier id;
    private final Resource resource;
    private final AtomicReference<NativeImage> image = new AtomicReference<>();
    private final AtomicInteger regionCount;

    public ESprite(Identifier id, Resource resource, int regionCount) {
        this.id = id;
        this.resource = resource;
        this.regionCount = new AtomicInteger(regionCount);
    }

    public NativeImage read() throws IOException {
        NativeImage nativeImage = image.get();
        if (nativeImage != null) return nativeImage;

        synchronized (this) {
            nativeImage = image.get();
            if (nativeImage != null) return nativeImage;

            try (InputStream inputStream = resource.getInputStream()) {
                NativeImage spriteImage = NativeImage.read(inputStream);
                image.set(spriteImage);
                return spriteImage;
            } catch (IOException e) {
                throw new IOException("Failed to load image " + this.id, e);
            }
        }
    }

    public void close() {
        int i = this.regionCount.decrementAndGet();
        if (i > 0) return;

        NativeImage nativeImage = this.image.getAndSet(null);
        if (nativeImage != null) nativeImage.close();
    }
}
