package my.snole.laba11.model.ant.AI;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import my.snole.laba11.baseAI.BaseAI;
import my.snole.laba11.model.ant.Ant;
import my.snole.laba11.model.Point;

import java.util.Timer;
import java.util.TimerTask;

public class WorkerAntAI extends BaseAI {
    private Ant ant;
    private final double fieldWidth;
    private boolean goingToDestination = true;
    private final double fieldHeight;
    private double speed = 15.0;


    public WorkerAntAI(Ant ant, double fieldWidth, double fieldHeight) {
        this.ant = ant;
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
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
        // Выход, если поток не активен
        if (!isActive) return;

        // Получение текущих координат ImageView
        ImageView imageView = ant.getImageView();
        final double currentX = imageView.getLayoutX();
        final double currentY = imageView.getLayoutY();

        // Определение целевых координат
        final double targetX = goingToDestination ? 0 : ant.getBirthX();
        final double targetY = goingToDestination ? 0 : ant.getBirthY();

        // Расчет направления движения к цели
        double dx = targetX - currentX;
        double dy = targetY - currentY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Нормализация вектора движения, если расстояние больше нуля
        if (distance > 0) {
            dx /= distance;
            dy /= distance;
        }

        // Расчет новых координат с учетом скорости
        final double newX = currentX + dx * speed;
        final double newY = currentY + dy * speed;

        // Обновление координат ImageView на потоке JavaFX
        Platform.runLater(() -> {
            imageView.setLayoutX(newX);
            imageView.setLayoutY(newY);
        });

        // Проверка на достижение цели и смена направления при необходимости
        if (Math.hypot(newX - targetX, newY - targetY) < speed) {
            goingToDestination = !goingToDestination;
        }
    }




}





