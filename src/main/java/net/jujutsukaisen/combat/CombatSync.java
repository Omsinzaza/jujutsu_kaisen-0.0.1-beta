package net.jujutsukaisen.combat;

import net.jujutsukaisen.capability.CursedEnergy;
import net.jujutsukaisen.network.JujutsuNetwork;
import net.jujutsukaisen.network.packet.SyncCursedEnergyS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public final class CombatSync {

    private CombatSync() {
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
                        energy.createCooldownArray())
        );
    }
}
