package game.levelBuilder.profile.profileSource;

import game.levelBuilder.profile.ScoreRuleProfile;

public interface ScoreRuleSource {

    ScoreRuleProfile getById(String id);
}
