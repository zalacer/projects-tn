package ch04.inheritance.reflection;

import static utils.ReflectionUtils.printClassMethods;

// 10. Use the MethodPrinter program in Section 4.5.1, “Enumerating Class
// Members,” on p. 160 to enumerate all methods of the int[] class. Extra credit if
// you can identify the one method (discussed in this chapter) that is wrongly
// described.

public class Ch0410MethodPrinter {

  public static void main(String[] args) {

    int[] a = new int[]{1,2,3};
    System.out.println(a); // [I@2a139a55
    System.out.println(a.getClass().getName()); // [I
    System.out.println(a.getClass().getCanonicalName()); // int[]

    printClassMethods(a);

    //   Declared methods in int[]:
    //   ==========================
    //   (None)
    //
    //   Declared methods in superclass java.lang.Object:
    //   ================================================
    //   protected void finalize[]
    //   public final void wait[]
    //   public final void wait[long arg0, int arg1]
    //   public final native void wait[long arg0]
    //   public boolean equals[java.lang.Object arg0]
    //   public java.lang.String toString[]
    //   public native int hashCode[]
    //   public final native java.lang.Class getClass[]
    //   protected native java.lang.Object clone[]
    //   public final native void notify[]
    //   public final native void notifyAll[]
    //   private static native void registerNatives[]

  }

}
