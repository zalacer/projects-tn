package utils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.awt.PageAttributes;
import java.io.PrintWriter;
import java.io.StringWriter;

// This class contains static methods and classes for representing Java
// primitives and Objects as Strings. It handles circular references and
// its universalToString() methods can generally be used to produce
// toString() output for arbitrary Java objects. There is a series of 
// these methods with varying number of argument parameters. All of them 
// wrap _universalToString which directly processes most ordinary objects
// and enums while calling specialized methods for arrays, collections  
// and maps when needed. All development and testing of this class was 
// done with Java version 1.8.0_66, JVM configuration including -Xmx2048m  
// and -Dfile.encoding=UTF-8, Eclipse version Mars.1 (Release 4.5.1) 
// configured at Java compiler compliance level 1.8 and no 3rd party
// library dependencies.

// The arguments of _universalToString are:
// Object obj - the object to be represented; primitives are autoboxed.
// boolean simpleName - controls use of class.getSimpleName() vs.
//   class.getName() to represent object types.
// boolean oneLine - controls format preference as single or multiline, 
//   however there are degrees of multilining parameter formats and 
//   for arrays and collections an attempt is made to represent 
//   parameters of these types concisely within one line depending on
//   their element types and providing max line length isn't exceeded.
// Map<Integer, Triple<Integer, String, String>>... h - is an array of
//   data crucial to resolving circular references and also improves
//   performance through field value lookup. Only the first element is
//   used and in this map the keys are object hashcodes and in the Triple
//   the Integer is the number of times a field was referenced after it 
//   was assigned a name, the first String is the field name and the last 
//   String is a String representation of the field value.

// Of these arguments, the map h is generated during processing and 
// is not included in the wrapper methods. Of the latter, one has only 
// an obj parameter with oneLine and simpleName defaulting  to true
// and h omitted; another adds oneLine as an argument with the same 
// default for simpleName; and the third adds simpleName as an
// argument. The first two try generating a String representation of
// a primitive or object with oneLine set to true, then if that exceeds
// maxLength _universalToString retries execution with oneLine set to 
// false. The third allows oneLine to be explicitly set to false, 
// however that may be overridden by the methods for processing arrays, 
// Collections and Maps in order to achieve more compact results.

// Regarding output format:
// 1. The max line length given by the global private static int field 
//    maxLength = 100 and 
// 2. The base indentation per level is given by the global private static 
//    int field bi = 2. 
// 3. Square brackets ([]) are used only to delimit arrays.
// 4. Curly brackets ({}) are used  only to delimit maps.
// 5. Round brackets(()) are used only to delimit collections and objects 
//    of all other types of classes.
// 6. Angle brackets (<>) are used only delimit object type parameters.
// 7. For objects of classes with a superclass that isn't java.lang.Object,
//    their superclass field values are given first within round brackets
//    followed by their class field values within separate round brackets
//    with no space or punctuation between the matching pairs of round
//    brackets.
// 8. Except for primitives, directly stringable objects including those of 
//    boxed types and objects of type java.lang.Object and 
//    java.awt.AttributeValue, the string representation of each object begins 
//    with its class name and generic types (if any and  determinable) and if 
//    oneLine is false this will be followed by a new line except in the case of
//    short arrays, collections and maps, where shorts means total line length
//    smaller or equal to maxLength.
// 9. In the cases of array objects and those implementing the Collection or
//     Map interfaces, their string representations include only their elements
//    or keys and values in the case of Maps and do not include their class fields 
//    per se, since this is a customary based on the implementations of their
//    their existing toString methods and because it conveys the information that
//    is most often useful. 
//10. In the representations of arrays and collections, their element values are 
//    comma separated. 
//11. In the representations of Maps, key:value pairs are separated with a colon
//    and multiple pairs are separated by commas.
//12. For other types of objects, multiple field values are comma separated.
//13. Objects of class Object are represented with Object.toString();
//14. Null is represented as the string null without quotes.
//15. String are surrounded with double quotes.
//16. Chars and Characters surrounded with single quotes.
//17. If all elements in an array or Collection are null, its generic type is
//    represented as the fictitious class Null (taking a Scala precedent). This 
//    extends to typing of Maps via their key set and values Collection view. 

// Some examples assuming simpleName is true:

// both int 1 and new Integer(1) are represented as: 1

// new int[]{1,2,3} is represented as: int[1,2,3]

// new Integer[]{1,2,3} is represented as Integer[1,2,3]

// new Employee("Jane",salary=101000.1) is represented as: 
//   Employee(name="Jane",salary=101000.1)
// (see below for the definition of class Employee)

// new Manager("Jane",salary=101000.1,23500.67) where Manager  
// extends Employee is represented as follows when onLine is true:
//   Manager(name="Jane",salary=101000.1)(bonus=23500.67)
// (see below for the definition of class Manager)

// given public static class Z { public Z z = this; }
// new Z() is represented as: Z(z=Z(z))

// given A a1 = new A();
//       B b1 = new B(a1);
//       a1.b = b1;
// a1 and b1 are circular references and with with oneLine true a1 is 
// represented as: A(b=B(a=A(b,aid=1),bid=3),aid)

// new HashSet<Integer>(Arrays.asList(1,2,3) is represented as:
//   "HashSet<Integer>(1,2,3)" 
// when oneLine is true.

// new HashMap<Integer,String>(){{put(1,"one");put(2,"two");put(3,"three");}};
// is represented as :
//   HashMap<Integer,String>{1:"one",2:"two",3:"three"}
// when oneLine is true.

// For other examples run this class's main() method and inspect the
// preceeding class and object definitions for clarification.

public class UniversalToString {

  private static int maxLength = 100; // max line length
  private static int bi = 2; // base indent per level

  // user interface and wrapper for _universalToString
  public static String universalToString(Object obj) {
    return _universalToString(obj, true, true);
  }

  // user interface and wrapper for _universalToString
  public static String universalToString(Object obj, boolean simpleName) {
    return _universalToString(obj, simpleName, true);
  }

  // user interface and wrapper for _universalToString
  public static String universalToString(Object obj, boolean simpleName, boolean oneLine) {
    return _universalToString(obj, simpleName, oneLine);
  }

