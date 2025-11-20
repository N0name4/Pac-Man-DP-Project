package game.ghostStrategies;

import game.entities.ghosts.Ghost;
import game.utils.Utils;

//Interface pour décrire les stratégies des différents fantômes (cette vidéo les explique bien : https://www.youtube.com/watch?v=ataGotQ7ir8)
public interface IGhostStrategy {
    // Chase Mode Strategy
    int[] getChaseTargetPosition();

    // Scatter Mode Strategy
    int[] getScatterTargetPosition();

    // Frightened Mode Strategy
    default int[] getFrightenedTargetPosition(Ghost ghost) {
        boolean axis = Utils.randomBool();

        return new int[] {
                ghost.getxPos() + (axis ? Utils.randomInt(-1, 1) * 32 : 0),
                ghost.getyPos() + (!axis ? Utils.randomInt(-1, 1) * 32 : 0)
        };
    }

    // Eaten Mode Strategy
    default int[] getEatenTargetPosition() {
        return new int[] {208, 200};
    }

    // House Mode Strategy
    default int[] getHouseTargetPosition() {
        return new int[] {208, 168};
    }
}
