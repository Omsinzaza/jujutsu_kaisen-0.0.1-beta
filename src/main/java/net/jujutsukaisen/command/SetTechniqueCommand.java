package net.jujutsukaisen.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.jujutsukaisen.capability.CursedEnergyProvider;
import net.jujutsukaisen.capability.InnateTechnique;
import net.jujutsukaisen.network.JujutsuNetwork;
import net.jujutsukaisen.network.packet.SyncCursedEnergyS2CPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public class SetTechniqueCommand {

    private static final SuggestionProvider<CommandSourceStack> TECHNIQUE_SUGGESTIONS = (context, builder) -> {
        for (InnateTechnique tech : InnateTechnique.values()) {
            builder.suggest(tech.name().toLowerCase());
        }
        return builder.buildFuture();
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("technique")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("technique", StringArgumentType.word())
                                .suggests(TECHNIQUE_SUGGESTIONS)
                                .executes(context -> {
                                    ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                    String techniqueName = StringArgumentType.getString(context, "technique");
                                    return setTechnique(context.getSource(), player, techniqueName);
                                }))));
    }

    private static int setTechnique(CommandSourceStack source, ServerPlayer player, String techniqueName) {
        InnateTechnique technique;
        try {
            technique = InnateTechnique.valueOf(techniqueName.toUpperCase());
        } catch (IllegalArgumentException e) {
            source.sendFailure(Component.literal("Unknown technique: " + techniqueName
                    + ". Valid options: none, limitless, ten_shadows"));
            return 0;
        }

        player.getCapability(CursedEnergyProvider.CURSED_ENERGY).ifPresent(energy -> {
            energy.setTechnique(technique);
            energy.setSelectedMoveIndex(0);
            energy.clearAllCooldowns();

            if (technique != InnateTechnique.LIMITLESS) {
                energy.setInfinityActive(false);
            }

            JujutsuNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                    new SyncCursedEnergyS2CPacket(energy.getEnergy(), energy.getMaxEnergy(),
                            energy.isInfinityActive(), energy.getTechnique(),
                            energy.getSelectedMoveIndex(), energy.createCooldownArray()));
        });

        String displayName = technique.name().toLowerCase().replace('_', ' ');
        String[] words = displayName.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }

        source.sendSuccess(() -> Component.literal("Set " + player.getName().getString()
                + "'s innate technique to " + sb), true);
        return 1;
    }
}
