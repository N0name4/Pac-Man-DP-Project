package game.ghostStrategies;

import game.Game;
import game.entities.ghosts.Ghost;
import game.utils.Utils;

public class LowPowerStrategy implements IGhostStrategy{
    // Must chase Pacman
    @Override
    public int[] getChaseTargetPosition() {
        return new int[] {
                Game.getPacman().getxPos(),
                Game.getPacman().getyPos()
        };
    }

    // Up Left moving
    @Override
    public int[] getScatterTargetPosition() {
        return new int[] {0, 0};
    }

    // Must run from Pacman
    @Override
    public int[] getFrightenedTargetPosition(Ghost ghost) {
        int pacmanX = Game.getPacman().getxPos();
        int pacmanY = Game.getPacman().getyPos();

        int ghostX = ghost.getxPos();
        int ghostY = ghost.getyPos();

        double direction = Utils.getDirection(pacmanX, pacmanY, ghostX, ghostY);

        return Utils.getPointDistanceDirection(
                ghostX, ghostY, 128, direction);
    }

}
