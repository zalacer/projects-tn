package tn;

public class PolymorphismTest {

    public static void main(String[] args) {

        Base b = new DerivedOne();
        b.f(); // prints "Base"

        b = new Base();
        b.f(); // prints "Base"

        Base.f(); // prints Base
        DerivedOne.f(); // prints DerivedOne

    }
}