package game;

import game.levelBuilder.DifficultyParams;
import game.entities.*;
import game.entities.ghosts.Blinky;
import game.entities.ghosts.Ghost;
import game.ghostFactory.*;
import game.ghostStates.EatenMode;
import game.ghostStates.FrightenedMode;
import game.observer.GameLifeListener;
import game.observer.Observer;
import game.observer.Pujet;
import game.utils.CollisionDetector;
import game.utils.CsvReader;
import game.utils.KeyHandler;

import java.awt.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

//Classe gérant le jeu en lui même
public class Game implements Observer, Pujet {
    // Game Object List
    private List<Entity> objects = new ArrayList();
    private List<Ghost> ghosts = new ArrayList();
    private static List<Wall> walls = new ArrayList();

    // Player Object
    private static Pacman pacman;
    // For Strategy Object
    private static Blinky blinky;

    // Game Start Inspection
    private static boolean firstInput = false;

    // Game Overall Params Object
    private final DifficultyParams difficultyParams;

    // Game Cycle Observer List
    private List<GameLifeListener> gameLifeListeners = new ArrayList<>();

    // Score & GameEnding State
    private int score = 0;
    private boolean finished = false;
    private static final int GAME_END_SCORE = 2000;

    public Game(DifficultyParams difficultyParams){
        this.difficultyParams = difficultyParams;
        Game.setFirstInput(false);
        init();
    }

    public static List<Wall> getWalls() {
        return walls;
    }

    public List<Entity> getEntities() {
        return objects;
    }

    //Mise à jour de toutes les entités
    public void update() {
        for (Entity o: objects) {
            if (!o.isDestroyed()) o.update();
        }
    }

    //Gestion des inputs
    public void input(KeyHandler k) {
        pacman.input(k);
    }

    //Rendu de toutes les entités
    public void render(Graphics2D g) {
        for (Entity o: objects) {
            if (!o.isDestroyed()) o.render(g);
        }
    }

    public static Pacman getPacman() {
        return pacman;
    }
    public static Blinky getBlinky() {
        return blinky;
    }

    // Pacman Observer Implementation
    @Override
    public void updatePacGumEaten(PacGum pg) {
        if (finished) return;

        pg.destroy();
        addScore(difficultyParams.getPacGumScore());
        checkRoundClear();
    }

    @Override
    public void updateSuperPacGumEaten(SuperPacGum spg) {
        if (finished) return;

        spg.destroy();
        addScore(difficultyParams.getSuperPacGumScore());

        for (Ghost gh : ghosts) {
            gh.getState().superPacGumEaten();
        }

        checkRoundClear();
    }

    @Override
    public void updateGhostCollision(Ghost gh) {
        if (finished) return;

        if (gh.getState() instanceof FrightenedMode) {
            gh.getState().eaten();
            addScore(difficultyParams.getGhostEatenScore());
            checkRoundClear();
        } else if (!(gh.getState() instanceof EatenMode)) {
            finished = true;
            notifyListenerGameOver();
        }
    }

    private void addScore(int delta) {
        score += delta;
        updateScoreUI();
    }

    private void updateScoreUI() {
        UIPanel uiPanel = GameLauncher.getUIPanel();
        if (uiPanel != null) uiPanel.setScore(score);
    }

    private void checkRoundClear() {
        if (!finished && score >= GAME_END_SCORE) {
            finished = true;
            notifyListenerRoundClear();
        }
    }

    // Pujet Implementation
    @Override
    public void registerListener(GameLifeListener listener) {
        gameLifeListeners.add(listener);
    }

    @Override
    public void removeListener(GameLifeListener listener) {
        gameLifeListeners.remove(listener);
    }

    @Override
    public void notifyListenerRoundClear() {
        ArrayList<GameLifeListener> snapshot = new ArrayList<>(gameLifeListeners);

        for (GameLifeListener listener : snapshot) {
            listener.updateRoundClear(this);
        }
    }

    @Override
    public void notifyListenerGameOver() {
        ArrayList<GameLifeListener> snapshot = new ArrayList<>(gameLifeListeners);

        for (GameLifeListener listener : snapshot) {
            listener.updateGameOver(this);
        }
    }

    public static void setFirstInput(boolean b) {
        firstInput = b;
    }

    public static boolean getFirstInput() {
        return firstInput;
    }

    // Map & Objects Initialization
    private void init() {
        List<List<String>> data = null;
        try {
            data = new CsvReader().parseCsv(getClass().getClassLoader().getResource("level/level.csv").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        int cellsPerRow = data.get(0).size();
        int cellsPerColumn = data.size();
        int cellSize = 8;

        CollisionDetector collisionDetector = new CollisionDetector(this);
        AbstractGhostFactory abstractGhostFactory = null;

        //Le niveau a une "grille", et pour chaque case du fichier csv, on affiche une entité parculière sur une case de la grille selon le caracère présent
        for(int xx = 0 ; xx < cellsPerRow ; xx++) {
            for(int yy = 0 ; yy < cellsPerColumn ; yy++) {
                String dataChar = data.get(yy).get(xx);
                if (dataChar.equals("x")) { //Création des murs
                    objects.add(new Wall(xx * cellSize, yy * cellSize));
                }else if (dataChar.equals("P")) { //Création de Pacman
                    pacman = new Pacman(xx * cellSize, yy * cellSize, difficultyParams);
                    pacman.setCollisionDetector(collisionDetector);

                    //Enregistrement des différents observers de Pacman
                    pacman.registerObserver(this);
                }else if (dataChar.equals("b") || dataChar.equals("p") || dataChar.equals("i") || dataChar.equals("c")) { //Création des fantômes en utilisant les différentes factories
                    switch (dataChar) {
                        case "b":
                            abstractGhostFactory = new BlinkyFactory();
                            break;
                        case "p":
                            abstractGhostFactory = new PinkyFactory();
                            break;
                        case "i":
                            abstractGhostFactory = new InkyFactory();
                            break;
                        case "c":
                            abstractGhostFactory = new ClydeFactory();
                            break;
                    }

                    Ghost ghost = abstractGhostFactory.makeGhost(xx * cellSize, yy * cellSize, difficultyParams);
                    ghosts.add(ghost);
                    if (dataChar.equals("b")) {
                        blinky = (Blinky) ghost;
                    }
                }else if (dataChar.equals(".")) { //Création des PacGums
                    objects.add(new PacGum(xx * cellSize, yy * cellSize));
                }else if (dataChar.equals("o")) { //Création des SuperPacGums
                    objects.add(new SuperPacGum(xx * cellSize, yy * cellSize));
                }else if (dataChar.equals("-")) { //Création des murs de la maison des fantômes
                    objects.add(new GhostHouse(xx * cellSize, yy * cellSize));
                }
            }
        }
        objects.add(pacman);
        objects.addAll(ghosts);

        for (Entity o : objects) {
            if (o instanceof Wall) {
                walls.add((Wall) o);
            }
        }
    }
}
