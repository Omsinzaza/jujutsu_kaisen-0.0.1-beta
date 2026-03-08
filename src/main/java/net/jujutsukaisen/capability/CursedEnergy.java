package net.jujutsukaisen.capability;

import net.jujutsukaisen.Config;
import net.minecraft.nbt.CompoundTag;

import java.util.EnumMap;
import java.util.Map;

public class CursedEnergy {
    private int energy;
    private int maxEnergy;
    private boolean infinityActive;
    private InnateTechnique technique;
    private int selectedMoveIndex;

    private int comboCount;
    private long lastMeleeTime;
    private long lastDashTime;
    private final EnumMap<TechniqueMove, Integer> moveCooldowns = new EnumMap<>(TechniqueMove.class);

    public CursedEnergy() {
        this.maxEnergy = Math.max(1, Config.getDefaultMaxEnergy());
        this.energy = Math.min(Math.max(0, Config.getStartingEnergy()), this.maxEnergy);
        this.infinityActive = false;
        this.technique = InnateTechnique.LIMITLESS;
        this.selectedMoveIndex = 0;
        this.comboCount = 0;
        this.lastMeleeTime = 0;
        this.lastDashTime = 0;

        for (TechniqueMove move : TechniqueMove.values()) {
            this.moveCooldowns.put(move, 0);
        }
    }

    public int getEnergy() {
        return energy;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public boolean isInfinityActive() {
        return infinityActive;
    }

    public InnateTechnique getTechnique() {
        return technique;
    }

    public int getSelectedMoveIndex() {
        return selectedMoveIndex;
    }

    public int getComboCount() {
        return comboCount;
    }

    public long getLastMeleeTime() {
        return lastMeleeTime;
    }

    public long getLastDashTime() {
        return lastDashTime;
    }

    public int getCooldownTicks(TechniqueMove move) {
        return this.moveCooldowns.getOrDefault(move, 0);
    }

    public boolean isMoveOnCooldown(TechniqueMove move) {
        return getCooldownTicks(move) > 0;
    }

    public boolean hasEnoughEnergyFor(TechniqueMove move) {
        return this.energy >= move.getEnergyCost();
    }

    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(energy, maxEnergy));
    }

    public void setMaxEnergy(int maxEnergy) {
        int cap = Math.max(1, Config.getMaxEnergyCap());
        this.maxEnergy = Math.max(0, Math.min(maxEnergy, cap));
        if (this.energy > this.maxEnergy) {
            this.energy = this.maxEnergy;
        }
    }

    public void setInfinityActive(boolean active) {
        this.infinityActive = active;
    }

    public void setTechnique(InnateTechnique technique) {
        this.technique = technique;
    }

    public void setSelectedMoveIndex(int index) {
        this.selectedMoveIndex = index;
    }

    public void setComboCount(int comboCount) {
        this.comboCount = comboCount;
    }

    public void setLastMeleeTime(long lastMeleeTime) {
        this.lastMeleeTime = lastMeleeTime;
    }

    public void setLastDashTime(long lastDashTime) {
        this.lastDashTime = lastDashTime;
    }

    public void setCooldownTicks(TechniqueMove move, int ticks) {
        this.moveCooldowns.put(move, Math.max(0, ticks));
    }

    public void startCooldown(TechniqueMove move) {
        setCooldownTicks(move, move.getCooldownTicks());
    }

    public boolean tickCooldowns() {
        boolean changed = false;
        for (TechniqueMove move : TechniqueMove.values()) {
            int remaining = this.moveCooldowns.getOrDefault(move, 0);
            if (remaining > 0) {
                this.moveCooldowns.put(move, remaining - 1);
                changed = true;
            }
        }
        return changed;
    }

    public void clearAllCooldowns() {
        for (TechniqueMove move : TechniqueMove.values()) {
            this.moveCooldowns.put(move, 0);
        }
    }

    public int[] createCooldownArray() {
        TechniqueMove[] values = TechniqueMove.values();
        int[] array = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            array[i] = getCooldownTicks(values[i]);
        }
        return array;
    }

    public void applyCooldownArray(int[] cooldowns) {
        TechniqueMove[] values = TechniqueMove.values();
        for (int i = 0; i < values.length; i++) {
            int ticks = i < cooldowns.length ? cooldowns[i] : 0;
            setCooldownTicks(values[i], ticks);
        }
    }

    public void addEnergy(int amount) {
        this.energy = Math.min(this.energy + amount, this.maxEnergy);
    }

    public void consumeEnergy(int amount) {
        this.energy = Math.max(0, this.energy - amount);
    }

    public void copyFrom(CursedEnergy source) {
        this.energy = source.energy;
        this.maxEnergy = source.maxEnergy;
        this.infinityActive = source.infinityActive;
        this.technique = source.technique;
        this.selectedMoveIndex = source.selectedMoveIndex;
        this.comboCount = source.comboCount;
        this.lastMeleeTime = source.lastMeleeTime;
        this.lastDashTime = source.lastDashTime;
        this.moveCooldowns.clear();
        this.moveCooldowns.putAll(source.moveCooldowns);
    }

    public void saveNBTData(CompoundTag nbt) {
        nbt.putInt("cursed_energy", energy);
        nbt.putInt("max_cursed_energy", maxEnergy);
        nbt.putBoolean("infinity_active", infinityActive);
        nbt.putString("technique", technique.name());
        nbt.putInt("selected_move_index", selectedMoveIndex);
        nbt.putInt("combo_count", comboCount);
        nbt.putLong("last_melee_time", lastMeleeTime);
        nbt.putLong("last_dash_time", lastDashTime);

        CompoundTag cooldownTag = new CompoundTag();
        for (Map.Entry<TechniqueMove, Integer> entry : moveCooldowns.entrySet()) {
            if (entry.getValue() > 0) {
                cooldownTag.putInt(entry.getKey().name(), entry.getValue());
            }
        }
        nbt.put("move_cooldowns", cooldownTag);
    }

    public void loadNBTData(CompoundTag nbt) {
        energy = nbt.contains("cursed_energy") ? nbt.getInt("cursed_energy") : Config.getStartingEnergy();
        maxEnergy = nbt.contains("max_cursed_energy") ? nbt.getInt("max_cursed_energy") : Config.getDefaultMaxEnergy();
        setMaxEnergy(maxEnergy);
        setEnergy(energy);

        infinityActive = nbt.getBoolean("infinity_active");
        if (nbt.contains("technique")) {
            try {
                technique = InnateTechnique.valueOf(nbt.getString("technique"));
            } catch (IllegalArgumentException e) {
                technique = InnateTechnique.NONE;
            }
        } else {
            technique = InnateTechnique.LIMITLESS;
        }
        selectedMoveIndex = nbt.getInt("selected_move_index");
        comboCount = nbt.getInt("combo_count");
        lastMeleeTime = nbt.getLong("last_melee_time");
        lastDashTime = nbt.getLong("last_dash_time");

        clearAllCooldowns();
        if (nbt.contains("move_cooldowns")) {
            CompoundTag cooldownTag = nbt.getCompound("move_cooldowns");
            for (TechniqueMove move : TechniqueMove.values()) {
                if (cooldownTag.contains(move.name())) {
                    setCooldownTicks(move, cooldownTag.getInt(move.name()));
                }
            }
        }
    }
}
