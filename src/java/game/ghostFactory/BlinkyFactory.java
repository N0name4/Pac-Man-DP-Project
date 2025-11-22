package game.ghostFactory;

import game.entities.ghosts.Blinky;
import game.entities.ghosts.Ghost;

//Factory concrète pour créer des fantômes Blinky
public class BlinkyFactory extends AbstractGhostFactory {
    public static Ghost singletonBlinky;

    @Override
    public Ghost makeGhost(int xPos, int yPos) {
        if(singletonBlinky == null) {
            //싱글톤 객체생성
            singletonBlinky = new Blinky(xPos, yPos);
            System.out.println("_________________new Blinky");
        }
        else {
            //blinky위치 설정
            singletonBlinky.resetInitValue(xPos, yPos);
            System.out.println("_________________Blinky Singleton Reset!!");
        }
        return singletonBlinky;
    }
}

