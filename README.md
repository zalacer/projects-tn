projects-tn

This is a growing collection of demo projects for reference.

Java8StreamTest - consists of two Java8 classes, one for performance testing of IntStream
    and the other to test performance of regular for loop. First build the project with
    Maven or import it into Eclipse or other compatible IDE and export a executable jar. 
    Then run the jar for each class separately on a quiescent system with no other java
    processes running. The java command line should have -Xmx1024 or greater to minimize
    latency due to garbage collection. The runtime of each class should be under a minute
    since they are not IO bound because all writes are to NullOutputStream.
