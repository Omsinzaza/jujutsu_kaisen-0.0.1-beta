package net.jujutsukaisen.combat.dash;

import net.jujutsukaisen.capability.CursedEnergy;
import net.minecraft.server.level.ServerPlayer;

public class DashContext {
    private final ServerPlayer player;
    private final CursedEnergy energy;

    public DashContext(ServerPlayer player, CursedEnergy energy) {
        this.player = player;
        this.energy = energy;
    }

    public ServerPlayer player() {
        return player;
    }

    public CursedEnergy energy() {
        return energy;
    }
}
