package my.snole.laba11.model.ant.AI;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import my.snole.laba11.baseAI.BaseAI;
import my.snole.laba11.model.ant.Ant;
import my.snole.laba11.model.Point;

import java.util.Timer;
import java.util.TimerTask;


public class WorkerAntAI extends BaseAI {
    private final Ant ant;
    private final double fieldWidth;
    private boolean goingToDestination = true;
    private final double fieldHeight;


    public WorkerAntAI(Ant ant, double fieldWidth, double fieldHeight) {
        this.ant = ant;
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
    }


    protected void update() {
        if (!isActive) {
            return;
        }
        ImageView imageView = ant.getImageView();
        Point currentPosition = new Point(imageView.getLayoutX(), imageView.getLayoutY());

        Point targetPosition = goingToDestination ? new Point(0, 0) : new Point(ant.getBirthX(), ant.getBirthY());

        Point direction = targetPosition.subtract(currentPosition);
        double distance = direction.getMagnitude();
        if (distance == 0) {
            return;
        }

        direction = direction.normalize();
        double speed = 5.0;
        Point moveStep = direction.scale(speed);

        Point newPosition = currentPosition.add(moveStep);

        Platform.runLater(() -> {
            imageView.setLayoutX(newPosition.getX());
            imageView.setLayoutY(newPosition.getY());
        });

        if (Math.hypot(newPosition.getX() - targetPosition.getX(), newPosition.getY() - targetPosition.getY()) < speed) {
            goingToDestination = !goingToDestination;
        }
    }
}

//    @Override
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






