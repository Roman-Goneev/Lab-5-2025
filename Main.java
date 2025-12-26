import functions.*;
import functions.basic.*;
import functions.meta.Power;
import java.io.*;
import functions.basic.IdentityFunction;
import functions.ArrayTabulatedFunction;
import functions.LinkedListTabulatedFunction;



public class Main {

    public static void main(String[] args) {

        FunctionPoint[] points1 = {
                new FunctionPoint(1.0, 1.0),
                new FunctionPoint(2.0, 4.0),
                new FunctionPoint(3.0, 9.0),
                new FunctionPoint(4.0, 16.0)
        };

        FunctionPoint[] points2 = {
                new FunctionPoint(1.0, 1.0),
                new FunctionPoint(2.0, 4.0),
                new FunctionPoint(3.0, 9.0),
                new FunctionPoint(4.0, 16.0)
        };

        FunctionPoint[] points3 = {
                new FunctionPoint(1.0, 6.0),
                new FunctionPoint(2.0, 9.0),
                new FunctionPoint(3.0, 12.0),
                new FunctionPoint(4.0, 15.0)
        };

        System.out.println(" ТЕСТ 1. toString()");
        ArrayTabulatedFunction arrayFunc1 = new ArrayTabulatedFunction(points1);
        LinkedListTabulatedFunction listFunc1 = new LinkedListTabulatedFunction(points1);

        System.out.println("Array: " + arrayFunc1);
        System.out.println("List:  " + listFunc1);
        System.out.println();

        System.out.println(" ТЕСТ 2. equals()");
        ArrayTabulatedFunction arrayFunc2 = new ArrayTabulatedFunction(points2);
        LinkedListTabulatedFunction listFunc2 = new LinkedListTabulatedFunction(points2);

        System.out.println("array1 == array2: " + arrayFunc1.equals(arrayFunc2));
        System.out.println("array1 == list1:  " + arrayFunc1.equals(listFunc1));
        System.out.println("array1 != array3: " + arrayFunc1.equals(new ArrayTabulatedFunction(points3)));
        System.out.println();

        System.out.println(" ТЕСТ 3. hashCode()");
        System.out.println("hash array1: " + arrayFunc1.hashCode());
        System.out.println("hash array2: " + arrayFunc2.hashCode());
        System.out.println("LinkedList1: " + listFunc1.hashCode());
        arrayFunc1.setPointY(0, 1.001);
        System.out.println("hash array1 (изменен): " + arrayFunc1.hashCode());
        listFunc1.setPointY(0, 1.001);
        System.out.println("hash LinkedList (изменен): " + listFunc1.hashCode());

        System.out.println();

        System.out.println(" ТЕСТ 4. clone()");
        ArrayTabulatedFunction arrayClone = (ArrayTabulatedFunction) arrayFunc2.clone();
        System.out.println("До:  " + arrayClone.getPointY(0));
        arrayFunc2.setPointY(0, 999.0);
        System.out.println("После: " + arrayClone.getPointY(0));


    }
}
