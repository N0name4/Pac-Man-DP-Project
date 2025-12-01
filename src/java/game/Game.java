package game;

import game.entities.*;
import game.entities.ghosts.Blinky;
import game.entities.ghosts.Ghost;
import game.ghostFactory.*;
import game.ghostStates.EatenMode;
import game.ghostStates.FrightenedMode;
import game.utils.CollisionDetector;
import game.utils.CsvReader;
import game.utils.KeyHandler;

import java.awt.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import game.utils.RandomLevelGenerator;
import game.utils.WFCLevelGenerator;

//Classe gérant le jeu en lui même
public class Game implements Observer {
    //Pour lister les différentes entités présentes sur la fenêtre
    private List<Entity> objects = new ArrayList();
    private List<Ghost> ghosts = new ArrayList();
    private static List<Wall> walls = new ArrayList();

    private static Pacman pacman;
    private static Blinky blinky;

    private static boolean firstInput = false;
    
    private List<List<String>> levelData;
    private int cellsPerRow = 28;  // 가로 (열 수)
    private int cellsPerColumn = 31; // 세로 (행 수)
    int cellSize = 8;


    public Game(){
        //Initialisation du jeu

        //Chargement du fichier csv du niveau
        List<List<String>> data = null;
        List<List<String>> data2 = null;
        try {
            data = new CsvReader().parseCsv(getClass().getClassLoader().getResource("level/level.csv").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        
        
        
        cellsPerRow = data.get(0).size();  // 가로 (열 수)
        cellsPerColumn =  data.size(); // 세로 (행 수)*/
        //data = new RandomLevelGenerator().generate(cellsPerRow, cellsPerColumn);
        //data = new WFCLevelGenerator().generate(cellsPerRow, cellsPerColumn);
        cellsPerRow = data.get(0).size();
        cellsPerColumn = data.size();
        
        for (int r = 0; r < data.size(); r++)          // row
        {
            for (int c = 0; c < data.get(0).size(); c++)   // col
            {
                System.out.print(data.get(r).get(c));
            }
            System.out.println();
        }
        
        this.levelData = data;

        CollisionDetector collisionDetector = new CollisionDetector(this);
        AbstractGhostFactory abstractGhostFactory = null;

        Integer inkyX = null;
        Integer inkyY = null;
        //Le niveau a une "grille", et pour chaque case du fichier csv, on affiche une entité parculière sur une case de la grille selon le caracère présent
        for(int xx = 0 ; xx < cellsPerRow ; xx++) {
            for(int yy = 0 ; yy < cellsPerColumn ; yy++) {
                String dataChar = data.get(yy).get(xx);
                if (dataChar.equals("x")) { //Création des murs
                    objects.add(new Wall(xx * cellSize, yy * cellSize));
                }else if (dataChar.equals("P")) { //Création de Pacman
                    pacman = new Pacman(xx * cellSize, yy * cellSize);
                    pacman.setCollisionDetector(collisionDetector);

                    //Enregistrement des différents observers de Pacman
                    //pacman.registerObserver(GameLauncher.getUIPanel());
                    pacman.registerObserver(this);
                }else if (dataChar.equals("b")) {
                    // 블링키 생성
                    abstractGhostFactory = new BlinkyFactory();
                    Ghost ghost = abstractGhostFactory.makeGhost(xx * cellSize, yy * cellSize);
                    ghosts.add(ghost);
                    blinky = (Blinky) ghost;
                    
                    // 블링키 생성 후, 저장된 Inky가 있으면 바로 생성
                    if (inkyX != null && inkyY != null) {
                        abstractGhostFactory = new InkyFactory();
                        Ghost inky = abstractGhostFactory.makeGhost(inkyX * cellSize, inkyY * cellSize);
                        ghosts.add(inky);
                        inkyX = null;  // 사용 후 초기화
                        inkyY = null;
                    }
                } else if (dataChar.equals("p")) {
                    // 핑키는 바로 생성 (순서 상관없음)
                    abstractGhostFactory = new PinkyFactory();
                    Ghost ghost = abstractGhostFactory.makeGhost(xx * cellSize, yy * cellSize);
                    ghosts.add(ghost);
                } else if (dataChar.equals("i")) {
                    // Inky는 일단 위치만 저장 (블링키가 아직 안 만들어졌을 수 있음)
                    inkyX = xx;
                    inkyY = yy;
                } else if (dataChar.equals("c")) {
                    abstractGhostFactory = new ClydeFactory();
                    Ghost ghost = abstractGhostFactory.makeGhost(xx * cellSize, yy * cellSize);
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
            Ghost inky = abstractGhostFactory.makeGhost(inkyX * cellSize, inkyY * cellSize);
            ghosts.add(inky);
        }
        
        objects.add(pacman);
        objects.addAll(ghosts);

        for (Entity o : objects) {
            if (o instanceof Wall) {
                walls.add((Wall) o);
            }
        }
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

    //Le jeu est notifiée lorsque Pacman est en contact avec une PacGum, une SuperPacGum ou un fantôme
    @Override
    public void updatePacGumEaten(PacGum pg) {
        pg.destroy(); //La PacGum est détruite quand Pacman la mange
    }

    @Override
    public void updateSuperPacGumEaten(SuperPacGum spg) {
        spg.destroy(); //La SuperPacGum est détruite quand Pacman la mange
        for (Ghost gh : ghosts) {
            gh.getState().superPacGumEaten(); //S'il existe une transition particulière quand une SuperPacGum est mangée, l'état des fantômes change
        }
    }

    @Override
    public void updateGhostCollision(Ghost gh) {
        if (gh.getState() instanceof FrightenedMode) {
            gh.getState().eaten(); //S'il existe une transition particulière quand le fantôme est mangé, son état change en conséquence
        }else if (!(gh.getState() instanceof EatenMode)) {
            System.out.println("Game over !\nScore : " + GameLauncher.getUIPanel().getScore()); //Quand Pacman rentre en contact avec un Fantôme qui n'est ni effrayé, ni mangé, c'est game over !
            System.exit(0); //TODO
        }
    }

    public static void setFirstInput(boolean b) {
        firstInput = b;
    }

    public static boolean getFirstInput() {
        return firstInput;
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
