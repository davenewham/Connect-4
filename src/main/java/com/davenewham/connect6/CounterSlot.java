package com.davenewham.connect6;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static com.davenewham.connect6.Connect6.HANDBAR;
import static com.davenewham.connect6.Connect6.SLOT;

public class CounterSlot extends JPanel {

    private int id;
    private Player player;
    private Connect6 connect6;

    public CounterSlot(int id, Connect6 connect6) {
        super(new FlowLayout(FlowLayout.CENTER, 0, 0));
        setPreferredSize(new Dimension(SLOT, HANDBAR));
        this.id = id;
        this.connect6 = connect6;

        addMouseListener(new CounterListener());
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public BoardPanel getBoard() {
        return connect6.getBoardPanel();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (connect6.isFinished()) {
            return;
        }

        if (connect6.getBoardPanel().isFull(id)) {
            return;
        }

        if (player != null) {
            graphics.drawImage(player.getImage(), 5, 45, null);
        }
    }

    public class CounterListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            getBoard().clicked(id);
            mouseEntered(mouseEvent);
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {
            setPlayer(getBoard().getCurrentPlayer());
            paint();
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {
            setPlayer(null);
            paint();
        }

        private void paint() {
            revalidate();
            repaint();
        }

    }

}
