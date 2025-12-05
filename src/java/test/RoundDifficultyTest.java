package test;

import game.Game;
import game.GameDirector;
import game.levelBuilder.DifficultyParams;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RoundDifficultyTest {

    @Test
    void roundProceedTest() {
        GameDirector gameDirector = new GameDirector();

        // Start FirstRound
        gameDirector.startFirstRound();
        System.out.println(gameDirector.getCurrentRound());
        Game firstGame = gameDirector.getCurrentGame();

        // Round Proceed
        gameDirector.updateRoundClear(gameDirector.getCurrentGame());
        gameDirector.updateRoundClear(gameDirector.getCurrentGame());
        System.out.println(gameDirector.getCurrentRound());
        Game nextGame = gameDirector.getCurrentGame();

        // Compare each DifficultyParams
        DifficultyParams firstParams = firstGame.getDifficultyParams();
        DifficultyParams nextParams = nextGame.getDifficultyParams();

        // Test
        Assertions.assertNotEquals(firstParams.getGhostSpeed(), nextParams.getGhostSpeed());
        Assertions.assertNotEquals(firstParams.getChaseTimer(), nextParams.getChaseTimer());
        Assertions.assertNotEquals(firstParams.getGhostEatenScore(), nextParams.getGhostEatenScore());


    }
}
