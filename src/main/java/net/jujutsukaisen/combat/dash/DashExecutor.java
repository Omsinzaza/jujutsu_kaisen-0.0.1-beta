package net.jujutsukaisen.combat.dash;

@FunctionalInterface
public interface DashExecutor {
    void execute(DashContext context);
}
