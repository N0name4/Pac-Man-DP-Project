package test;

import game.Game;
import game.GameDirector;
import game.levelBuilder.*;
import game.levelBuilder.profile.ScoreRuleProfile;
import game.levelBuilder.profile.SpeedProfile;
import game.levelBuilder.profile.TimerProfile;
import game.levelBuilder.profile.profileSource.ConcreteScoreRuleSource;
import game.levelBuilder.profile.profileSource.ConcreteSpeedSource;
import game.levelBuilder.profile.profileSource.ConcreteTimerSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DifficultyParamsTest {

    @Test
    void parameterInjectionTest() {
        // Round Profile : Easy Speed & Easy Timer & EasyScore
        ConcreteSpeedSource speedSource = new ConcreteSpeedSource();
        SpeedProfile speedProfile = speedSource.getById("EasySpeed");
        ConcreteTimerSource timerSource = new ConcreteTimerSource();
        TimerProfile timerProfile = timerSource.getById("EasyTimer");
        ConcreteScoreRuleSource ruleSource = new ConcreteScoreRuleSource();
        ScoreRuleProfile ruleProfile = ruleSource.getById("EasyScore");

        // Parameter Assembling Operation
        RoundTemplateSource roundTemplateSource = new RoundTemplateSource();
        ParameterAssembler parameterAssembler = new ParameterAssembler();
        DifficultyParams params = parameterAssembler.assemble(roundTemplateSource.getTemplateForRound(1));

        // Test
        Assertions.assertEquals(params.getGhostSpeed(), speedProfile.getGhostSpeed());
        Assertions.assertEquals(params.getPacmanSpeed(), speedProfile.getPacmanSpeed());
        Assertions.assertEquals(params.getChaseTimer(), timerProfile.getChaseTimer());
        Assertions.assertEquals(params.getScatterTimer(), timerProfile.getScatterTimer());
        Assertions.assertEquals(params.getFrightenedTimer(), timerProfile.getFrightenedTimer());
        Assertions.assertEquals(params.getPacGumScore(), ruleProfile.getPacGumScore());
        Assertions.assertEquals(params.getSuperPacGumScore(), ruleProfile.getSuperPacGumScore());
        Assertions.assertEquals(params.getGhostEatenScore(), ruleProfile.getGhostEatenScore());
    }
}
