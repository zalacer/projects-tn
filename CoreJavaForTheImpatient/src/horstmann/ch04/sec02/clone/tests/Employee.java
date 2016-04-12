package horstmann.ch04.sec02.clone.tests;

public class Employee implements Cloneable {
    private String name;
    private double salary;
        
    public Employee(String name, double salary) {
        this.name = name;
        this.salary = salary;
    }

    public void raiseSalary(double byPercent) {
        double raise = salary * byPercent / 100;
        salary += raise;    
    }
    
    public final String getName() {
        return name;
    }
    
    public double getSalary() {
        return salary;
    }
    
    public String toString() {
        return getClass().getSimpleName() + "[name=" + name
            + ",salary=" + salary + "]";
    }
    
    public Employee clone() throws CloneNotSupportedException {
        return (Employee) super.clone();
    }
}
