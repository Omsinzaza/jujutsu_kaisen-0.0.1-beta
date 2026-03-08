package net.jujutsukaisen.move.cast;

import net.jujutsukaisen.capability.TechniqueMove;
import net.jujutsukaisen.move.cast.executor.BlueMoveExecutor;
import net.jujutsukaisen.move.cast.executor.CursedBlastMoveExecutor;
import net.jujutsukaisen.move.cast.executor.DivineDogsMoveExecutor;
import net.jujutsukaisen.move.cast.executor.HollowPurpleMoveExecutor;
import net.jujutsukaisen.move.cast.executor.InfinityCrushMoveExecutor;
import net.jujutsukaisen.move.cast.executor.InfinityToggleMoveExecutor;
import net.jujutsukaisen.move.cast.executor.RedMoveExecutor;
import net.minecraft.network.chat.Component;

import java.util.EnumMap;
import java.util.Map;

public final class MoveExecutors {
    private static final EnumMap<TechniqueMove, MoveExecutor> EXECUTORS = new EnumMap<>(TechniqueMove.class);

    static {
        register(TechniqueMove.CURSED_BLAST, new CursedBlastMoveExecutor());
        register(TechniqueMove.LIMITLESS_INFINITY_TOGGLE, new InfinityToggleMoveExecutor());
        register(TechniqueMove.LIMITLESS_INFINITY_CRUSH, new InfinityCrushMoveExecutor());
        register(TechniqueMove.LIMITLESS_BLUE, new BlueMoveExecutor());
        register(TechniqueMove.LIMITLESS_RED, new RedMoveExecutor());
        register(TechniqueMove.LIMITLESS_HOLLOW_PURPLE, new HollowPurpleMoveExecutor());
        register(TechniqueMove.TEN_SHADOWS_DIVINE_DOGS, new DivineDogsMoveExecutor());
    }

    private MoveExecutors() {
    }

    private static void register(TechniqueMove move, MoveExecutor executor) {
        EXECUTORS.put(move, executor);
    }

    public static void cast(TechniqueMove move, MoveCastContext context) {
        MoveExecutor executor = EXECUTORS.get(move);
        if (executor == null) {
            context.player().sendSystemMessage(Component.literal("§cNo move executor registered for " + move.getDisplayName()));
            return;
        }
        executor.execute(context);
    }

    public static Map<TechniqueMove, MoveExecutor> view() {
        return Map.copyOf(EXECUTORS);
    }
}
