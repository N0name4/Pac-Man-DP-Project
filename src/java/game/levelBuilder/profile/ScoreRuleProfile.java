package game.levelBuilder.profile;

public class ScoreRuleProfile {

    // Item Eaten
    private final int pacGumScore;
    private final int superPacGumScore;

    // Ghost Collision
    private final int ghostEatenScore;

    public ScoreRuleProfile(int pacGumScore, int superPacGumScore, int ghostEatenScore) {
        this.pacGumScore = pacGumScore;
        this.superPacGumScore = superPacGumScore;
        this.ghostEatenScore = ghostEatenScore;
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
}
