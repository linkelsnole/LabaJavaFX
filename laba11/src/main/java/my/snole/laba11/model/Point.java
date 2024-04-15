package my.snole.laba11.model;

public class Point {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    /**
     * Сложение векторов
     */
    public Point add(Point other) {
        return new Point(this.x + other.x, this.y + other.y);
    }

    /**
     * Вычитание векторов
     */
    public Point subtract(Point other) {
        return new Point(this.x - other.x, this.y - other.y);
    }

    /**
     * Масштабирование вектора
     */
    public Point scale(double scalar) {
        return new Point(this.x * scalar, this.y * scalar);
    }

    /**
     * Нормализация вектора
     */
    public Point normalize() {
        double magnitude = this.getMagnitude();
        if (magnitude == 0) {
            return new Point(0, 0);
        }
        return this.scale(1.0 / magnitude);
    }

    /**
     *  Получение длины вектора
     */
    public double getMagnitude() {
        return Math.sqrt(x * x + y * y);
    }

    /**
     * ! Метод для логов
     */
    @Override
    public String toString() {
        return String.format("Point(x=%.2f, y=%.2f)", x, y);
    }
}
