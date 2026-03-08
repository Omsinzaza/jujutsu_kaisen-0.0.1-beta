package net.jujutsukaisen.combat.melee;

@FunctionalInterface
public interface MeleeExecutor {
    void execute(MeleeContext context);
}
