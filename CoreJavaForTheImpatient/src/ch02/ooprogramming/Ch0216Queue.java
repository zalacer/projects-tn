package ch02.ooprogramming;

import java.util.Arrays;

import utils.Queue;

// 16. Implement a class Queue, an unbounded queue of strings. Provide methods add,
// adding at the tail, and remove, removing at the head of the queue. Store elements
// as a linked list of nodes. Make Node a nested class. Should it be static or not?

// In my implementation I made the Node class private and Queue works the same if its
// also static or not. I thought private was the best choice unless Node would be
// separately and publicly defined, which may be useful if each of its instances contained
// a more complex structure than one string and for possible interfacing with other classes
// based on Node data compatibility.  Normally I would not create a new class to encapsulate
// a single string, however a precedent for that is the Hadoop Text class but that has 
// some different properties than String to reduce the need for object creation, since it 
// can be constructed from byte arrays (as can String) as well as strings, and also offers 
// improved serializability.
     
public class Ch0216Queue {

    public static void main(String[] args) {
        
        Queue q = new Queue();
        boolean b1 = q.add("hello"); 
        System.out.println(b1); // true
        q.add("world");
        System.out.println(q); // Queue(hello, world)
        String x = q.remove();
        System.out.println(x); // hello
        System.out.println(q);
        String y = q.remove(); // world
        System.out.println(y);
        System.out.println(q); // Queue()
        
        System.out.println();
        Queue q1 = new Queue(Arrays.asList(new String[] {"one", "two", "three"}));
        System.out.println(q1); // Queue(one, two, three)
        System.out.println(q1.hashCode()); // 104133933
        Queue q2 = new Queue(Arrays.asList(new String[] {"do", "re", "mi"}));
        System.out.println(q2); // Queue(do, re, mi)
        System.out.println(q2.hashCode()); // 751377407
        Queue q3 = new Queue(Arrays.asList(new String[] {"fe", "fi", "fo", "fum"}));
        System.out.println(q3); // Queue(fe, fi, fo, fum)
        System.out.println(q3.hashCode()); // 180147188
        System.out.println();
        for (int i=0; i<3; i++) {
            q1.remove(); q2.remove(); q3.remove();
            System.out.println(q1); System.out.println(q2); System.out.println(q3);
            System.out.println();
        }

//        Queue(two, three)
//        Queue(re, mi)
//        Queue(fi, fo, fum)
//
//        Queue(three)
//        Queue(mi)
//        Queue(fo, fum)
//
//        Queue()
//        Queue()
//        Queue(fum)

        
    }

}
