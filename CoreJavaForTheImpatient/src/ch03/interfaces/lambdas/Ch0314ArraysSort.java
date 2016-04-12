package ch03.interfaces.lambdas;

import java.util.Arrays;
import java.util.Comparator;

// 14. Write a call to Arrays.sort that sorts employees by salary, breaking ties by
// name. Use Comparator.thenComparing. Then do this in reverse order.

public class Ch0314ArraysSort {

  public static void main(String[] args) {

    Employee joeGrow = new Employee("joe grow", 100.);
    Employee peetBeet = new Employee("peet beet", 125.);
    Employee maryHome = new Employee("mary home", 150.);
    Employee annSham = new Employee("anne sham", 175.);
    Employee joeBlow = new Employee("joe blow", 100.);
    Employee arnAlpha = new Employee("arn alpha", 2.);

    Employee[] e = new Employee[] { joeGrow, peetBeet, maryHome, annSham, joeBlow, arnAlpha };

    Arrays.sort(e, Comparator.comparingDouble(Employee::getSalary)
        .thenComparing(Employee::getName));
    for(Employee x : e) System.out.println(x);
    //  Employee(arn alpha,2.0)
    //  Employee(joe blow,100.0)
    //  Employee(joe grow,100.0)
    //  Employee(peet beet,125.0)
    //  Employee(mary home,150.0)
    //  Employee(anne sham,175.0)

    System.out.println();

    Arrays.sort(e, Comparator.comparingDouble(Employee::getSalary)
        .thenComparing(Employee::getName)
        .reversed());
    for(Employee x : e) System.out.println(x);
    //  Employee(anne sham,175.0)
    //  Employee(mary home,150.0)
    //  Employee(peet beet,125.0)
    //  Employee(joe grow,100.0)
    //  Employee(joe blow,100.0)
    //  Employee(arn alpha,2.0)

  }

}
