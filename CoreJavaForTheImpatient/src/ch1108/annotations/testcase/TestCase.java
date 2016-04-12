package ch1108.annotations.testcase;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(METHOD)
@Retention(SOURCE)
@Repeatable(TestCases.class)
public @interface TestCase {
  String params() default "";
  String expected() default "";
}

