package ch1108.annotations.testcase.runtime;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(METHOD)
@Retention(RUNTIME)
@Repeatable(TestCases.class)
public @interface TestCase {
  String params() default "";
  String expected() default "";
}

