package my.snole.laba11.baseAI;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;


public abstract class BaseAI extends Thread {
    protected Timer timer;
    protected boolean isActive = true;
    private static final List<BaseAI> allAI = new ArrayList<>();
    public BaseAI() {
        synchronized (allAI) {
            allAI.add(this);
        }
    }

    public synchronized void startAI() {
        isActive = true;
    }

    public synchronized void stopAI() {
        isActive = false;
        notify();
    }

    public static void stopAllAI() {
        synchronized (allAI) {
            for (BaseAI ai : allAI) {
                ai.stopAI();
            }
        }
    }

    public static void startAllAI() {
        synchronized (allAI) {
            for (BaseAI ai : allAI) {
                ai.startAI();
            }
        }
    }

    public boolean isAIActive() {
        return isActive; }

    public void setAIPriority(int priority) { // задает приоритет
        setPriority(priority);
    }

    public int getAIPriority() {
        return getPriority();
    }
}


