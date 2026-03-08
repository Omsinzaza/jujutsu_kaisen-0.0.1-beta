package net.jujutsukaisen.network.packet;

import net.jujutsukaisen.capability.CursedEnergyProvider;
import net.jujutsukaisen.capability.TechniqueMove;
import net.jujutsukaisen.network.JujutsuNetwork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;
import java.util.function.Supplier;

public class SwitchMoveC2SPacket {

    public SwitchMoveC2SPacket() {
    }

    public SwitchMoveC2SPacket(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                player.getCapability(CursedEnergyProvider.CURSED_ENERGY).ifPresent(energy -> {
                    List<TechniqueMove> moves = TechniqueMove.getMovesForTechnique(energy.getTechnique());
                    if (!moves.isEmpty()) {
                        int nextIndex = energy.getSelectedMoveIndex() + 1;
                        if (nextIndex >= moves.size()) {
                            nextIndex = 0;
                        }
                        energy.setSelectedMoveIndex(nextIndex);

                        JujutsuNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                                new SyncCursedEnergyS2CPacket(energy.getEnergy(), energy.getMaxEnergy(),
                                        energy.isInfinityActive(), energy.getTechnique(),
                                        energy.getSelectedMoveIndex(), energy.createCooldownArray()));
                    }
                });
            }
        });
        return true;
    }
}
