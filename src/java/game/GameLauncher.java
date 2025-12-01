package game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.awt.*;
import java.net.URL;

//Point d'entrée de l'application
public class GameLauncher {
    private static UIPanel uiPanel;
    private static JFrame mainFrame;
    private static JPanel gameWindow;
    private static GameplayPanel gameplayPanel;
    private static int level;

    public static void main(String[] args) {
        System.out.println("*** 프로그램 시작***");
        SkinSelector.set("PacmanSkin", "pacman.png"); //초기 팩맨 노란색 설정
        SkinSelector.set("PacmanSpeed", "2"); //초기 속도 2
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
        mainFrame.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 0, 10, 0);


        URL imgURL = GameLauncher.class.getClassLoader().getResource("img/title.png");
        ImageIcon titleIcon = new ImageIcon(imgURL);

        JLabel titleLabel = new JLabel(titleIcon);
        mainFrame.add(titleLabel, gbc);

        Dimension buttonSize = new Dimension(200, 40);

        gbc.gridy++;
        JButton startButton = new JButton("게임 시작");
        startButton.setPreferredSize(buttonSize);
        startButton.setFocusPainted(false);
        startButton.setContentAreaFilled(false);
        startButton.setOpaque(true);
        startButton.setBorderPainted(true);

        startButton.setBackground(Color.white);
        startButton.setForeground(Color.BLACK);
        mainFrame.add(startButton, gbc);

        gbc.gridy++;
        JButton optionsButton = new JButton("옵션");
        optionsButton.setPreferredSize(buttonSize);
        optionsButton.setFocusPainted(false);
        optionsButton.setContentAreaFilled(false);
        optionsButton.setOpaque(true);
        optionsButton.setBorderPainted(true);

        optionsButton.setBackground(Color.white);
        optionsButton.setForeground(Color.BLACK);
        mainFrame.add(optionsButton, gbc);

        gbc.gridy++;
        JButton exitButton = new JButton("종료");
        exitButton.setPreferredSize(buttonSize);
        exitButton.setFocusPainted(false);
        exitButton.setContentAreaFilled(false);
        exitButton.setOpaque(true);
        exitButton.setBorderPainted(true);

        exitButton.setBackground(Color.white);
        exitButton.setForeground(Color.BLACK);
        mainFrame.add(exitButton, gbc);

        startButton.addActionListener(e -> {
            mainFrame.dispose();
            levelPage();
        });

        optionsButton.addActionListener(e -> {
            optionMenu(mainFrame);
        });

