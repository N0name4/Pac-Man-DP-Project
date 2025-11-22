package game.levelBuilder.profile;

// Profile about State Timer Parameter
// chase duration / scatter duration / frightened duration
public class TimerProfile {

    private final int chaseTimer;
    private final int scatterTimer;
    private final int frightenedTimer;

    public TimerProfile(int chaseTimer, int scatterTimer, int frightenedTimer) {
        this.chaseTimer = chaseTimer;
        this.scatterTimer = scatterTimer;
        this.frightenedTimer = frightenedTimer;
    }

    public int getChaseTimer() {
        return chaseTimer;
    }

    public int getScatterTimer() {
        return scatterTimer;
    }

    public int getFrightenedTimer() {
        return frightenedTimer;
    }
}
