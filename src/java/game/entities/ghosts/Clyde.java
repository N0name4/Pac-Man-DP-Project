package game.entities.ghosts;

import game.levelBuilder.DifficultyParams;
import game.ghostStrategies.ClydeStrategy;

//Classe concrète de Clyde (le fantôme jaune)
public class Clyde extends Ghost {
    public Clyde(int xPos, int yPos, DifficultyParams difficultyParams) {
        super(xPos, yPos, "clyde.png", difficultyParams);
        setStrategy(difficultyParams.getGhostAIProfile().createClydeStrategy(this));
    }
}
