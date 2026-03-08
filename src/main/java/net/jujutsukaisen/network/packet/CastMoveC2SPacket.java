package net.jujutsukaisen.network.packet;

import net.jujutsukaisen.Config;
import net.jujutsukaisen.capability.CursedEnergyProvider;
import net.jujutsukaisen.capability.TechniqueMove;
import net.jujutsukaisen.move.cast.MoveCastContext;
import net.jujutsukaisen.move.cast.MoveCasting;
import net.jujutsukaisen.move.cast.MoveExecutors;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class CastMoveC2SPacket {

    public CastMoveC2SPacket() {
    }

    public CastMoveC2SPacket(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }

            long processTime = System.currentTimeMillis();
            long lastTime = player.getPersistentData().getLong(MoveCasting.LAST_CAST_KEY);
            if (processTime - lastTime < Config.getGlobalCastBufferMs()) {
                return;
            }
            player.getPersistentData().putLong(MoveCasting.LAST_CAST_KEY, processTime);

            player.getCapability(CursedEnergyProvider.CURSED_ENERGY).ifPresent(energy -> {
                List<TechniqueMove> moves = TechniqueMove.getMovesForTechnique(energy.getTechnique());
                int selectedMoveIndex = energy.getSelectedMoveIndex();
                if (selectedMoveIndex < 0 || selectedMoveIndex >= moves.size()) {
                    return;
                }

                TechniqueMove currentMove = moves.get(selectedMoveIndex);
                MoveExecutors.cast(currentMove, new MoveCastContext(player, energy));
            });
        });
        context.setPacketHandled(true);
        return true;
    }
}
