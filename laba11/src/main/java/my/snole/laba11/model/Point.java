package my.snole.laba11.model;

public class Point {
    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    // Сложение точек (векторов)
    public Point add(Point other) {
        return new Point(this.x + other.x, this.y + other.y);
    }

    // Вычитание точек (векторов)
    public Point subtract(Point other) {
        return new Point(this.x - other.x, this.y - other.y);
    }

    // Масштабирование точки (вектора)
    public Point scale(double scalar) {
        return new Point((int)(this.x * scalar), (int)(this.y * scalar));
    }

    // Нормализация вектора
    public Point normalize() {
        double magnitude = this.getMagnitude();
        if (magnitude == 0) {
            return new Point(0, 0); // или выбросить исключение
        }
        return this.scale(1.0 / magnitude);
    }

    // Получение длины вектора
    public double getMagnitude() {
        return Math.sqrt(x*x + y*y);
    }

    // Переопределение метода toString для удобства отладки
    @Override
    public String toString() {
        return String.format("Point(x=%d, y=%d)", x, y);
    }
}
