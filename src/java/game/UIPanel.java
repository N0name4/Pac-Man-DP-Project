package game;

import javax.swing.*;
import java.awt.*;

// Score Adjustment Domain Removed
// Only View Score Area
public class UIPanel extends JPanel {
    public static int width;
    public static int height;

    private int score = 0;
    private JLabel scoreLabel;

    public UIPanel(int width, int height) {
        this.width = width;
        this.height = height;

        setPreferredSize(new Dimension(width, height));
        this.setBackground(Color.black);

        scoreLabel = new JLabel("Score: " + score);
        scoreLabel.setFont(scoreLabel.getFont().deriveFont(20.0F));
        scoreLabel.setForeground(Color.white);
        this.add(scoreLabel, BorderLayout.WEST);
    }

    // Game call
    public void setScore(int score) {
        this.score = score;
        this.scoreLabel.setText("Score: " + score);
    }

    public int getScore() {
        return score;
    }
}
