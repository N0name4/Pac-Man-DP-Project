package test;

import game.Game;
import game.GameDirector;
import game.levelBuilder.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RoundParameterTest {

    @Test
    void roundObjectTest() {
        RoundTemplateSource roundTemplateSource = new RoundTemplateSource();
        ParameterAssembler parameterAssembler = new ParameterAssembler();

        RoundTemplate template = roundTemplateSource.getTemplateForRound(1);
        DifficultyParams params = parameterAssembler.assemble(template);

        RoundTemplate templateB = roundTemplateSource.getTemplateForRound(3);
        DifficultyParams paramsB = parameterAssembler.assemble(templateB);

        Assertions.assertEquals(params.getGhostSpeed(), paramsB.getGhostSpeed());
    }
}
