package game.entities.ghosts;

import game.levelBuilder.DifficultyParams;
import game.ghostStrategies.PinkyStrategy;

//Classe concrète de Pinky (le fantôme rose)
public class Pinky extends Ghost {
    public Pinky(int xPos, int yPos, DifficultyParams difficultyParams) {
        super(xPos, yPos, "pinky.png", difficultyParams);
        setStrategy(difficultyParams.getGhostAIProfile().createPinkyStrategy(this));
    }
}
