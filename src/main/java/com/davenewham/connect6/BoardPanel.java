package com.davenewham.connect6;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.davenewham.connect6.Connect6.*;
import static com.davenewham.connect6.Player.*;

public class BoardPanel extends JPanel {

    private int w;
    private int h;
    private int sw;

    /**
     * 0 for empty
     * 1 for player 1
     * 2 for player 2
     */

    /**
     * 0 for human player
     * 1 for non-human player(ai)
     */

    private int difficulty;
    private short[][] board;
    private Player currentPlayer;
    private Player winner;

    private ConnectAI ai;

    private JLabel label;

    private CounterSlot[] slots;

    public BoardPanel(int width, int height, int slotWidth) {
        super(new FlowLayout(FlowLayout.CENTER, 0, 0));
        difficulty = 10;
        w = width;
        h = height;
        sw = slotWidth;
        setPreferredSize(new Dimension(w * slotWidth, h * slotWidth));

        board = new short[width][height];
        currentPlayer = ONE;
        slots = new CounterSlot[w];

        display(currentPlayer);
        display(currentPlayer.getOther());

        ai = new ConnectAI(this);
    }

    public void start() {
        if (currentPlayer.isAi()) {
            ai();
        }
    }

    private void ai() {
        //short move = bestMove(currentPlayer, board, difficulty);
        short move = ai.bestMove(board, currentPlayer.getId(), 6);
        if (board[move][h - 1] != 0) {
            System.out.println("full");
        }

        placeDisks(move, currentPlayer);
    }

    private void display(Player player) {
        if (!player.isAi()) {
            Connect6.showNameDialog(player);
        }
    }

    public CounterSlot[] getSlots() {
        return slots;
    }

