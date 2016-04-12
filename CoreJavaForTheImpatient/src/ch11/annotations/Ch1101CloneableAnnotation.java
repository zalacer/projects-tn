package ch11.annotations;

// 1. Describe how Object.clone could be modified to use a @Cloneable
// annotation instead of the Cloneable marker interface.

// Object.clone is a native method (like most of Object.class methods) as shown at 
// (0) http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/8u40-b25/java/lang/Object.java#Object.clone%28%29
// The source for it is in jvm.cpp which is available online for Java 8 at
// (1) http://hg.openjdk.java.net/jdk8/jdk8/hotspot/file/2720ab7a0d70/src/share/vm/prims/jvm.cpp
// and for Java 7 at 
// (2) http://hg.openjdk.java.net/jdk7/jdk7/hotspot/file/9b0ca45cd756/src/share/vm/prims/jvm.cpp
// 
// Inspection of these sources shows there is a C++ function named JVM_GetClassAnnotations
// that is mentioned as an argument of JVM_ENTRY() on line 1547 in (1). Code for this 
// function is in 
// (3) https://llvm.org/svn/llvm-project/vmkit/trunk/lib/j3/ClassLib/OpenJDK/OpenJDK.inc 

// It should be possible to use this function to determine if an object is annotated with
// @Cloneable. If so, then in the #ifdef ASSERT in (1) line 546 add a statment to the 
// effect that for any object so annotated guarantee(klass->is_cloneable()) where klass is 
// the object's class. Doing this avoids throwing a CloneNotSupportedException at line 561 
// and proceeding to clone the object by making a shallow copy of it.
//
// (Copies of documents (0)-(4) referred to above are available in 
// Ch1101CloneableAnnotationDocs in this project, but I recommend using the URL for (1) to
// locate the line numbers mentioned.)

public class Ch1101CloneableAnnotation {

}
