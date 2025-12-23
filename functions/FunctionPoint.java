package functions;

import java.util.function.DoubleToLongFunction;

// Реализуем Serializable
public class FunctionPoint implements java.io.Serializable, Cloneable{
    private double x;
    private double y;

    // Обязательный элемент для Serializable
    private static final long serialVersionUID = 1L;

    public FunctionPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public FunctionPoint(FunctionPoint point) {
        this.x = point.x;
        this.y = point.y;
    }

    public FunctionPoint() {
        this.x = 0;
        this.y = 0;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public void setX(double x) { this.x = x; }

    public void setY(double y)
    {
        this.y = y;
    }

    // Возвращает текстовое описание точки
    @Override
    public String toString(){
        return String.format("(%s; %s)", x, y);
    }

    // Проверяет, одинаковые ли точки(одни и те же координаты)
    @Override
    public boolean equals(Object o){
        if (this == o) return true; // Сравниваем адреса в памяти
        if (o == null || getClass() != o.getClass()) return false; // Если пришел null или другого класс, то false

        FunctionPoint that = (FunctionPoint) o; // Приводим к типу FunctionPoint
        // Копируем биты double в long
        return Double.doubleToLongBits(x) == Double.doubleToLongBits(that.x)
                && Double.doubleToLongBits(y) == Double.doubleToLongBits(that.y);
    }

    // Возвращает значение хэш-кода для объекта точки
    @Override
    public int hashCode(){
        long xBits = Double.doubleToLongBits(x); // Копирование 64 битов double в long
        long yBits = Double.doubleToLongBits(y); // Копирование 64 битов double в long

        int xHigh = (int) (xBits >> 32); // Старшие 32 бита x
        int xLow = (int) xBits; // Младшие 32 бита x
        int yHigh = (int) (yBits >> 32); // Старшие 32 бита y
        int yLow = (int) yBits; // Младшие 32 бита y

        return xHigh ^ xLow ^ yHigh ^ yLow; // смешивает все 4 части
    }

    // Возвращает объект-копию для объекта точки
    @Override
    public Object clone() throws CloneNotSupportedException{
        return super.clone();
    }
}
