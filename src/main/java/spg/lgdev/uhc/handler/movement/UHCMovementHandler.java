package spg.lgdev.uhc.handler.movement;

import spg.lgdev.movement.ListenType;
import spg.lgdev.movement.MovementListener;
import spg.lgdev.movement.MovementType;
import spg.lgdev.uhc.border.BorderRadius;

public class UHCMovementHandler {

    public static void register() {
        new MovementListener()
                .type(MovementType.XYZ)
                .listenType(ListenType.EVERY_BLOCK_WITHOUT_Y)
                .callback(movementValues -> {
                    BorderRadius.checkPlayer(movementValues.getPlayer(), null, false);
                }).start();
    }
}
