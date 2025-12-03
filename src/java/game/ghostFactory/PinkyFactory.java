package game.ghostFactory;

import game.entities.ghosts.Blinky;
import game.levelBuilder.DifficultyParams;
import game.entities.ghosts.Ghost;
import game.entities.ghosts.Pinky;

//Factory concrète pour créer des fantômes Pinky
public class PinkyFactory extends AbstractGhostFactory {
    public static Ghost singletonPinky;
    @Override
    public Ghost makeGhost(int xPos, int yPos, DifficultyParams difficultyParams) {
        if(singletonPinky == null) {
            //싱글톤 객체생성
            singletonPinky = new Pinky(xPos, yPos, difficultyParams);
        }
        else {
            //blinky위치 설정
            singletonPinky.resetInitValue(xPos, yPos, difficultyParams);
        }
        return singletonPinky;
    }
}
