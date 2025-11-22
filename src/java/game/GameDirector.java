package game;

import game.utils.CsvReader;

import javax.swing.*;
import java.net.URISyntaxException;
import java.util.List;

public class GameDirector {
    private List<List<String>> map;
    private int ghostNumber = 4;
    private float ghostSpeed;
    private float pacmanSpeed;

    public GameDirector(int level) {
        mapLoader(level);
        setGhostNumber(level);
    }

    private void mapLoader(int level) {
        switch (level) {
            case 1:
                try {
                    this.map = new CsvReader().parseCsv(getClass().getClassLoader().getResource("level/level.csv").toURI());
                    System.out.println("*** Success to load map #1 ***");
                } catch (URISyntaxException e) {
                    System.out.println("*** Fail to load map #1 ***");
                    e.printStackTrace();
                }
                break;
            case 2:
                //Level 2
                JOptionPane.showMessageDialog(null, "해당 레벨은 아직 만들어지지 않았습니다!"); //맵 생성기 추가시 삭제.
                System.exit(0);
                //동환님의 Level 자동 생성기 등을 이곳에서 사용하여 data를 만듬.
                break;
            default:
                /*
                 * 이상한 값이 들어오면 레벨 1 로딩되게
                 * 혹은 에러를 뱉고 게임을 종료할 수도 있을듯.
                 * */
                try {
                    this.map = new CsvReader().parseCsv(getClass().getClassLoader().getResource("level/level.csv").toURI());
                    System.out.println("*** Success to load map #1 ***");
                } catch (URISyntaxException e) {
                    System.out.println("*** Fail to load map #1 ***");
                    e.printStackTrace();
                }
                break;
        }
    }

    //setter
    public void setGhostNumber(int ghostNumber) {
        this.ghostNumber = ghostNumber;
    }
    public void setGhostSpeed(float ghostSpeed) {
        this.ghostSpeed = ghostSpeed;
    }
    public void setPacmanSpeed(float pacmanSpeed) {
        this.pacmanSpeed = pacmanSpeed;
    }


    //getter
    public int getGhostNumber() {
        return ghostNumber;
    }
    public float getGhostSpeed() {
        return ghostSpeed;
    }
    public float getPacmanSpeed() {
        return pacmanSpeed;
    }
    public List<List<String>> getMap() {
        return map;
    }
}
