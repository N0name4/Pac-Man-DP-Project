package game.entities.ghosts;

import game.LevelBuilder.DifficultyParams;
import game.ghostStrategies.BlinkyStrategy;
import game.ghostStrategies.LowPowerStrategy;

//Classe concrète de Blinky (le fantôme rouge)
public class Blinky extends Ghost {
    public Blinky(int xPos, int yPos, DifficultyParams difficultyParams) {
        super(xPos, yPos, "blinky.png", difficultyParams);
        setStrategy(new BlinkyStrategy());
    }
}