  @SafeVarargs
  public static String _universalToString(Object obj, boolean simpleName, boolean oneLine,
      Map<Integer, Triple<Integer, String, String>>... h) {

    if (Objects.isNull(obj))
      return "null";

    Class<? extends Object> objClass = obj.getClass();
    String objClassName = objClass.getName();

    String cname = simpleName ? objClass.getSimpleName() : objClass.getName();

    if (objClassName.equals("java.lang.Object"))
      return obj.toString();

    if (objClassName.matches("java.lang.String"))
      return "\"" + obj + "\"";

    if (objClassName.matches("java.lang.Character"))
      return "'" + obj + "'";

    if (isStringable(obj)) {
      return obj.toString();
    }

    int hash = Objects.hashCode(obj);
    Map<Integer, Triple<Integer, String, String>> hashes = null;

    if (h.length == 0) {
      hashes = new HashMap<Integer, Triple<Integer, String, String>>();
    } else {
      hashes = h[0];
    }

    boolean isEnum = false;
    if (obj.getClass().isEnum())
      isEnum = true;

    if (objClass.isArray()) {
      return universalToString4Array(obj, simpleName, oneLine);
    }

    if (obj instanceof Collection) {
      return universalToString4Collection(obj, simpleName, oneLine);
    }

    if (obj instanceof Map) {
      return universalToString4Map(obj, simpleName, oneLine);
    }

    List<Class<? extends Object>> il = new ArrayList<>();
    if (!objClass.getSuperclass().getName().equals("java.lang.Object")) {
      il.add(objClass.getSuperclass());
    }
    il.add(objClass);
    StringBuilder sb = new StringBuilder();
    String sbs = "";
    int[] indent = new int[2];

    for (int i = 0; i < il.size(); i++) {
      if (i == 0) {
        if (il.size() == 2) {
          if (simpleName) {
            sb.append(il.get(1).getSimpleName() + "(");
          } else {
            sb.append(il.get(1).getName() + "(");
          }
        } else {
          if (simpleName) {
            sb.append(il.get(0).getSimpleName() + "(");
          } else {
            sb.append(il.get(0).getName() + "(");
          }
        }
        indent[0] = bi;
      } else {
        if (oneLine) {
          sb.append("(");
        } else {
          sb.append("\n" + space(bi) + "(");
        }
        indent[1] = bi + 1;
      }

      Class<? extends Object> tclass = il.get(i);

      // enum processing setup
      StringBuilder sb1 = null; // for accumulating enum values array elements
      int c1 = 0; // counter for enum values array elements accumulated in sb1
      List<String> elist = null;
      boolean enumActive = false;

      if (isEnum) {
        sb1 = new StringBuilder();
        elist = new ArrayList<String>();
        enumActive = true;
      }

      if (enumActive && !isEnum) {
        sb1 = null;
        elist = null;
      }
      // end of enum processing setup

      String uts = "";
      boolean first = true;

      for (Field f : tclass.getDeclaredFields()) {
        f.setAccessible(true);
        String name = f.getName();
        Class<?> fclass = f.getType();
        String fclassName = fclass.getName();
        Object value = null;
        
        // handling of Enum values array, i.e. ENUM$VALUES
        if (isEnum) {
          int elen = 0;
          if (c1 == 0) {
            sb1.append("values=");
          }
          if (fclass.getName().equals(obj.getClass().getName())) {
            // the class of each element in ENUM$VALUES is that of the enum
            c1++;
            elist.add(name);
            continue;
          }
          if (name.equals("ENUM$VALUES")) {
            try {
              value = f.get(obj);
            } catch (IllegalArgumentException | IllegalAccessException e) {
              System.out.println("exception when referencing parameter " + name + " " + "with value of type "
                  + value.getClass().getName() + " in object of " + obj.getClass().getName());
              continue;
            }
            if (Objects.nonNull(value) && fclass.isArray()) {
              elen = ((Object[]) value).length;
            } else {
              elen = 0;
            }
            if (elen > 0 && c1 == elen) {
              uts = universalToString4Array(elist.toArray(), simpleName, oneLine, newHashes());
              // remove quotes placed around elist elements by universalToString4Array 
              // since they represent enum constants not Strings
              uts = uts.replaceAll("\"", "");
              // replace the className assigned by universalToString4Array with the enum's
              uts = uts.replaceFirst("^[^\\[]*", cname);
              sb1.append(uts);
              if (oneLine) {
                sb.append(sb1.toString() + ",");
              } else if (i == 0) {
                sb.append("\n" + space(indent[0]) + sb1.toString() + ",");
              } else {
                if (first) {
                  sb.append(sb1.toString() + ",");
                  first = false;
                } else {
                  // additional indent[1] indentation for values elements if they were 
                  // put on separate lines to minimize horizontal space consumption
                  sb.append("\n" + space(indent[1]) + indent(sb1.toString(), indent[1]) + ",");
                }
              }
            }
            isEnum = false;
            continue;
          }
        } // end of Enum handling

        boolean utsAssigned = false;
        boolean skipStringMatch = false;
        boolean resolved = false;
        int vhash = 0;

        try {
          value = f.get(obj);
        } catch (IllegalArgumentException | IllegalAccessException e) {
          System.out.println("exception when referencing parameter " + name + " " + "with value of type "
              + value.getClass().getName() + " in object of " + obj.getClass().getName());
          continue;
        }

        if (Objects.isNull(value)) {
          uts = "null";
          utsAssigned = true;
          skipStringMatch = true;
        } 
        
        vhash = Objects.hashCode(value);

        // Output parameter condensation is substituting just a parameter name for 
        // name=value when it's been previously defined by such (in current object).
        // For doing this simply with decent accuracy for boxed types and strings, 
        // their hashcodes are modified to include a dependence on their immediately 
        // enclosing object. This procedure could be carried through to arrays, 
        // collections, maps and enums when the need arises.

        if (fclassName.equals("java.lang.Object")) {
          uts = obj.toString();
          utsAssigned = true;
        }

        if (fclassName.matches("java.lang.String") && !skipStringMatch) {
          uts = "\"" + value + "\"";
          utsAssigned = true;
        }

        if (fclassName.matches("java.lang.Character")) {
          uts = "'" + value + "'";
          utsAssigned = true;
        }

        if (isStringable(fclassName)) { // this includes all previous converted to String
          if (!utsAssigned)
            uts = value.toString();
          vhash = (43 * (37 * (31 + hash) + vhash) + uts.hashCode());
          if (hashes.containsKey(vhash)) {
            hashes.get(vhash).setValue(uts);
            hashes.get(vhash).setCount(hashes.get(vhash).getCount() + 1);
            if (Objects.isNull(hashes.get(vhash).getName())) {
              hashes.get(vhash).setName(name);
            }
          } else {
            hashes.put(vhash, new Triple<Integer, String, String>(1, name, uts));
          }
          resolved = true;
        }

        String vtype = null;

        if (!resolved) {
          if (fclass.isArray())
            vtype = "array";
          if (getAllInterfaces(fclassName).contains("java.util.Collection"))
            vtype = "collection";
          if (getAllInterfaces(fclassName).contains("java.util.Map"))
            vtype = "map";
          if (fclass.isEnum())
            vtype = "enum";
          if (Objects.nonNull(vtype)) {
            if (hashes.containsKey(vhash) && Objects.nonNull(hashes.get(vhash).getValue())) {
              uts = hashes.get(vhash).getValue();
              resolved = true;
            }
          }
        }

        if (!resolved && Objects.nonNull(vtype)) {
          switch (vtype) {
          case "array":
            uts = universalToString4Array(value, simpleName, oneLine, hashes);
            resolved = true;
            break;
          case "collection":
            uts = universalToString4Collection(value, simpleName, oneLine, hashes);
            resolved = true;
            break;
          case "map":
            uts = universalToString4Map(value, simpleName, oneLine, hashes);
            resolved = true;
            break;
          case "enum":
            uts = _universalToString(value, simpleName, oneLine, hashes);
            resolved = true;
            break;
          }
        }

        if (resolved && Objects.nonNull(vtype)) {
          hashes.get(vhash).setCount(hashes.get(vhash).getCount() + 1);
          if (Objects.isNull(hashes.get(vhash).getName())) {
            hashes.get(vhash).setName(name);
          }
        }

        if (resolved) {
          if (hashes.get(vhash).getCount().intValue() > 1) {
            String hname = hashes.get(vhash).getName();
            if (name.equals(hname)) {
              // append only the parameter name when it has been 
              // already stored and the current parameter has the
              // same name and vhash 
              if (oneLine) {
                sb.append(name + ",");
              } else if (i == 0) {
                sb.append("\n" + space(indent[i]) + name + ",");
              } else if (first) {
                sb.append(name + ",");
                first = false;
              } else {
                sb.append("\n" + space(indent[i]) + name + ",");
              }
            } else {
              // if the parameter's vhash has already been stored with 
              // another name, hname, append name=hname
              if (oneLine) {
                sb.append(name + "=" + hname + ",");
              } else if (i == 0) {
                sb.append("\n" + space(indent[i]) + name + "=" + hname + ",");
              } else if (first) {
                sb.append(name + "=" + hname + ",");
                first = false;
              } else {
                sb.append("\n" + space(indent[i]) + name + "=" + hname + ",");
              }
            }
          } else {
            if (oneLine) {
              sb.append(name + "=" + uts + ",");
            } else if (i == 0) {
              sb.append("\n" + space(indent[i]) + name + "=" + indent(uts, indent[i] + name.length() + 1) + ",");
            } else if (first) {
              sb.append(name + "=" + indent(uts, indent[i] + name.length() + 2) + ",");
              first = false;
            } else {
              sb.append("\n" + space(indent[i]) + name + "=" + indent(uts, indent[i] + name.length() + 2) + ",");
            }
          }
        }

        if (!resolved) {
          // When a parameter is first encountered its name and value are
          // saved in the hashes map, so the next time it occurs it's
          // designated in the output with just the parameter name. 
          // In some cases, however, a compound object may contain other  
          // objects with identically named parameters with different 
          // values. In that case all but the first occurence of such a 
          // parameter name is qualified by appending 
          // "@"+Integer.toHexString(vhash) to it so the resulting name is
          // unique in the output string. Therefore, the following code 
          // renames a parameter that has the same name as but different 
          // value than one previously occurring in the current object.
          if (!hashes.keySet().contains(vhash)) {
            for (Integer j : hashes.keySet()) {
              if (Objects.nonNull(hashes.get(j).getName())) {
                if (hashes.get(j).getName().equals(name) && j.intValue() != vhash) {
                  name = name + "@" + Integer.toHexString(vhash);
                  break;
                }
              }
            }
            hashes.put(vhash, new Triple<Integer, String, String>(1, name, null));
          } else {
            hashes.get(vhash).setCount(hashes.get(vhash).getCount() + 1);
            if (Objects.isNull(hashes.get(vhash).getName())) {
              hashes.get(vhash).setName(name);
            }
          }

          if (hashes.get(vhash).getCount().intValue() > 1) {
            String hname = hashes.get(vhash).getName();
            if (name.equals(hname)) {
              if (oneLine) {
                sb.append(name + ",");
              } else if (i == 0) {
                sb.append("\n" + space(indent[i]) + name + ",");
              } else if (first) {
                sb.append(name + ",");
                first = false;
              } else {
                sb.append("\n" + space(indent[i]) + name + ",");
              }
            } else {
              if (oneLine) {
                sb.append(name + "=" + hname + ",");
              } else if (i == 0) {
                sb.append("\n" + space(indent[i]) + name + "=" + hname + ",");
              } else if (first) {
                sb.append(name + "=" + hname + ",");
                first = false;
              } else {
                sb.append("\n" + space(indent[i]) + name + "=" + hname + ",");
              }
            }
          } else {
            if (hashes.containsKey(vhash) && Objects.nonNull(hashes.get(vhash).getValue())) {
              uts = hashes.get(vhash).getValue();
            } else {
              uts = _universalToString(value, simpleName, oneLine, hashes);
            }
            hashes.get(vhash).setCount(1);
            hashes.get(vhash).setName(name);
            if (oneLine) {
              sb.append(name + "=" + uts + ",");
            } else if (i == 0) {
              sb.append("\n" + space(indent[i]) + name + "=" + indent((String) uts, indent[i]) + ",");
            } else if (first) {
              sb.append(name + "=" + indent((String) value, indent[i] + name.length() + 2) + ",");
              first = false;
            } else {
              sb.append(
                  "\n" + space(indent[i]) + name + "=" + indent((String) value, indent[i] + name.length() + 2) + ",");
            }
          }
          if (Objects.nonNull(uts))
            hashes.get(vhash).setValue(uts);
        }
      }

      sbs = sb.toString().replaceFirst(",$", "\\)");
      if (i < il.size() - 1) {
        sb.delete(0, sb.length());
        sb.append(sbs);
      }
    }

    sbs = sb.toString().replaceFirst(",$", "\\)");
    if (oneLine) {
      int sbsMaxlength = Integer.MIN_VALUE;
      String[] sbsa = sbs.split("\n");
      for (int i = 0; i < sbsa.length; i++) {
        if (sbsa[i].length() > sbsMaxlength)
          sbsMaxlength = sbsa[i].length();
      }
      if (sbsMaxlength > maxLength)
        return _universalToString(obj, simpleName, false);
    }

    hashes.put(hash, new Triple<Integer, String, String>(0, null, sbs));
    return sbs;
  }

