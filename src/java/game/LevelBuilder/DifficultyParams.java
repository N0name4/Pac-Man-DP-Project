package game.LevelBuilder;

public class DifficultyParams {

    // Moving Entity Speed
    private final int pacmanSpeed;
    private final int ghostSpeed;

    // State Timer
    private final int frightenedTimer;
    private final int chaseTimer;
    private final int scatterTimer;

    public DifficultyParams(int pacmanSpeed, int ghostSpeed, int frightenedTimer, int chaseTimer, int scatterTimer) {
        this.pacmanSpeed = pacmanSpeed;
        this.ghostSpeed = ghostSpeed;
        this.frightenedTimer = frightenedTimer;
        this.chaseTimer = chaseTimer;
        this.scatterTimer = scatterTimer;
    }

    public static DifficultyParams easyLevel() {
        return new DifficultyParams(
                2, 1, 60 * 15, 60 * 10, 60 * 10);
    }

    // Original Parameter
    public static DifficultyParams normalLevel() {
        return new DifficultyParams(
                2, 2, 60 * 7, 60 * 20, 60 * 5
        );
    }

    public static DifficultyParams hardLevel() {
        return new DifficultyParams(
                2, 3, 60 * 5, 60 * 25, 60 * 5
        );
    }

    public int getPacmanSpeed() {
        return pacmanSpeed;
    }

    public int getGhostSpeed() {
        return ghostSpeed;
    }

    public int getFrightenedTimer() {
        return frightenedTimer;
    }

    public int getChaseTimer() {
        return chaseTimer;
    }

    public int getScatterTimer() {
        return scatterTimer;
    }
}
