package game.ghostFactory;

import game.levelBuilder.DifficultyParams;
import game.entities.ghosts.Clyde;
import game.entities.ghosts.Ghost;

//Factory concrète pour créer des fantômes Clyde
public class ClydeFactory extends AbstractGhostFactory {
    @Override
    public Ghost makeGhost(int xPos, int yPos, DifficultyParams difficultyParams) {
        return new Clyde(xPos, yPos, difficultyParams);
    }
}