        exitButton.addActionListener(e -> System.exit(0));

        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    private static void levelPage() {
        mainFrame = new JFrame("Select Level");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(448, 400);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        URL imgURL = GameLauncher.class.getClassLoader().getResource("img/levelTitle.png");
        ImageIcon titleIcon = new ImageIcon(imgURL);
        JLabel titleLabel = new JLabel(titleIcon, JLabel.CENTER); // 중앙 정렬
        mainPanel.add(titleLabel, BorderLayout.NORTH);


        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 5, 15, 15)); // 행, 열, 수평간격, 수직간격


        for (int i = 1; i <= 15; i++) {
            int levelNum = i;
            JButton levelButton = new JButton(String.valueOf(i));
            levelButton.addActionListener(e -> {
                level = levelNum;
                System.out.println("level " + level + " started...");
                mainFrame.dispose();
                startGame();
            });
            levelButton.setFocusPainted(false);
            levelButton.setContentAreaFilled(false);
            levelButton.setOpaque(true);
            levelButton.setBorderPainted(true);

            levelButton.setBackground(Color.white);
            levelButton.setForeground(Color.BLACK);

            buttonPanel.add(levelButton);
        }

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.setBackground(Color.WHITE);
        buttonPanel.setBackground(Color.WHITE);
        mainFrame.getContentPane().setBackground(Color.WHITE);

        mainFrame.add(mainPanel);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    private static void optionMenu(JFrame parent) {
        System.out.println("\n옵션메뉴 진입________________");
        JDialog optionDialog = new JDialog(parent, "옵션 메뉴", true);
        optionDialog.setLayout(new BoxLayout(optionDialog.getContentPane(), BoxLayout.Y_AXIS));
        optionDialog.setSize(500, 250);

        JPanel speedPanel = new JPanel();
        JPanel bottomButtons = new JPanel();
        JPanel checkBoxPanel = new JPanel();

        JCheckBox invincibleMode = new JCheckBox("Invincible");

        JButton closeButton = new JButton("닫기");
        JButton saveButton = new JButton("저장하기");

        JLabel speedLabel = new JLabel("팩맨 이동 속도:");
        JLabel speedValueLabel = new JLabel("현재 속도: 50");
        JSlider speedSlider = new JSlider(0, 100, 50);

        JLabel colorLabel = new JLabel("팩맨 색상:");
        String[] colors = {"노랑", "빨강", "파랑", "초록", "핑크", "하양"};
        JComboBox<String> colorCombo = new JComboBox<>(colors);
        colorCombo.setMaximumSize(new Dimension(200, 30));

        JPanel colorPanel = new JPanel();
        colorPanel.setLayout(new BoxLayout(colorPanel, BoxLayout.X_AXIS));
        colorPanel.add(colorLabel);
        colorPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        colorPanel.add(colorCombo);

        String inv = SkinSelector.get("invincible");
        if ("true".equals(inv)) {
            invincibleMode.setSelected(true);
        }

        speedSlider.setMajorTickSpacing(10);
        speedSlider.setMinorTickSpacing(1);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.addChangeListener(e -> {
            int value = speedSlider.getValue();
            speedValueLabel.setText("현재 속도: " + value);
            SkinSelector.set("PacmanSpeed", String.valueOf(1+value/60));
        });
        closeButton.addActionListener(e -> optionDialog.dispose());
        saveButton.addActionListener(e -> {
            int pacmanSpeed = speedSlider.getValue();
            int enemySpeed; //.getValue()형식으로 얻어내는거 생각해보기

            switch ((String)colorCombo.getSelectedItem()) {
                case "노랑" :
                    SkinSelector.set("PacmanSkin", "pacman.png");
                    break;
                case "빨강" :
                    SkinSelector.set("PacmanSkin", "pacman_red.png");
                    break;
                case "파랑" :
                    SkinSelector.set("PacmanSkin", "pacman_blue.png");
                    break;
                case "초록" :
                    SkinSelector.set("PacmanSkin", "pacman_green.png");
                    break;
                case "핑크" :
                    SkinSelector.set("PacmanSkin", "pacman_pink.png");
                    break;
                case "하양" :
                    SkinSelector.set("PacmanSkin", "pacman_white.png");
                    break;
                default:
                    break;
            }
            if (invincibleMode.isSelected()) {
                SkinSelector.set("invincible", "true");
            }
            if(!invincibleMode.isSelected()) {
                SkinSelector.set("invincible", "false");
            }
            /*
             *
             * 이곳에서 실제 옵션 변경이 일어나면 될것 같습니다.
             *
             *
             * */
            System.out.println("저장버튼 클릭됨!!");
            optionDialog.dispose();

            //entity.putValue(); 식으로 저장.
        });

        speedPanel.setLayout(new BoxLayout(speedPanel, BoxLayout.X_AXIS));
        speedPanel.setOpaque(false);
        speedPanel.add(speedLabel, BorderLayout.NORTH);
        speedPanel.add(speedSlider, BorderLayout.CENTER);
        speedPanel.add(speedValueLabel, BorderLayout.SOUTH);

        invincibleMode.setAlignmentX(Component.LEFT_ALIGNMENT);
        checkBoxPanel.add(invincibleMode);
        checkBoxPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        closeButton.setFocusPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setOpaque(true);
        closeButton.setBorderPainted(true);
        closeButton.setBackground(Color.white);
        closeButton.setForeground(Color.BLACK);

        saveButton.setFocusPainted(false);
        saveButton.setContentAreaFilled(false);
        saveButton.setOpaque(true);
        saveButton.setBorderPainted(true);
        saveButton.setBackground(Color.white);
        saveButton.setForeground(Color.BLACK);

        closeButton.setMaximumSize(new Dimension(100, 30));
        saveButton.setMaximumSize(new Dimension(100, 30));

        bottomButtons.setLayout(new BoxLayout(bottomButtons, BoxLayout.X_AXIS));
        bottomButtons.setOpaque(false);
        bottomButtons.add(closeButton);
        bottomButtons.add(Box.createRigidArea(new Dimension(5,0)));
        bottomButtons.add(saveButton);
        bottomButtons.add(Box.createRigidArea(new Dimension(15,0)));

        colorPanel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        optionDialog.add(colorPanel);
        optionDialog.add(Box.createRigidArea(new Dimension(0,15)));
        optionDialog.add(checkBoxPanel);
        optionDialog.add(Box.createRigidArea(new Dimension(0,15)));
        optionDialog.add(speedPanel);
        optionDialog.add(Box.createRigidArea(new Dimension(0,15)));
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
            gameplayPanel = new GameplayPanel(448, 496); // 레벨을 여기서 설정.
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