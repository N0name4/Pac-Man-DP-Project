package game;

import javax.swing.*;
import java.io.IOException;

//Point d'entrée de l'application
public class GameLauncher {
    private static UIPanel uiPanel;

    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setTitle("Pacman");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel gameWindow = new JPanel();

        Game tempGame = new Game();

        int cellsPerRow = tempGame.getCellsPerRow();
        int cellsPerColumn = tempGame.getCellsPerColumn();
        int cellSize = tempGame.getCellSize();
        
        int gameWidth = cellsPerRow * cellSize;
        int gameHeight = cellsPerColumn * cellSize;
        
        //Création de la "zone de jeu"
        try {
            gameWindow.add(new GameplayPanel(gameWidth,gameHeight, tempGame));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Création de l'UI (pour afficher le score)
        uiPanel = new UIPanel(256,gameHeight);
        gameWindow.add(uiPanel);
        tempGame.getPacman().registerObserver(uiPanel);

        window.setContentPane(gameWindow);
        window.setResizable(false);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    public static UIPanel getUIPanel() {
        return uiPanel;
    }
}
