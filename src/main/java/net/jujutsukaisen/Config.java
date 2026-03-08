package net.jujutsukaisen;

import net.jujutsukaisen.capability.TechniqueMove;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.EnumMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = JujutsuMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final EnumMap<TechniqueMove, ForgeConfigSpec.IntValue> MOVE_COSTS = new EnumMap<>(TechniqueMove.class);
    private static final EnumMap<TechniqueMove, ForgeConfigSpec.IntValue> MOVE_COOLDOWNS = new EnumMap<>(TechniqueMove.class);

    private static final ForgeConfigSpec.IntValue STARTING_ENERGY;
    private static final ForgeConfigSpec.IntValue DEFAULT_MAX_ENERGY;
    private static final ForgeConfigSpec.IntValue MAX_ENERGY_CAP;
    private static final ForgeConfigSpec.IntValue CE_REGEN_AMOUNT;
    private static final ForgeConfigSpec.IntValue CE_REGEN_INTERVAL_TICKS;
    private static final ForgeConfigSpec.IntValue GLOBAL_CAST_BUFFER_MS;

    private static final ForgeConfigSpec.IntValue DASH_COST;
    private static final ForgeConfigSpec.IntValue DASH_COOLDOWN_MS;
    private static final ForgeConfigSpec.DoubleValue DASH_HORIZONTAL_STRENGTH;
    private static final ForgeConfigSpec.DoubleValue DASH_VERTICAL_STRENGTH;

    private static final ForgeConfigSpec.IntValue MELEE_COMBO_RESET_MS;
    private static final ForgeConfigSpec.IntValue MELEE_SWING_COOLDOWN_MS;
    private static final ForgeConfigSpec.IntValue MELEE_FINISHER_HITS;
    private static final ForgeConfigSpec.DoubleValue MELEE_RANGE;
    private static final ForgeConfigSpec.DoubleValue MELEE_NORMAL_DAMAGE;
    private static final ForgeConfigSpec.DoubleValue MELEE_FINISHER_DAMAGE;
    private static final ForgeConfigSpec.DoubleValue MELEE_NORMAL_KNOCKBACK;
    private static final ForgeConfigSpec.DoubleValue MELEE_FINISHER_KNOCKBACK;

    private static final ForgeConfigSpec.IntValue INFINITY_DRAIN_AMOUNT;
    private static final ForgeConfigSpec.IntValue INFINITY_DRAIN_INTERVAL_TICKS;
    private static final ForgeConfigSpec.DoubleValue INFINITY_RADIUS;
    private static final ForgeConfigSpec.DoubleValue INFINITY_PROJECTILE_SLOW_FACTOR;
    private static final ForgeConfigSpec.DoubleValue INFINITY_REPEL_RADIUS;
    private static final ForgeConfigSpec.DoubleValue INFINITY_REPEL_STRENGTH;
    private static final ForgeConfigSpec.IntValue INFINITY_SLOWNESS_DURATION_TICKS;
    private static final ForgeConfigSpec.IntValue INFINITY_SLOWNESS_AMPLIFIER;

    private static final ForgeConfigSpec.DoubleValue INFINITY_CRUSH_RADIUS;
    private static final ForgeConfigSpec.DoubleValue INFINITY_CRUSH_DAMAGE;
    private static final ForgeConfigSpec.DoubleValue INFINITY_CRUSH_KNOCKBACK;

    private static final ForgeConfigSpec.DoubleValue CURSED_BLAST_DAMAGE;
    private static final ForgeConfigSpec.DoubleValue CURSED_BLAST_VELOCITY;
    private static final ForgeConfigSpec.DoubleValue CURSED_BLAST_INACCURACY;

    private static final ForgeConfigSpec.DoubleValue BLUE_HOLD_DISTANCE;
    private static final ForgeConfigSpec.DoubleValue BLUE_HOLD_PULL_RADIUS;
    private static final ForgeConfigSpec.DoubleValue BLUE_HOLD_PULL_STRENGTH;
    private static final ForgeConfigSpec.DoubleValue BLUE_HOLD_TICK_DAMAGE;
    private static final ForgeConfigSpec.DoubleValue BLUE_LAUNCH_SPEED;
    private static final ForgeConfigSpec.DoubleValue BLUE_FLIGHT_PULL_RADIUS;
    private static final ForgeConfigSpec.DoubleValue BLUE_FLIGHT_PULL_STRENGTH;
    private static final ForgeConfigSpec.DoubleValue BLUE_IMPACT_RADIUS;
    private static final ForgeConfigSpec.DoubleValue BLUE_IMPACT_MIN_DAMAGE;
    private static final ForgeConfigSpec.DoubleValue BLUE_IMPACT_MAX_DAMAGE;
    private static final ForgeConfigSpec.DoubleValue BLUE_IMPACT_PULL_STRENGTH;
    private static final ForgeConfigSpec.IntValue BLUE_BLOCK_BREAK_RADIUS;
    private static final ForgeConfigSpec.DoubleValue BLUE_BLOCK_LAUNCH_STRENGTH;

    private static final ForgeConfigSpec.DoubleValue RED_HOLD_DISTANCE;
    private static final ForgeConfigSpec.DoubleValue RED_LAUNCH_SPEED;
    private static final ForgeConfigSpec.IntValue RED_MAX_FLIGHT_TICKS;
    private static final ForgeConfigSpec.DoubleValue RED_RECOIL_STRENGTH;
    private static final ForgeConfigSpec.DoubleValue RED_IMPACT_RADIUS;
    private static final ForgeConfigSpec.DoubleValue RED_IMPACT_MIN_DAMAGE;
    private static final ForgeConfigSpec.DoubleValue RED_IMPACT_MAX_DAMAGE;
    private static final ForgeConfigSpec.DoubleValue RED_IMPACT_PUSH_STRENGTH;
    private static final ForgeConfigSpec.DoubleValue RED_IMPACT_VERTICAL_PUSH;
    private static final ForgeConfigSpec.IntValue RED_BLOCK_BREAK_RADIUS;
    private static final ForgeConfigSpec.DoubleValue RED_BLOCK_PUSH_STRENGTH;

    private static final ForgeConfigSpec.IntValue HOLLOW_PURPLE_CHARGE_DURATION_TICKS;
    private static final ForgeConfigSpec.IntValue HOLLOW_PURPLE_SHOOT_DELAY_TICKS;
    private static final ForgeConfigSpec.DoubleValue HOLLOW_PURPLE_HOVER_DISTANCE;
    private static final ForgeConfigSpec.DoubleValue HOLLOW_PURPLE_GROUNDED_LIFT;
    private static final ForgeConfigSpec.DoubleValue HOLLOW_PURPLE_AIR_LIFT;
    private static final ForgeConfigSpec.DoubleValue HOLLOW_PURPLE_LAUNCH_SPEED;
    private static final ForgeConfigSpec.IntValue HOLLOW_PURPLE_FLIGHT_DURATION_TICKS;
    private static final ForgeConfigSpec.DoubleValue HOLLOW_PURPLE_DESTRUCTION_RADIUS;
    private static final ForgeConfigSpec.DoubleValue HOLLOW_PURPLE_DAMAGE_PER_TICK;
    private static final ForgeConfigSpec.BooleanValue HOLLOW_PURPLE_BREAK_BLOCKS;

    private static final ForgeConfigSpec.IntValue DIVINE_DOG_LIFETIME_TICKS;
    private static final ForgeConfigSpec.IntValue DIVINE_DOG_SUMMONING_TICKS;
    private static final ForgeConfigSpec.DoubleValue DIVINE_DOG_MAX_HEALTH;
    private static final ForgeConfigSpec.DoubleValue DIVINE_DOG_MOVEMENT_SPEED;
    private static final ForgeConfigSpec.DoubleValue DIVINE_DOG_ATTACK_DAMAGE;
    private static final ForgeConfigSpec.DoubleValue DIVINE_DOG_RECALL_RANGE;
    private static final ForgeConfigSpec.DoubleValue DIVINE_DOG_SUMMON_FORWARD_OFFSET;
    private static final ForgeConfigSpec.DoubleValue DIVINE_DOG_SUMMON_SIDE_OFFSET;

    static {
        BUILDER.push("general");
        STARTING_ENERGY = BUILDER.comment("Starting cursed energy for a new player capability.")
                .defineInRange("starting_energy", 100, 0, 100000);
        DEFAULT_MAX_ENERGY = BUILDER.comment("Default max cursed energy for a new player capability.")
                .defineInRange("default_max_energy", 100, 1, 100000);
        MAX_ENERGY_CAP = BUILDER.comment("Hard cap used when max energy is set through code or NBT.")
                .defineInRange("max_energy_cap", 1000, 1, 100000);
        CE_REGEN_AMOUNT = BUILDER.comment("How much cursed energy regenerates per regen tick when Infinity is off.")
                .defineInRange("ce_regen_amount", 5, 0, 100000);
        CE_REGEN_INTERVAL_TICKS = BUILDER.comment("Ticks between passive cursed energy regeneration pulses.")
                .defineInRange("ce_regen_interval_ticks", 20, 1, 72000);
        GLOBAL_CAST_BUFFER_MS = BUILDER.comment("Minimum milliseconds between move cast packets.")
                .defineInRange("global_cast_buffer_ms", 150, 0, 60000);
        BUILDER.pop();

        BUILDER.push("dash");
        DASH_COST = BUILDER.defineInRange("cost", 10, 0, 100000);
        DASH_COOLDOWN_MS = BUILDER.defineInRange("cooldown_ms", 1000, 0, 600000);
        DASH_HORIZONTAL_STRENGTH = BUILDER.defineInRange("horizontal_strength", 2.0D, 0.0D, 100.0D);
        DASH_VERTICAL_STRENGTH = BUILDER.defineInRange("vertical_strength", 0.5D, 0.0D, 100.0D);
        BUILDER.pop();

        BUILDER.push("melee_combo");
        MELEE_COMBO_RESET_MS = BUILDER.defineInRange("reset_ms", 1500, 0, 600000);
        MELEE_SWING_COOLDOWN_MS = BUILDER.defineInRange("swing_cooldown_ms", 300, 0, 600000);
        MELEE_FINISHER_HITS = BUILDER.defineInRange("finisher_hits", 4, 1, 100);
        MELEE_RANGE = BUILDER.defineInRange("range", 1.5D, 0.1D, 32.0D);
        MELEE_NORMAL_DAMAGE = BUILDER.defineInRange("normal_damage", 6.0D, 0.0D, 100000.0D);
        MELEE_FINISHER_DAMAGE = BUILDER.defineInRange("finisher_damage", 12.0D, 0.0D, 100000.0D);
        MELEE_NORMAL_KNOCKBACK = BUILDER.defineInRange("normal_knockback", 0.3D, 0.0D, 100.0D);
        MELEE_FINISHER_KNOCKBACK = BUILDER.defineInRange("finisher_knockback", 1.5D, 0.0D, 100.0D);
        BUILDER.pop();

        BUILDER.push("moves");
        registerMoveBalance(TechniqueMove.CURSED_BLAST, "cursed_blast", 20, 20);
        registerMoveBalance(TechniqueMove.LIMITLESS_INFINITY_TOGGLE, "limitless_infinity_toggle", 0, 10);
        registerMoveBalance(TechniqueMove.LIMITLESS_INFINITY_CRUSH, "limitless_infinity_crush", 30, 80);
        registerMoveBalance(TechniqueMove.LIMITLESS_BLUE, "limitless_blue", 10, 60);
        registerMoveBalance(TechniqueMove.LIMITLESS_RED, "limitless_red", 15, 80);
        registerMoveBalance(TechniqueMove.LIMITLESS_HOLLOW_PURPLE, "limitless_hollow_purple", 80, 240);
        registerMoveBalance(TechniqueMove.TEN_SHADOWS_DIVINE_DOGS, "ten_shadows_divine_dogs", 25, 120);
        BUILDER.pop();

        BUILDER.push("infinity");
        INFINITY_DRAIN_AMOUNT = BUILDER.defineInRange("drain_amount", 1, 0, 100000);
        INFINITY_DRAIN_INTERVAL_TICKS = BUILDER.defineInRange("drain_interval_ticks", 10, 1, 72000);
        INFINITY_RADIUS = BUILDER.defineInRange("radius", 3.0D, 0.0D, 64.0D);
        INFINITY_PROJECTILE_SLOW_FACTOR = BUILDER.defineInRange("projectile_slow_factor", 0.1D, 0.0D, 1.0D);
        INFINITY_REPEL_RADIUS = BUILDER.defineInRange("repel_radius", 2.5D, 0.0D, 64.0D);
        INFINITY_REPEL_STRENGTH = BUILDER.defineInRange("repel_strength", 0.5D, 0.0D, 20.0D);
        INFINITY_SLOWNESS_DURATION_TICKS = BUILDER.defineInRange("slowness_duration_ticks", 10, 0, 72000);
        INFINITY_SLOWNESS_AMPLIFIER = BUILDER.defineInRange("slowness_amplifier", 5, 0, 255);
        BUILDER.pop();

        BUILDER.push("infinity_crush");
        INFINITY_CRUSH_RADIUS = BUILDER.defineInRange("radius", 4.0D, 0.0D, 64.0D);
        INFINITY_CRUSH_DAMAGE = BUILDER.defineInRange("damage", 20.0D, 0.0D, 100000.0D);
        INFINITY_CRUSH_KNOCKBACK = BUILDER.defineInRange("knockback", 1.5D, 0.0D, 100.0D);
        BUILDER.pop();

        BUILDER.push("cursed_blast");
        CURSED_BLAST_DAMAGE = BUILDER.defineInRange("damage", 12.0D, 0.0D, 100000.0D);
        CURSED_BLAST_VELOCITY = BUILDER.defineInRange("velocity", 1.5D, 0.0D, 20.0D);
        CURSED_BLAST_INACCURACY = BUILDER.defineInRange("inaccuracy", 1.0D, 0.0D, 20.0D);
        BUILDER.pop();

        BUILDER.push("blue");
        BLUE_HOLD_DISTANCE = BUILDER.defineInRange("hold_distance", 2.0D, 0.0D, 64.0D);
        BLUE_HOLD_PULL_RADIUS = BUILDER.defineInRange("hold_pull_radius", 5.0D, 0.0D, 64.0D);
        BLUE_HOLD_PULL_STRENGTH = BUILDER.defineInRange("hold_pull_strength", 0.05D, 0.0D, 20.0D);
        BLUE_HOLD_TICK_DAMAGE = BUILDER.defineInRange("hold_tick_damage", 1.0D, 0.0D, 100000.0D);
        BLUE_LAUNCH_SPEED = BUILDER.defineInRange("launch_speed", 2.0D, 0.0D, 20.0D);
        BLUE_FLIGHT_PULL_RADIUS = BUILDER.defineInRange("flight_pull_radius", 6.0D, 0.0D, 64.0D);
        BLUE_FLIGHT_PULL_STRENGTH = BUILDER.defineInRange("flight_pull_strength", 0.5D, 0.0D, 20.0D);
        BLUE_IMPACT_RADIUS = BUILDER.defineInRange("impact_radius", 6.0D, 0.0D, 64.0D);
        BLUE_IMPACT_MIN_DAMAGE = BUILDER.defineInRange("impact_min_damage", 5.0D, 0.0D, 100000.0D);
        BLUE_IMPACT_MAX_DAMAGE = BUILDER.defineInRange("impact_max_damage", 20.0D, 0.0D, 100000.0D);
        BLUE_IMPACT_PULL_STRENGTH = BUILDER.defineInRange("impact_pull_strength", 1.0D, 0.0D, 20.0D);
        BLUE_BLOCK_BREAK_RADIUS = BUILDER.defineInRange("block_break_radius", 1, 0, 16);
        BLUE_BLOCK_LAUNCH_STRENGTH = BUILDER.defineInRange("block_launch_strength", 0.4D, 0.0D, 20.0D);
        BUILDER.pop();

        BUILDER.push("red");
        RED_HOLD_DISTANCE = BUILDER.defineInRange("hold_distance", 2.0D, 0.0D, 64.0D);
        RED_LAUNCH_SPEED = BUILDER.defineInRange("launch_speed", 3.5D, 0.0D, 20.0D);
        RED_MAX_FLIGHT_TICKS = BUILDER.defineInRange("max_flight_ticks", 100, 1, 72000);
        RED_RECOIL_STRENGTH = BUILDER.defineInRange("recoil_strength", 0.5D, 0.0D, 20.0D);
        RED_IMPACT_RADIUS = BUILDER.defineInRange("impact_radius", 10.0D, 0.0D, 64.0D);
        RED_IMPACT_MIN_DAMAGE = BUILDER.defineInRange("impact_min_damage", 10.0D, 0.0D, 100000.0D);
        RED_IMPACT_MAX_DAMAGE = BUILDER.defineInRange("impact_max_damage", 40.0D, 0.0D, 100000.0D);
        RED_IMPACT_PUSH_STRENGTH = BUILDER.defineInRange("impact_push_strength", 3.0D, 0.0D, 20.0D);
        RED_IMPACT_VERTICAL_PUSH = BUILDER.defineInRange("impact_vertical_push", 1.0D, 0.0D, 20.0D);
        RED_BLOCK_BREAK_RADIUS = BUILDER.defineInRange("block_break_radius", 2, 0, 16);
        RED_BLOCK_PUSH_STRENGTH = BUILDER.defineInRange("block_push_strength", 1.5D, 0.0D, 20.0D);
        BUILDER.pop();

        BUILDER.push("hollow_purple");
        HOLLOW_PURPLE_CHARGE_DURATION_TICKS = BUILDER.defineInRange("charge_duration_ticks", 324, 1, 72000);
        HOLLOW_PURPLE_SHOOT_DELAY_TICKS = BUILDER.defineInRange("shoot_delay_ticks", 35, 0, 72000);
        HOLLOW_PURPLE_HOVER_DISTANCE = BUILDER.defineInRange("hover_distance", 4.0D, 0.0D, 64.0D);
        HOLLOW_PURPLE_GROUNDED_LIFT = BUILDER.defineInRange("grounded_lift", 0.08D, 0.0D, 10.0D);
        HOLLOW_PURPLE_AIR_LIFT = BUILDER.defineInRange("air_lift", 0.02D, 0.0D, 10.0D);
        HOLLOW_PURPLE_LAUNCH_SPEED = BUILDER.defineInRange("launch_speed", 3.0D, 0.0D, 20.0D);
        HOLLOW_PURPLE_FLIGHT_DURATION_TICKS = BUILDER.defineInRange("flight_duration_ticks", 200, 1, 72000);
        HOLLOW_PURPLE_DESTRUCTION_RADIUS = BUILDER.defineInRange("destruction_radius", 10.0D, 0.0D, 64.0D);
        HOLLOW_PURPLE_DAMAGE_PER_TICK = BUILDER.defineInRange("damage_per_tick", 500.0D, 0.0D, 100000.0D);
        HOLLOW_PURPLE_BREAK_BLOCKS = BUILDER.comment("Whether launched Hollow Purple deletes blocks in its radius.")
                .define("break_blocks", true);
        BUILDER.pop();

        BUILDER.push("divine_dogs");
        DIVINE_DOG_LIFETIME_TICKS = BUILDER.defineInRange("lifetime_ticks", 1200, 1, 72000);
        DIVINE_DOG_SUMMONING_TICKS = BUILDER.defineInRange("summoning_ticks", 15, 0, 72000);
        DIVINE_DOG_MAX_HEALTH = BUILDER.defineInRange("max_health", 30.0D, 1.0D, 100000.0D);
        DIVINE_DOG_MOVEMENT_SPEED = BUILDER.defineInRange("movement_speed", 0.35D, 0.0D, 10.0D);
        DIVINE_DOG_ATTACK_DAMAGE = BUILDER.defineInRange("attack_damage", 8.0D, 0.0D, 100000.0D);
        DIVINE_DOG_RECALL_RANGE = BUILDER.defineInRange("recall_range", 64.0D, 1.0D, 256.0D);
        DIVINE_DOG_SUMMON_FORWARD_OFFSET = BUILDER.defineInRange("summon_forward_offset", 2.0D, 0.0D, 32.0D);
        DIVINE_DOG_SUMMON_SIDE_OFFSET = BUILDER.defineInRange("summon_side_offset", 1.0D, 0.0D, 32.0D);
        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    private static void registerMoveBalance(TechniqueMove move, String key, int defaultCost, int defaultCooldown) {
        BUILDER.push(key);
        MOVE_COSTS.put(move, BUILDER.defineInRange("cost", defaultCost, 0, 100000));
        MOVE_COOLDOWNS.put(move, BUILDER.defineInRange("cooldown_ticks", defaultCooldown, 0, 72000));
        BUILDER.pop();
    }

    public static int getStartingEnergy() {
        return STARTING_ENERGY.get();
    }

    public static int getDefaultMaxEnergy() {
        return DEFAULT_MAX_ENERGY.get();
    }

    public static int getMaxEnergyCap() {
        return MAX_ENERGY_CAP.get();
    }

    public static int getCeRegenAmount() {
        return CE_REGEN_AMOUNT.get();
    }

    public static int getCeRegenIntervalTicks() {
        return CE_REGEN_INTERVAL_TICKS.get();
    }

    public static long getGlobalCastBufferMs() {
        return GLOBAL_CAST_BUFFER_MS.get();
    }

    public static int getDashCost() {
        return DASH_COST.get();
    }

    public static long getDashCooldownMs() {
        return DASH_COOLDOWN_MS.get();
    }

    public static double getDashHorizontalStrength() {
        return DASH_HORIZONTAL_STRENGTH.get();
    }

    public static double getDashVerticalStrength() {
        return DASH_VERTICAL_STRENGTH.get();
    }

    public static long getMeleeComboResetMs() {
        return MELEE_COMBO_RESET_MS.get();
    }

    public static long getMeleeSwingCooldownMs() {
        return MELEE_SWING_COOLDOWN_MS.get();
    }

    public static int getMeleeFinisherHits() {
        return MELEE_FINISHER_HITS.get();
    }

    public static double getMeleeRange() {
        return MELEE_RANGE.get();
    }

    public static float getMeleeNormalDamage() {
        return MELEE_NORMAL_DAMAGE.get().floatValue();
    }

    public static float getMeleeFinisherDamage() {
        return MELEE_FINISHER_DAMAGE.get().floatValue();
    }

    public static float getMeleeNormalKnockback() {
        return MELEE_NORMAL_KNOCKBACK.get().floatValue();
    }

    public static float getMeleeFinisherKnockback() {
        return MELEE_FINISHER_KNOCKBACK.get().floatValue();
    }

    public static int getMoveCost(TechniqueMove move) {
        return valueOrDefault(MOVE_COSTS, move, 0);
    }

    public static int getMoveCooldownTicks(TechniqueMove move) {
        return valueOrDefault(MOVE_COOLDOWNS, move, 0);
    }

    public static int getInfinityDrainAmount() {
        return INFINITY_DRAIN_AMOUNT.get();
    }

    public static int getInfinityDrainIntervalTicks() {
        return INFINITY_DRAIN_INTERVAL_TICKS.get();
    }

    public static double getInfinityRadius() {
        return INFINITY_RADIUS.get();
    }

    public static double getInfinityProjectileSlowFactor() {
        return INFINITY_PROJECTILE_SLOW_FACTOR.get();
    }

    public static double getInfinityRepelRadius() {
        return INFINITY_REPEL_RADIUS.get();
    }

    public static double getInfinityRepelStrength() {
        return INFINITY_REPEL_STRENGTH.get();
    }

    public static int getInfinitySlownessDurationTicks() {
        return INFINITY_SLOWNESS_DURATION_TICKS.get();
    }

    public static int getInfinitySlownessAmplifier() {
        return INFINITY_SLOWNESS_AMPLIFIER.get();
    }

    public static double getInfinityCrushRadius() {
        return INFINITY_CRUSH_RADIUS.get();
    }

    public static float getInfinityCrushDamage() {
        return INFINITY_CRUSH_DAMAGE.get().floatValue();
    }

    public static float getInfinityCrushKnockback() {
        return INFINITY_CRUSH_KNOCKBACK.get().floatValue();
    }

    public static float getCursedBlastDamage() {
        return CURSED_BLAST_DAMAGE.get().floatValue();
    }

    public static float getCursedBlastVelocity() {
        return CURSED_BLAST_VELOCITY.get().floatValue();
    }

    public static float getCursedBlastInaccuracy() {
        return CURSED_BLAST_INACCURACY.get().floatValue();
    }

    public static double getBlueHoldDistance() {
        return BLUE_HOLD_DISTANCE.get();
    }

    public static double getBlueHoldPullRadius() {
        return BLUE_HOLD_PULL_RADIUS.get();
    }

    public static double getBlueHoldPullStrength() {
        return BLUE_HOLD_PULL_STRENGTH.get();
    }

    public static float getBlueHoldTickDamage() {
        return BLUE_HOLD_TICK_DAMAGE.get().floatValue();
    }

    public static double getBlueLaunchSpeed() {
        return BLUE_LAUNCH_SPEED.get();
    }

    public static double getBlueFlightPullRadius() {
        return BLUE_FLIGHT_PULL_RADIUS.get();
    }

    public static double getBlueFlightPullStrength() {
        return BLUE_FLIGHT_PULL_STRENGTH.get();
    }

    public static double getBlueImpactRadius() {
        return BLUE_IMPACT_RADIUS.get();
    }

    public static float getBlueImpactMinDamage() {
        return BLUE_IMPACT_MIN_DAMAGE.get().floatValue();
    }

    public static float getBlueImpactMaxDamage() {
        return BLUE_IMPACT_MAX_DAMAGE.get().floatValue();
    }

    public static double getBlueImpactPullStrength() {
        return BLUE_IMPACT_PULL_STRENGTH.get();
    }

    public static int getBlueBlockBreakRadius() {
        return BLUE_BLOCK_BREAK_RADIUS.get();
    }

    public static double getBlueBlockLaunchStrength() {
        return BLUE_BLOCK_LAUNCH_STRENGTH.get();
    }

    public static double getRedHoldDistance() {
        return RED_HOLD_DISTANCE.get();
    }

    public static double getRedLaunchSpeed() {
        return RED_LAUNCH_SPEED.get();
    }

    public static int getRedMaxFlightTicks() {
        return RED_MAX_FLIGHT_TICKS.get();
    }

    public static double getRedRecoilStrength() {
        return RED_RECOIL_STRENGTH.get();
    }

    public static double getRedImpactRadius() {
        return RED_IMPACT_RADIUS.get();
    }

    public static float getRedImpactMinDamage() {
        return RED_IMPACT_MIN_DAMAGE.get().floatValue();
    }

    public static float getRedImpactMaxDamage() {
        return RED_IMPACT_MAX_DAMAGE.get().floatValue();
    }

    public static double getRedImpactPushStrength() {
        return RED_IMPACT_PUSH_STRENGTH.get();
    }

    public static double getRedImpactVerticalPush() {
        return RED_IMPACT_VERTICAL_PUSH.get();
    }

    public static int getRedBlockBreakRadius() {
        return RED_BLOCK_BREAK_RADIUS.get();
    }

    public static double getRedBlockPushStrength() {
        return RED_BLOCK_PUSH_STRENGTH.get();
    }

    public static int getHollowPurpleChargeDurationTicks() {
        return HOLLOW_PURPLE_CHARGE_DURATION_TICKS.get();
    }

    public static int getHollowPurpleShootDelayTicks() {
        return HOLLOW_PURPLE_SHOOT_DELAY_TICKS.get();
    }

    public static double getHollowPurpleHoverDistance() {
        return HOLLOW_PURPLE_HOVER_DISTANCE.get();
    }

    public static double getHollowPurpleGroundedLift() {
        return HOLLOW_PURPLE_GROUNDED_LIFT.get();
    }

    public static double getHollowPurpleAirLift() {
        return HOLLOW_PURPLE_AIR_LIFT.get();
    }

    public static double getHollowPurpleLaunchSpeed() {
        return HOLLOW_PURPLE_LAUNCH_SPEED.get();
    }

    public static int getHollowPurpleFlightDurationTicks() {
        return HOLLOW_PURPLE_FLIGHT_DURATION_TICKS.get();
    }

    public static double getHollowPurpleDestructionRadius() {
        return HOLLOW_PURPLE_DESTRUCTION_RADIUS.get();
    }

    public static float getHollowPurpleDamagePerTick() {
        return HOLLOW_PURPLE_DAMAGE_PER_TICK.get().floatValue();
    }

    public static boolean isHollowPurpleBreakBlocks() {
        return HOLLOW_PURPLE_BREAK_BLOCKS.get();
    }

    public static int getDivineDogLifetimeTicks() {
        return DIVINE_DOG_LIFETIME_TICKS.get();
    }

    public static int getDivineDogSummoningTicks() {
        return DIVINE_DOG_SUMMONING_TICKS.get();
    }

    public static double getDivineDogMaxHealth() {
        return DIVINE_DOG_MAX_HEALTH.get();
    }

    public static double getDivineDogMovementSpeed() {
        return DIVINE_DOG_MOVEMENT_SPEED.get();
    }

    public static double getDivineDogAttackDamage() {
        return DIVINE_DOG_ATTACK_DAMAGE.get();
    }

    public static double getDivineDogRecallRange() {
        return DIVINE_DOG_RECALL_RANGE.get();
    }

    public static double getDivineDogSummonForwardOffset() {
        return DIVINE_DOG_SUMMON_FORWARD_OFFSET.get();
    }

    public static double getDivineDogSummonSideOffset() {
        return DIVINE_DOG_SUMMON_SIDE_OFFSET.get();
    }

    private static int valueOrDefault(Map<TechniqueMove, ForgeConfigSpec.IntValue> map, TechniqueMove move, int fallback) {
        ForgeConfigSpec.IntValue value = map.get(move);
        return value != null ? value.get() : fallback;
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
    }
}
