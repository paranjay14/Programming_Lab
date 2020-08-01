package com.company;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Time{
    public static long cur_time;
}


public class Main {
    public static Time time;
    public static void main(String s[]) throws InterruptedException {
        time = new Time();
        time.cur_time = 0;
        Que qSE = new Que(time, "S", "E");
        Que qES = new Que(time, "E", "S");
        Que qSW = new Que(time, "S", "W");
        Que qWS = new Que(time, "W", "S");
        Que qWE = new Que(time, "W", "E");
        Que qEW = new Que(time, "E", "W");

        gui window = new gui(qSE,qES,qSW,qWS,qWE,qEW);
        window.initialise();
        time.cur_time = System.currentTimeMillis()/1000;
        updateTimer(time.cur_time, window,qSE,qES,qSW,qWS,qWE,qEW);
    }

    // Starts a timer that increments value of cur_time every second. This cur_time is taken mod with 180, thus the timer restarts every 3 minutes.
    public static void updateTimer(long start_time, gui window, Que queSE, Que queES, Que queSW, Que queWS, Que queWE, Que queEW){
        Timer timer;
        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                time.cur_time = System.currentTimeMillis()/1000 - start_time;
                long t = 60;

                time.cur_time++;
                time.cur_time = time.cur_time%(3*t);

                ProcessThread pThreadSE = new ProcessThread(time, true, queSE, queES, queSW, queWS, queWE, queEW, 1);
                ProcessThread pThreadES = new ProcessThread(time, true, queSE, queES, queSW, queWS, queWE, queEW, 2);
                ProcessThread pThreadSW = new ProcessThread(time, true, queSE, queES, queSW, queWS, queWE, queEW, 3);
                ProcessThread pThreadWS = new ProcessThread(time, true, queSE, queES, queSW, queWS, queWE, queEW, 4);
                ProcessThread pThreadWE = new ProcessThread(time, true, queSE, queES, queSW, queWS, queWE, queEW, 5);
                ProcessThread pThreadEW = new ProcessThread(time, true, queSE, queES, queSW, queWS, queWE, queEW, 6);

                Thread tSE = new Thread(pThreadSE, "Thread_SE");
                Thread tES = new Thread(pThreadES, "Thread_ES");
                Thread tSW = new Thread(pThreadSW, "Thread_SW");
                Thread tWS = new Thread(pThreadWS, "Thread_WS");
                Thread tWE = new Thread(pThreadWE, "Thread_WE");
                Thread tEW = new Thread(pThreadEW, "Thread_EW");

                tSE.start();
                tES.start();
                tSW.start();
                tWS.start();
                tWE.start();
                tEW.start();

                try{
                    tSE.join();
                    tES.join();
                    tSW.join();
                    tWS.join();
                    tWE.join();
                    tEW.join();
                }
                catch(Exception e){System.out.println("Exception E has occurred");}

                window.setTime(time);
                window.updateLightTable(time,t);
                window.updateVehicleTable(0);
            }
        });
        timer.start();
    }


}