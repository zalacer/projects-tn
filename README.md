##projects-tn

This is a collection of demo projects for reference.

**Java8StreamTest** consists of two Java8 main classes, one for performance testing of IntStream
    and the other to test performance of the imperative for loop. First build the project with
    Maven to generate an executable jar or import it into Eclipse or other compatible IDE and
    export an executable jar. Then run the jar for each class separately on a quiescent system
    with no other executing java processes. The java command line should have -Xmx1024 or greater
    to minimize latency due to garbage collection. The runtime of each class should be under a
    minute since they are not IO bound because all writes during the test phase are to 
    NullOutputStream and no reading is done. My tests with these classes demonstrated IntStream
    performance is statistically more than 5 times faster on average and has a significantly
    larger mean/stddev ratio on average compared to imperative for loop performance.

**FactorialTailCall** is a Scala sbt project illustrating how a non tail recursive function can
    be converted into one that is for compiler optimization to avoid a StackOverflowError exception.
    It also demonstrates the ease of using Scala BigInt and sbt project configuration with a .scala
    configuration file and the git assembly plugin. After building the assembly jar, running it on
    my laptop computed the factorial of 100K in 5 seconds (with output redirected to a file). This
    project is configured for Scala 2.10.4 and sbt 0.13.7 and should work with later versions of
    scala and some earlier versions of sbt.
    
**JAX-RSdemo1** is a demo RESTful web service project implemented using JAX-RS and Apache CXF. This is
    a Maven Java project exported from Eclipse. A review of RESTful web services and instructions for
    building a RESTful web services project in Eclipse are in JAX-RSdemo1/RESTful-Web-Services.pdf.
    
**JavaClassMethodOverrideDemo** is a Java project demonstrating class method hiding    

