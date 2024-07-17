package ru.feytox.etherology;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import ru.feytox.etherology.commands.DevCommands;
import ru.feytox.etherology.magic.lens.LensModifier;
import ru.feytox.etherology.magic.lens.RedstoneLensEffects;
import ru.feytox.etherology.magic.staff.StaffPatterns;
import ru.feytox.etherology.network.EtherologyNetwork;
import ru.feytox.etherology.registry.block.EBlockFamilies;
import ru.feytox.etherology.registry.block.EBlocks;
import ru.feytox.etherology.registry.block.ExtraBlocksRegistry;
import ru.feytox.etherology.registry.entity.EntityRegistry;
import ru.feytox.etherology.registry.item.EItemGroups;
import ru.feytox.etherology.registry.item.EItems;
import ru.feytox.etherology.registry.misc.*;
import ru.feytox.etherology.registry.world.WorldGenRegistry;
import ru.feytox.etherology.util.delayedTask.ServerTaskManager;

public class Etherology implements ModInitializer {

    public static final Logger ELOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "etherology";
    public static final int GAME_ID;
    private static final ObjectArrayList<ServerWorld> loadedWorlds = new ObjectArrayList<>();

    @Override
    public void onInitialize() {
        ExtraBlocksRegistry.registerAll();
        EItems.registerItems();
        EBlocks.registerAll();
        ResourceReloaders.registerServerData();
        EtherologyNetwork.registerCommonSide();
        EBlockFamilies.registerFamilies();
        DevCommands.register();
        EtherSounds.registerAll();
        RecipesRegistry.registerAll();
        ScreenHandlersRegistry.registerServerSide();
        WorldGenRegistry.registerWorldGen();
        StaffPatterns.registerAll();
        EItemGroups.registerAll();
        LootTablesModifyRegistry.registerAll();
        TradeOffersModificationRegistry.registerAll();
        EntityRegistry.registerServerSide();
        LootConditions.registerAll();
        DispenserBehaviors.registerAll();
        EventsRegistry.registerGameEvents();
        LensModifier.registerAll();
        EffectsRegistry.registerAll();
        ComponentTypes.registerAll();

        ServerWorldEvents.LOAD.register((server, world) -> loadedWorlds.add(world));
        ServerWorldEvents.UNLOAD.register((server, world) -> loadedWorlds.remove(world));

        ServerTickEvents.END_SERVER_TICK.register(server -> ServerTaskManager.INSTANCE.tickTasks());
        ServerTickEvents.END_WORLD_TICK.register(world -> RedstoneLensEffects.getServerState(world).tick(world));
    }

    // TODO: 16.07.2024 consider using something else
    @Nullable
    public static ServerWorld getAnyServerWorld() {
        return loadedWorlds.isEmpty() ? null : loadedWorlds.getFirst();
    }

    static {
        GAME_ID = Random.create().nextBetween(0, 999999999);
    }
}