package game.entities.ghosts;

import game.levelBuilder.DifficultyParams;
import game.ghostStrategies.BlinkyStrategy;

//Classe concrète de Blinky (le fantôme rouge)
public class Blinky extends Ghost {
    public Blinky(int xPos, int yPos, DifficultyParams difficultyParams) {
        super(xPos, yPos, "blinky.png", difficultyParams);
        setStrategy(difficultyParams.getGhostAIProfile().createBlinkyStrategy(this));
    }
}
