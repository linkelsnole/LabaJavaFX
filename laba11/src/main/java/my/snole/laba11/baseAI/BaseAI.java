package my.snole.laba11.baseAI;


import java.util.*;

public abstract class BaseAI extends Thread {
    protected Timer timer;
    protected boolean isActive = true;
    private volatile boolean paused = false;
    private final Object lock = new Object();


    @Override
    public void run() {
        while (isActive) {
            synchronized (lock) {
                while (paused) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                } update();
                try {
                    Thread.sleep(1000/60);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    protected abstract void update();


    public synchronized void startAI() {
        if (!isAlive()) {
            isActive = true;
            start();
        }
    }

    public synchronized void stopAI() {
        isActive = false;
        notifyAll();
    }

    public void pauseAI() {
        paused = true;
    }

    public void resumeAI() {
        synchronized (lock) {
            paused = false;
            lock.notify();
        }
    }

    public boolean isAIActive() {
        return isActive;
    }

    public void setAIPriority(int priority) {
        setPriority(priority);
    }

    public int getAIPriority() {
        return getPriority();
    }
}









