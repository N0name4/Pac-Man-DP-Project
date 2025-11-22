package game.levelBuilder;

import game.levelBuilder.profile.ProfileSlot;

import java.util.Map;

public class RoundTemplate {

    // Round Parameter
    private final int roundNumber;

    // Difficulty Parameter
    private final String speedProfileId;
    private final String timerProfileId;

    public RoundTemplate(int roundNumber, String speedProfileId, String timerProfileId) {
        this.roundNumber = roundNumber;
        this.speedProfileId = speedProfileId;
        this.timerProfileId = timerProfileId;
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
}
