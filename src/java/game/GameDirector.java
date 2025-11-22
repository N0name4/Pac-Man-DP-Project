package game;

import game.levelBuilder.DifficultyParams;
import game.levelBuilder.ParameterAssembler;
import game.levelBuilder.RoundTemplate;
import game.levelBuilder.RoundTemplateSource;

public class GameDirector {

    private final RoundTemplateSource roundTemplateSource;
    private final ParameterAssembler parameterAssembler;
    private int currentRound = 1;

    public GameDirector(
            RoundTemplateSource roundTemplateSource, ParameterAssembler parameterAssembler) {
        this.roundTemplateSource = roundTemplateSource;
        this.parameterAssembler = parameterAssembler;
    }

    // Default Constructor
    public GameDirector() {
        this(
                new RoundTemplateSource(),
                new ParameterAssembler()
        );
    }

    public Game startRound() {
        return createGameForRound(currentRound);
    }

    private Game createGameForRound(int roundNumber) {
        // Get Round Template
        RoundTemplate template = roundTemplateSource.getTemplateForRound(roundNumber);

        // Get Parameter from Round Template
        DifficultyParams params = parameterAssembler.assemble(template);

        return new Game(params);
    }
}
