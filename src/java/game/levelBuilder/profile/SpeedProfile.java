package game.levelBuilder.profile;

// Profile about Moving Entity Speed Parameter
// pacman speed / ghost speed
public class SpeedProfile {

    private final int pacmanSpeed;
    private final int ghostSpeed;

    public SpeedProfile(int pacmanSpeed, int ghostSpeed) {
        this.pacmanSpeed = pacmanSpeed;
        this.ghostSpeed = ghostSpeed;
    }

    public int getPacmanSpeed() {
        return pacmanSpeed;
    }

    public int getGhostSpeed() {
        return ghostSpeed;
    }
}
