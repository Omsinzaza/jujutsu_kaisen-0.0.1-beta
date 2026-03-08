package net.jujutsukaisen.combat.melee;

public final class MeleeCombos {
    private static final MeleeExecutor DEFAULT_EXECUTOR = new StandardMeleeExecutor();

    private MeleeCombos() {
    }

    public static void attack(MeleeContext context) {
        DEFAULT_EXECUTOR.execute(context);
    }
}
