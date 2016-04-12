package ch03.interfaces.lambdas;

public class Employee implements Measurable {
  private String name;
  private double salary;

  public Employee(String name, double salary) {
    this.name = name;
    this.salary = salary;
  }

  public Employee(String name) {
    this(name, 0);
  }

  public double getMeasure() {
    return salary;
  }

  public void raiseSalary(double byPercent) {
    double raise = salary * byPercent / 100;
    salary += raise;    
  }

  public String getName() {
    return name;
  }

  public double getSalary() {
    return salary;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
   @Override
   public String toString() {
     StringBuilder builder = new StringBuilder();
     builder.append("Employee(");
     builder.append(name);
     builder.append(",");
     builder.append(salary);
     builder.append(")");
     return builder.toString();
   }


}