  @SafeVarargs
  public static String universalToString4Array(Object obj, boolean simpleName, boolean oneLine,
      Map<Integer, Triple<Integer, String, String>>... h) {

    if (Objects.isNull(obj))
      return "null";
    if (!obj.getClass().isArray())
      throw new IllegalArgumentException("arg obj is not an array");

    int hash = Objects.hashCode(obj);
    Map<Integer, Triple<Integer, String, String>> hashes = null;

    if (h.length == 0) {
      hashes = new HashMap<Integer, Triple<Integer, String, String>>();
    } else {
      hashes = h[0];
      if (hashes.containsKey(hash) && Objects.nonNull(hashes.get(hash).getValue()))
        return hashes.get(hash).getValue();
    }

    Class<?> compType = obj.getClass().getComponentType();
    String name = getType(obj, simpleName);
    if (Array.getLength(obj) == 0)
      return name + "[]";
    String pre = name + "[";
    String indent = space(bi);
    String uts = "";
    String sbstring = "";
    String sb1string = "";
    StringBuilder sb = new StringBuilder();
    StringBuilder sb1 = new StringBuilder();
    sb.append(pre);

    for (int i = 0; i < Array.getLength(obj); i++) {
      if (compType.getName().matches("java.lang.String")) {
        sb1.append("\"" + Array.get(obj, i) + "\",");
      } else if (compType.getName().matches("char") || compType.getName().matches("java.lang.Character")) {
        sb1.append("'" + Array.get(obj, i) + "',");
      } else {
        sb1.append(Array.get(obj, i) + ",");
      }
    }

    sb1string = sb1.substring(0, sb1.length() - 1) + "]";

    if (compType.isPrimitive() || isStringable(compType)) {
      // test to see if it can fit in either 1st or 2nd line
      if ((sb1string + pre).length() <= maxLength) {
        // maxLength is a global private static int field
        // put it all on one line since it fits
        sbstring = sb.append(sb1string).toString();
        hashes.put(hash, new Triple<Integer, String, String>(0, null, sbstring));
        return sbstring;
      } else if ((sb1string + indent).length() <= maxLength) {
        // put array representation on 2nd line since it fits
        sbstring = sb.append("\n" + indent + sb1string).toString();
        hashes.put(hash, new Triple<Integer, String, String>(0, null, sbstring));
        return sbstring;
      } else { // go multiline
        // this is the multiline sink for arrays of primitives or stringables
        for (int i = 0; i < Array.getLength(obj); i++) {
          if (i == 0)
            sb.append("\n");
          sb.append(indent);
          sb.append(indent(Array.get(obj, i), indent.length()) + ",\n");
        }
        sbstring = sb.substring(0, sb.length() - 2) + "]";
        hashes.put(hash, new Triple<Integer, String, String>(0, null, sbstring));
        return sbstring;
      }
    }

    if (!(compType.isPrimitive() || isStringable(compType))) {
      // try one line first
      String[] elements = new String[Array.getLength(obj)]; // for full multiline use if needed
      sb1.delete(0, sb1.length());
      for (int i = 0; i < Array.getLength(obj); i++) {
        uts = _universalToString(Array.get(obj, i), simpleName, true, newHashes());
        elements[i] = uts;
        sb1.append(uts + ",");
      }
      sb1string = sb1.substring(0, sb1.length() - 1) + "]";
      if ((sb1string + pre).length() <= maxLength) {
        sbstring = sb.append(sb1string).toString();
        hashes.put(hash, new Triple<Integer, String, String>(0, null, sbstring));
        return sbstring;
      } else if ((sb1string + indent).length() <= maxLength) {
        sbstring = sb.append("\n" + indent + sb1string).toString();
        hashes.put(hash, new Triple<Integer, String, String>(0, null, sbstring));
        return sbstring;
      }
      // begin multiline sink for !(compType.isPrimitive() || isStringable(compType))
      // first see if each element can fit on one line
      boolean elementsEachOnOneLine = true;
      for (int i = 0; i < elements.length - 1; i++) {
        if ((elements[i] + 1 + indent).length() > maxLength) {
          elementsEachOnOneLine = false;
        }
      }
      if ((elements[elements.length - 1] + 1 + indent).length() > maxLength) {
        elementsEachOnOneLine = false;
      }
      if (elementsEachOnOneLine) {
        sb1.delete(0, sb1.length());
        for (int i = 0; i < elements.length; i++) {
          sb1.append(indent + elements[i] + ",\n");
        }
        sb1string = sb1.substring(0, sb1.length() - 2) + "]";
        sbstring = sb.append("\n" + sb1string).toString();
        hashes.put(hash, new Triple<Integer, String, String>(0, null, sbstring));
        return sbstring;
      } else {
        // cannot simply use the oneLine==true element strings
        sb1.delete(0, sb1.length());
        for (int i = 0; i < Array.getLength(obj); i++) {
          uts = _universalToString(Array.get(obj, i), simpleName, false, newHashes());
          sb1.append(indent + indent(uts, bi) + ",\n");
        }
        sb1string = sb1.substring(0, sb1.length() - 2) + "]";
        sbstring = sb.append("\n" + sb1string).toString();
        hashes.put(hash, new Triple<Integer, String, String>(0, null, sbstring));
        return sbstring;
      }
    }

    return sbstring;
  }

  @SafeVarargs
  public static String universalToString4Collection(Object obj, boolean simpleName, boolean oneLine,
      Map<Integer, Triple<Integer, String, String>>... h) {

    if (Objects.isNull(obj))
      return "null";
    if (!(obj instanceof Collection))
      throw new IllegalArgumentException("arg obj is not a Collection");

    int hash = Objects.hashCode(obj);
    Map<Integer, Triple<Integer, String, String>> hashes = null;

    if (h.length == 0) {
      hashes = new HashMap<Integer, Triple<Integer, String, String>>();
    } else {
      hashes = h[0];
      if (hashes.containsKey(hash) && Objects.nonNull(hashes.get(hash).getValue()))
        return hashes.get(hash).getValue();
    }

    Class<? extends Object> colClass = obj.getClass();
    String colClassName = simpleName ? colClass.getSimpleName() : colClass.getName();
    @SuppressWarnings("unchecked")
    Collection<Object> co = (Collection<Object>) obj;
    Object[] oa = co.toArray();
    String compClassName = getType(obj, simpleName);
    String fullTypedName = colClassName + compClassName;
    if (oa.length == 0)
      return fullTypedName + "()";
    StringBuilder sb = new StringBuilder();
    String sbstring = "";
    String pre = fullTypedName;
    sb.append(pre);
    String uts = universalToString4Array(oa, simpleName, oneLine, newHashes());
    String utsmod = "(" + replaceLast(uts, "\\]$", "\\)").replaceFirst("^[^\\[]+\\[", "");
    if (oneLine) {
      sb.append(utsmod);
      sbstring = sb.toString();
      hashes.put(hash, new Triple<Integer, String, String>(0, null, sbstring));
    } else {
      // universalToString4Array already did the indenting for !oneLine
      sb.append(utsmod);
      sbstring = sb.toString();
    }
    hashes.put(hash, new Triple<Integer, String, String>(0, null, sbstring));
    return sbstring;
  }

