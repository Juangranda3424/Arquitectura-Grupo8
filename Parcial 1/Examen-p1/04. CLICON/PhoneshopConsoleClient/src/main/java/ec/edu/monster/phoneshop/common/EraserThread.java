package ec.edu.monster.phoneshop.common;

import java.io.PrintStream;
import java.io.PrintWriter;

class EraserThread implements Runnable {
    private boolean stop;
    private PrintStream out;

    public EraserThread(String prompt, PrintStream out) {
        this.out = out;
        this.out.println(prompt);
    }

    public void run () {
        stop = true;
        while (stop) {
            out.print("\010*");

            try {
                Thread.currentThread().sleep(1);
            } catch(InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }

    public void stopMasking() {
        this.stop = false;
    }
}