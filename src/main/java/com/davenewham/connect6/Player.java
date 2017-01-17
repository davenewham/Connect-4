package com.davenewham.connect6;

import javax.swing.*;
import java.awt.image.BufferedImage;

public enum Player {

    ONE(1),
    TWO(2);

    private short id;
    private String name;
    private boolean ai;

    private JButton renameButton;
    private JButton aiButton;
    private JLabel label;

    Player(int id) {
        this((short) id);
    }

    Player(short id) {
        this.id = id;
    }

    public short getId() {
        return id;
    }

    public Player getOther() {
        if (this == ONE) {
            return TWO;
        } else {
            return ONE;
        }
    }

    public boolean hasName() {
        return name != null;
    }

    public String getName() {
        if (name == null || name.isEmpty()) return "Player " + (this == TWO ? "Two" : "One");
        else return name;
    }

    public void setName() {
        if (renameButton != null) {
            // renameButton.setText("Rename " + getName());
            label.setText(getName());
        }
    }

    public void setName(String name) {
        this.name = name;
        setName();
    }

    public boolean isAi() {
        return ai;
    }

    public void setAi(boolean ai) {
        this.ai = ai;

        if (ai) {
            setName("Computer");
        } else {
            Connect6.showNameDialog(this);
        }
    }

    public JButton getRenameButton() {
        return renameButton;
    }

    public void setRenameButton(JButton renameButton) {
        this.renameButton = renameButton;
    }

    public JButton getAiButton() {
        return aiButton;
    }

    public void setAiButton(JButton aiButton) {
        this.aiButton = aiButton;
    }

    public JLabel getLabel() {
        return label;
    }

    public void setLabel(JLabel label) {
        this.label = label;
    }

    public BufferedImage getImage() {
        if (this == ONE) {
            return Connect6.imgRed;
        } else {
            return Connect6.imgYellow;
        }
    }

    public static Player fromId(int id) {
        return fromId((short) id);
    }

    public static Player fromId(short id) {
        for (Player player : values()) {
            if (player.id == id) {
                return player;
            }
        }

        throw new IllegalArgumentException(id + " is not a valid player id");
    }

}
