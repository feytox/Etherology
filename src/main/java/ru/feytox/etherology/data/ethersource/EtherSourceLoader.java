package ru.feytox.etherology.data.ethersource;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import ru.feytox.etherology.util.misc.EIdentifier;

import java.util.HashMap;
import java.util.Map;

import static ru.feytox.etherology.Etherology.ELOGGER;

public class EtherSourceLoader extends JsonDataLoader implements IdentifiableResourceReloadListener {

    // TODO: 11.07.2024 consider using codecs... before it's too late :skull:

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    public static final EtherSourceLoader INSTANCE = new EtherSourceLoader();
    private Map<Identifier, Float> etherItems = ImmutableMap.of();
    private boolean loaded = false;

    private EtherSourceLoader() {
        super(GSON, "ether_sources");
    }

    public Map<Identifier, Float> getEtherItems() {
        if (!loaded) {
            ELOGGER.error("EtherSources didn't reloaded!");
            return new HashMap<>();
        }
        return etherItems;
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        ImmutableMap.Builder<Identifier, Float> builder = ImmutableMap.builder();

        prepared.forEach((id, json) -> {
            try {
                Map<Identifier, Float> result = EtherSourcesDeserializer.INSTANCE.deserialize(json);
                builder.putAll(result);
            } catch (Exception e) {
                ELOGGER.error("Couldn't parse EtherSources from {}", id, e);
            }
        });

        etherItems = builder.build();
        loaded = true;
    }

    @Override
    public Identifier getFabricId() {
        return EIdentifier.of("ether_sources");
    }
}
