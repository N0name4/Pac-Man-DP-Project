package game.levelBuilder;

public class RoundTemplateBuilder {

    private int roundNumber;

    // Profile
    private String speedProfileId;
    private String timerProfileId;

    // Default Value in RoundTemplate
    private static final String DEFAULT_SPEED = "NormalSpeed";
    private static final String DEFAULT_TIMER = "NormalTimer";

    public RoundTemplateBuilder withRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
        return this;
    }

    public RoundTemplateBuilder withSpeedProfile(String speedProfileId) {
        this.speedProfileId = speedProfileId;
        return this;
    }

    public RoundTemplateBuilder withTimerProfile(String timerProfileId) {
        this.timerProfileId = timerProfileId;
        return this;
    }

    public RoundTemplate build() {
        if (roundNumber <= 0) roundNumber = 1;

        // Default Parameter Injection
        if (speedProfileId == null) speedProfileId = DEFAULT_SPEED;
        if (timerProfileId == null) timerProfileId = DEFAULT_TIMER;

        return new RoundTemplate(
                roundNumber,
                speedProfileId,
                timerProfileId
        );
    }
}
