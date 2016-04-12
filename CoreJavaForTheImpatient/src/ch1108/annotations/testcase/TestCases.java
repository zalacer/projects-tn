package ch1108.annotations.testcase;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(METHOD)
@Retention(SOURCE)
public @interface TestCases {
  TestCase[] value();
}
