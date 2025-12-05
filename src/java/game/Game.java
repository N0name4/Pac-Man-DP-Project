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
import game.map.RandomLevelGenerator;
import game.utils.CollisionDetector;
import game.utils.KeyHandler;
import game.SkinSelector;

import java.awt.*;
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
    private static boolean invincibleMode = false;

    // Game Overall Params Object
    private final DifficultyParams difficultyParams;

    // Game Cycle Observer List
    private List<GameLifeListener> gameLifeListeners = new ArrayList<>();

    // Score & GameEnding State
    private int score = 0;
    private boolean finished = false;
    private static final int GAME_END_SCORE = 2000;  // Game Goal Score

    private List<List<String>> levelData;
    private int cellsPerRow = 28;
    private int cellsPerColumn = 31;
    int cellSize = 8;


    public Game(DifficultyParams difficultyParams){
        this.difficultyParams = difficultyParams;
        Game.setFirstInput(false);
        if(SkinSelector.get("invincible").equals("true")) {
            invincibleMode = true;
        }
        else {
            invincibleMode = false;
        }
        init();
    }

    public void update() {
        for (Entity o: objects) {
            if (!o.isDestroyed()) o.update();
        }
    }

    public void input(KeyHandler k) {
        pacman.input(k);
    }

    public void render(Graphics2D g) {
        for (Entity o: objects) {
            if (!o.isDestroyed()) o.render(g);
        }
    }

    public static List<Wall> getWalls() {
        return walls;
    }

    public List<Entity> getEntities() {
        return objects;
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

        // State Change
        for (Ghost gh : ghosts) {
            gh.getState().superPacGumEaten();
        }

        checkRoundClear();
    }

    @Override
    public void updateGhostCollision(Ghost gh) {
        if (finished) return;

        if(!invincibleMode) {
            // State Change
            if (gh.getState() instanceof FrightenedMode) {
                gh.getState().eaten();
                addScore(difficultyParams.getGhostEatenScore());
                checkRoundClear();
            } else if (!(gh.getState() instanceof EatenMode)) {
                finished = true;
                notifyListenerGameOver();
            }
        }
    }

    // Score Domain Implementation (from UIPanel)
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
        walls.clear();  
        objects.clear();
        ghosts.clear();


        data = new RandomLevelGenerator().generate(cellsPerRow, cellsPerColumn);

        cellsPerRow = data.get(0).size();
        cellsPerColumn = data.size();

        this.levelData = data;

        CollisionDetector collisionDetector = new CollisionDetector(this);
        AbstractGhostFactory abstractGhostFactory = null;


        long start = System.nanoTime(); //시간 측정용

        Integer inkyX = null;
        Integer inkyY = null;
        //Le niveau a une "grille", et pour chaque case du fichier csv, on affiche une entité parculière sur une case de la grille selon le caracère présent
        for (int xx = 0; xx < cellsPerRow; xx++) {
            for (int yy = 0; yy < cellsPerColumn; yy++) {
                String dataChar = data.get(yy).get(xx);
                if (dataChar.equals("x")) { //Création des murs
                    objects.add(new Wall(xx * cellSize, yy * cellSize));
                } else if (dataChar.equals("P")) { //Création de Pacman
                    pacman = new Pacman(xx * cellSize, yy * cellSize, difficultyParams);
                    pacman.setCollisionDetector(collisionDetector);

                    //Enregistrement des différents observers de Pacman
                    //pacman.registerObserver(GameLauncher.getUIPanel());
                    pacman.registerObserver(this);
                } else if (dataChar.equals("b")) {
                    // 블링키 생성
                    abstractGhostFactory = new BlinkyFactory();
                    Ghost ghost = abstractGhostFactory.makeGhost(xx * cellSize, yy * cellSize, difficultyParams);
                    ghosts.add(ghost);
                    blinky = (Blinky) ghost;

                    // 블링키 생성 후, 저장된 Inky가 있으면 바로 생성
                    if (inkyX != null && inkyY != null) {
                        abstractGhostFactory = new InkyFactory();
                        Ghost inky = abstractGhostFactory.makeGhost(inkyX * cellSize, inkyY * cellSize, difficultyParams);
                        ghosts.add(inky);
                        inkyX = null;  // 사용 후 초기화
                        inkyY = null;
                    }
                } else if (dataChar.equals("p")) {
                    // 핑키는 바로 생성 (순서 상관없음)
                    abstractGhostFactory = new PinkyFactory();
                    Ghost ghost = abstractGhostFactory.makeGhost(xx * cellSize, yy * cellSize, difficultyParams);
                    ghosts.add(ghost);
                } else if (dataChar.equals("i")) {
                    // Inky는 일단 위치만 저장 (블링키가 아직 안 만들어졌을 수 있음)
                    inkyX = xx;
                    inkyY = yy;
                } else if (dataChar.equals("c")) {
                    abstractGhostFactory = new ClydeFactory();
                    Ghost ghost = abstractGhostFactory.makeGhost(xx * cellSize, yy * cellSize, difficultyParams);
                    ghosts.add(ghost);
                } else if (dataChar.equals(".")) {
                    objects.add(new PacGum(xx * cellSize, yy * cellSize));
                } else if (dataChar.equals("o")) {
                    objects.add(new SuperPacGum(xx * cellSize, yy * cellSize));
                } else if (dataChar.equals("-")) {
                    objects.add(new GhostHouse(xx * cellSize, yy * cellSize));
                }
            }
        }

        // 루프 끝났는데 Inky만 저장되어 있고 블링키가 없었다면 여기서 생성
        if (inkyX != null && inkyY != null) {
            abstractGhostFactory = new InkyFactory();
            Ghost inky = abstractGhostFactory.makeGhost(inkyX * cellSize, inkyY * cellSize, difficultyParams);
            ghosts.add(inky);
        }

        long end = System.nanoTime();
        long elapsedNs = end - start;
        double elapsedMs = elapsedNs / 1_000_000.0;

        System.out.println("_____________map creation time_____________");
        System.out.println("ns: " + elapsedNs);
        System.out.println(String.format("ms: %.3f", elapsedMs));

        objects.add(pacman);
        objects.addAll(ghosts);

        for (Entity o : objects) {
            if (o instanceof Wall) {
                walls.add((Wall) o);
            }
        }
    }

    public List<List<String>> getLevelData() {
        return levelData;
    }

    public int getCellsPerRow() {
        return cellsPerRow;
    }

    public int getCellsPerColumn() {
        return cellsPerColumn;
    }

    public int getCellSize() {
        return cellSize;
    }
}
