package com.davenewham.connect6;

import static com.davenewham.connect6.Connect6.SLOTS_WIDE;
import static com.davenewham.connect6.Connect6.SLOTS_HIGH;

public class ConnectAI {

    //Adjustable weights that impact numerical outcome
    private static float negWeight = 3;
    private static float posWeight = 1;
    private static float winBase = 2;
    private static float winExp = 0.1f;

    BoardPanel bp;
    int maxDepth = 0;

    public ConnectAI(BoardPanel bp) {
        this.bp = bp;
    }

    public short bestMove(short[][] board, short id, int depth) {

        maxDepth = depth;

        //Generates possible outcomes and calculates quality of result
        float[] values = new float[7];
        for (short i = 0; i < SLOTS_WIDE; i++) {
            short r = getRow(board, i, id);
            if (r == -1) {
                values[i] = -32768;
            } else {
                short[][] b = clone(board, SLOTS_WIDE, SLOTS_HIGH);
                b[i][r] = id;
                if (BoardPanel.score(b, i, r) >= 4) {
                    values[i] = 1;
                } else values[i] = checkMin(b, id, 1);
            }
        }

        //Finds move with best quality
        short maxSlot = 0;
        float maxValue = values[0];

        for (short i = 1; i < SLOTS_WIDE; i++) {
            if (values[i] > maxValue) {
                maxSlot = i;
                maxValue = values[i];
            }
        }

        return maxSlot;

    }

    private float checkMax(short[][] board, short id, int depth) {
        if (depth > maxDepth) return 0;

        float[] values = new float[7];

        //Generates possible outcomes and calculates quality of result
        for (short i = 0; i < SLOTS_WIDE; i++) {
            short c = getRow(board, i, id);
            if (c != -1) {
                short[][] b = clone(board, SLOTS_WIDE, SLOTS_HIGH);
                b[i][c] = id;
                if (BoardPanel.score(b, i, c) >= 4) {
                    values[i] = (float) Math.pow(winBase, -winExp * depth);
                } else values[i] = checkMin(b, id, depth + 1);
            }
        }

        //Finds node with best quality
        float maxValue = values[0];

        for (int i = 1; i < SLOTS_WIDE; i++) {
            if (values[i] > maxValue) {
                maxValue = values[i];
            }
        }

        return maxValue;
    }

    private float checkMin(short[][] board, short id, int depth) {
        if (depth > maxDepth) return 0;

        float[] values = new float[7];

        //Gets opponents id
        short oppid = 1;
        if (id == 1) oppid = 2;

        //Generates possible outcomes and calculates quality of result
        for (short i = 0; i < SLOTS_WIDE; i++) {
            short c = getRow(board, i, oppid);
            if (c != -1) {
                short[][] b = clone(board, SLOTS_WIDE, SLOTS_HIGH);
                b[i][c] = oppid;
                if (BoardPanel.score(b, i, c) >= 4) {
                    values[i] = (float) -Math.pow(winBase, -winExp * depth);
                } else values[i] = checkMax(b, id, depth + 1);
            }
        }

        //Returns general
        return nodeQuality(values);
    }

    //Quantifies the  outcome of a decision
    private float nodeQuality(float[] values) {
        //Sum of positive values
        float posSum = 0;
        //Sum of negative values
        float negSum = 0;

        for (float v: values) {
            if (v > 0) {
                posSum += v;
            } else {
                negSum += v;
            }
        }

        return (posWeight * posSum + negWeight * negSum) / SLOTS_WIDE;
    }

    //Gets row of disk when dropped in a column
    private short getRow(short[][] board, short s, short id) {
        for (short i = 0; i < SLOTS_HIGH; i++) {
            if (board[s][i] == 0) {
                return i;
            }
        }
        return -1;
    }

    //Clones the board array
    private short[][] clone(short[][] orig, int w, int h) {
        short[][] board = new short[w][h];
        for (int i = 0; i < orig.length; i++) {
            for (int j = 0; j < orig[i].length; j++) {
                board[i][j] = orig[i][j];
            }
        }

        return board;
    }
}