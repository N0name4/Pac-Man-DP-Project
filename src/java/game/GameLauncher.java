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

    public static void main(String[] args) {
        System.out.println("프로그램 시작");
        MainPage();
        /*
        * 메인페이지 생성은 MainPage();
        * 게임 시작은 startGame();
        * 옵션 메뉴는 optionMenu(); 안에 두었습니다.
        * */

    }
    public static void frameDisposer() {
        mainFrame.dispose();
    }

    public static void MainPage() {
        System.out.println("메인페이지 진입");
        mainFrame = new JFrame("Pacman Menu");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(500, 500);
        mainFrame.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));

        JButton startButton = new JButton("게임 시작");
        JButton optionsButton = new JButton("옵션");
        JButton exitButton = new JButton("종료");

        mainFrame.add(startButton);
        mainFrame.add(optionsButton);
        mainFrame.add(exitButton);


        startButton.addActionListener((ActionEvent e) -> {
            mainFrame.dispose();
            startGame();
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

    private static void startGame() {
        System.out.println("게임 시작");
        mainFrame = new JFrame("Pacman");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gameWindow = new JPanel();

        try {
            gameplayPanel = new GameplayPanel(448, 496);
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

    private static void optionMenu(JFrame parent) {
        System.out.println("옵션메뉴 진입");
        JDialog optionDialog = new JDialog(parent, "옵션 메뉴", true);
        optionDialog.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        optionDialog.setSize(500, 500);

        JPanel speedPanel = new JPanel(new BorderLayout());
        speedPanel.setOpaque(false);

        JLabel speedLabel = new JLabel("팩맨 이동 속도:");
        JLabel speedValueLabel = new JLabel("현재 속도: 50");
        JSlider speedSlider = new JSlider(0, 100, 50);

        speedSlider.setMajorTickSpacing(20);
        speedSlider.setMinorTickSpacing(5);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.addChangeListener(e -> {
            int value = speedSlider.getValue();
            speedValueLabel.setText("현재 속도: " + value);
        });

        speedPanel.add(speedLabel, BorderLayout.NORTH);
        speedPanel.add(speedSlider, BorderLayout.CENTER);
        speedPanel.add(speedValueLabel, BorderLayout.SOUTH);

        JCheckBox checkBox1 = new JCheckBox("체크박스 1번", true);
        JCheckBox checkBox2 = new JCheckBox("체크박스 2번", true);
        checkBox1.setOpaque(false);
        checkBox2.setOpaque(false);

        String[] difficulties = {"쉬움", "보통", "어려움"};
        JComboBox<String> difficultyCombo = new JComboBox<>(difficulties);

        JButton closeButton = new JButton("닫기");
        closeButton.addActionListener(e -> optionDialog.dispose());

        optionDialog.add(checkBox1);
        optionDialog.add(checkBox2);
        optionDialog.add(difficultyCombo);
        optionDialog.add(speedPanel);
        optionDialog.add(closeButton);

        optionDialog.setLocationRelativeTo(parent);
        optionDialog.setVisible(true);
    }

    public static UIPanel getUIPanel() {
        return uiPanel;
    }
    public static GameplayPanel getGameplayPanel() {
        return gameplayPanel;
    }
}
