package game.levelBuilder;

public class DifficultyParams {

    // Moving Entity Speed
    private final int pacmanSpeed;
    private final int ghostSpeed;

    // State Timer
    private final int chaseTimer;
    private final int scatterTimer;
    private final int frightenedTimer;

    // Score Rule for Round
    private final int pacGumScore;
    private final int superPacGumScore;
    private final int ghostEatenScore;

    public DifficultyParams(Builder builder) {
        this.pacmanSpeed = builder.pacmanSpeed;
        this.ghostSpeed = builder.ghostSpeed;
        this.chaseTimer = builder.chaseTimer;
        this.scatterTimer = builder.scatterTimer;
        this.frightenedTimer = builder.frightenedTimer;
        this.pacGumScore = builder.pacGumScore;
        this.superPacGumScore = builder.superPacGumScore;
        this.ghostEatenScore = builder.ghostEatenScore;
    }

    public int getPacmanSpeed() {
        return pacmanSpeed;
    }

    public int getGhostSpeed() {
        return ghostSpeed;
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

    public int getPacGumScore() {
        return pacGumScore;
    }

    public int getSuperPacGumScore() {
        return superPacGumScore;
    }

    public int getGhostEatenScore() {
        return ghostEatenScore;
    }

    // Inline Builder Pattern
    public static class Builder {
        // Default Value Init
        private int pacmanSpeed = 2;
        private int ghostSpeed = 2;

        private int chaseTimer = 60 * 20;
        private int scatterTimer = 60 * 5;
        private int frightenedTimer = 60 * 7;

        private int pacGumScore = 10;
        private int superPacGumScore = 100;
        private int ghostEatenScore = 500;

        public Builder pacmanSpeed(int pacmanSpeed) {
            this.pacmanSpeed = pacmanSpeed;
            return this;
        }

        public Builder ghostSpeed(int ghostSpeed) {
            this.ghostSpeed = ghostSpeed;
            return this;
        }

        public Builder chaseTimer(int chaseTimer) {
            this.chaseTimer = chaseTimer;
            return this;
        }

        public Builder scatterTimer(int scatterTimer) {
            this.scatterTimer = scatterTimer;
            return this;
        }

        public Builder frightenedTimer(int frightenedTimer) {
            this.frightenedTimer = frightenedTimer;
            return this;
        }

        public Builder pacGumScore(int pacGumScore) {
            this.pacGumScore = pacGumScore;
            return this;
        }

        public Builder superPacGumScore(int superPacGumScore) {
            this.superPacGumScore = superPacGumScore;
            return this;
        }

        public Builder ghostEatenScore(int ghostEatenScore) {
            this.ghostEatenScore = ghostEatenScore;
            return this;
        }

        public DifficultyParams build() {
            if (pacmanSpeed <= 0) {
                throw new IllegalArgumentException("Pacman Speed Exception");
            }
            if (ghostSpeed <= 0) {
                throw new IllegalArgumentException("Ghost Speed Exception");
            }

            return new DifficultyParams(this);
        }
    }
}
