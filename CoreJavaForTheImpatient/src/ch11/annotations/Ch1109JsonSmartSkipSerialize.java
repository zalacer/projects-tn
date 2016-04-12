package ch11.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import static com.cedarsoftware.util.io.JsonWriter.FIELD_SPECIFIERS;

//2. If annotations had existed in early versions of Java, then the Serializable
//interface would surely have been an annotation. Implement a @Serializable
//annotation. Choose a text or binary format for persistence. Provide classes for
//streams or readers/writers that persist the state of objects by saving and restoring all
//fields that are primitive values or themselves serializable. Don’t worry about cyclic
//references for now.
//
//3. Repeat the preceding assignment, but do worry about cyclic references.
//
//4. Add a @Transient annotation to your serialization mechanism that acts like the
//transient modifier.

// Instead of reinventing serialization framework, I chose to enhance json 
// serialization provided in json-io (util.io) because this is something that
// seems useful and usable to me and for others. It's hosted at 
//     https://github.com/jdereg/json-io 
// and is available from Maven. In particular util.io.JsonWriter is clumsy to 
// configure to skip fields since that requires creating an array, a list and 
// two maps. To facilitate this I implemented the @Skip field runtime annotation 
// that tags fields to be skipped for serialization. The serialization tool 
// which uses this annotation is jsonSmartSkipSerialize() shown below.
// json-io does not require implementation of a special or tag interface such 
// as Serializable and it can serialize most Java objects as is including those 
// with circular dependencies. So this solution is also handles exercise 3. @Skip 
// can be changed to @Transient in answer to exercise 4. I considered using 
// @Transient in the first place, but I think it's more practical to use an 
// ordinary, simple word denoting an action such as skip rather than transient, 
// which, as an adjective, denotes a property but no implied action, at least
// for me, and has CPU register level connotations like volatile.

public class Ch1109JsonSmartSkipSerialize {

  @Target({ElementType.FIELD, ElementType.TYPE})
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Skip {}

  public static <T> String jsonSmartSkipSerialize(T t) {
    Class<? extends Object> clazz = t.getClass();
    Field[] declaredFields = clazz.getDeclaredFields();
    List<String> fields2Include = new ArrayList<>();
    for (Field f : declaredFields)
      if (f.getAnnotation(Skip.class) == null)
        fields2Include.add(f.getName());
    Map<Class<? extends Object>, List<String>> fieldSpecifiers = new HashMap<>();
    fieldSpecifiers.put(clazz, fields2Include);
    Map<String,Object> optionalArgs = new HashMap<>();
    optionalArgs.put(FIELD_SPECIFIERS, fieldSpecifiers);
    String result = JsonWriter.objectToJson(t, optionalArgs);
    return result;
  }

  public static class Employee {
    private String firstName;
    private String lastName;
    @Skip private transient String confidentialInfo;

    public Employee(String f, String l) {
      this.firstName= f;
      this.lastName = l;
    }

    public String getFirstName() {
      return firstName;
    }
    public String getLastName() {
      return lastName;
    }
    public String getConfidentialInfo() {
      return confidentialInfo;
    }
    public void setFirstName(String firstName) {
      this.firstName = firstName;
    }
    public void setLastName(String lastName) {
      this.lastName = lastName;
    }
    public void setConfidentialInfo(String confidentialInfo) {
      this.confidentialInfo = confidentialInfo;
    }
    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
      result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
      return result;
    }
    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Employee other = (Employee) obj;
      if (firstName == null) {
        if (other.firstName != null)
          return false;
      } else if (!firstName.equals(other.firstName))
        return false;
      if (lastName == null) {
        if (other.lastName != null)
          return false;
      } else if (!lastName.equals(other.lastName))
        return false;
      return true;
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("Employee[firstName=");
      builder.append(firstName);
      builder.append(", lastName=");
      builder.append(lastName);
      builder.append(", confidentialInfo=");
      builder.append(confidentialInfo);
      builder.append("]");
      return builder.toString();
    }
  }

  public static void main(String[] args) {

    Employee margie = new Employee("margie", "fessler");
    margie.setConfidentialInfo("birthday: 19870411"); 
    System.out.println(margie);
    //Employee[firstName=margie, lastName=fessler, confidentialInfo=birthday: 19870411]
    String margieConfidentialInfo = margie.getConfidentialInfo();

    String margieJson = jsonSmartSkipSerialize(margie);
    System.out.println(margieJson);
    // note confidentialInfo is missing in margieJson since its annotated with @Skip
    //{"@type":"ch11.annotations.Ch1102Serializable$Employee","firstName":"margie","lastName":"fessler"}

    // to reconstruct margie
    Employee margieFromJson = (Employee) JsonReader.jsonToJava(margieJson); 
    System.out.println(margieFromJson);
    //Employee[firstName=margie, lastName=fessler, confidentialInfo=null]

    //now add back confidentialInfo        
    margieFromJson.setConfidentialInfo(margieConfidentialInfo); 
    System.out.println(margieFromJson);
    //Employee[firstName=margie, lastName=fessler, confidentialInfo=birthday: 19870411]

  }

}
