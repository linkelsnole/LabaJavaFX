package my.snole.laba11.model.ant.AI;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import my.snole.laba11.baseAI.BaseAI;
import my.snole.laba11.model.SingletonDynamicArray;
import my.snole.laba11.model.ant.Ant;
import my.snole.laba11.model.Point;
import my.snole.laba11.model.ant.WorkerAnt;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;


public class WorkerAntAI extends BaseAI {
    private final double fieldWidth;
    private boolean goingToDestination = true;
    private final double fieldHeight;
    private Map<WorkerAnt, Point> lastPositions = new HashMap<>();
    private Map<WorkerAnt, Boolean> goingToDestinations = new HashMap<>();


    public WorkerAntAI(double fieldWidth, double fieldHeight) {
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
    }


    /**
     *
     */
    @Override
    protected void update() {
        if (!isActive) return;
        
        ConcurrentLinkedQueue<Ant> ants = SingletonDynamicArray.getInstance().getAntsList();
        for (Ant ant : ants) {
            if (ant instanceof WorkerAnt) {
                WorkerAnt worker = (WorkerAnt) ant;
                ImageView imageView = worker.getImageView();
                Point currentPosition = lastPositions.getOrDefault(worker, new Point(imageView.getLayoutX(), imageView.getLayoutY()));
                boolean goingToDestination = goingToDestinations.getOrDefault(worker, true);

                Point targetPosition = goingToDestination ? new Point(0, 0) : new Point(worker.getBirthX(), worker.getBirthY());
                Point direction = targetPosition.subtract(currentPosition);
                double distance = direction.getMagnitude();

                if (distance == 0) {
                    goingToDestination = !goingToDestination;
                    goingToDestinations.put(worker, goingToDestination);
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

                lastPositions.put(worker, newPosition);
                goingToDestinations.put(worker, goingToDestination);

                if (Math.hypot(newPosition.getX() - targetPosition.getX(), newPosition.getY() - targetPosition.getY()) < speed) {
                    goingToDestination = !goingToDestination;
                    goingToDestinations.put(worker, goingToDestination);
                }
            }
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