  @SafeVarargs
  public static String universalToString4Map(Object obj, boolean simpleName, boolean oneLine,
      Map<Integer, Triple<Integer, String, String>>... h) {

    if (Objects.isNull(obj))
      return "null";
    if (!(obj instanceof Map))
      throw new IllegalArgumentException("arg obj is not a Map");

    int hash = Objects.hashCode(obj);
    Map<Integer, Triple<Integer, String, String>> hashes = null;

    if (h.length == 0) {
      hashes = new HashMap<Integer, Triple<Integer, String, String>>();
    } else {
      hashes = h[0];
      if (hashes.containsKey(hash) && Objects.nonNull(hashes.get(hash).getValue()))
        return hashes.get(hash).getValue();
    }

    String className, pre, post, con, k, v;
    Object ko, vo;
    StringBuilder sb = new StringBuilder();
    StringBuilder sb1 = new StringBuilder();
    String sbstring = "";
    String sb1string = "";
    @SuppressWarnings("unchecked")
    Map<Object, Object> co = (Map<Object, Object>) obj;
    Object[] oa = co.keySet().toArray();
    Class<? extends Object> c = obj.getClass();
    className = simpleName ? c.getSimpleName() : c.getName();
    String mapType = getType(obj, simpleName);
    if (oa.length == 0)
      return className + mapType + "{}";
    pre = className + mapType + "{";
    sb.append(pre);
    post = "}";
    con = ":";
    boolean notOneLine = false;

    // do the oneLine==true computation first
    Map<String, String> tm = new LinkedHashMap<>();
    for (int j = 0; j < oa.length; j++) {
      // key processing
      ko = oa[j];
      k = _universalToString(ko, simpleName, true, newHashes());
      // value processing
      vo = co.get(oa[j]);
      v = _universalToString(vo, simpleName, true, newHashes());
      tm.put(k, v);
    }
    for (String s : tm.keySet()) {
      sb1.append(s + con + tm.get(s) + ",");
    }
    sb1string = sb1.substring(0, sb1.length() - 1) + post;
    if ((sb1string + pre).length() > maxLength) {
      notOneLine = true;
    } else {
      sbstring = sb.append(sb1string).toString();
    }
    if (notOneLine) {
      // find maxKeyLength
      int maxKeyLength = Integer.MIN_VALUE;
      for (String s : tm.keySet()) {
        if (s.length() > maxKeyLength)
          maxKeyLength = s.length();
      }
      // determine if key-values can all be formatted on one line
      boolean kvsEachOnOneLine = true;
      String ts = "";
      sb1.delete(0, sb1.length());
      String[] ta = tm.keySet().toArray(new String[tm.size()]);
      for (int i = 0; i < ta.length; i++) {
        ts = String.format(space(bi) + "%1$" + maxKeyLength + "s" + con + "%2$s,\n", ta[i], tm.get(ta[i]));
        if (ts.length() > maxLength) {
          kvsEachOnOneLine = false;
          break;
        } else {
          sb1.append(ts);
        }
      }
      if (kvsEachOnOneLine) {
        sb1string = sb1.substring(0, sb1.length() - 2) + post;
        sbstring = sb.append("\n" + sb1string).toString();
      } else {
        // try with keys in oneLine format and and values in multiline format
        kvsEachOnOneLine = true;
        sb1.delete(0, sb1.length());
        //        int tsMaxLineLength = Integer.MIN_VALUE;
        //        int firstLineLength = 0;
        for (int j = 0; j < oa.length; j++) {
          vo = co.get(oa[j]);
          v = _universalToString(vo, simpleName, false, newHashes());
          ts = String.format(space(bi) + "%1$" + maxKeyLength + "s" + con + "%2$s,\n", ta[j],
              indent(v, bi + maxKeyLength + con.length()));
          String[] tsplit = ts.split("\n");
          for (int m = 0; m < tsplit.length; m++) {
            if (tsplit[m].length() > maxLength) {
              kvsEachOnOneLine = false;
              break;
            }
          }
          if (kvsEachOnOneLine) {
            sb1.append(ts);
          } else
            break;
        }
        if (kvsEachOnOneLine) {
          sb1string = sb1.substring(0, sb1.length() - 2) + post;
          sbstring = sb.append("\n" + sb1string).toString();
        } else {
          // multiline format for keys and values
          sb1.delete(0, sb1.length());
          maxKeyLength = Integer.MIN_VALUE;
          List<String[]> kl = new ArrayList<>();
          List<String[]> vl = new ArrayList<>();
          String[] ksplit = null;
          String[] vsplit = null;
          for (int j = 0; j < oa.length; j++) {
            // key multiline resolution
            ko = oa[j];
            k = _universalToString(ko, simpleName, false, newHashes());
            // value multiline resolution
            vo = co.get(oa[j]);
            v = _universalToString(vo, simpleName, false, newHashes());
            ksplit = k.split("\n");
            vsplit = v.split("\n");
            // make ksplit and vsplit have the same length padding with "" when needed
            String[][] oar = align(ksplit, vsplit);
            kl.add(oar[0]);
            vl.add(oar[1]);
            for (int m = 0; m < ksplit.length; m++) {
              if (ksplit[m].length() > maxKeyLength)
                maxKeyLength = ksplit[m].length();
            }
          }
          Formatter f = new Formatter(sb1, Locale.US);
          String t = "";
          for (int m = 0; m < kl.size(); m++) {
            for (int n = 0; n < kl.get(m).length; n++) {
              t = kl.get(m)[n];
              if (Objects.isNull(t))
                t = "";
              if (n == 0) {
                f.format(space(bi) + "%1$-" + (maxKeyLength + 1) + "s" + con + " %2$s\n", t, vl.get(m)[n]);
              } else if (n == kl.get(m).length - 1) {
                f.format(space(bi) + "%1$-" + (maxKeyLength + con.length() + 1) + "s" + " %2$s,\n", t, vl.get(m)[n]);
              } else {
                f.format(space(bi) + "%1$-" + (maxKeyLength + con.length() + 1) + "s" + " %2$s\n", t, vl.get(m)[n]);
              }
            }
          }
          f.close();
          sb1string = sb1.substring(0, sb1.length() - 2) + post;
          sbstring = sb.append("\n" + sb1string).toString();
        }
      }
    }

    hashes.put(hash, new Triple<Integer, String, String>(0, null, sbstring));
    return sbstring;
  }

  public static boolean isStringable(Object o) {
    // test if object's class is one known to have a reasonable toString() method
    Class<? extends Object> c = o.getClass();
    if (c.getName().matches("java.lang.String|java.lang.Integer|java.lang.Long"
        + "|java.lang.Double|java.lang.Byte|java.lang.Character|java.lang.Boolean" 
        + "|java.lang.Short|java.lang.Float")
        || (!c.getName().equals("java.lang.Object") && Objects.nonNull(c.getSuperclass())
            && c.getSuperclass().getName().equals("java.awt.AttributeValue"))
        || c.getName().equals("java.awt.AttributeValue"))
      return true;
    return false;
  }

  public static boolean isStringable(String s) {
    
    if (s.matches("int|long|double|byte|char|boolean|short|float"
        + "java.lang.String|java.lang.Integer|java.lang.Long"
        + "|java.lang.Double|java.lang.Byte|java.lang.Character|java.lang.Boolean" 
        + "|java.lang.Short|java.lang.Float")) {
      return true;
    } 

    Class<?> c = null;
    try {
      c = Class.forName(s);
    } catch (ClassNotFoundException e) {
      System.err.println("isStringable(String): cannot find class for "+s
          +"\n"+shortenedStackTrace(e, 1));
    }
    
    if (Objects.nonNull(c)) {
      if ((!c.getName().equals("java.lang.Object") && Objects.nonNull(c.getSuperclass())
          && c.getSuperclass().getName().equals("java.awt.AttributeValue"))
          || c.getName().equals("java.awt.AttributeValue")) {
        return true;
      }
    }
  
    return false;
  }
  
  public static List<String> getAllInterfaces(String s) {
    if (Objects.isNull(s)) {
      throw new IllegalArgumentException("getAllInterfaces: s is null");
    }
    
    if (s.length() == 0) {
      throw new IllegalArgumentException("getAllInterfaces: s is empty");
    }

    Class<?> c = null;
    try {
      c = Class.forName(s);
    } catch (ClassNotFoundException e) {
      System.err.println("getAllInterfaces: cannot get class for name "+s
          + "\n"+shortenedStackTrace(e, 1));
    }
    
    Set<String> z = new HashSet<>();

    while (c != null) {
      Class<?>[] itfs = c.getInterfaces();
      for (Class<?> x : itfs) z.add(x.getName());
      c = c.getSuperclass();
    }

    return new ArrayList<String>(z);
  }
  
  public static String shortenedStackTrace(Throwable t, int maxLines) {
    StringWriter writer = new StringWriter();
    t.printStackTrace(new PrintWriter(writer));
    String[] lines = writer.toString().split("\n");
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < Math.min(lines.length, maxLines); i++) {
      sb.append(lines[i]).append("\n");
    }

