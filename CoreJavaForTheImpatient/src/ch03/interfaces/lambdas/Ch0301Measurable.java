package ch03.interfaces.lambdas;

// 1. Provide an interface Measurable with a method double getMeasure() that
// measures an object in some way. Make Employee implement Measurable.
// Provide a method double average(Measurable[] objects) that
// computes the average measure. Use it to compute the average salary of an array of
// employees.

// interface Measurable and class Employee are defined in separate files in this package

public class Ch0301Measurable {

  public static double average(Measurable[] objects) {
    double sum = 0;
    for (Measurable m : objects)
      sum += m.getMeasure();
    return sum / objects.length;
  }

  public static void main(String[] args) {

    Employee joe = new Employee("joe", 125000.);
    Employee mary = new Employee("mary", 150000.);
    Employee jane = new Employee("jane", 175000.);
    Employee[] e = new Employee[] { joe, mary, jane };
    double avg = average(e);
    System.out.println(avg); // 150000.0

  }

}
