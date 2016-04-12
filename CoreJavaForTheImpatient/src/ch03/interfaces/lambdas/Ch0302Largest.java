package ch03.interfaces.lambdas;

// 2. Continue with the preceding exercise and provide a method Measurable
// largest(Measurable[] objects). Use it to find the name of the employee
// with the largest salary. Why do you need a cast?

// A cast is required because largest returns a Measurable which generally
// does not have a name field or getName() method.  Casting it to Employeee
// enables access to getName().

public class Ch0302Largest {

  public static Measurable largest(Measurable[] objects) {
    double max = Double.NEGATIVE_INFINITY;
    Measurable obj = null;
    double measure = 0;

    for (Measurable m : objects) {
      measure = m.getMeasure();
      if (measure > max) obj = m;
    }

    return obj;
  }

  public static void main(String[] args) {

    Employee joe = new Employee("joe", 125000.);
    Employee mary = new Employee("mary", 150000.);
    Employee jane = new Employee("jane", 175000.);
    Employee[] e = new Employee[] { joe, mary, jane };
    String name = ((Employee) largest(e)).getName();
    System.out.println(name); //jane
  }

}
