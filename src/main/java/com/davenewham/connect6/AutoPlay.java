package com.davenewham.connect6;

import java.util.Timer;
import java.util.TimerTask;

public class AutoPlay {

    private int slot;
    private Connect6 connect;

    public AutoPlay(Connect6 connect) {
        this.connect = connect;
    }

    public void start() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                boolean full = !connect.getBoardPanel().clicked(slot);
                slot += 2;

                if (full) {
                    if (slot % 2 == 0) {
                        slot = 3;
                    } else {
                        timer.cancel();
                        return;
                    }
                }

                if (slot >= Connect6.SLOTS_WIDE) {
                    slot = slot % 2 == 0 ? 0 : 1;
                }
            }
        };

        timer.schedule(task, 0L, 50L);
    }

}
