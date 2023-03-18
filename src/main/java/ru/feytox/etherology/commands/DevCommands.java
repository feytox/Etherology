package ru.feytox.etherology.commands;

import com.mojang.brigadier.arguments.FloatArgumentType;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.entity.player.PlayerEntity;
import ru.feytox.etherology.EtherologyComponents;
import ru.feytox.etherology.components.IFloatComponent;
import ru.feytox.etherology.util.feyapi.EIdentifier;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DevCommands {
    public static void register() {
        ArgumentTypeRegistry.registerArgumentType(new EIdentifier("component"),
                ComponentArgumentType.class, ConstantArgumentSerializer.of(ComponentArgumentType::component));

        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) ->
                dispatcher.register(literal("feytox")
                        .then(literal("set")
                                .then(argument("component name", ComponentArgumentType.component())
                                        .then(argument("new value", FloatArgumentType.floatArg())
                                                .executes(context -> {
                                                    String componentName = context.getInput().split(" ")[2];
                                                    float value = Float.parseFloat(context.getInput().split(" ")[3]);
                                                    PlayerEntity player = context.getSource().getPlayer();
                                                    if (player != null) {
                                                        ComponentKey<IFloatComponent> componentKey =
                                                                EtherologyComponents.fromString(componentName);

                                                        if (componentKey != null) {
                                                            componentKey.get(player).setValue(value);
                                                        }
                                                    }

                                                    return 1;
                                                })))))));
    }
}
