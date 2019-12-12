package cn.infomany;

import java.util.Timer;

public class Main {

    public static void main(String[] args) {
        Timer timer = new Timer();
        MyTimerTask myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask, 1000, 10000);
    }
}
