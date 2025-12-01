package game.levelBuilder;

import game.levelBuilder.profile.GhostAIProfile;
import game.levelBuilder.profile.ProfileSlot;

import java.util.Map;

public class RoundTemplate {

    // Round Parameter
    private final int roundNumber;

    // Difficulty Parameter
    private final String speedProfileId;
    private final String timerProfileId;
    private final String scoreRuleProfileId;

    private final GhostAIProfile ghostAIProfile;

    public RoundTemplate(int roundNumber, String speedProfileId, String timerProfileId, String scoreRuleProfileId, GhostAIProfile ghostAIProfile) {
        this.roundNumber = roundNumber;
        this.speedProfileId = speedProfileId;
        this.timerProfileId = timerProfileId;
        this.scoreRuleProfileId = scoreRuleProfileId;
        this.ghostAIProfile = ghostAIProfile;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public String getSpeedProfileId() {
        return speedProfileId;
    }

    public String getTimerProfileId() {
        return timerProfileId;
    }

    public String getScoreRuleProfileId() {
        return scoreRuleProfileId;
    }

    public GhostAIProfile getGhostAIProfile() {
        return ghostAIProfile;
    }
}
