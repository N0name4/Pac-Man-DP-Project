package game.levelBuilder.profile;

import game.entities.ghosts.Ghost;
import game.ghostStrategies.IGhostStrategy;

public interface GhostAIProfile {

    IGhostStrategy createBlinkyStrategy(Ghost self);

    IGhostStrategy createClydeStrategy(Ghost self);

    IGhostStrategy createInkyStrategy(Ghost self);

    IGhostStrategy createPinkyStrategy(Ghost self);
}
