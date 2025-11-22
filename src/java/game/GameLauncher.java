package game;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.awt.*;

public class GameLauncher {
    private static UIPanel uiPanel;
    private static JFrame mainFrame;
    private static JPanel gameWindow;
    private static GameplayPanel gameplayPanel;
    private static int level;

    public static void main(String[] args) {
        System.out.println("*** 프로그램 시작***");
        MainPage();
        /*
        * 메인페이지 생성은 MainPage();
        * 게임 시작은 startGame()
        * 레벨 메뉴는 levelPage()
        * 옵션 메뉴는 optionMenu() 안에 두었습니다.
        * */
    }
    public static void frameDisposer() {
        gameplayPanel.stopGame();
        mainFrame.dispose();
    }

    public static void MainPage() {
        System.out.println("\n메인페이지 진입_________________");
        mainFrame = new JFrame("Pacman Menu");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(448, 496);
        mainFrame.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));

        JButton startButton = new JButton("게임 시작");
        JButton optionsButton = new JButton("옵션");
        JButton exitButton = new JButton("종료");

        mainFrame.add(startButton);
        mainFrame.add(optionsButton);
        mainFrame.add(exitButton);


        startButton.addActionListener((ActionEvent e) -> {
            mainFrame.dispose();
            levelPage();
            //startGame();
        });

        optionsButton.addActionListener((ActionEvent e) -> {
            optionMenu(mainFrame);
        });

        exitButton.addActionListener((ActionEvent e) -> {
            System.exit(0);
        });

        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    private static void levelPage() {
        mainFrame = new JFrame("Select Level");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(448,496);

        JPanel levelWindow = new JPanel();

        levelWindow.setSize(448,496);
        levelWindow.setLayout(new FlowLayout());

        JButton level1 = new JButton("1");
        JButton level2 = new JButton("2");
        JButton level3 = new JButton("3");

        level1.addActionListener(e -> {
            level = 1;
            System.out.println("level " +level+ " started...");
            mainFrame.dispose();
            startGame();
        });
        level2.addActionListener(e -> {
            level = 2;
            System.out.println("level " +level+ " started...");
            mainFrame.dispose();
            startGame();
        });
        level3.addActionListener(e -> {
            level = 3;
            System.out.println("level " +level+ " started...");
            mainFrame.dispose();
            startGame();
        });


        levelWindow.add(level1);
        levelWindow.add(level2);
        levelWindow.add(level3);

        mainFrame.add(levelWindow);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    private static void optionMenu(JFrame parent) {
        System.out.println("\n옵션메뉴 진입________________");
        JDialog optionDialog = new JDialog(parent, "옵션 메뉴", true);
        optionDialog.setLayout(new BoxLayout(optionDialog.getContentPane(), BoxLayout.Y_AXIS));
        optionDialog.setSize(500, 300);

        JPanel speedPanel = new JPanel();
        JPanel bottomButtons = new JPanel();

        JCheckBox checkBox1 = new JCheckBox("체크박스 1번", true);
        JCheckBox checkBox2 = new JCheckBox("체크박스 2번", true);

        JButton closeButton = new JButton("닫기");
        JButton saveButton = new JButton("저장하기");

        JLabel speedLabel = new JLabel("팩맨 이동 속도:");
        JLabel speedValueLabel = new JLabel("현재 속도: 50");
        JSlider speedSlider = new JSlider(0, 100, 50);

        String[] difficulties = {"쉬움", "보통", "어려움"};
        JComboBox<String> difficultyCombo = new JComboBox<>(difficulties);
        difficultyCombo.setMaximumSize(new Dimension(200, 30));

        speedSlider.setMajorTickSpacing(20);
        speedSlider.setMinorTickSpacing(5);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.addChangeListener(e -> {
            int value = speedSlider.getValue();
            speedValueLabel.setText("현재 속도: " + value);
        });
        closeButton.addActionListener(e -> optionDialog.dispose());
        saveButton.addActionListener(e -> {
            int pacmanSpeed = speedSlider.getValue();
            int enemySpeed; //.getValue()형식으로 얻어내는거 생각해보기
            System.out.println("저장버튼 클릭됨!!");
            /*
             *
             * 이곳에서 실제 옵션 변경이 일어나면 될것 같습니다.
             *
             *
             * */
            //entity.putValue(); 식으로 저장.
        });

        speedPanel.setLayout(new BoxLayout(speedPanel, BoxLayout.X_AXIS));
        speedPanel.setOpaque(false);
        speedPanel.add(speedLabel, BorderLayout.NORTH);
        speedPanel.add(speedSlider, BorderLayout.CENTER);
        speedPanel.add(speedValueLabel, BorderLayout.SOUTH);

        bottomButtons.setLayout(new BoxLayout(bottomButtons, BoxLayout.X_AXIS));
        bottomButtons.setOpaque(false);
        bottomButtons.add(closeButton);
        bottomButtons.add(saveButton);

        checkBox1.setOpaque(false);
        checkBox2.setOpaque(false);


        optionDialog.add(checkBox1);
        optionDialog.add(checkBox2);
        optionDialog.add(difficultyCombo);
        optionDialog.add(speedPanel);
        optionDialog.add(bottomButtons);

        optionDialog.setLocationRelativeTo(parent);
        optionDialog.setVisible(true);
    }

    private static void startGame() {
        System.out.println("\n게임 시작____________________");
        mainFrame = new JFrame("Pacman");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gameWindow = new JPanel();

        try {
            gameplayPanel = new GameplayPanel(448, 496, level); // 레벨을 여기서 설정.
            gameWindow.add(gameplayPanel);
        } catch (IOException e) {
            e.printStackTrace();
        }

        uiPanel = new UIPanel(256, 496);
        gameWindow.add(uiPanel);

        mainFrame.setContentPane(gameWindow);
        mainFrame.setResizable(false);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    public static UIPanel getUIPanel() {
        return uiPanel;
    }
    public static GameplayPanel getGameplayPanel() {
        return gameplayPanel;
    }
}
