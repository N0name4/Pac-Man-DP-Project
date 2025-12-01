package game.observer;

import game.Game;

public interface GameLifeListener {

    void updateRoundClear(Game game);
    void updateGameOver(Game game);
}
