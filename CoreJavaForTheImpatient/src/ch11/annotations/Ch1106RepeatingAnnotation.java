package ch11.annotations;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

//5. Define an annotation @Todo that contains a message describing whatever it is that
//needs to be done. Define an annotation processor that produces a reminder list from
//a source file. Include a description of the annotated item and the todo message.

//6. Turn the annotation of the preceding exercise into a repeating annotation.

public class Ch1106RepeatingAnnotation {
    
    @Target({TYPE,
        CONSTRUCTOR,
        FIELD,
        METHOD,
        PARAMETER,
        LOCAL_VARIABLE})
    @Retention(RUNTIME)
    @Repeatable(Todos.class)
    public @interface Todo {
        String msg() default "default";
    }
    
    @Target({TYPE,
        CONSTRUCTOR,
        FIELD,
        METHOD,
        PARAMETER,
        LOCAL_VARIABLE})
    @Retention(RUNTIME)
    @interface Todos {
        Todo[] value();
    }
    
    @Todo(msg = "message 1")
    @Todo(msg = "message 2")
    @Todo(msg = "message 3")
    public static class D2 {}
    
    public static String generateTodoList2(Class<?> c) throws Exception {
        String cname = c.getSimpleName();
        StringBuilder b = new StringBuilder();
        // class annotation
        Todo[] a = c.getAnnotationsByType(Todo.class);
        for (Todo todo : a) {
            b.append("\n"+cname+":"+todo.msg());
        }
        return b.toString();
    }

    public static void main(String[] args) throws Exception {

        System.out.println(generateTodoList2(D2.class));
//        output:
//            D2:message 1
//            D2:message 2
//            D2:message 3

    }

}
