package net.jujutsukaisen.move.cast.executor;

import net.jujutsukaisen.capability.TechniqueMove;
import net.jujutsukaisen.move.cast.MoveCastContext;
import net.jujutsukaisen.move.cast.MoveCasting;
import net.jujutsukaisen.move.cast.MoveExecutor;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class InfinityToggleMoveExecutor implements MoveExecutor {
    @Override
    public void execute(MoveCastContext context) {
        TechniqueMove move = TechniqueMove.LIMITLESS_INFINITY_TOGGLE;
        if (context.energy().isMoveOnCooldown(move)) {
            MoveCasting.sendCooldownMessage(context.player(), move, context.energy().getCooldownTicks(move));
            return;
        }

        boolean newState = !context.energy().isInfinityActive();
        if (newState && context.energy().getEnergy() <= 0) {
            context.player().sendSystemMessage(Component.literal("§cNot enough Cursed Energy to activate Infinity"));
            return;
        }

        context.energy().setInfinityActive(newState);
        context.energy().startCooldown(move);
        MoveCasting.syncEnergy(context);

        context.player().level().playSound(null, context.player().blockPosition(),
                newState ? SoundEvents.BEACON_ACTIVATE : SoundEvents.BEACON_DEACTIVATE,
                SoundSource.PLAYERS, 1.0F, 1.0F);
    }
}
