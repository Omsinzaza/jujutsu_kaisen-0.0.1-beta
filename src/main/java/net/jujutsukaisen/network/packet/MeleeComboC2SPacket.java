package net.jujutsukaisen.network.packet;

import net.jujutsukaisen.capability.CursedEnergyProvider;
import net.jujutsukaisen.combat.melee.MeleeCombos;
import net.jujutsukaisen.combat.melee.MeleeContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MeleeComboC2SPacket {

    public MeleeComboC2SPacket() {
    }

    public MeleeComboC2SPacket(FriendlyByteBuf buf) {
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

            player.getCapability(CursedEnergyProvider.CURSED_ENERGY).ifPresent(energy ->
                    MeleeCombos.attack(new MeleeContext(player, energy))
            );
        });
        context.setPacketHandled(true);
        return true;
    }
}
