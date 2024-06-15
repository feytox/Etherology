package ru.feytox.etherology.commands;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import lombok.val;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import ru.feytox.etherology.magic.ether.EtherComponent;
import ru.feytox.etherology.registry.misc.EtherologyComponents;

import java.util.function.BiConsumer;
import java.util.function.Function;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DevCommands {

    public static void register() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) ->
                dispatcher.register(literal("feytox")
                        .then(literal("points")
                                .executes(context -> getValue(context, EtherComponent::getPoints))
                                .then(argument("new value", FloatArgumentType.floatArg())
                                .executes(context -> setValue(context, EtherComponent::setPoints))))
                        .then(literal("max")
                                .executes(context -> getValue(context, EtherComponent::getMaxPoints))
                                .then(argument("new value", FloatArgumentType.floatArg())
                                        .executes(context -> setValue(context, EtherComponent::setMaxPoints))))
                        .then(literal("regen")
                                .executes(context -> getValue(context, EtherComponent::getPointsRegen))
                                .then(argument("new value", FloatArgumentType.floatArg())
                                        .executes(context -> setValue(context, EtherComponent::setPointsRegen))))
                )));
    }

    private static int getValue(CommandContext<ServerCommandSource> context, Function<EtherComponent, Float> dataFunc) {
        PlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;

        val optionalData = EtherologyComponents.ETHER.maybeGet(player);
        if (optionalData.isEmpty()) return 0;

        player.sendMessage(Text.of(String.valueOf(dataFunc.apply(optionalData.get()))));
        return 1;
    }

    private static int setValue(CommandContext<ServerCommandSource> context, BiConsumer<EtherComponent, Float> dataConsumer) {
        PlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;

        val optionalData = EtherologyComponents.ETHER.maybeGet(player);
        if (optionalData.isEmpty()) return 0;

        float value = Float.parseFloat(context.getInput().split(" ")[2]);
        dataConsumer.accept(optionalData.get(), value);
        return 1;
    }
}
