projects-tn

This is a collection of demo projects for reference.

Java8StreamTest - consists of two Java8 main classes, one for performance testing of IntStream
    and the other to test performance of the imperative for loop. First build the project with
    Maven to generate an executable jar or import it into Eclipse or other compatible IDE and
    export an executable jar. Then run the jar for each class separately on a quiescent system
    with no other executing java processes. The java command line should have -Xmx1024 or greater
    to minimize latency due to garbage collection. The runtime of each class should be under a
    minute since they are not IO bound because all writes during the test phase are to 
    NullOutputStream and no reading is done. My tests with these classes demonstrated IntStream
    performance is statistically more than 5 times faster on average and has a significantly
    larger mean/stddev ratio on average compared to imperative for loop performance.
