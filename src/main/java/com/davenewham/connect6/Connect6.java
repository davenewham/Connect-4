package com.davenewham.connect6;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Connect6 {

    public static int SLOTS_WIDE = 7;
    public static int SLOTS_HIGH = 6;

    public static int HANDBAR = 120;
    public static int SIDEBAR = 200;
    public static int SLOT = 80;
    public static int MARGIN = 10;

    public static int BOARD_WIDTH = SLOT * SLOTS_WIDE;
    public static int BOARD_HEIGHT = SLOT * SLOTS_HIGH;

    public static int WIDTH = BOARD_WIDTH + SIDEBAR;
    public static int HEIGHT = BOARD_HEIGHT + HANDBAR;

    public static BufferedImage imgBoard;
    public static BufferedImage imgRed;
    public static BufferedImage imgYellow;

    private static JFrame frame;

    private BoardPanel boardPanel;

    private JPanel mainPanel;
    private JPanel leftPanel;

    private Player first;

    private Player[] players;

    public Connect6() {
        String[] choices = {"Player vs. Player", "Player vs. AI", "AI vs. AI"};
        String input = (String) JOptionPane.showInputDialog(null, "Choose your gamemode", "Gamemode", JOptionPane.QUESTION_MESSAGE, null, choices, choices[1]);
        System.out.print(input);

        if (!input.equalsIgnoreCase(choices[0])) {
            Player.TWO.setAi(true);

            if (input.equalsIgnoreCase(choices[2])) {
                Player.ONE.setAi(true);
            }
        }


        first = Player.ONE;
        players = new Player[2];

        try {
            imgBoard = ImageIO.read(BoardPanel.class.getClassLoader().getResourceAsStream("board.png"));
            imgRed = ImageIO.read(BoardPanel.class.getClassLoader().getResourceAsStream("red-circle.png"));
            imgYellow = ImageIO.read(BoardPanel.class.getClassLoader().getResourceAsStream("yellow-circle.png"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return;
        }

        frame = new JFrame("Connect 6");
        frame.setIconImage(first.getImage());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(createMain());
        frame.pack();
        frame.setVisible(true);

        boardPanel.start();
    }

    public boolean isFinished() {
        return boardPanel.isFull() || boardPanel.isWon();
    }

    public JPanel createMain() {
        mainPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        leftPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        leftPanel.setBackground(Color.RED);
        leftPanel.setPreferredSize(new Dimension(SLOT * SLOTS_WIDE, HEIGHT));

        createBoard();
        leftPanel.add(createHand());
        leftPanel.add(boardPanel);

        mainPanel.add(leftPanel);
        mainPanel.add(createSidebar());
        return mainPanel;
    }

    public JPanel createSidebar() {
        JPanel sidebar = new JPanel();

        sidebar.setBackground(new Color(242, 242, 242));
        sidebar.setPreferredSize(new Dimension(SIDEBAR + MARGIN, HEIGHT));

        JLabel label = new JLabel();
        boardPanel.setLabel(label);
        sidebar.add(label);
        sidebar.add(Box.createVerticalStrut(25));

        JButton restart = new JButton("Restart");
        restart.addMouseListener(new RestartListener());
        restart.setPreferredSize(new Dimension(SIDEBAR, 50));
        sidebar.add(restart);
        sidebar.add(Box.createVerticalStrut(25));

        setup(sidebar, Player.ONE);
        setup(sidebar, Player.TWO);

        return sidebar;
    }

    public void setup(JPanel sidebar, Player player) {
        JLabel name = new JLabel(player.getName(), JLabel.CENTER);
        double height = name.getPreferredSize().getHeight();
        name.setPreferredSize(new Dimension(SIDEBAR, (int) height));
        player.setLabel(name);

        JButton rename = new JButton("Rename");
        player.setRenameButton(rename);
        player.setName();
        rename.addMouseListener(new RenameListener(player, rename));
        rename.setPreferredSize(new Dimension(SIDEBAR / 2 - 3, 50));

        JButton ai = new JButton("Use AI");
        player.setAiButton(ai);
        ai.addMouseListener(new RenameListener(player, ai));
        ai.setPreferredSize(new Dimension(SIDEBAR / 2 - 3, 50));
        ai.setEnabled(false);

        sidebar.add(name);
        sidebar.add(Box.createVerticalStrut(25));
        sidebar.add(rename);
        sidebar.add(ai);
        sidebar.add(Box.createVerticalStrut(25));
    }

    public JPanel createHand() {
        JPanel hand = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        hand.setBackground(new Color(242, 242, 242));
        hand.setPreferredSize(new Dimension(SLOT * SLOTS_WIDE + MARGIN, HANDBAR));

        for (int i = 0; i < SLOTS_WIDE; i++) {
            CounterSlot panel = new CounterSlot(i, this);
            panel.setBackground(new Color(242, 242, 242));
            hand.add(panel);
            boardPanel.getSlots()[i] = panel;
        }

        return hand;
    }


    static void showNameDialog(Player player) {

        String response = JOptionPane.showInputDialog(null, "Enter a new name for " + player.getName() + ":", player.hasName() ? player.getName() : null);

        if (response != null && !response.equals("")) {
            player.setName(response);
        } else {
            player.setName(null);
        }
    }


    public BoardPanel createBoard() {
        boardPanel = new BoardPanel(SLOTS_WIDE, SLOTS_HIGH, SLOT);
        boardPanel.setBackground(Color.PINK);
        boardPanel.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        return boardPanel;
    }

    public BoardPanel getBoardPanel() {
        return boardPanel;
    }

    public static JFrame getFrame() {
        return frame;
    }

    public class RestartListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            short[][] board = boardPanel.getBoard();
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    board[i][j] = 0;
                }
            }

            boardPanel.setCurrentPlayer(first);
            Connect6.getFrame().setIconImage(first.getImage());
            first = first.getOther();
            boardPanel.setWinner(null);
            boardPanel.setName();

            boardPanel.repaint();
            boardPanel.revalidate();

            for (CounterSlot slot : boardPanel.getSlots()) {
                slot.revalidate();
                slot.repaint();
            }

            boardPanel.start();
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {
        }

    }

    public class RenameListener implements MouseListener {

        private Player player;
        private JButton button;

        public RenameListener(Player player, JButton button) {
            this.player = player;
            this.button = button;
        }

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            if (button.isEnabled()) {
                showNameDialog(player);
            }
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {
        }

    }

}
