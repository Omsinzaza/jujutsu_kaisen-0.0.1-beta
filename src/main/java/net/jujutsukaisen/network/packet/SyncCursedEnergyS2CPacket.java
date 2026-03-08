package net.jujutsukaisen.network.packet;

import net.jujutsukaisen.capability.CursedEnergyProvider;
import net.jujutsukaisen.capability.InnateTechnique;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncCursedEnergyS2CPacket {
    private final int energy;
    private final int maxEnergy;
    private final boolean infinityActive;
    private final InnateTechnique technique;
    private final int selectedMoveIndex;
    private final int[] moveCooldowns;

    public SyncCursedEnergyS2CPacket(int energy, int maxEnergy, boolean infinityActive, InnateTechnique technique,
            int selectedMoveIndex, int[] moveCooldowns) {
        this.energy = energy;
        this.maxEnergy = maxEnergy;
        this.infinityActive = infinityActive;
        this.technique = technique;
        this.selectedMoveIndex = selectedMoveIndex;
        this.moveCooldowns = moveCooldowns;
    }

    public SyncCursedEnergyS2CPacket(FriendlyByteBuf buf) {
        this.energy = buf.readInt();
        this.maxEnergy = buf.readInt();
        this.infinityActive = buf.readBoolean();
        InnateTechnique parsedTechnique;
        try {
            parsedTechnique = InnateTechnique.valueOf(buf.readUtf());
        } catch (IllegalArgumentException e) {
            parsedTechnique = InnateTechnique.NONE;
        }
        this.technique = parsedTechnique;
        this.selectedMoveIndex = buf.readInt();

        int cooldownCount = buf.readVarInt();
        this.moveCooldowns = new int[cooldownCount];
        for (int i = 0; i < cooldownCount; i++) {
            this.moveCooldowns[i] = buf.readVarInt();
        }
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(energy);
        buf.writeInt(maxEnergy);
        buf.writeBoolean(infinityActive);
        buf.writeUtf(technique.name());
        buf.writeInt(selectedMoveIndex);
        buf.writeVarInt(moveCooldowns.length);
        for (int cooldown : moveCooldowns) {
            buf.writeVarInt(cooldown);
        }
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.getCapability(CursedEnergyProvider.CURSED_ENERGY)
                        .ifPresent(energyCap -> {
                            energyCap.setEnergy(this.energy);
                            energyCap.setMaxEnergy(this.maxEnergy);
                            energyCap.setInfinityActive(this.infinityActive);
                            energyCap.setTechnique(this.technique);
                            energyCap.setSelectedMoveIndex(this.selectedMoveIndex);
                            energyCap.applyCooldownArray(this.moveCooldowns);
                        });
            }
        });
        return true;
    }
}
