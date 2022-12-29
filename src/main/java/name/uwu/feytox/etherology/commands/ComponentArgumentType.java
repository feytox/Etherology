package name.uwu.feytox.etherology.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import name.uwu.feytox.etherology.components.IFloatComponent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static name.uwu.feytox.etherology.EtherologyComponents.ETHER_MAX;

public class ComponentArgumentType implements ArgumentType<String>, Serializable {
    private final ComponentKey<IFloatComponent> componentKey;

    private ComponentArgumentType(ComponentKey<IFloatComponent> componentKey) {
        this.componentKey = componentKey;
    }

    public static ComponentArgumentType component() {
        return new ComponentArgumentType(ETHER_MAX);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        int argBeginning = reader.getCursor();
        if(!reader.canRead()){
            reader.skip();
        }
        while(reader.canRead() && reader.peek() != ' ') {
            reader.skip();
        }
        return reader.getString().substring(argBeginning, reader.getCursor());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        List<String> components = new ArrayList<>(
                List.of("ETHER_MAX", "ETHER_REGEN", "ETHER_POINTS"));

        components.forEach(builder::suggest);
        return builder.buildFuture();
    }
}
