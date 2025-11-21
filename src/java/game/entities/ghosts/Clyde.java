package game.entities.ghosts;

import game.LevelBuilder.DifficultyParams;
import game.ghostStrategies.ClydeStrategy;
import game.ghostStrategies.LowPowerStrategy;

//Classe concrète de Clyde (le fantôme jaune)
public class Clyde extends Ghost {
    public Clyde(int xPos, int yPos, DifficultyParams difficultyParams) {
        super(xPos, yPos, "clyde.png", difficultyParams);
        setStrategy(new ClydeStrategy(this));
    }
}
