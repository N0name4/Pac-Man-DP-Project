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

    // Game Objects
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

    // First Round Starter Called by GameplayPanel init
    public void startFirstRound() {
        currentRound = 1;
        createGameForRound(currentRound);
    }

    private void createGameForRound(int roundNumber) {
        // Old Game Subscribing Remove
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

    // GameLifeListener Implementation
    @Override
    public void updateRoundClear(Game game) {
        // Old Game request ignoring
        if (game != currentGame) return;

        // show Overlay (GameplayPanel) -> ProceedRound (Callback)
        gameplayPanel.showRoundClearOverlay(this::proceedRound);
    }

    @Override
    public void updateGameOver(Game game) {
        if (game != currentGame) return;

        gameplayPanel.showGameOverOverlay(this::restartRound);
    }

    // Go to Next Round -> Game Creating
    private void proceedRound() {
        currentRound += 1;
        createGameForRound(currentRound);
    }

    // Remain to Current Round -> Game ReCreating
    // Must Recreate Game because init all state and map for Game Objects
    private void restartRound() {
        createGameForRound(currentRound);
    }

    public Game getGame(){return currentGame;}
}
