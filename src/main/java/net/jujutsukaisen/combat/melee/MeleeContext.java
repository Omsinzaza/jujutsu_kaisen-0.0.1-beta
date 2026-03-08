package net.jujutsukaisen.combat.melee;

import net.jujutsukaisen.capability.CursedEnergy;
import net.minecraft.server.level.ServerPlayer;

public class MeleeContext {
    private final ServerPlayer player;
    private final CursedEnergy energy;

    public MeleeContext(ServerPlayer player, CursedEnergy energy) {
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
