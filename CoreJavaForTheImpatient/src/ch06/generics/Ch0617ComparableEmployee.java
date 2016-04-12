package ch06.generics;

import static java.lang.Math.round;

import java.util.ArrayList;
import java.util.Collections;

import horstmann.ch04.sec02.clone.tests.Employee;

// 17. Define a class Employee that implements Comparable<Employee>. Using
// the javap utility, demonstrate that a bridge method has been synthesized. What
// does it do?

// here are the results of running javap on ComparableEmployee.class as defined below: 
//
//   $ javap -private Ch0617ComparableEmployee$ComparableEmployee.class
//   Compiled from "Ch0617ComparableEmployee.java"
//   public class ch06.generics.Ch0617ComparableEmployee$ComparableEmployee 
//       extends horstmann.ch04.sec02.clone.tests.Employee 
//       implements java.lang.Comparable<horstmann.ch04.sec02.clone.tests.Employee> {
//     public ch06.generics.Ch0617ComparableEmployee$ComparableEmployee(java.lang.String, double);
//     public int compareTo(horstmann.ch04.sec02.clone.tests.Employee);
//     public int compareTo(java.lang.Object);
//   }
// 
// The bridge method is compareTo(java.lang.Object).
// Based on the text, this bridge method overrides ComparableEmployee.compareTo(Employee e) 
// and then calls the latter after determining it's the right method to call for the 
// ComparableEmployee class. More generally, this is an instance of a process of dynamic 
// method lookup in which the VM inspects the class of an object to find its version of
// a particular method and then execute it. A bridge method is interposed to facilitate
// this process for a given object and method. 

public class Ch0617ComparableEmployee {

  public static class ComparableEmployee 
      extends Employee implements Comparable<Employee> {

    public ComparableEmployee(String name, double salary) {
      super(name, salary);
    }

    @Override
    public int compareTo(Employee e) {
      return ((int) round(this.getSalary() - e.getSalary()));
    }

  }

  public static void main(String[] args) {

    ComparableEmployee e1 = new ComparableEmployee("joe", 1.);
    ComparableEmployee e2 = new ComparableEmployee("jane", 2.);
    ComparableEmployee e3 = new ComparableEmployee("polly", 3.);
    ArrayList<ComparableEmployee> ale = new ArrayList<>();
    ale.add(e3); ale.add(e1); ale.add(e2);
    System.out.println(ale);
    //        [ComparableEmployee[name=polly,salary=3.0], 
    //         ComparableEmployee[name=joe,salary=1.0], 
    //         ComparableEmployee[name=jane,salary=2.0]]

    Collections.sort(ale);
    System.out.println(ale);
    //        [ComparableEmployee[name=joe,salary=1.0], 
    //         ComparableEmployee[name=jane,salary=2.0], 
    //         ComparableEmployee[name=polly,salary=3.0]]

  }

}
