package my.snole.laba11.model.ant.AI;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import my.snole.laba11.baseAI.BaseAI;
import my.snole.laba11.model.ant.Ant;
import my.snole.laba11.model.ant.WarriorAnt;

import java.util.Timer;
import java.util.TimerTask;


public class WarriorAntAI extends BaseAI {
    private Ant ant;
    private final double radius;
    private double angle = 0.0;
    private final double speed = 30.0;
    private final double centerX;
    private final double centerY;

    public WarriorAntAI(Ant ant, double centerX, double centerY, double radius) {
        this.ant = ant;
        this.radius = radius;
        this.centerX = centerX;
        this.centerY = centerY;
    }

    @Override
    public void run() {
        super.run();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isActive) update();
            }
        }, 0, 100);
    }

    private synchronized void update() {
        if (!isActive) return;

        // Увеличение угла для следующего кадра анимации
        angle += speed / 100.0;
        angle %= (2 * Math.PI);

        // Вычисление новых координат относительно центра окружности
        final double newX = centerX + radius * Math.cos(angle);
        final double newY = centerY + radius * Math.sin(angle);

        // Обновление координат ImageView на потоке JavaFX Application
        Platform.runLater(() -> {
            ant.getImageView().setLayoutX(newX);
            ant.getImageView().setLayoutY(newY);
        });
    }
}



