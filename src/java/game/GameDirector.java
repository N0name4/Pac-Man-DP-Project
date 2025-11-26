package game;

import game.levelBuilder.DifficultyParams;
import game.levelBuilder.ParameterAssembler;
import game.levelBuilder.RoundTemplate;
import game.levelBuilder.RoundTemplateSource;
import game.observer.GameLifeListener;

public class GameDirector implements GameLifeListener {

    // Round Publisher
    private final RoundTemplateSource roundTemplateSource;
    private final ParameterAssembler parameterAssembler;

    private GameplayPanel gameplayPanel;
    private Game currentGame;

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

    // GameplayPanel Setter (or Constructor)
    public void setGameplayPanel(GameplayPanel panel) {
        this.gameplayPanel = panel;
    }

    public void startFirstRound() {
        currentRound = 1;
        createGameForRound(currentRound);
    }

    private void createGameForRound(int roundNumber) {
        if (currentGame != null) currentGame.removeListener(this);

        // Get Round Template
        RoundTemplate template = roundTemplateSource.getTemplateForRound(roundNumber);
        // Get Parameter from Round Template
        DifficultyParams params = parameterAssembler.assemble(template);

        Game newGame = new Game(params);
        newGame.registerListener(this);

        if (gameplayPanel != null) {
            gameplayPanel.setGame(newGame);
        }
        currentGame = newGame;
    }

    @Override
    public void updateRoundClear(Game game) {
        if (game != currentGame) return;

        if (gameplayPanel != null) gameplayPanel.showRoundClearOverlay();

        currentRound += 1;
        createGameForRound(currentRound);
    }

    @Override
    public void updateGameOver(Game game) {
        if (game != currentGame) return;

        if (gameplayPanel != null) gameplayPanel.showGameOverOverlay();

        createGameForRound(currentRound);
    }
}
