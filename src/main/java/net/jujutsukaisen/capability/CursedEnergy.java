package net.jujutsukaisen.capability;

import net.jujutsukaisen.Config;
import net.minecraft.nbt.CompoundTag;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.IntSupplier;

public class CursedEnergy {
    private static final int DEFAULT_STARTING_ENERGY = 100;
    private static final int DEFAULT_MAX_ENERGY = 100;
    private static final int DEFAULT_MAX_ENERGY_CAP = 1000;

    private int energy;
    private int maxEnergy;
    private boolean infinityActive;
    private InnateTechnique technique;
    private int selectedMoveIndex;

    private int comboCount;
    private long lastMeleeTime;
    private long lastDashTime;
    private boolean configApplied;
    private final EnumMap<TechniqueMove, Integer> moveCooldowns = new EnumMap<>(TechniqueMove.class);

    public CursedEnergy() {
        this.energy = DEFAULT_STARTING_ENERGY;
        this.maxEnergy = DEFAULT_MAX_ENERGY;
        this.infinityActive = false;
        this.technique = InnateTechnique.LIMITLESS;
        this.selectedMoveIndex = 0;
        this.comboCount = 0;
        this.lastMeleeTime = 0;
        this.lastDashTime = 0;
        this.configApplied = false;

        for (TechniqueMove move : TechniqueMove.values()) {
            this.moveCooldowns.put(move, 0);
        }

        ensureConfigApplied();
    }

    public int getEnergy() {
        ensureConfigApplied();
        return this.energy;
    }

    public int getMaxEnergy() {
        ensureConfigApplied();
        return this.maxEnergy;
    }

    public boolean isInfinityActive() {
        return this.infinityActive;
    }

    public InnateTechnique getTechnique() {
        return this.technique;
    }

    public int getSelectedMoveIndex() {
        return this.selectedMoveIndex;
    }

    public int getComboCount() {
        return this.comboCount;
    }

    public long getLastMeleeTime() {
        return this.lastMeleeTime;
    }

    public long getLastDashTime() {
        return this.lastDashTime;
    }

    public int getCooldownTicks(TechniqueMove move) {
        return this.moveCooldowns.getOrDefault(move, 0);
    }

    public boolean isMoveOnCooldown(TechniqueMove move) {
        return getCooldownTicks(move) > 0;
    }

    public boolean hasEnoughEnergyFor(TechniqueMove move) {
        ensureConfigApplied();
        return this.energy >= move.getEnergyCost();
    }

    public void setEnergy(int energy) {
        ensureConfigApplied();
        this.energy = Math.max(0, Math.min(energy, this.maxEnergy));
    }

    public void setMaxEnergy(int maxEnergy) {
        int cap = readConfigInt(Config::getMaxEnergyCap, DEFAULT_MAX_ENERGY_CAP);
        this.maxEnergy = Math.max(1, Math.min(maxEnergy, cap));
        if (this.energy > this.maxEnergy) {
            this.energy = this.maxEnergy;
        }
        this.configApplied = true;
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
        ensureConfigApplied();
        this.energy = Math.min(this.energy + amount, this.maxEnergy);
    }

    public void consumeEnergy(int amount) {
        ensureConfigApplied();
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
        this.configApplied = true;
        this.moveCooldowns.clear();
        this.moveCooldowns.putAll(source.moveCooldowns);
    }

    public void saveNBTData(CompoundTag nbt) {
        ensureConfigApplied();
        nbt.putInt("cursed_energy", this.energy);
        nbt.putInt("max_cursed_energy", this.maxEnergy);
        nbt.putBoolean("infinity_active", this.infinityActive);
        nbt.putString("technique", this.technique.name());
        nbt.putInt("selected_move_index", this.selectedMoveIndex);
        nbt.putInt("combo_count", this.comboCount);
        nbt.putLong("last_melee_time", this.lastMeleeTime);
        nbt.putLong("last_dash_time", this.lastDashTime);

        CompoundTag cooldownTag = new CompoundTag();
        for (Map.Entry<TechniqueMove, Integer> entry : this.moveCooldowns.entrySet()) {
            if (entry.getValue() > 0) {
                cooldownTag.putInt(entry.getKey().name(), entry.getValue());
            }
        }
        nbt.put("move_cooldowns", cooldownTag);
    }

    public void loadNBTData(CompoundTag nbt) {
        int loadedEnergy = nbt.contains("cursed_energy")
                ? nbt.getInt("cursed_energy")
                : readConfigInt(Config::getStartingEnergy, DEFAULT_STARTING_ENERGY);

        int loadedMaxEnergy = nbt.contains("max_cursed_energy")
                ? nbt.getInt("max_cursed_energy")
                : readConfigInt(Config::getDefaultMaxEnergy, DEFAULT_MAX_ENERGY);

        this.maxEnergy = Math.max(1, Math.min(loadedMaxEnergy, readConfigInt(Config::getMaxEnergyCap, DEFAULT_MAX_ENERGY_CAP)));
        this.energy = Math.max(0, Math.min(loadedEnergy, this.maxEnergy));
        this.configApplied = true;

        this.infinityActive = nbt.getBoolean("infinity_active");
        if (nbt.contains("technique")) {
            try {
                this.technique = InnateTechnique.valueOf(nbt.getString("technique"));
            } catch (IllegalArgumentException e) {
                this.technique = InnateTechnique.NONE;
            }
        } else {
            this.technique = InnateTechnique.LIMITLESS;
        }

        this.selectedMoveIndex = nbt.getInt("selected_move_index");
        this.comboCount = nbt.getInt("combo_count");
        this.lastMeleeTime = nbt.getLong("last_melee_time");
        this.lastDashTime = nbt.getLong("last_dash_time");

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

    private void ensureConfigApplied() {
        if (this.configApplied) {
            return;
        }

        try {
            int configuredMaxEnergy = Math.max(1, Config.getDefaultMaxEnergy());
            int configuredCap = Math.max(1, Config.getMaxEnergyCap());
            this.maxEnergy = Math.min(configuredMaxEnergy, configuredCap);
            this.energy = Math.min(Math.max(0, Config.getStartingEnergy()), this.maxEnergy);
            this.configApplied = true;
        } catch (IllegalStateException ignored) {
        }
    }

    private static int readConfigInt(IntSupplier supplier, int fallback) {
        try {
            return supplier.getAsInt();
        } catch (IllegalStateException ignored) {
            return fallback;
        }
    }
}
