package functions;

import javax.swing.*;
import java.awt.*;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

// Реализуем TabulatedFunction и Externalizable
public class LinkedListTabulatedFunction implements TabulatedFunction, Externalizable {

    // Класс для узла списка
    private class FunctionNode {
        FunctionPoint point;
        FunctionNode prev;
        FunctionNode next;

        public FunctionPoint getPoint(){
            return point;
        }
    }

    private FunctionNode head; // Голова списка
    private int count; // Количество точек

    // 2 serialVersionUID для Externalizable
    private static final long serialVersionUID = 1L;

    // 3 Пустой конструктор
    public LinkedListTabulatedFunction() {
        this.count = 0;
        this.head = new FunctionNode();
        this.head.prev = head;
        this.head.next = head;
    }

    // Конструктор из ЛР 2 и 3
    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX || pointsCount < 2) {
            throw new IllegalArgumentException("Недопустимые аргументы для создания функции");
        }
        this.count = pointsCount;
        this.head = new FunctionNode();
        this.head.prev = head;
        this.head.next = head;

        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            // Cразу добавляем в хвост, count увеличится в addNodeToTail
            addNodeToTail().point = new FunctionPoint(leftX + i * step, 0);
        }
        // Корректируем count, так как addNodeToTail его не меняет
        this.count = pointsCount;
    }

    // Конструктор из ЛР 2 и 3
    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX || values.length < 2) {
            throw new IllegalArgumentException("Недопустимые аргументы для создания функции");
        }
        this.count = values.length;
        this.head = new FunctionNode();
        this.head.prev = head;
        this.head.next = head;

        double step = (rightX - leftX) / (values.length - 1);
        for (int i = 0; i < values.length; i++) {
            addNodeToTail().point = new FunctionPoint(leftX + i * step, values[i]);
        }
    }

    // Задание 1 ЛР 4
    public LinkedListTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("Должно быть минимум 2 точки");
        }
        // Инициализация головы списка
        this.count = 0;
        this.head = new FunctionNode();
        this.head.prev = head;
        this.head.next = head;

        for (int i = 0; i < points.length; ++i) {
            // Проверка на сортировку при добавлении
            if (i > 0 && points[i - 1].getX() >= points[i].getX()) {
                throw new IllegalArgumentException("Абсциссы точек не отсортированы");
            }
            // Глубокое копирование
            addNodeToTail().point = new FunctionPoint(points[i]);
        }
    }

    // Методы работы со списком
    private FunctionNode getNodeByIndex(int index) {
        // Оптимизация: если index > count/2 - идем с хвоста
        if (index > count / 2) {
            FunctionNode current = head.prev;
            for (int i = count - 1; i > index; i--) {
                current = current.prev;
            }
            return current;
        } else {
            FunctionNode current = head.next;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
            return current;
        }
    }

    // Добавляет узел в конец списка
    private FunctionNode addNodeToTail() {
        FunctionNode newNode = new FunctionNode();
        newNode.prev = head.prev;
        newNode.next = head;
        head.prev.next = newNode;
        head.prev = newNode;
        count ++;
        return newNode;
    }

    // Добавляет узел перед узлом с заданным индексом
    private FunctionNode addNodeByIndex(int index) {
        // Если index == count, вставляем в хвост (перед head)
        FunctionNode nextNode = (index == count) ? head : getNodeByIndex(index);
        FunctionNode newNode = new FunctionNode();
        newNode.prev = nextNode.prev;
        newNode.next = nextNode;
        nextNode.prev.next = newNode;
        nextNode.prev = newNode;
        count++;
        return newNode;
    }

    private FunctionNode deleteNodeByIndex(int index) {
        FunctionNode toDelete = getNodeByIndex(index);
        toDelete.prev.next = toDelete.next;
        toDelete.next.prev = toDelete.prev;
        toDelete.prev = null;
        toDelete.next = null;
        count--;
        return toDelete;
    }

    // Реализация методов интерфейса TabulatedFunction

    public double getLeftDomainBorder() {
        return head.next.point.getX();
    }

    public double getRightDomainBorder() {
        return head.prev.point.getX();
    }

    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }
        FunctionNode current = head.next;
        // Идем до 'head', а не 'count', т.к. список кольцевой
        while (current != head) {
            // Точное попадание в узел
            if (current.point.getX() == x) {
                return current.point.getY();
            }
            // Попадание между узлами (проверяем, что current.next - не голова)
            if (current.next != head && current.point.getX() < x && x < current.next.point.getX()) {
                double x1 = current.point.getX();
                double y1 = current.point.getY();
                double x2 = current.next.point.getX();
                double y2 = current.next.point.getY();
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
            current = current.next;
        }
        // Проверка на точное попадание в последнюю точку
        if (x == getRightDomainBorder()) {
            return head.prev.point.getY();
        }
        return Double.NaN;
    }

    public int getPointsCount() {
        return count;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= count) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс " + index + " за границами " + count);
        }
    }

    public FunctionPoint getPoint(int index) {
        checkIndex(index);
        return new FunctionPoint(getNodeByIndex(index).point);
    }


    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        checkIndex(index);
        double newX = point.getX();
        FunctionNode node = getNodeByIndex(index);
        double leftBound = (node.prev == head) ? Double.NEGATIVE_INFINITY : node.prev.point.getX();
        double rightBound = (node.next == head) ? Double.POSITIVE_INFINITY : node.next.point.getX();
        if (newX <= leftBound || newX >= rightBound) {
            throw new InappropriateFunctionPointException("Абсцисса новой точки за границами");
        }
        node.point = new FunctionPoint(point);
    }

    public double getPointX(int index) {
        checkIndex(index);
        return getNodeByIndex(index).point.getX();
    }

    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        checkIndex(index);
        FunctionNode node = getNodeByIndex(index);
        double leftBound = (node.prev == head) ? Double.NEGATIVE_INFINITY : node.prev.point.getX();
        double rightBound = (node.next == head) ? Double.POSITIVE_INFINITY : node.next.point.getX();
        if (x <= leftBound || x >= rightBound) {
            throw new InappropriateFunctionPointException("Абсцисса новой точки за границами");
        }
        node.point.setX(x);
    }

    public double getPointY(int index) {
        checkIndex(index);
        return getNodeByIndex(index).point.getY();
    }

    public void setPointY(int index, double y) {
        checkIndex(index);
        getNodeByIndex(index).point.setY(y);
    }

    public void deletePoint(int index) {
        checkIndex(index);
        if (count < 3) {
            throw new IllegalStateException("Удаление невозможно: должно быть минимум 2 точки");
        }
        deleteNodeByIndex(index);
    }

    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        FunctionNode current = head.next;
        int index = 0;
        // Ищем место для вставки
        while (current != head && current.point.getX() < point.getX()) {
            current = current.next;
            index++;
        }
        // Проверка на дубликат
        if (current != head && current.point.getX() == point.getX()) {
            throw new InappropriateFunctionPointException("Точка с такой абсциссой уже существует");
        }
        // Вставляем узел перед 'current' (т.е. по 'index')
        addNodeByIndex(index).point = new FunctionPoint(point);
    }

    // Методы интерфейса Externalizable - 9 задание
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(count);
        FunctionNode current = head.next;
        // Записываем каждую точку
        while (current != head) {
            out.writeObject(current.point);
            current = current.next;
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int readCount = in.readInt();
        // Убедимся, что список пуст
        this.head.prev = head;
        this.head.next = head;
        this.count = 0;

        for (int i = 0; i < readCount; i++) {
            FunctionPoint point = (FunctionPoint) in.readObject();
            addNodeToTail().point = point; // Добавляем в хвост
            this.count++; // Увеличиваем count
        }
    }

    // 3 Задание
    @Override
    // Текстовое представление функции
    public String toString() {
        StringBuilder sb = new StringBuilder(); // Буфер для сборки строки
        sb.append("LinkedListTabulatedFunction: {"); // Заголовок класса
        sb.append("Левая граница = ").append(getLeftDomainBorder()); // Левая граница
        sb.append(", Правая граница = ").append(getRightDomainBorder()); // Правая граница
        sb.append(", количесво точек = ").append(getPointsCount()); // Колчиство точек
        sb.append("\n ["); // Переход на новую строку

        // Обход списка
        FunctionNode current = head.next; // Старт с первой точки
        int index = 0; // Счетчик индексов
        while (current != head) { // Пока не вернулись к head
            sb.append("\n ").append(index).append(current.point);
            if (current.next != head) { // Если это не последняя точка, то ставим запятую
                sb.append(",");
            }
            current = current.next; // Переход к следующему узлу
            index++;
        }

        sb.append("\n ]"); // Закрытие массива
        sb.append("\n}"); // и объекта
        return sb.toString();
    }

    @Override
    // Сравнение функций
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TabulatedFunction)) return false; // Если оbj не TabulatedFunction, то False

        TabulatedFunction other = (TabulatedFunction) obj;
        if (obj instanceof LinkedListTabulatedFunction) { // Если оbj не LinkedListTabulatedFunction, то False
            LinkedListTabulatedFunction otherLL = (LinkedListTabulatedFunction) obj;
            if (count != otherLL.count) return false; // Если разное количество точек, то false

            FunctionNode thisCurrent = head.next; // Указатель на голову первого списка
            FunctionNode otherCurrent = otherLL.head.next; // Указатель на голову второго списка
            while (thisCurrent != head) { // Пока не вернулись к голове
                if (!thisCurrent.point.equals(otherCurrent.point)) { // Попарно сравниваем
                    return false; // Если точки не равны, то false
                }
                thisCurrent = thisCurrent.next; // Переход к следующей точке
                otherCurrent = otherCurrent.next;
            }
        }
        else {
            if (count != other.getPointsCount()) return false; // Проверка количества точек
            if (getLeftDomainBorder() != other.getLeftDomainBorder()) return false; // Проверка левой границы
            if (getRightDomainBorder() != other.getRightDomainBorder()) return false; // Правой границы

            // Обход списка
            for (int i = 0; i < getPointsCount(); i++) {
                if (!this.getPoint(i).equals(other.getPoint(i))) { // Если точки не равны
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode(){
        int result = count; //
        FunctionNode current = head.next;
        while (current != head){
            result = (3 * result) + current.getPoint().hashCode(); //
            current = current.next;
        }
        return result;
    }

    @Override
    //
    public LinkedListTabulatedFunction clone(){
        LinkedListTabulatedFunction clone = new LinkedListTabulatedFunction(); // Копия класса

        FunctionNode current = head.next; // Старт с первой реальной точки(пропуская head)
        FunctionNode newHead = new FunctionNode(); // Новый узел
        newHead.prev = newHead; //
        newHead.next = newHead;
        clone.count = 0;

        if (count > 0) {
            FunctionNode newCurrent = new FunctionNode(); // Создает новый узел
            newCurrent.point = new FunctionPoint(current.point); // Глубокая копия первой точки
            // Вставляю между prevNew и newHead
            newCurrent.prev = newHead;
            newCurrent.next = newHead;
            newHead.prev = newCurrent;
            newHead.next = newCurrent;
            clone.count++;

            current = current.next; // Переходит к следующей точке
            FunctionNode prevNew = newCurrent;

            while (current != head) {
                newCurrent = new FunctionNode(); // Создание нового узла для следующей точки
                newCurrent.point = new FunctionPoint(current.point); // Глубокое копирование текущей точки
                newCurrent.prev = prevNew;
                newCurrent.next = newHead;
                prevNew.next = newCurrent;
                newHead.prev = newCurrent;

                prevNew = newCurrent;
                current = current.next;
                clone.count++;
            }
        }
        return clone; // Возвращает готовый клон
    }

}
