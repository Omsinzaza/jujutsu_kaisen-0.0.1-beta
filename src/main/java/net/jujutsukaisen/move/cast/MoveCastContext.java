package net.jujutsukaisen.move.cast;

import net.jujutsukaisen.capability.CursedEnergy;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class MoveCastContext {
    private final ServerPlayer player;
    private final CursedEnergy energy;

    public MoveCastContext(ServerPlayer player, CursedEnergy energy) {
        this.player = player;
        this.energy = energy;
    }

    public ServerPlayer player() {
        return player;
    }

    public CursedEnergy energy() {
        return energy;
    }

    public ServerLevel serverLevel() {
        return player.serverLevel();
    }
}
