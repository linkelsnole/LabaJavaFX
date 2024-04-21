package my.snole.laba11.model.ant.AI;

import javafx.application.Platform;
import my.snole.laba11.baseAI.BaseAI;
import my.snole.laba11.model.Point;
import my.snole.laba11.model.SingletonDynamicArray;
import my.snole.laba11.model.ant.Ant;
import my.snole.laba11.model.ant.WarriorAnt;

import java.util.Vector;


import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WarriorAntAI extends BaseAI {
    private Map<WarriorAnt, Double> angles = new HashMap<>();

    @Override
    protected void update() {
        if (!isActive) return;

        ConcurrentLinkedQueue<Ant> ants = SingletonDynamicArray.getInstance().getAntsList();
        for (Ant ant : ants) {
            if (ant instanceof WarriorAnt warrior) {
                double angle = angles.getOrDefault(warrior, 0.0);
                double speed = 10.0;
                angle += speed / 100.0;
                angle %= (2 * Math.PI);
                angles.put(warrior, angle);

                Point center = new Point((int) warrior.getBirthX(), (int) warrior.getBirthY());
                Point offset = new Point((int) (warrior.getRadius() * Math.cos(angle)), (int) (warrior.getRadius() * Math.sin(angle)));
                Point newPosition = center.add(offset);

                Platform.runLater(() -> {
                    warrior.getImageView().setLayoutX(newPosition.getX());
                    warrior.getImageView().setLayoutY(newPosition.getY());
                });
            }
        }
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




