package net.jujutsukaisen.combat.dash;

public final class Dashing {
    private static final DashExecutor DEFAULT_EXECUTOR = new StandardDashExecutor();

    private Dashing() {
    }

    public static void dash(DashContext context) {
        DEFAULT_EXECUTOR.execute(context);
    }
}
