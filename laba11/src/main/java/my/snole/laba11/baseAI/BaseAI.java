package my.snole.laba11.baseAI;


import java.util.*;

public abstract class BaseAI extends Thread {
    protected Timer timer;
    protected boolean isActive = true;
    private boolean paused = false;
    private final Object lock = new Object();
    private static final List<BaseAI> allAI = new ArrayList<>();

    public BaseAI() {
        allAI.add(this);
    }

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
    }

    public void pauseAI() {
        synchronized (lock) {
            paused = true;
        }
    }

    public void resumeAI() {
        synchronized (lock) {
            paused = false;
            lock.notify();
        }
    }

    public static void stopAIByType(Class<?> aiType) {
        synchronized (allAI) {
            for (BaseAI ai : allAI) {
                if (aiType.isInstance(ai)) {
                    ai.pauseAI();
                }
            }
        }
    }
    public static void startAIByType(Class<?> aiType) {
        synchronized (allAI) {
            for (BaseAI ai : allAI) {
                if (aiType.isInstance(ai)) {
                    ai.resumeAI();
                }
            }
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



//public synchronized void startAI() {
//        isActive = true;
//        start();
//    }
//
//    public synchronized void stopAI() {
//        isActive = false;
//    }
//
//    public void setAIPriority(int priority) {
//        this.setPriority(priority);
//    }
//
//    public int getAIPriority() {
//        return this.getPriority();
//    }
//
//    public boolean getAIActive() {
//        return isActive;
//    }






//public abstract class BaseAI extends Thread {
//    protected Timer timer;
//    protected boolean isActive = true;
//    private final Object lock = new Object();
//    private static final List<BaseAI> allAI = new ArrayList<>();
//    public BaseAI() {
//        synchronized (allAI) {
//            allAI.add(this);
//        }
//    }
//
//    public synchronized void startAI() {
//        isActive = true;
//    }
//
//    public synchronized void stopAI() {
//        isActive = false;
//        notify();
//    }
//
//    public static void stopAllAI() {
//        synchronized (allAI) {
//            for (BaseAI ai : allAI) {
//                ai.stopAI();
//            }
//        }
//    }
//
//    public static void startAllAI() {
//        synchronized (allAI) {
//            for (BaseAI ai : allAI) {
//                ai.startAI();
//            }
//        }
//    }
//
//    public boolean isAIActive() {
//        return isActive; }
//
//    public void setAIPriority(int priority) {
//        setPriority(priority);
//    }
//
//    public int getAIPriority() {
//        return getPriority();
//    }
//}


