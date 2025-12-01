package game.ghostFactory;

import game.entities.ghosts.Blinky;
import game.levelBuilder.DifficultyParams;
import game.entities.ghosts.Ghost;
import game.entities.ghosts.Inky;

//Factory concrète pour créer des fantômes Inky
public class InkyFactory extends AbstractGhostFactory {
    public static Ghost singletonInky;
    @Override
    public Ghost makeGhost(int xPos, int yPos, DifficultyParams difficultyParams) {
        if(singletonInky == null) {
            //싱글톤 객체생성
            singletonInky = new Inky(xPos, yPos, difficultyParams);
        }
        else {
            //blinky위치 설정
            singletonInky.resetInitValue(xPos, yPos, difficultyParams);
        }
        return singletonInky;
    }
}
