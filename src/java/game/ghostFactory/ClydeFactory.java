package game.ghostFactory;

import game.entities.ghosts.Blinky;
import game.levelBuilder.DifficultyParams;
import game.entities.ghosts.Clyde;
import game.entities.ghosts.Ghost;

//Factory concrète pour créer des fantômes Clyde
public class ClydeFactory extends AbstractGhostFactory {
    public static Ghost singletonClyde;
    @Override
    public Ghost makeGhost(int xPos, int yPos, DifficultyParams difficultyParams) {
        if(singletonClyde == null) {
            //싱글톤 객체생성
            singletonClyde = new Clyde(xPos, yPos, difficultyParams);
        }
        else {
            //blinky위치 설정
            singletonClyde.resetInitValue(xPos, yPos, difficultyParams);
        }
        return singletonClyde;
    }
}
