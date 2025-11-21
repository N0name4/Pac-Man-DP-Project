package game.entities.ghosts;

import game.Game;
import game.LevelBuilder.DifficultyParams;
import game.ghostStrategies.InkyStrategy;

//Classe concrète de Inky (le fantôme bleu)
public class Inky extends Ghost {
    public Inky(int xPos, int yPos, DifficultyParams difficultyParams) {
        super(xPos, yPos, "inky.png", difficultyParams);
        setStrategy(new InkyStrategy(Game.getBlinky()));
    }
}
