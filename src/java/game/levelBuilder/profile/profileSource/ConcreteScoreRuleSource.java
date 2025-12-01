package game.levelBuilder.profile.profileSource;

import game.levelBuilder.profile.ScoreRuleProfile;

import java.util.HashMap;
import java.util.Map;

public class ConcreteScoreRuleSource implements ScoreRuleSource{

    private final Map<String, ScoreRuleProfile> table = new HashMap<>();

    public ConcreteScoreRuleSource() {
        table.put("EasyScore", new ScoreRuleProfile(20, 200, 1000));
        table.put("NormalScore", new ScoreRuleProfile(10, 100, 500));
        table.put("HardScore", new ScoreRuleProfile(5, 50, 250));
    }

    @Override
    public ScoreRuleProfile getById(String id) {
        ScoreRuleProfile scoreRuleProfile = table.get(id);

        if (scoreRuleProfile == null) {
            throw new IllegalArgumentException("Unknown ScoreRuleProfile Exception: " + id);
        }

        return scoreRuleProfile;
    }
}
