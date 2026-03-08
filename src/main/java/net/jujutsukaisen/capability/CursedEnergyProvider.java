package net.jujutsukaisen.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CursedEnergyProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static Capability<CursedEnergy> CURSED_ENERGY = CapabilityManager.get(new CapabilityToken<CursedEnergy>() {
    });

    private CursedEnergy energy = null;
    private final LazyOptional<CursedEnergy> optional = LazyOptional.of(this::createCursedEnergy);

    private CursedEnergy createCursedEnergy() {
        if (this.energy == null) {
            this.energy = new CursedEnergy();
        }
        return this.energy;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CURSED_ENERGY) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createCursedEnergy().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createCursedEnergy().loadNBTData(nbt);
    }
}
