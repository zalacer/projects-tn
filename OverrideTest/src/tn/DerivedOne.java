package tn;

public class DerivedOne extends Base {

    int a = 7; // hides B.a

    public static void f() {
        System.out.println("DerivedOne"); // hides Base.f()
    }
}