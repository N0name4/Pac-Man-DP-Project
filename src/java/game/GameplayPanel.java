package game;

import game.utils.KeyHandler;

import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

//Panneau de la "zone de jeu"
public class GameplayPanel extends JPanel implements Runnable {
    public static int width;
    public static int height;
    private Thread thread;
    private boolean running = false;

    private BufferedImage img;
    private Graphics2D g;
//    private Image backgroundImage;

    private KeyHandler key;

    // Current Game Object
    private Game game;

    private List<List<String>> levelData;
    private int cellsPerRow;
    private int cellsPerColumn;
    private int cellSize;

    // Overlay Part
    private String overlayText = null;  // Overlay Text
    private int overlayTimer = 0;  // Overlay present Time
    private boolean overlayActive = false;  // Overlay active flag
    private boolean firstInput = false;  // User Round First input
    private static final int OVERLAY_FRAMES = 120;  // Overlay Standard Duration - 2sec (60fps)

    // Callback Method
    private Runnable finishCallback;

    public GameplayPanel(int width, int height) throws IOException {
        this.width = width;
        this.height = height;

        setPreferredSize(new Dimension(width, height));
        setFocusable(true);
        requestFocus();
    }

    public GameplayPanel(int width, int height, Game game) throws IOException {
        this.width = width;
        this.height = height;
        this.game = game;
        setPreferredSize(new Dimension(width, height));
        setFocusable(true);
        requestFocus();
    }

    @Override
    public void addNotify() {
        super.addNotify();

        if (thread == null) {
            thread = new Thread(this, "GameThread");
            thread.start();
        }
    }

    //initialisation du jeu
    public void init() {
        running = true;
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g = (Graphics2D) img.getGraphics();

        key = new KeyHandler(this);

        //game = new Game();

        levelData = game.getLevelData();
        cellsPerRow = game.getCellsPerRow();
        cellsPerColumn = game.getCellsPerColumn();
        cellSize = game.getCellSize();
        // Game Director Initialization
        GameDirector gameDirector = new GameDirector();
        gameDirector.setGameplayPanel(this);
        gameDirector.startFirstRound();
    }

    public void setGame(Game game) {
        this.game = game;

        // Ready Overlay
        this.overlayText = "READY!";
        this.overlayActive = true;
        this.overlayTimer = 0;
        this.finishCallback = null;
        this.firstInput = true;
    }

    // Round Cleared Overlay
    public void showRoundClearOverlay(Runnable clearFunc) {
        this.overlayText = "ROUND CLEARED!";
        this.overlayActive = true;
        this.overlayTimer = OVERLAY_FRAMES;
        this.finishCallback = clearFunc;
        this.firstInput = false;
    }

    // Game Over Overlay
    public void showGameOverOverlay(Runnable overFunc) {
        this.overlayText = "GAME OVER";
        this.overlayActive = true;
        this.overlayTimer = OVERLAY_FRAMES;
        this.finishCallback = overFunc;
        this.firstInput = false;
    }

    public void update() {
        // Overlay State - Round Clear / Game Over
        if (overlayActive && !firstInput) {
            updateOverlay();
            return;
        }

        // Ready / Play State
        if (game != null) game.update();
        updateOverlay();
    }

    // Update Overlay Presentation for Several condition
    private void updateOverlay() {
        // Ready State until First Key input
        if (firstInput) {
            if (Game.getFirstInput()) {
                overlayText = null;
                overlayActive = false;
                firstInput = false;
            }
            return;
        }

        // Play State
        if (!overlayActive) return;

        // Overlay State
        overlayTimer--;
        if (overlayTimer <= 0) {
            overlayActive = false;
            overlayTimer = 0;
            overlayText = null;

            if (finishCallback != null) {
                Runnable cb = finishCallback;
                finishCallback = null;
                cb.run();
            }
        }
    }

    // Rendering Overlay Messages
    private void renderOverlay(Graphics2D g) {
        if (overlayText == null) return;

        g.setColor(new Color(0, 0, 0, 150));
        int boxHeight = 80;
        int boxY = height / 2 - boxHeight / 2;
        g.fillRect(0, boxY, width, boxHeight);

        g.setColor(Color.WHITE);
        g.setFont(g.getFont().deriveFont(Font.BOLD, 24f));
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(overlayText);
        int textX = (width - textWidth) / 2;
        int textY = height / 2 + fm.getAscent() / 2 - 4;

        g.drawString(overlayText, textX, textY);
    }

    //gestion des inputs
    public void input(KeyHandler key) {
        if (game != null && key != null) game.input(key);
        if (key.k_esc.isPressed) {
            System.out.println("esc pressed");
            this.stopGame();
            SwingUtilities.invokeLater(() -> {
                try {
                    GameLauncher.frameDisposer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            GameLauncher.MainPage();
        }
    }

    //"rendu du jeu" ; on prépare ce qui va être affiché en dessinant sur une "image" : un fond et les entités du jeu au dessus
    public void render() {
        if (g != null) {
            // 고정 배경 이미지 대신 단색으로 화면 클리어
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, width, height);

         // 2) 맵 데이터 기반으로 "벽 배경" 자동 그리기
            if (levelData != null) {
                for (int y = 0; y < cellsPerColumn; y++) {
                    for (int x = 0; x < cellsPerRow; x++) {
                        String cell = levelData.get(y).get(x);

                        // 'x'는 벽
                        if ("x".equals(cell)) {
                            g.setColor(new Color(0, 0, 150)); // 파란 벽 색 (원하는 색으로)
                            g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                        }
                    }
                }
            }
            game.render(g); // 엔티티(벽, 펠릿, 팩맨, 고스트)를 그린다
        }
        renderOverlay(g);
    }

    //Affichage du jeu : on affiche l'image avec le rendu
    public void draw() {
        Graphics g2 = this.getGraphics();

        if (g2 != null) {
            g2.drawImage(img, 0, 0, width, height, null);
            g2.dispose();
        }
    }

    @Override
    public void run() {
        init();

        //Pour faire en sorte que le jeu tourne à 60FPS (tutoriel consulté : https://www.youtube.com/watch?v=LhUN3EKZiio)
        final double GAME_HERTZ = 60.0;
        final double TBU = 1000000000 / GAME_HERTZ; //Time before update

        final int MUBR = 5; // Must update before render

        double lastUpdateTime = System.nanoTime();
        double lastRenderTime;

        final double TARGET_FPS = 60.0;
        final double TTBR = 1000000000 / TARGET_FPS; //Total time before render

        int frameCount = 0;
        int lastSecondTime = (int) (lastUpdateTime / 1000000000);
        int oldFrameCount = 0;

        while (running) {
            double now = System.nanoTime();
            int updateCount = 0;
            while ((now - lastUpdateTime) > TBU && (updateCount < MUBR)) {
                input(key);
                update();
                lastUpdateTime += TBU;
                updateCount++;
            }

            if (now - lastUpdateTime > TBU) {
                lastUpdateTime = now - TBU;
            }

            render();
            draw();
            lastRenderTime = now;
            frameCount++;

            int thisSecond = (int) (lastUpdateTime / 1000000000);
            if (thisSecond > lastSecondTime) {
                if (frameCount != oldFrameCount) {
                    //System.out.println("FPS : " + frameCount);
                    oldFrameCount = frameCount;
                }
                frameCount = 0;
                lastSecondTime = thisSecond;
            }

            while ((now - lastRenderTime < TTBR) && (now - lastUpdateTime < TBU)) {
                Thread.yield();

                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                    System.err.println("ERROR yielding thread");
                }

                now = System.nanoTime();
            }
        }
    }
}
