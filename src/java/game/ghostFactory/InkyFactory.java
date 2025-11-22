package game.ghostFactory;

import game.entities.ghosts.Clyde;
import game.entities.ghosts.Ghost;
import game.entities.ghosts.Inky;

//Factory concrète pour créer des fantômes Inky
public class InkyFactory extends AbstractGhostFactory {
    public static Ghost singletonInky;
    @Override
    public Ghost makeGhost(int xPos, int yPos) {

        if(singletonInky == null) {
            singletonInky = new Inky(xPos, yPos);
            System.out.println("_________________new Inky!");
        }
        else {
            singletonInky.resetInitValue(xPos,yPos);
            System.out.println("_________________Inky Singleton Reset!!");
        }

        return singletonInky;
    }
}
