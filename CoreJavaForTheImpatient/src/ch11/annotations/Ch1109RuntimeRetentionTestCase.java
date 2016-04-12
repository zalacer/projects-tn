package ch11.annotations;

//9. Implement the @TestCase annotation as a runtime annotation and provide a tool
//that checks it. Again, assume that the test methods are static and restrict yourself to a
//reasonable set of parameter and return types that can be described by strings in the
//annotation elements.

// My solution and supporting classes are in ch1108.annotations.testcase.runtime. In that
// package, Ch1109RuntimeRetentionTestCase.runTestCases() is the annotation processor. It 
// supports all primitive types and their corresponding boxed types. It could be more 
// generally extended by allowing TestCase annotation params and expected strings to include 
// JSON serialized objects as can be created with 
//     com.cedarsoftware.util.io.JsonWriter.objectToJson() 
// and which is demonstrated in Ch1109JsonSmartSkipSerialize.

public class Ch1109RuntimeRetentionTestCase {

}


