package game.ghostFactory;

import game.entities.ghosts.Clyde;
import game.entities.ghosts.Ghost;
import game.entities.ghosts.Pinky;

//Factory concrète pour créer des fantômes Pinky
public class PinkyFactory extends AbstractGhostFactory {
    public static Ghost singletonPinky;
    @Override
    public Ghost makeGhost(int xPos, int yPos) {

        if(singletonPinky == null) {
            singletonPinky = new Pinky(xPos, yPos);
            System.out.println("_________________new pinky!");
        }
        else {
            singletonPinky.resetInitValue(xPos,yPos);
            System.out.println("_________________pinky Singleton Reset!!");
        }

        return singletonPinky;
    }
}
