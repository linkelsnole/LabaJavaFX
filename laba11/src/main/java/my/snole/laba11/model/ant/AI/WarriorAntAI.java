package my.snole.laba11.model.ant.AI;

import javafx.application.Platform;
import my.snole.laba11.baseAI.BaseAI;
import my.snole.laba11.model.Point;
import my.snole.laba11.model.ant.Ant;



public class WarriorAntAI extends BaseAI {
    private final Ant ant;
    private final double radius;
    private double angle = 0.0;
    private final double centerX;
    private final double centerY;

    public WarriorAntAI(Ant ant, double centerX, double centerY, double radius) {
        this.ant = ant;
        this.radius = radius;
        this.centerX = centerX;
        this.centerY = centerY;
    }


    protected void update() {
        if (!isActive) return;

        double speed = 5.0;
        angle += speed / 100.0;
        angle %= (2 * Math.PI);


        Point center = new Point((int) centerX, (int) centerY);
        Point offset = new Point((int) (radius * Math.cos(angle)), (int) (radius * Math.sin(angle)));
        Point newPosition = center.add(offset);


        Platform.runLater(() -> {
            ant.getImageView().setLayoutX(newPosition.getX());
            ant.getImageView().setLayoutY(newPosition.getY());
        });
    }

}

//@Override
//    public void run() {
//        super.run();
//        timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                if (isActive) update();
//            }
//        }, 0, 100);
//    }




