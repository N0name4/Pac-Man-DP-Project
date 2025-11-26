package game.observer;

public interface Pujet {
    void registerListener(GameLifeListener listener);
    void removeListener(GameLifeListener listener);
    void notifyListenerRoundClear();
    void notifyListenerGameOver();
}
