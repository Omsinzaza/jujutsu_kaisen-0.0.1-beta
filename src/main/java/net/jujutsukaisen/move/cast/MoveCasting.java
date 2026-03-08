package net.jujutsukaisen.move.cast;

import net.jujutsukaisen.capability.CursedEnergy;
import net.jujutsukaisen.capability.TechniqueMove;
import net.jujutsukaisen.network.JujutsuNetwork;
import net.jujutsukaisen.network.packet.SyncCursedEnergyS2CPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.PacketDistributor;

import java.util.Locale;
import java.util.UUID;

public final class MoveCasting {
    public static final String LAST_CAST_KEY = "jujutsukaisen_last_cast";
    public static final String ACTIVE_BLUE_KEY = "jujutsukaisen_active_blue";
    public static final String ACTIVE_RED_KEY = "jujutsukaisen_active_red";
    public static final String ACTIVE_HOLLOW_PURPLE_KEY = "jujutsukaisen_active_hollow_purple";

    private MoveCasting() {
    }

    public static boolean tryConsumeMove(MoveCastContext context, TechniqueMove move) {
        CursedEnergy energy = context.energy();
        ServerPlayer player = context.player();

        if (energy.isMoveOnCooldown(move)) {
            sendCooldownMessage(player, move, energy.getCooldownTicks(move));
            return false;
        }

        if (!energy.hasEnoughEnergyFor(move)) {
            player.sendSystemMessage(Component.literal(
                    "§cNot enough Cursed Energy (" + move.getEnergyCost() + " required)"));
            return false;
        }

        if (move.getEnergyCost() > 0) {
            energy.consumeEnergy(move.getEnergyCost());
        }
        energy.startCooldown(move);
        syncEnergy(player, energy);
        return true;
    }

    public static void sendCooldownMessage(ServerPlayer player, TechniqueMove move, int remainingTicks) {
        float seconds = remainingTicks / 20.0F;
        player.sendSystemMessage(Component.literal(
                String.format(Locale.ROOT, "§e%s is on cooldown: %.1fs", move.getDisplayName(), seconds)));
    }

    public static <T extends Entity> T getTrackedEntity(ServerPlayer player, String nbtKey, Class<T> clazz) {
        if (!player.getPersistentData().hasUUID(nbtKey)) {
            return null;
        }

        UUID uuid = player.getPersistentData().getUUID(nbtKey);
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return null;
        }

        Entity entity = serverLevel.getEntity(uuid);
        if (clazz.isInstance(entity) && entity != null && entity.isAlive()) {
            return clazz.cast(entity);
        }

        player.getPersistentData().remove(nbtKey);
        return null;
    }

    public static void syncEnergy(MoveCastContext context) {
        syncEnergy(context.player(), context.energy());
    }

    public static void syncEnergy(ServerPlayer player, CursedEnergy energy) {
        JujutsuNetwork.INSTANCE.send(
                PacketDistributor.PLAYER.with(() -> player),
                new SyncCursedEnergyS2CPacket(
                        energy.getEnergy(),
                        energy.getMaxEnergy(),
                        energy.isInfinityActive(),
                        energy.getTechnique(),
                        energy.getSelectedMoveIndex(),
                        energy.createCooldownArray()));
    }
}
