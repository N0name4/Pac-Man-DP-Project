package game.levelBuilder.profile.profileSource;

import game.Game;
import game.entities.ghosts.Ghost;
import game.ghostStrategies.*;
import game.levelBuilder.profile.GhostAIProfile;

public class ClassicGhostAI implements GhostAIProfile {

    @Override
    public IGhostStrategy createBlinkyStrategy(Ghost self) {
        return new BlinkyStrategy();
    }

    @Override
    public IGhostStrategy createClydeStrategy(Ghost self) {
        return new ClydeStrategy(self);
    }

    @Override
    public IGhostStrategy createInkyStrategy(Ghost self) {
        return new InkyStrategy(Game.getBlinky());
    }

    @Override
    public IGhostStrategy createPinkyStrategy(Ghost self) {
        return new PinkyStrategy();
    }
}