    return sb.toString();
  }
  
  public static final String space(int length) {
    // create a new String consisting of a space repeated length times
    char[] data = new char[length];
    Arrays.fill(data, ' ');
    return new String(data);
  }

  public static String indent(Object o, int i) {
    // indent components of a string separated by a newline by i spaces
    String s = "" + o;
    String q = s.replaceAll("\r\n", "\n");
    q = q.replaceAll("\n", "\n" + space(i));
    return q;
  }

  public static boolean hasElements(Object o) {
    Class<?> c = o.getClass();
    if (c.isArray() || Iterable.class.isAssignableFrom(c) || Map.class.isAssignableFrom(c)) {
      return true;
    } else {
      return false;
    }
  }

  public static boolean hasElements(Class<?> c) {
    if (c.isArray() || Iterable.class.isAssignableFrom(c) || Map.class.isAssignableFrom(c)) {
      return true;
    } else {
      return false;
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static String getType(Object o, boolean simpleName, StringBuilder... sbArray) {
    // Attempts to determine the possible generic type of o by examining the
    // types of its elements, if any, otherwise returns o's class name. 
    // If an object is generically typed but has no subelements, its type 
    // parameters are generally irretrievable, especially after having cast 
    // it to Object.

    StringBuilder sb = null;
    if (sbArray.length > 0) {
      sb = sbArray[0];
    } else {
      sb = new StringBuilder();
    }

    Class<?> c = o.getClass();
    Class<?> type = null;
    Iterator oit = null;
    String name = "";
    String name2 = "";
    Set<Class<?>> cs = new HashSet<>();
    Iterator csit = null;
    Object tmp = null;
    Object save = null;
    boolean saveDone = false;
    boolean oneNotNull = false;

    if (c.isArray()) {
      type = c.getComponentType();
      name = simpleName ? type.getSimpleName() : type.getName();
      if (type.isPrimitive()) {
        sb.append(name);
      } else {
        oneNotNull = false;
        for (int i = 0; i < Array.getLength(o); i++) {
          tmp = Array.get(o, i);
          if (Objects.nonNull(tmp)) {
            oneNotNull = true;
            break;
          }
        }
        if (hasElements(type) && oneNotNull) {
          name2 = getType(tmp, simpleName, new StringBuilder());
          sb.append(name + name2);
        } else if (hasElements(type) && !oneNotNull && Array.getLength(o) > 0) {
          sb.append(name + "<Null>");
        } else {
          sb.append(name);
        }
      }

    } else if (Iterable.class.isAssignableFrom(c)) {
      cs.clear();
      oit = ((Iterable) o).iterator();
      saveDone = false;
      save = null;
      while (oit.hasNext()) {
        tmp = oit.next();
        if (Objects.nonNull(tmp)) {
          cs.add(tmp.getClass());
          if (!saveDone) {
            save = tmp;
            saveDone = true;
          }
        }
      }
      if (cs.size() == 0) {
        return simpleName ? c.getSimpleName() : c.getName();
      } else if (cs.size() == 1) {
        csit = cs.iterator();
        type = (Class<?>) csit.next();
        name = simpleName ? type.getSimpleName() : type.getName();
        sb.append("<" + name);
        if (saveDone && hasElements(save)) {
          name = getType(save, simpleName, new StringBuilder());
          sb.append(name + ">");
        } else {
          sb.append(">");
        }
      } else if (cs.size() > 1) {
        name = simpleName ? "Object" : "java.lang.Object";
        sb.append("<" + name + ">");
      }

    } else if (Map.class.isAssignableFrom(c)) {
      Map<Object, Object> map = (Map<Object, Object>) o;
      if (map.keySet().size() == 0 || map.values().size() == 0) 
        return simpleName ? c.getSimpleName() : c.getName();
      name = getType(map.keySet(), simpleName, new StringBuilder());
      sb.append(name.substring(0, name.length() - 1) + ",");
      name = getType(map.values(), simpleName, new StringBuilder());
      sb.append(name.substring(1, name.length()));

    } else {
      name = simpleName ? c.getSimpleName() : c.getName();
      sb.append(name);
    }

    return sb.toString();
  }

  public static HashMap<Integer, Triple<Integer, String, String>> newHashes() {
    return new HashMap<Integer, Triple<Integer, String, String>>();
  }

  public static String replaceLast(String text, String regex, String replacement) {
    return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
  }

  public static String[][] align(String[] a, String[] b) {
    int alen = a.length;
    int blen = b.length;
    int len = 0;

    if (alen > blen) {
      len = alen;
      b = Arrays.copyOf(b, alen);
      for (int m = blen; m < alen; m++) {
        b[m] = "";
      }
    } else if (blen > alen) {
      len = blen;
      a = Arrays.copyOf(a, blen);
      for (int m = alen; m < blen; m++) {
        a[m] = "";
      }
    } else {
      len = alen;
    }

    String[][] r = new String[2][len];
    r[0] = a;
    r[1] = b;
    return r;
  }

  public static class Triple<R, S, T> {

    private R count;
    private S name;
    private T value;

    public Triple() {
    };

    public Triple(R count, S name, T value) {
      this.count = count;
      this.name = name;
      this.value = value;
    }

    public R getCount() {
      return count;
    }

    public void setCount(R count) {
      this.count = count;
    }

    public S getName() {
      return name;
    }

    public void setName(S name) {
      this.name = name;
    }

    public T getValue() {
      return value;
    }

    public void setValue(T value) {
      this.value = value;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((count == null) ? 0 : count.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((value == null) ? 0 : value.hashCode());
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
      @SuppressWarnings("rawtypes")
      Triple other = (Triple) obj;
      if (count == null) {
        if (other.count != null)
          return false;
      } else if (!count.equals(other.count))
        return false;
      if (value == null) {
        if (other.value != null)
          return false;
      } else if (!value.equals(other.value))
        return false;
      if (name == null) {
        if (other.name != null)
          return false;
      } else if (!name.equals(other.name))
        return false;
      return true;
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("Triple[count=");
      builder.append(count);
      builder.append(",name=");
      builder.append(name);
      builder.append(",value=");
      builder.append(value);
      builder.append("]");
      return builder.toString();
    }
  }

  // following are classes for universalToString demos
  
  public static class Z {
    public Z z = this;  
  }

  public static class Atchim {
    public Blygen blygen;
    int atchimId = 1;

    public Atchim() {
      super();
    }

    public Atchim(Blygen blygen) {
      this.blygen = blygen;
    }
  }

  public static class Blygen {
    public Atchim atchim;
    public int blygenId = 3;

    public Blygen() {
      super();
    }

    public Blygen(Atchim atchim) {
      this.atchim = atchim;
    }
  }

  public static class Cancrán {
    Atchim atchim;
    Blygen blygen;
    int cancránId = 5;

    public Cancrán() {
      super();
    }

    public Cancrán(Atchim atchim, Blygen blygen) {
      this.atchim = atchim;
      this.blygen = blygen;
    }
  }

  public static class Dumpe {
    Atchim atchim;
    Blygen blygen;
    Cancrán cancrán;
    int dumpeId = 7;

    public Dumpe() {
      super();
    }

    public Dumpe(Atchim atchim, Blygen blygen, Cancrán cancrán) {
      this.atchim = atchim;
      this.blygen = blygen;
      this.cancrán = cancrán;
    }
  }

  public static class A {
    public B b;
    int aid = 1;

    public A() {
      super();
    }

    public A(B b) {
      this.b = b;
    }
  }

  public static class B {
    public A a;
    public int bid = 3;

    public B() {
      super();
    }

    public B(A a) {
      this.a = a;
    }
  }

  public static class C {
    A a;
    B b;
    int cid = 5;

    public C() {
      super();
    }

    public C(A a, B b) {
      this.a = a;
      this.b = b;
    }
  }

  public static class D {
    A a;
    B b;
    C c;
    int did = 7;

    public D() {
      super();
    }

    public D(A a, B b, C c) {
      this.a = a;
      this.b = b;
      this.c = c;
    }
  }

  public static class A2 {
    public B2 x;
    public int a2id = 2;

    public A2() {
      super();
    }

    public A2(B2 b) {
      this.x = b;
    }
  }

  public static class B2 {
    public A2 y;
    public int b2id = 4;

    public B2() {
      super();
    }

    public B2(A2 a) {
      this.y = a;
    }
  }

  public static class C2 {
    A2 x;
    B2 y;
    int c2id = 6;

    public C2() {
      super();
    }

    public C2(A2 a, B2 b) {
      this.x = a;
      this.y = b;
    }
  }

  public static class D2 {
    A2 x;
    B2 y;
    C2 z;
    int d2id = 8;

    public D2() {
      super();
    }

    public D2(A2 a, B2 b, C2 c) {
      this.x = a;
      this.y = b;
      this.z = c;
    }
  }

  public static class F {
    G g;
    int fid = 9;

    public F() {
      super();
    }

    public F(G g) {
      this.g = g;
    }
  }

  public static class G {
    H h;
    int gid = 10;

    public G() {
      super();
    }

    public G(H h) {
      this.h = h;
    }
  }

  public static class H {
    F f;
    int hid = 11;

    public H() {
      super();
    }

    public H(F f) {
      this.f = f;
    }
  }

  public static class Employee {
    private String name;
    private double salary;

    public Employee(String name, double salary) {
      this.name = name;
      this.salary = salary;
    }

    public void raiseSalary(double byPercent) {
      double raise = salary * byPercent / 100;
      salary += raise;
    }

    public final String getName() {
      return name;
    }

    public double getSalary() {
      return salary;
    }

    @Override
    public String toString() {
      return universalToString(this, true, true);
    }
  }

  public static class Manager extends Employee {
    private double bonus;

    public Manager(String name, double salary) {
      super(name, salary);
      bonus = 0;
    }

    public Manager(String name, double salary, double bonus) {
      super(name, salary);
      this.bonus = bonus;
    }

    public void setBonus(double bonus) {
      this.bonus = bonus;
    }

    public double getSalary() { // Overrides superclass method
      return super.getSalary() + bonus;
    }

    @Override
    public String toString() {
      return universalToString(this, true, true);
    }
  }

  public static class ArrayListSubClass extends ArrayList<Object> {
    private static final long serialVersionUID = 1L;
    int alscid = 9;

    public ArrayListSubClass() {
      super();
    }

    public ArrayListSubClass(Collection<? extends Object> c) {
      super(c);
    }

    @Override
    public String toString() {
      return universalToString(this, false, true);
    }
  }

  public enum PaperSize {
    ISO_4A0("ISO 216 4A0", "mm", 1632, 2378), ISO_2A0("ISO 216 2A0", "mm", 1189, 1682), ISO_A0("ISO 216 A0", "mm", 841,
        1189);
    final String info;
    final String unit;
    final double width;
    final double height;

    PaperSize(String info, String unit, double width, double height) {
      this.info = info;
      this.unit = unit;
      this.width = width;
      this.height = height;
    }
  }

  public static void main(String[] args) {

    Employee e1 = new Employee("Joe", 81000.09);
    Employee e2 = new Employee("Ivy", 91000.02);
    Employee e3 = new Employee("May", 81000.09);
    Manager m1 = new Manager("Jane", 101000.10);
    Manager m2 = new Manager("Zack", 115000.50);
    Manager m3 = new Manager("Elise", 124998.50);
    m1.setBonus(23500.67);
    m2.setBonus(59430.00);
    m3.setBonus(36100.19);
    Z z = new Z();
    A a1 = new A();
    B b1 = new B(a1);
    a1.b = b1;
    C c1 = new C(a1, b1);
    A2 a2 = new A2();
    B2 b2 = new B2(a2);
    a2.x = b2;
    C2 c2 = new C2(a2, b2);
    D d1 = new D(a1, b1, c1);
    D2 d2 = new D2(a2, b2, c2);
    Atchim atchim1 = new Atchim();
    Blygen blygen1 = new Blygen(atchim1);
    atchim1.blygen = blygen1;
    Cancrán cancrán1 = new Cancrán(atchim1, blygen1);
    Dumpe dumpe1 = new Dumpe(atchim1, blygen1, cancrán1);
    F f = new F();
    G g = new G();
    H h = new H();
    f.g = g;
    g.h = h;
    h.f = f;
    List<Object> arrayListObjects = new ArrayList<>(Arrays.asList(f, g, h));
    ArrayListSubClass arrayListSubClassObjects = new ArrayListSubClass(Arrays.asList(f, g, h));
    Set<Integer> setIntegers = new HashSet<>(Arrays.asList(1, 2, 3));
    Set<Object> setObjects = new LinkedHashSet<>(Arrays.asList(a1, b1, c1, d1));
    PaperSize ps4a0Enum = PaperSize.ISO_4A0;
    PaperSize ps2a0Enum = PaperSize.ISO_2A0;
    PaperSize psa0Enum = PaperSize.ISO_A0;
    PaperSize[] psa = new PaperSize[] { ps4a0Enum, ps2a0Enum, psa0Enum };
    List<PaperSize> psl = new ArrayList<>(Arrays.asList(ps4a0Enum, ps2a0Enum, psa0Enum));
    Map<String, PaperSize> psm = new LinkedHashMap<>();
    psm.put("ps4a0", ps4a0Enum);
    psm.put("ps2a0", ps2a0Enum);
    psm.put("psa0", psa0Enum);
    Map<Integer, String> map1 = new HashMap<>();
    map1.put(1, "one");
    map1.put(2, "two");
    map1.put(3, "three");
    LinkedHashMap<Character, Object> map2 = new LinkedHashMap<>();
    map2.put('x', a1);
    map2.put('y', b1);
    map2.put('z', c1);
    String[] rs = new String[] { "VGQ7GN2QcQ7e09e5D43joG3Heed", "s4s4l1cr9abNBhJhkCm6f84r9d7",
        "CDVV3Jl5sCaDTBe071KGtM6S65G", "D760cu2GpAQbaSKKN1mGlTcF8F1", "Ld856j49EANHi0891EK2QQsi19V",
        "Ja84eo0t1RKjKT18H84heJD3P6j", "mg16dL2mb3Pcqg4jK7R9fnvV0p3", "2QKvEcR3FjLDFsBfl3hrihaIpFl",
        "0ad6Dn2Ar11J1AMvVhMGi7rMQ49", "Kf9Q1f60SD19p3FpcKtH4bnCb7R", "Oo3iI26SvFoHjakbup18nTT7Ue2",
        "GL8uBr5veMMQaQl0c0EdFHP45U4", "9695MBGcDOqR0Nc4EvJcoCVE9o9", "sC4P3ne8rU5jI9qA5JNiVVumG1N",
        "2nbeATbbrPPC00L00GjfVtRIE1R" };
    List<String> l1 = new ArrayList<>(Arrays.asList(rs[0], rs[1], rs[2], rs[3], rs[4]));
    List<String> l2 = new ArrayList<>(Arrays.asList(rs[5], rs[6], rs[7], rs[8], rs[9]));
    List<String> l3 = new ArrayList<>(Arrays.asList(rs[10], rs[11], rs[12], rs[13], rs[14]));
    Map<List<String>, PaperSize> map3 = new HashMap<>();
    map3.put(l1, psa0Enum);
    map3.put(l2, ps2a0Enum);
    map3.put(l3, ps4a0Enum);
    Map<PaperSize, List<String>> map4 = new HashMap<>();
    map4.put(psa0Enum, l1);
    map4.put(ps2a0Enum, l2);
    map4.put(ps4a0Enum, l3);
    PageAttributes pa1 = new PageAttributes();

    System.out.println("universalToString(new Object(), true, true):");
    System.out.println("// An object obj of class Object is represented by obj.toString()");
    System.out.println(universalToString(new Object(), true, true) + "\n");
    //  java.lang.Object@3d4eac69

    System.out.println("universalToString(null, true, true):");
    System.out.println("// null is represented as the string \"null\" without quotes.");
    System.out.println(universalToString(null, true, true) + "\n");
    //  null    

    System.out.println("universalToString(\"hello\", true, true):");
    System.out.println("// Representations of all other Strings are double quoted.");
    System.out.println(universalToString("hello", true, true) + "\n");
    //  "hello"

    System.out.println("universalToString('a', true, true):");
    System.out.println("// Representations of chars and Characters are single quoted.");
    System.out.println(universalToString('a', true, true) + "\n");
    //  'a'

    System.out.println("universalToString(1, true, true):");
    System.out.println("// The representation of any number is just its value.");
    System.out.println(universalToString(1, true, true) + "\n");
    //  1

    System.out.println("universalToString(new Integer(5), true, true):");
    System.out.println(universalToString(new Integer(5), true, true) + "\n");
    //  5

    System.out.println("universalToString(new Double(3.14159), true, true):");
    System.out.println(universalToString(new Double(3.14159), true, true) + "\n");
    //  3.14159

    System.out.println("universalToString(0xa31.0p-3F, true, true):");
    System.out.println("// This demonstrates a floating point literal in hexadecimal.");
    System.out.println(universalToString(0xa31.0p-3F, true, true) + "\n");
    //  326.125

    System.out.println("universalToString(new Float(1.23456792E8F), true, true):");
    System.out.println(universalToString(new Float(1.23456792E8F), true, true) + "\n");
    //  1.23456792E8

    System.out.println("universalToString(new int[0], true, true):");
    System.out.println("// The representation of an array with class name className has the"
        + "\n// format className[e0, e1, e2,...] where e0, e1, e2... are the" + "\n// representations of its elements");
    System.out.println(universalToString(new int[0], true, true) + "\n");
    //  int[]

    System.out.println("universalToString(new int[] { 1 }, false, false):");
    System.out.println(universalToString(new int[] { 1 }, false, false) + "\n");
    //  int[1]

    System.out.println("universalToString(new int[] { 1, 2, 3 }, true, false):");
    System.out.println("// The representation of an array, collection or map is formatted on"
        + "\n// a single line when its total length <= maxLength");
    System.out.println(universalToString(new int[] { 1, 2, 3 }, true, false) + "\n");
    //  int[1,2,3]  

    System.out.println("universalToString(new int[] { \n"
        + "        1000000000, 1100000000, 1110000000, 1111000000, 1111100000, 1111110000, \n"
        + "        1111111000, 1111111100, 1111111110, 1111111111}, false, true):");
    System.out.println("// The representation of an array, collection or map is formatted on"
        + "\n// multiple lines when its total length > maxLength");
    System.out.println(universalToString(new int[] { 1000000000, 1100000000, 1110000000, 1111000000, 1111100000,
        1111110000, 1111111000, 1111111100, 1111111110, 1111111111 }, true, true) + "\n");
    //  int[1000000000,
    //      1100000000,
    //      1110000000,
    //      1111000000,
    //      1111100000,
    //      1111110000,
    //      1111111000,
    //      1111111100,
    //      1111111110,
    //      1111111111]

    System.out.println("universalToString(new long[] { 1L, 2L, 3L }, true, true):");
    System.out.println(universalToString(new long[] { 1L, 2L, 3L }, true, true) + "\n");
    //  long[1,2,3]

    System.out.println("universalToString(new double[] { 1., 2., 3. }, true, true):");
    System.out.println(universalToString(new double[] { 1., 2., 3. }, true, true) + "\n");
    //  double[1.0,2.0,3.0]

    System.out.println("universalToString(new byte[] { 1, 2, 3 }, true, true):");
    System.out.println(universalToString(new byte[] { 1, 2, 3 }, true, true) + "\n");
    //  byte[1,2,3]

    System.out.println("universalToString(new char[] { 'a', 'b', 'c' }, true, true):");
    System.out.println(universalToString(new char[] { 'a', 'b', 'c' }, true, true) + "\n");
    //  char['a','b','c']

    System.out.println("universalToString(new boolean[] { true, false, true }, true, true):");
    System.out.println(universalToString(new boolean[] { true, false, true }, true, true) + "\n");
    //  boolean[true,false,true]

    System.out.println("universalToString(new float[] { 1.e0f, 2.e1f, 3.e2f }, true, true):");
    System.out.println(universalToString(new float[] { 1.e0f, 2.e1f, 3.e2f }, true, true) + "\n");
    //  float[1.0,20.0,300.0]

    System.out.println("universalToString(new short[] { 1, 2, 3 }, true, true):");
    System.out.println(universalToString(new short[] { 1, 2, 3 }, true, true) + "\n");
    //  short[1,2,3]

    System.out.println("universalToString(new Character[] { 'a', 'b', 'c' }, true, true):");
    System.out.println(universalToString(new Character[] { 'a', 'b', 'c' }, true, true) + "\n");
    //  Character['a','b','c']

    System.out.println("universalToString(new String[] { \"one\", \"two\", \"three\" }, true, true):");
    System.out.println(universalToString(new String[] { "one", "two", "three" }, true, true) + "\n");
    //  String["one","two","three"]

    System.out
        .println("universalToString(new Integer[] { new Integer(1), new Integer(2), new Integer(3) }, true, true):");
    System.out.println(
        universalToString(new Integer[] { new Integer(1), new Integer(2), new Integer(3) }, true, true) + "\n");
    //  Integer[1,2,3]   

    System.out.println("universalToString(e1, true, true):");
    System.out.println(universalToString(e1, true, true) + "\n");
    //  Employee(name="Joe",salary=81000.09)

    System.out.println("universalToString(e1, true, false):");
    System.out.println(universalToString(e1, true, false) + "\n");
    //  Employee(
    //      name="Joe",
    //      salary=81000.09)

    System.out.println("universalToString(m1, true, true):");
    System.out.println(universalToString(m1, true, true) + "\n");
    //  Manager(name="Jane",salary=101000.1)(bonus=23500.67)

    System.out.println("universalToString(m1, true, false):");
    System.out.println(universalToString(m1, true, false) + "\n");
    //  Manager(
    //      name="Jane",
    //      salary=101000.1)
    //      (bonus=23500.67)

    System.out.println("universalToString(new Employee[] { e1, e2, e3 }, true, true):");
    System.out.println("// Auto-formatting on multiple lines");
    System.out.println(universalToString(new Employee[] { e1, e2, e3 }, true, true) + "\n");
    //  Employee[
    //    Employee(name="Joe",salary=81000.09),
    //    Employee(name="Ivy",salary=91000.02),
    //    Employee(name="May",salary=81000.09)]

    System.out.println("universalToString(new ArrayList<Employee>(Arrays.asList(e1, e2, e3)), true, true):");
    System.out.println(universalToString(new ArrayList<Employee>(Arrays.asList(e1, e2, e3)), true, true) + "\n");
    //  ArrayList<Employee>(
    //      Employee(name="Joe",salary=81000.09),
    //      Employee(name="Ivy",salary=91000.02),
    //      Employee(name="May",salary=81000.09))

    System.out.println("universalToString(new Manager[] { m1, m2, m3 }, true, true):");
    System.out.println("// Manager extends Employee so Employee fields are printed within"
        + "\n// first set of parentheses and Manager fields in the second");
    System.out.println(universalToString(new Manager[] { m1, m2, m3 }, true, true) + "\n");
    //  Manager[
    //          Manager(name="Jane",salary=101000.1)(bonus=23500.67),
    //          Manager(name="Zack",salary=115000.5)(bonus=59430.0),
    //          Manager(name="Elise",salary=124998.5)(bonus=36100.19)]   
    
    System.out.println("universalToString(z, true, true):");
    System.out.println("// This ia boundary test of handling circular references for a class which"
        + "\n// contains itself as the value of its only field.");
    System.out.println(universalToString(z, true, true) + "\n");

    System.out.println("universalToString(a1, true, true):");
    System.out.println("// This shows field \"condensation\": after defining b, its next occurrence is just"
        + "\n// its name, which bounds the representation of circular references in a finite span."
        + "\n// aid is similarly condensed although its not a circular reference.");
    System.out.println(universalToString(a1, true, true) + "\n");
    //  A(b=B(a=A(b,aid=1),bid=3),aid)    

    System.out.println("universalToString(atchim1, true, false):");
    System.out.println(
        "// This must be run with oneLine false to be formatted on multiple lines" + "\n// given maxLength==100");
    System.out.println(universalToString(atchim1, true, false) + "\n");
    //  Atchim(
    //    blygen=Blygen(
    //      atchim=Atchim(
    //        blygen,
    //        atchimId=1),
    //      blygenId=3),
    //    atchimId)

    System.out.println("universalToString(dumpe1, true, true):");
    System.out
        .println("// This is auto-formatted on multiple lines when oneLine is true" + "\n// provided maxLength < 125");
    System.out.println(universalToString(dumpe1, true, true) + "\n");
    //  Dumpe(
    //    atchim=Atchim(
    //      blygen=Blygen(
    //        atchim,
    //        blygenId=3),
    //      atchimId=1),
    //    blygen,
    //    cancrán=Cancrán(
    //      atchim,
    //      blygen,
    //      cancránId=5),
    //    dumpeId=7)

    System.out.println("universalToString(new Object[] { a1, b1, c1, d1 }, true, true):");
    System.out.println("// The representation of each element in arrays, collections and maps is"
        + "\n// separately computed so there's no parameter condensation between them."
        + "\n// The point of this is to avoid confusion between the representation of an"
        + "\n// object by itself and as an element. This consideration does not apply to"
        + "\n// objects contained within other objects as the values of fields and which"
        + "\n// may be entire arrays, collections or maps.");
    System.out.println(universalToString(new Object[] { a1, b1, c1, d1 }, true, true) + "\n");
    //  Object[
    //    A(b=B(a=A(b,aid=1),bid=3),aid),
    //    B(a=A(b=B(a,bid=3),aid=1),bid),
    //    C(a=A(b=B(a,bid=3),aid=1),b,cid=5),
    //    D(a=A(b=B(a,bid=3),aid=1),b,c=C(a,b,cid=5),did=7)]

    System.out.println("universalToString(d2, true, false):");
    System.out.println("// A field may be renamed to prevent confusion with that of another field having the"
        + "\n// same name in an enclosing context. For example, A2's x field was renamed to"
        + "\n// x@4b1210ee, since D2 got first dibs on x as a field name and A2's x field occurs"
        + "\n// in the expansion of D2's x field. If this renaming wasn't done, then the meaning"
        + "\n// of A2.y=x would be ambiguous.");
    System.out.println(universalToString(d2, true, false) + "\n");
    //  D2(
    //    x=A2(
    //      x@4b1210ee=B2(
    //        y=x,
    //        b2id=4),
    //      a2id=2),
    //    y=x@4b1210ee,
    //    z=C2(
    //      x,
    //      y=x@4b1210ee,
    //      c2id=6),
    //    d2id=8)

    System.out.println("universalToString(f, true, true):");
    System.out.println(universalToString(f, true, true) + "\n");
    //  F(g=G(h=H(f=F(g,fid=9),hid=11),gid=10),fid)

    System.out.println("universalToString(g, true, true):");
    System.out.println(universalToString(g, true, true) + "\n");
    //  G(h=H(f=F(g=G(h,gid=10),fid=9),hid=11),gid)

    System.out.println("universalToString(h, true, true):");
    System.out.println(universalToString(h, true, true) + "\n");
    //  H(f=F(g=G(h=H(f,hid=11),gid=10),fid=9),hid)

    System.out.println("universalToString(new Object[]{f, g, h}, true, false):");
    System.out.println("// Array types are guessed by congruence of element types or" + "\n// are resolved to Object");
    System.out.println(universalToString(new Object[] { f, g, h }, true, false) + "\n");
    //  Object[
    //    F(g=G(h=H(f=F(g,fid=9),hid=11),gid=10),fid),
    //    G(h=H(f=F(g=G(h,gid=10),fid=9),hid=11),gid),
    //    H(f=F(g=G(h=H(f,hid=11),gid=10),fid=9),hid)]

    System.out.println("universalToString(arrayListObjects, true, false):");
    System.out
        .println("// Generic types are guessed by congruence of element types or" + "\n// are resolved to Object");
    System.out.println(universalToString(arrayListObjects, true, false) + "\n");
    //  ArrayList<Object>(
    //    F(g=G(h=H(f=F(g,fid=9),hid=11),gid=10),fid),
    //    G(h=H(f=F(g=G(h,gid=10),fid=9),hid=11),gid),
    //    H(f=F(g=G(h=H(f,hid=11),gid=10),fid=9),hid))

    System.out.println("universalToString(arrayListSubClassObjects, true, false):");
    System.out.println(universalToString(arrayListSubClassObjects, true, false) + "\n");
    //  ArrayListSubClass<Object>(
    //    F(g=G(h=H(f=F(g,fid=9),hid=11),gid=10),fid),
    //    G(h=H(f=F(g=G(h,gid=10),fid=9),hid=11),gid),
    //    H(f=F(g=G(h=H(f,hid=11),gid=10),fid=9),hid))

    System.out.println("arrayListSubClassObjects.toString():");
    System.out.println("// This demonstrates multiline format with simpleName false and"
        + "\n// use of universalToString in a class's toString method.");
    System.out.println(arrayListSubClassObjects.toString() + "\n");
    //  tests.UniversalToString$ArrayListSubClass<java.lang.Object>(
    //    tests.UniversalToString$F(
    //      g=tests.UniversalToString$G(
    //        h=tests.UniversalToString$H(
    //          f=tests.UniversalToString$F(
    //            g,
    //            fid=9),
    //          hid=11),
    //        gid=10),
    //      fid),
    //    tests.UniversalToString$G(
    //      h=tests.UniversalToString$H(
    //        f=tests.UniversalToString$F(
    //          g=tests.UniversalToString$G(
    //            h,
    //            gid=10),
    //          fid=9),
    //        hid=11),
    //      gid),
    //    tests.UniversalToString$H(
    //      f=tests.UniversalToString$F(
    //        g=tests.UniversalToString$G(
    //          h=tests.UniversalToString$H(
    //            f,
    //            hid=11),
    //          gid=10),
    //        fid=9),
    //      hid))

    System.out.println("universalToString(setIntegers, true, false):");
    System.out.println("// Another example of auto-formatting on one line, this time for" + "\n// a Collection");
    System.out.println(universalToString(setIntegers, true, false) + "\n");
    //  HashSet<Integer>(1,2,3)

    System.out.println("universalToString(setObjects, true, false):");
    System.out.println(universalToString(setObjects, true, false) + "\n");
    //  LinkedHashSet<Object>(
    //    A(b=B(a=A(b,aid=1),bid=3),aid),
    //    B(a=A(b=B(a,bid=3),aid=1),bid),
    //    C(a=A(b=B(a,bid=3),aid=1),b,cid=5),
    //    D(a=A(b=B(a,bid=3),aid=1),b,c=C(a,b,cid=5),did=7))

    System.out.println("universalToString(ps4a0Enum, true, true):");
    System.out.println("// Another example of auto-formatting on multiple lines, this time for an"
        + "\n// Enum. Multiline formatting of arrays, Collections and Maps attemps to"
        + "\n// to fit each element or key-value pair on a single line if possible."
        + "\n// That's why the values array is printed on one line.");
    System.out.println(universalToString(ps4a0Enum, true, true) + "\n");
    //  PaperSize(
    //    name="ISO_4A0",
    //    ordinal=0)
    //    (info="ISO 216 4A0",
    //     unit="mm",
    //     width=1632.0,
    //     height=2378.0,
    //     values=PaperSize[ISO_4A0,ISO_2A0,ISO_A0])

    System.out.println("universalToString(map1, true, true):");
    System.out.println("// Key-value pairs of a Map are separated with a colon.");
    System.out.println(universalToString(map1, true, true) + "\n");
    //  HashMap<Integer,String>{1:"one",2:"two",3:"three"}

    System.out.println("universalToString(map2, true, false):");
    System.out.println(universalToString(map2, true, false) + "\n");
    //  LinkedHashMap<Character,Object>{
    //    'x':A(b=B(a=A(b,aid=1),bid=3),aid),
    //    'y':B(a=A(b=B(a,bid=3),aid=1),bid),
    //    'z':C(a=A(b=B(a,bid=3),aid=1),b,cid=5)}

    System.out.println("universalToString(psa, true, false):");
    System.out.println("// An example of an array of Enums.");
    System.out.println(universalToString(psa, true, false) + "\n");
    //  PaperSize[
    //    PaperSize(
    //      name="ISO_4A0",
    //      ordinal=0)
    //      (info="ISO 216 4A0",
    //       unit="mm",
    //       width=1632.0,
    //       height=2378.0,
    //       values=PaperSize[ISO_4A0,ISO_2A0,ISO_A0]),
    //    PaperSize(
    //      name="ISO_2A0",
    //      ordinal=1)
    //      (info="ISO 216 2A0",
    //       unit="mm",
    //       width=1189.0,
    //       height=1682.0,
    //       values=PaperSize[ISO_4A0,ISO_2A0,ISO_A0]),
    //    PaperSize(
    //      name="ISO_A0",
    //      ordinal=2)
    //      (info="ISO 216 A0",
    //       unit="mm",
    //       width=841.0,
    //       height=1189.0,
    //       values=PaperSize[ISO_4A0,ISO_2A0,ISO_A0])]

    System.out.println("universalToString(psl, true, false):");
    System.out.println("// A collection of Enums.");
    System.out.println(universalToString(psl, true, false) + "\n");
    //  ArrayList<PaperSize>(
    //    PaperSize(
    //      name="ISO_4A0",
    //      ordinal=0)
    //      (info="ISO 216 4A0",
    //       unit="mm",
    //       width=1632.0,
    //       height=2378.0,
    //       values=PaperSize[ISO_4A0,ISO_2A0,ISO_A0]),
    //    PaperSize(
    //      name="ISO_2A0",
    //      ordinal=1)
    //      (info="ISO 216 2A0",
    //       unit="mm",
    //       width=1189.0,
    //       height=1682.0,
    //       values=PaperSize[ISO_4A0,ISO_2A0,ISO_A0]),
    //    PaperSize(
    //      name="ISO_A0",
    //      ordinal=2)
    //      (info="ISO 216 A0",
    //       unit="mm",
    //       width=841.0,
    //       height=1189.0,
    //       values=PaperSize[ISO_4A0,ISO_2A0,ISO_A0]))        

    maxLength = 100;
    System.out.println("universalToString(psm, true, true):");
    System.out.println("// With maxLength==100 this shows auto-formatting on multiple lines but with"
        + "\n// each field on a single line");
    System.out.println(universalToString(psm, true, true) + "\n");
    //  LinkedHashMap<String,PaperSize>{
    //    "ps4a0":PaperSize(
    //              name="ISO_4A0",
    //              ordinal=0)
    //              (info="ISO 216 4A0",
    //               unit="mm",
    //               width=1632.0,
    //               height=2378.0,
    //               values=PaperSize[ISO_4A0,ISO_2A0,ISO_A0]),
    //    "ps2a0":PaperSize(
    //              name="ISO_2A0",
    //              ordinal=1)
    //              (info="ISO 216 2A0",
    //               unit="mm",
    //               width=1189.0,
    //               height=1682.0,
    //               values=PaperSize[ISO_4A0,ISO_2A0,ISO_A0]),
    //     "psa0":PaperSize(
    //              name="ISO_A0",
    //              ordinal=2)
    //              (info="ISO 216 A0",
    //               unit="mm",
    //               width=841.0,
    //               height=1189.0,
    //               values=PaperSize[ISO_4A0,ISO_2A0,ISO_A0])}

    maxLength = 30;
    System.out.println("universalToString(psm, true, true):");
    System.out.println("// With maxLength==30 the values array fields have also been multilined,"
        + "\n// however some line lengths still exceed 30 since they aren't suitable for compression");
    System.out.println(universalToString(psm, true, true) + "\n");
    //  LinkedHashMap<String,PaperSize>{
    //    "ps4a0" : PaperSize(
    //                name="ISO_4A0",
    //                ordinal=0)
    //                (info="ISO 216 4A0",
    //                 unit="mm",
    //                 width=1632.0,
    //                 height=2378.0,
    //                 values=PaperSize[
    //                   ISO_4A0,
    //                   ISO_2A0,
    //                   ISO_A0]),
    //    "ps2a0" : PaperSize(
    //                name="ISO_2A0",
    //                ordinal=1)
    //                (info="ISO 216 2A0",
    //                 unit="mm",
    //                 width=1189.0,
    //                 height=1682.0,
    //                 values=PaperSize[
    //                   ISO_4A0,
    //                   ISO_2A0,
    //                   ISO_A0]),
    //    "psa0"  : PaperSize(
    //                name="ISO_A0",
    //                ordinal=2)
    //                (info="ISO 216 A0",
    //                 unit="mm",
    //                 width=841.0,
    //                 height=1189.0,
    //                 values=PaperSize[
    //                   ISO_4A0,
    //                   ISO_2A0,
    //                   ISO_A0])}

    maxLength = 100;
    System.out.println("universalToString(map3, true, true):");
    System.out.println("// A more complex map example with Collection keys and Enum values."
        + "\n// The point is to demonstrate alignment when both have multiple lines.");
    System.out.println(universalToString(map3, true, true) + "\n");
    //  HashMap<ArrayList<String>,PaperSize>{
    //    ArrayList<String>(               : PaperSize(
    //      "Ja84eo0t1RKjKT18H84heJD3P6j",     name="ISO_2A0",
    //      "mg16dL2mb3Pcqg4jK7R9fnvV0p3",     ordinal=1)
    //      "2QKvEcR3FjLDFsBfl3hrihaIpFl",     (info="ISO 216 2A0",
    //      "0ad6Dn2Ar11J1AMvVhMGi7rMQ49",      unit="mm",
    //      "Kf9Q1f60SD19p3FpcKtH4bnCb7R")      width=1189.0,
    //                                          height=1682.0,
    //                                          values=PaperSize[ISO_4A0,ISO_2A0,ISO_A0]),
    //    ArrayList<String>(               : PaperSize(
    //      "VGQ7GN2QcQ7e09e5D43joG3Heed",     name="ISO_A0",
    //      "s4s4l1cr9abNBhJhkCm6f84r9d7",     ordinal=2)
    //      "CDVV3Jl5sCaDTBe071KGtM6S65G",     (info="ISO 216 A0",
    //      "D760cu2GpAQbaSKKN1mGlTcF8F1",      unit="mm",
    //      "Ld856j49EANHi0891EK2QQsi19V")      width=841.0,
    //                                          height=1189.0,
    //                                          values=PaperSize[ISO_4A0,ISO_2A0,ISO_A0]),
    //    ArrayList<String>(               : PaperSize(
    //      "Oo3iI26SvFoHjakbup18nTT7Ue2",     name="ISO_4A0",
    //      "GL8uBr5veMMQaQl0c0EdFHP45U4",     ordinal=0)
    //      "9695MBGcDOqR0Nc4EvJcoCVE9o9",     (info="ISO 216 4A0",
    //      "sC4P3ne8rU5jI9qA5JNiVVumG1N",      unit="mm",
    //      "2nbeATbbrPPC00L00GjfVtRIE1R")      width=1632.0,
    //                                          height=2378.0,
    //                                          values=PaperSize[ISO_4A0,ISO_2A0,ISO_A0])}

    System.out.println("universalToString(map4, true, true):");
    System.out.println("// Reversal of the previous map this time with Enum keys and Collection values.");
    System.out.println(universalToString(map4, true, true) + "\n");
    //  HashMap<PaperSize,ArrayList<String>>{
    //    PaperSize(                                   : ArrayList<String>(
    //      name="ISO_4A0",                                "Oo3iI26SvFoHjakbup18nTT7Ue2",
    //      ordinal=0)                                     "GL8uBr5veMMQaQl0c0EdFHP45U4",
    //      (info="ISO 216 4A0",                           "9695MBGcDOqR0Nc4EvJcoCVE9o9",
    //       unit="mm",                                    "sC4P3ne8rU5jI9qA5JNiVVumG1N",
    //       width=1632.0,                                 "2nbeATbbrPPC00L00GjfVtRIE1R")
    //       height=2378.0,                              
    //       values=PaperSize[ISO_4A0,ISO_2A0,ISO_A0])   ,
    //    PaperSize(                                   : ArrayList<String>(
    //      name="ISO_2A0",                                "Ja84eo0t1RKjKT18H84heJD3P6j",
    //      ordinal=1)                                     "mg16dL2mb3Pcqg4jK7R9fnvV0p3",
    //      (info="ISO 216 2A0",                           "2QKvEcR3FjLDFsBfl3hrihaIpFl",
    //       unit="mm",                                    "0ad6Dn2Ar11J1AMvVhMGi7rMQ49",
    //       width=1189.0,                                 "Kf9Q1f60SD19p3FpcKtH4bnCb7R")
    //       height=1682.0,                              
    //       values=PaperSize[ISO_4A0,ISO_2A0,ISO_A0])   ,
    //    PaperSize(                                   : ArrayList<String>(
    //      name="ISO_A0",                                 "VGQ7GN2QcQ7e09e5D43joG3Heed",
    //      ordinal=2)                                     "s4s4l1cr9abNBhJhkCm6f84r9d7",
    //      (info="ISO 216 A0",                            "CDVV3Jl5sCaDTBe071KGtM6S65G",
    //       unit="mm",                                    "D760cu2GpAQbaSKKN1mGlTcF8F1",
    //       width=841.0,                                  "Ld856j49EANHi0891EK2QQsi19V")
    //       height=1189.0,                              
    //       values=PaperSize[ISO_4A0,ISO_2A0,ISO_A0])   }

    System.out.println("universalToString(pa1, true, true):");
    System.out.println("// This provides content identical to (new java.awt.PageAttributes()).toString(),"
        + "\n// but not using that method and with a PageAttributes prefix and auto-formatting"
        + "\n// on multiple lines.");
    System.out.println(universalToString(pa1, true, true) + "\n");
    //  PageAttributes(
    //    color=monochrome,
    //    media=na-letter,
    //    orientationRequested=portrait,
    //    origin=physical,
    //    printQuality=normal,
    //    printerResolution=int[72,72,3])
    //    
    System.out.println("// Output of (new java.awt.PageAttributes()).toString() for comparison.");
    System.out.println(pa1.toString());
    //  color=monochrome,media=na-letter,orientation-requested=portrait,origin=physical,print-quality=normal,printer-resolution=[72,72,3]

  }

}