    public short[][] getBoard() {
        return board;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public JLabel getLabel() {
        return label;
    }

    public void setLabel(JLabel label) {
        this.label = label;
        setName();
    }
    /* Sets name of each player,
     * defaults second player to 'computer' if playing against ai. */

    public void setName() {
        if (winner != null) {
            label.setText(winner.getName() + " wins");
            return;
        }

        if (isFull()) {
            label.setText("Board is full - game is a draw");
            return;
        }

        label.setText(currentPlayer.getName() + "'s turn");
    }

    // catches if board is full - ends game
    public boolean isFull() {
        for (int x = 0; x < w; x++) {
            if (board[x][h - 1] == 0) {
                return false;
            }
        }

        return true;
    }

    public boolean isFull(int row) {
        for (int y = 0; y < SLOTS_HIGH; y++) {
            if (board[row][y] == 0) {
                return false;
            }
        }

        return true;
    }

    public boolean isWon() {
        return winner != null;
    }

    public void updateArray(short[][] board) {
        this.board = board;
    }


    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        int width = 0;
        int height = 0;

        int totalWidth = w * SLOT;
        int totalHeight = h * SLOT;

        do {
            width += imgBoard.getWidth();
            while (totalHeight >= height) {
                height += imgBoard.getHeight();
                graphics.drawImage(imgBoard, width - imgBoard.getWidth(), height - imgBoard.getHeight(), null);
            }

            height = 0;
        } while (totalWidth >= width);

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (board[x][y] == 1) {
                    graphics.drawImage(imgRed, x * sw + 5, (h - y - 1) * sw + 5, null);
                } else if (board[x][y] == 2) {
                    graphics.drawImage(imgYellow, x * sw + 5, (h - y - 1) * sw + 5, null);
                }
            }
        }
    }

    public short getIndex(short index) {
        for (short y = 0; y < SLOTS_HIGH; y++) {
            if (board[index][y] == 0) {
                return y;
            }
        }

        return -1;
    }

    public boolean placeDisks(short index, Player player) {
        if (winner != null) {
            return false;
        }

        for (short y = 0; y < SLOTS_HIGH; y++) {
            if (board[index][y] == 0) {
                board[index][y] = player.getId();

                updateArray(board);

                for (CounterSlot slot : slots) {
                    if (slot != null && slot.getPlayer() != null) {
                        slot.revalidate();
                        slot.repaint();
                    }
                }

                // repaints components (needs looking at - currently waits for next move)
                revalidate();
                repaint();

                isComplete(index, y);

                // changes player
                currentPlayer = player.getOther();

                Connect6.getFrame().setIconImage(currentPlayer.getImage());
                setName();


                if (currentPlayer.isAi()) {
                    ai();
                }

                return true;
            }
        }

        return false;
    }

    // places the players piece into the clicked column.
    public boolean clicked(int x) {
        return clicked((short) x);
    }

    // places the players piece into the clicked column.
    public boolean clicked(short x) {
        return winner == null && placeDisks(x, currentPlayer);
    }

    public boolean isComplete(short x, short y) {
        boolean won = checkWin(x, y);
        if (won && winner == null) {
            winner = currentPlayer;

            setName();

            JOptionPane pane = new JOptionPane(winner.getName() + " is the winner");
            JDialog dialog = pane.createDialog(this, "Connect 4");
            dialog.show();
        }
        return won;
    }

    public static int score(short[][] board, short x, short y) {
        int score = 0;

        int adj;
        if ((adj = score(board, board[x][y], x, y, 1, 0)) > score) {
            score = adj;
        }

        if ((adj = score(board, board[x][y], x, y, 0, 1)) > score) {
            score = adj;
        }

        if ((adj = score(board, board[x][y], x, y, 1, 1)) > score) {
            score = adj;
        }

        if ((adj = score(board, board[x][y], x, y, 1, -1)) > score) {
            score = adj;
        }

        return score;
    }

    public boolean checkWin(short x, short y) {
        return score(board, x, y) >= 4;
    }

    // checks adjacent slots in the direction determined by the 
    private boolean checkAdjacent(int id, short x, short y, int xd, int yd) {
        return score(board, id, x, y, xd, yd) >= 4;
    }

    private static int score(short[][] board, int id, short x, short y, int xd, int yd) {
        short adj = 1;
        short xt = (short) (x - xd);
        short yt = (short) (y - yd);

        while (xt >= 0 && yt >= 0 && yt < SLOTS_HIGH && board[xt][yt] == id) {
            adj++;
            xt -= xd;
            yt -= yd;
        }

        xt = (short) (x + xd);
        yt = (short) (y + yd);

        while (xt < SLOTS_WIDE && yt >= 0 && yt < SLOTS_HIGH && board[xt][yt] == id) {
            adj++;
            xt += xd;
            yt += yd;
        }

        return adj;
    }

    private short bestMove(Player player, short[][] orig, int depth) {
        double mod = 1D / depth;
        if (depth - 1 <= 0) {
            return 0;
        }

        short[][] board = new short[w][h];
        for (int i = 0; i < orig.length; i++) {
            for (int j = 0; j < orig[i].length; j++) {
                board[i][j] = orig[i][j];
            }
        }

        short pos = -1;
        double best = 0;
        for (short x = 0; x < board.length; x++) {
            short y = getIndex(x);
            if (y <= -1) {
                continue;
            }

            if (pos == -1) {
                pos = x;
            }

            board[x][y] = player.getId();
            double val = score(player, board, depth, mod);

            board[x][getIndex(x)] = 0;
            if (val > best) {
                System.out.println("Placing in index #" + x + " gives " + val + ". Previous: #" + pos + " " + best);
                best = val;
                pos = x;
            }
        }

        return pos;
    }

    public double score(Player player, short[][] orig, int depth, double mod) {
        if (depth - 1 <= 0) {
            return 0;
        }

        short[][] board = new short[w][h];
        for (int i = 0; i < orig.length; i++) {
            for (int j = 0; j < orig[i].length; j++) {
                board[i][j] = orig[i][j];
            }
        }

        short pos = -1;
        double best = 0;
        for (short x = 0; x < board.length; x++) {
            short y = getIndex(x);
            if (y <= -1) {
                continue;
            }

            if (pos == -1) {
                pos = x;
            }

            board[x][y] = player.getId();
            int val = 4 / score(board, x, y);
            if (val == 1) {
                pos = x;
                best = mod * val;
                break;
            }

            val *= mod * score(player, board, depth - 1, mod);

            board[x][getIndex(x)] = 0;
            if (val > best) {
                System.out.println("Placing in index #" + x + " gives " + val + ". Previous: #" + pos + " " + best);
                best = val;
                pos = x;
            }
        }

        return best;
    }

}