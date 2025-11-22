package game.ghostFactory;

import game.entities.ghosts.Clyde;
import game.entities.ghosts.Ghost;

//Factory concrète pour créer des fantômes Clyde
public class ClydeFactory extends AbstractGhostFactory {
    public static Ghost singletonClyde;
    @Override
    public Ghost makeGhost(int xPos, int yPos) {
        if(singletonClyde == null) {
            singletonClyde = new Clyde(xPos, yPos);
            System.out.println("_________________new Clyde!");
        }
        else {
            singletonClyde.resetInitValue(xPos,yPos);
            System.out.println("_________________Clyde Singleton Reset!!");
        }

        return singletonClyde;
    }
}
