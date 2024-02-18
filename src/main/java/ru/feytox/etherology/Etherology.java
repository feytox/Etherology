package ru.feytox.etherology;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;
import ru.feytox.etherology.animation.PredicateAnimations;
import ru.feytox.etherology.animation.TriggerAnimations;
import ru.feytox.etherology.block.etherealGenerators.EtherealGeneratorDispenserBehavior;
import ru.feytox.etherology.commands.DevCommands;
import ru.feytox.etherology.magic.lens.RedstoneLensEffects;
import ru.feytox.etherology.magic.staff.StaffPatterns;
import ru.feytox.etherology.network.EtherologyNetwork;
import ru.feytox.etherology.registry.block.EBlockFamilies;
import ru.feytox.etherology.registry.block.EBlocks;
import ru.feytox.etherology.registry.custom.EtherologyRegistry;
import ru.feytox.etherology.registry.entity.EntityRegistry;
import ru.feytox.etherology.registry.item.EItemGroups;
import ru.feytox.etherology.registry.item.EItems;
import ru.feytox.etherology.registry.item.EtherEnchantments;
import ru.feytox.etherology.registry.util.*;
import ru.feytox.etherology.util.delayedTask.ServerTaskManager;
import ru.feytox.etherology.world.gen.EWorldGeneration;

public class Etherology implements ModInitializer {

    public static final Logger ELOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "etherology";
    public static final int GAME_ID;

    @Override
    public void onInitialize() {
        EItems.registerItems();
        EBlocks.registerAll();
        ResourceReloaders.registerServerData();
        EtherologyNetwork.registerPackets();
        EBlockFamilies.registerFamilies();
        DevCommands.register();
        EtherSounds.registerAll();
        PredicateAnimations.registerAll();
        TriggerAnimations.registerAll();
        EtherEnchantments.registerAll();
        RecipesRegistry.registerAll();
        ScreenHandlersRegistry.registerServerSide();
        EtherealGeneratorDispenserBehavior.register();
        EWorldGeneration.generateWorldGen();
        EtherologyRegistry.buildRegistry();
        StaffPatterns.registerAll();
        EItemGroups.registerAll();
        LootTablesModifyRegistry.registerAll();
        TradeOffersModificationRegistry.registerAll();
        EntityRegistry.registerServerSide();

        ServerTickEvents.END_SERVER_TICK.register(server -> ServerTaskManager.INSTANCE.tickTasks());
        ServerTickEvents.END_WORLD_TICK.register(world -> RedstoneLensEffects.getServerState(world).tick(world));
    }

    static {
        GAME_ID = Random.create().nextBetween(0, 999999999);
    }
}