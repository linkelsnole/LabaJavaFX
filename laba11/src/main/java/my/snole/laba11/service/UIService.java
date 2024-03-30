package my.snole.laba11.service;

import my.snole.laba11.model.Point;

public class UIService {

    public String generateTimeString(boolean isSimulationStopped, long stopTime, long startTime) {
        long timeFromStart = isSimulationStopped ? stopTime : System.currentTimeMillis() - startTime;
        long second = (timeFromStart / 1000) % 60;
        long minute = (timeFromStart / (1000 * 60)) % 60;
        long hour = (timeFromStart / (1000 * 60 * 60)) % 24;
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    public int parseInput(String text) {
        if (text != null && !text.isBlank() && text.matches("[0-9]+")) {
            return Integer.parseInt(text);
        }
        throw new IllegalArgumentException();
    }

    public boolean checkProbability(float f) {
        float probability = (float) Math.random();
        return f >= probability;
    }

    public Point generateRandomPoint() {
        int x = (int) (Math.random() * 600);
        int y = (int) (Math.random() * 550);
        return new Point(x, y);
    }
}
