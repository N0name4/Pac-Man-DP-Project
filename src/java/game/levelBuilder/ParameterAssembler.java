package game.levelBuilder;

import game.levelBuilder.profile.ScoreRuleProfile;
import game.levelBuilder.profile.SpeedProfile;
import game.levelBuilder.profile.TimerProfile;
import game.levelBuilder.profile.profileSource.*;

public class ParameterAssembler {

    // Parameter Source
    private final SpeedSource speedSource;
    private final TimerSource timerSource;
    private final ScoreRuleSource scoreRuleSource;

    public ParameterAssembler(SpeedSource speedSource, TimerSource timerSource, ScoreRuleSource scoreRuleSource) {
        this.speedSource = speedSource;
        this.timerSource = timerSource;
        this.scoreRuleSource = scoreRuleSource;
    }

    public ParameterAssembler() {
        this(
                new ConcreteSpeedSource(),
                new ConcreteTimerSource(),
                new ConcreteScoreRuleSource()
        );
    }

    public DifficultyParams assemble(RoundTemplate template) {
        SpeedProfile speed = speedSource.getById(template.getSpeedProfileId());
        TimerProfile timer = timerSource.getById(template.getTimerProfileId());
        ScoreRuleProfile scoreRule = scoreRuleSource.getById(template.getScoreRuleProfileId());

        return new DifficultyParams.Builder()
                .pacmanSpeed(speed.getPacmanSpeed())
                .ghostSpeed(speed.getGhostSpeed())
                .chaseTimer(timer.getChaseTimer())
                .scatterTimer(timer.getScatterTimer())
                .frightenedTimer(timer.getFrightenedTimer())
                .pacGumScore(scoreRule.getPacGumScore())
                .superPacGumScore(scoreRule.getSuperPacGumScore())
                .ghostEatenScore(scoreRule.getGhostEatenScore())
                .ghostAIProfile(template.getGhostAIProfile())
                .build();
    }
}
