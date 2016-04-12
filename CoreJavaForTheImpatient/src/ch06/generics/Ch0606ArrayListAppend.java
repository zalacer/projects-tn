package ch06.generics;

import java.util.ArrayList;

import horstmann.ch04.sec02.clone.tests.Employee;
import horstmann.ch04.sec02.clone.tests.Manager;

// 6. Implement a generic method that appends all elements from one array list to
// another. Use a wildcard for one of the type arguments. Provide two equivalent
// solutions, one with a ? extends E wildcard and one with ? super E.

public class Ch0606ArrayListAppend {

  public static <T> boolean append1(ArrayList<T> a, ArrayList<? extends T> b) {
    return a.addAll(b);
  }

  public static <T> boolean append2(ArrayList<? super T> a, ArrayList<T> b) {
    return a.addAll(b);
  }

  public static void main(String[] args) {

    Employee e1 = new Employee("joe", 1.);
    Employee e2 = new Employee("jane", 2.);
    Manager  m1 = new Manager("dracic", 3.);
    m1.setBonus(.5);
    Manager  m2 = new Manager("schartz", 4.);
    m2.setBonus(.6);
    ArrayList<Employee> ale = new ArrayList<>();
    ale.add(e1); ale.add(e2);
    System.out.println("ale = "+ale);
    ArrayList<Employee> aleCopy = new ArrayList<Employee>(ale);
    System.out.println("aleCopy = "+aleCopy);
    ArrayList<Manager> alm = new ArrayList<>();
    alm.add(m1); alm.add(m2);
    System.out.println("alm = "+alm);

    append1(ale, alm);
    System.out.println("ale = "+ale);

    append2(aleCopy, alm);
    System.out.println("aleCopy = "+aleCopy);
  }

}

//output:
//ale = [Employee[name=joe,salary=1.0], Employee[name=jane,salary=2.0]]
//aleCopy = [Employee[name=joe,salary=1.0], Employee[name=jane,salary=2.0]]
//alm = [Manager[name=dracic,salary=3.0][bonus=0.5], Manager[name=schartz,salary=4.0][bonus=0.6]]
//ale = [Employee[name=joe,salary=1.0], Employee[name=jane,salary=2.0], Manager[name=dracic,salary=3.0][bonus=0.5], Manager[name=schartz,salary=4.0][bonus=0.6]]
//aleCopy = [Employee[name=joe,salary=1.0], Employee[name=jane,salary=2.0], Manager[name=dracic,salary=3.0][bonus=0.5], Manager[name=schartz,salary=4.0][bonus=0.6]]
