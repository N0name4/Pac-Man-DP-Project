package game.levelBuilder;

import game.levelBuilder.profile.SpeedProfile;
import game.levelBuilder.profile.TimerProfile;
import game.levelBuilder.profile.profileSource.ConcreteSpeedSource;
import game.levelBuilder.profile.profileSource.ConcreteTimerSource;
import game.levelBuilder.profile.profileSource.SpeedSource;
import game.levelBuilder.profile.profileSource.TimerSource;

public class ParameterAssembler {

    // Parameter Source
    private final SpeedSource speedSource;
    private final TimerSource timerSource;

    public ParameterAssembler(SpeedSource speedSource, TimerSource timerSource) {
        this.speedSource = speedSource;
        this.timerSource = timerSource;
    }

    public ParameterAssembler() {
        this(
                new ConcreteSpeedSource(),
                new ConcreteTimerSource()
        );
    }

    public DifficultyParams assemble(RoundTemplate template) {
        SpeedProfile speed = speedSource.getById(template.getSpeedProfileId());
        TimerProfile timer = timerSource.getById(template.getTimerProfileId());

        return new DifficultyParams.Builder()
                .pacmanSpeed(speed.getPacmanSpeed())
                .ghostSpeed(speed.getGhostSpeed())
                .chaseTimer(timer.getChaseTimer())
                .scatterTimer(timer.getScatterTimer())
                .frightenedTimer(timer.getFrightenedTimer())
                .build();
    }
}
