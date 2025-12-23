package functions;

import java.lang.reflect.Array;

// Класс реализует интерфейс TabulatedFunction, который теперь наследует Function и Serializable
public class ArrayTabulatedFunction implements TabulatedFunction, Cloneable {
    private FunctionPoint[] points;
    private static final long serialVersionUID = 1L;

    //
    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX || pointsCount < 2) {
            throw new IllegalArgumentException("Недопустимые аргументы для создания функции");
        }
        this.points = new FunctionPoint[pointsCount];
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; ++i) {
            points[i] = new FunctionPoint(leftX + i * step, 0);
        }
    }

    //
    public ArrayTabulatedFunction(double leftX, double rightX, double[] values) {
        int count = values.length;
        if (leftX >= rightX || count < 2) {
            throw new IllegalArgumentException("Недопустимые аргументы для создания функции");
        }
        this.points = new FunctionPoint[count];
        double step = (rightX - leftX) / (count - 1);
        for (int i = 0; i < count; ++i) {
            points[i] = new FunctionPoint(leftX + i * step, values[i]);
        }
    }

    // Новый конструктор ЛР 4
    public ArrayTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("Должно быть минимум 2 точки");
        }
        // Проверка на сортировку
        for (int i = 0; i < points.length - 1; ++i) {
            if (points[i].getX() >= points[i + 1].getX()) {
                throw new IllegalArgumentException("Абсциссы точек не отсортированы");
            }
        }
        // Глубокое копирование для инкапсуляции
        this.points = new FunctionPoint[points.length];
        for (int i = 0; i < points.length; ++i) {
            this.points[i] = new FunctionPoint(points[i]);
        }
    }

    public double getLeftDomainBorder() {
        return points[0].getX();
    }

    public double getRightDomainBorder() {
        return points[points.length - 1].getX();
    }

    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }

        for (int i = 0; i < points.length - 1; ++i) {
            if (points[i].getX() <= x && x <= points[i + 1].getX()) {
                double x1 = points[i].getX();
                double y1 = points[i].getY();
                double x2 = points[i + 1].getX();
                double y2 = points[i + 1].getY();

                // Линейная интерполяция
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
        }
        return Double.NaN;
    }

    public int getPointsCount() {
        return points.length;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= points.length) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " за границами [0, " + (points.length - 1) + "]");
        }
    }

    public FunctionPoint getPoint(int index) {
        checkIndex(index);
        return new FunctionPoint(points[index]); // Возвращает копию
    }

    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        checkIndex(index);
        double newX = point.getX();
        double leftBound = (index > 0) ? points[index - 1].getX() : Double.NEGATIVE_INFINITY;
        double rightBound = (index < points.length - 1) ? points[index + 1].getX() : Double.POSITIVE_INFINITY;

        if (newX <= leftBound || newX >= rightBound) {
            throw new InappropriateFunctionPointException("Абсцисса новой точки за границами (" + leftBound + ", " + rightBound + ")");
        }
        points[index] = new FunctionPoint(point);
    }

    public double getPointX(int index) {
        checkIndex(index);
        return points[index].getX();
    }

    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        checkIndex(index);
        double leftBound = (index > 0) ? points[index - 1].getX() : Double.NEGATIVE_INFINITY;
        double rightBound = (index < points.length - 1) ? points[index + 1].getX() : Double.POSITIVE_INFINITY;

        if (x <= leftBound || x >= rightBound) {
            throw new InappropriateFunctionPointException("Абсцисса новой точки за границами (" + leftBound + ", " + rightBound + ")");
        }
        points[index].setX(x);
    }

    public double getPointY(int index) {
        checkIndex(index);
        return points[index].getY();
    }

    public void setPointY(int index, double y) {
        checkIndex(index);
        points[index].setY(y);
    }

    public void deletePoint(int index) {
        checkIndex(index);
        if (points.length < 3) {
            throw new IllegalStateException("Удаление невозможно: должно быть минимум 2 точки");
        }
        FunctionPoint[] newPoints = new FunctionPoint[points.length - 1];
        System.arraycopy(points, 0, newPoints, 0, index);
        System.arraycopy(points, index + 1, newPoints, index, points.length - index - 1);
        points = newPoints;
    }

    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        int insertIndex = 0;
        while (insertIndex < points.length && points[insertIndex].getX() < point.getX()) {
            insertIndex++;
        }

        if (insertIndex < points.length && points[insertIndex].getX() == point.getX()) {
            throw new InappropriateFunctionPointException("Точка с такой абсциссой" + point.getX() + " уже существует");
        }

        FunctionPoint[] newPoints = new FunctionPoint[points.length + 1];
        System.arraycopy(points, 0, newPoints, 0, insertIndex);
        newPoints[insertIndex] = new FunctionPoint(point); // Глубокое копирование
        System.arraycopy(points, insertIndex, newPoints, insertIndex + 1, points.length - insertIndex);
        points = newPoints;
    }

    // 2 Задание
    @Override
    // Текстовое представление функции
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < points.length; i++) {
            sb.append(points[i].toString());
            if (i < points.length - 1) sb.append(", ");
        }
        sb.append("}");
        return sb.toString();
    }

    // Сравнение функций
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TabulatedFunction)) return false; // Если о не TabulatedFunction, то False

        // Быстрая проверка для ArrayTabulatedFunction
        if (o instanceof ArrayTabulatedFunction other) { // Если o не ArrayTabulatedFunction, то False
            if (points.length != other.points.length) return false; // Если разное количество точек, то false
            for (int i = 0; i < points.length; i++) {
                if (!points[i].equals(other.points[i])) return false; // Если хотя бы одна пара точек не равна, то false(функции не равны)
            }
            return true;
        }

        // Медленная проверка для TabulatedFunction
        TabulatedFunction other = (TabulatedFunction) o;
        if (points.length != other.getPointsCount()) return false; // Если разное количество точек, то false
        for (int i = 0; i < points.length; i++){
            if (!points[i].equals(other.getPoint(i))) return false; // Если хотя бы одна пара точек не равна, то false(функции не равны)
        }
        return true;
    }

    // Хэш-код функции
    @Override
    public int hashCode(){
        int result = points.length; // Количество точек
        for (FunctionPoint point : points) {  // point — текущая точка
            result ^= point.hashCode(); // Хэш-код точки
        }
        return result;
    }

    // Глубокое копирование
    @Override
    public Object clone(){
        try {
            ArrayTabulatedFunction clone = (ArrayTabulatedFunction) super.clone(); // Поверхностное копирование
            clone.points = new FunctionPoint[points.length]; // Клонирование массива
            for (int i = 0; i < points.length; i++) {
                clone.points[i] = (FunctionPoint) points[i].clone(); // Клонирование каждой точки(глубокое копирование)
            }
            return clone;
        }
        catch (CloneNotSupportedException e){
            throw new RuntimeException();
        }
    }
}