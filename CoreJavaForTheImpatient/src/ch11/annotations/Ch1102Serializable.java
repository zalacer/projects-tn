package ch11.annotations;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.zip.GZIPInputStream.GZIP_MAGIC;
import static utils.ModifierUtil.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.SynchronousQueue;
import java.util.jar.Attributes;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.xml.bind.DatatypeConverter;

//  ch11.annotations
//  2. If annotations had existed in early versions of Java, then the Serializable
//  interface would surely have been an annotation. Implement a @Serializable
//  annotation. Choose a text or binary format for persistence. Provide classes for
//  streams or readers/writers that persist the state of objects by saving and restoring all
//  fields that are primitive values or themselves serializable. Don’t worry about cyclic
//  references for now.

//  This class contains a simple Java serialization system using only Oracle Java libraries.
//  It does all serialization to strings for convenience and provides utilities for writing
//  and reading a string to and from a file with and without compression.  
//  
//  A serialization string generally consists of a readable part with basic information about 
//  what is serialized in the following data part which is a byte array in hex string format. 
//  Formats of these serialization strings vary by which serializer created them and there are 
//  separate serializers for primitive and boxed types, strings, enums, arrays, collections, 
//  maps, Attributes, BitSets and another for all other types. There is a  master serializer 
//  named serialize or ser which calls all the rest, which in turn can call it or any other 
//  the other serializer as needed. For each serializer there is a corresponding deserializer.
//  
//  Advantages of serializing to strings are that facilitates testing and enables building 
//  a heierarchy which helps to reduce code, since a string can include another string. An 
//  example of this is that if a class contains a field with an array value, that value is 
//  captured as a serialized string which is then included in the data part of the overall
//  serialization string for the class. Similarly, collections are basically serialized as 
//  arrays and maps are serialized as pairs of arrays.
//  
//  Deserialization works by reading the serialized class name from a serialization string, 
//  instantiating it, possibly adding fields or components and returning the instance. As part 
//  of this process, the data component is converted back to a byte array with which a 
//  DataInputStream over a ByteArrayInputStream is instantiated. Tracking of fields is done by
//  saving their names and types to a list during serialization in exactly the same order as 
//  their values are written to a DataOutputStream, and including that list in serialization 
//  strings for reversing this process during deserialization. Type information is used for
//  for object instantiation and verification of the current types of fields. If a field is 
//  missing or its type has changed, deserialization of it is skipped and a message regarding 
//  that is printed. 
//  
//  Accomodating classes with constructors that take arguments is straightforward. For example, 
//  most collections can be constructed from another collection such as list, which can be 
//  generated from an array, which is precisely the data that is serialized for them.
//  
//  Something I learned from this exercise is that using an interface or annotation to
//  designate serializability is just a matter of communication and may be a false advertisement. 
//  Several public methods have been implemented to actually determine  serializatibility 
//  including isSerializable(), isSerializableArray(), isSerializableClass(), 
//  isSerializableCollection() and isSerializableMap(). These methods return true for classes
//  are annotated with @Serializable and they are used to determine serializability automatically
//  prior to serialization.

// Here are tables of the serialization string and list formats which are written by the
// serializers and read by the deserializers:

//  What is returned as a string by the serializers:
//  ===========================================================================================
//  serializer                  format of returned string
//  ==========================  ===============================================================
//  serialize:                  typeName+"^"+hexString+"^"+list.get(0)+"^"+list.get(1)...
//  serializeNull:              "Null"
//  serializePrimitiveOrBoxed:  typeName+"^"+hexString
//  serializeString:            "java.lang.String^"+hexString
//  serializeArray:             "array^"+componentTypeName+"^"+hexString
//  serializeEnum:              "enum^" + typeName+"^"+enumName
//  serializeCollection:        "collection^"+typeName+"@array^"+componentTypeName+"^"+hexString
//  serializeMap:               "map^"+typeName+"@array^"+componentTypeName+"^"+hexString
//                                             +"@array^"+componentTypeName+"^"+hexString
//  serializeBitSet:            "java.util.BitSet@array^"+byte+"^"+hexString
//  serializeAttributesName:    "java.util.jar.Attributes.Name^" + hexString
//  
//  (all the serializers also return "Null" if their argument is null)

//  What is added by serialize to its list for non null field values:
//  ==================================================================================
//  type of field                   string added to list                  # components
//  ==============================  ====================================  ============
//  Primitive or Boxed:             name+":"+typeName                          2
//  String:                         name+":java.lang.String"                   2
//  Array:                          name+":array:"+componentTypeName"          3
//  Enum:                           name+":enum:"+typeName+":"+enumName        4
//  Collection:                     name+":collection:"+typeName               3
//  Map:                            name+":map:"+typeName"                     3
//  BitSet:                         name+":java.util.BitSet"                   2
//  AttributesName:                 name+":java.util.jar.Attributes.Name"      2
//  All other:                      name+":"+typeName                          2

//  What is added by serialize to its list for null field values:
//  =============================================================
//  field type   string added to list
//  ===========  ========================================
//  Array:       name+":array:+componentTypeName+":Null"
//  Enum:        name+":enum:"+typeName+":Null"
//  Collection:  name+":collection:"+typeName+":Null"
//  Map:         name+":map:"+typeName+":Null"
//  All other:   name+":"+typeName+":Null" 

//  For usage examples see main().

public class Ch1102Serializable {
  
  // useAnnotation's value toggles use of @Serializable for determining
  // serializability as implemented in isSerializable(Object) and
  // isSerializable(Class<?>)
  public static boolean useAnnotation = false;
  
  public static String ser(Object o) {
    return serialize(o);
  }

  public static Object des(String s) {
    return deserialize(s);
  }
  
  public static Object serdes(Object o) {
    return deserialize(serialize(o));
  }
  
  public static String serialize(Object o) {
    if (Objects.isNull(o)) return serializeNull();
    
    Class<?> oclass = o.getClass();
    String oclassName = oclass.getName();
    
    if (!(useAnnotation && hasSerializableAnnotation(oclass) || !useAnnotation))
      throw new IllegalArgumentException("serialize: cannot serialize o");
    
    if (oclass.isArray()) return serializeArray(o);
    if (isString(oclass)) return serializeString((String) o);
    if (isBoxed(oclass)) return serializePrimitiveOrBoxed(o, false);
    if (oclass.isEnum()) return serializeEnum(o);
    if (isSerializableCollection(o)) return serializeCollection(o);
    if (isMap(oclass)) return serializeMap(o, oclassName);    
    if (isBitSet(oclass)) return serializeBitSet((BitSet) o);
    if (isAttributesName(oclass)) return serializeAttributesName((Attributes.Name) o);
    
    if (!isSerializable(o)) 
      throw new IllegalArgumentException("serialize: o is not serializable");
    
    String obstring = "";
    ByteArrayOutputStream bout = new ByteArrayOutputStream(100);
    DataOutputStream dout = new DataOutputStream(bout);
    StringBuilder sb = new StringBuilder();
    List<String> list = new ArrayList<>();
    String name = null;
    Class<?> type = null;
    String typeName = null;
    String actualTypeName = null;
    Class<?> componentType = null;
    String componentTypeName = null;
    Object v = null;
    
    @SuppressWarnings("unchecked")
    List<Field> flist = (List<Field>) getFields(oclass, "ser");
 
    if (flist.size() > 0) {
      for (Field f : flist) {
        if (hasPrivate(f.getModifiers()))
          f.setAccessible(true);
        name = f.getName();
        type = f.getType();
        
        try {
          v = f.get(o);
        } catch (IllegalArgumentException | IllegalAccessException e) {
          System.out.println("serialize: cannot get value for field "+name
              + "- skipping this field"); e.printStackTrace(); continue;
        }
        
        // avoid interface types such as List and 
        // Map while preserving primitive types
        if (Objects.nonNull(v) && !isPrimitive(type)) {
          if (isMap(type)) {
            type =  v.getClass().getSuperclass();
          } else {
            type = v.getClass();
          }
        }
        
        typeName = type.getName();
        
        if (type.isArray()) {
          typeName = "array";
          componentType = type.getComponentType();
          componentTypeName = componentType.getName();
        } else if (isEnum(type)) {
          actualTypeName = typeName;
          typeName = "enum";
        } else if (isCollection(type)) {
          if (Objects.nonNull(v) && isSerializableCollection(v)
              || isSupportedCollection(type)) {
            actualTypeName = typeName;
            typeName = "collection";
          } else {
            System.out.println("serialize:"+oclassName+"."+name+" of type " + typeName
                +" is an unsupported collection " + "and won't be serialized");
            continue;
          }
        } else if (isMap(type)) {
          if (Objects.nonNull(v) && isSerializableMap(v) || isSupportedMap(type)) {
            actualTypeName = typeName;
            typeName = "map";
          } else {
            System.out.println("serialize:"+oclassName+"."+name+" of type" + typeName
                +" is an unsupported map " + "and won't be serialized");
            continue;
          }
        } else if (isBitSet(type)) {
          actualTypeName = typeName;
          typeName = "bitset";
        } else if (isAttributesName(oclass)) {
          actualTypeName = typeName;
          typeName = "attributesname";
        }

        // for null objects only list is updated and no data is written to dout
        if (Objects.isNull(v)) {
          if (typeName.equals("array")) {
            list.add(name + ":array:" + componentTypeName + ":Null");
          } else if (typeName.equals("enum")) {
            list.add(name +":enum:" + actualTypeName + ":Null");
          } else if (typeName.equals("collection")) {
            list.add(name + ":collection:" + actualTypeName + ":Null");
          } else if (typeName.equals("map")) {
            list.add(name + ":map:" + actualTypeName + ":Null");
          } else if (typeName.equals("bitset")) {
            list.add(name + "java.util.BitSet:Null");
          } else if (typeName.equals("attributesname")) {
            list.add(name+"java.util.jar.Attributes.Name:Null");  
          } else {
            list.add(name + ":" + typeName + ":Null");
          }
          continue;
        }
        
        try {
          if (isPrimitiveOrBoxed(typeName)) {
            switch (typeName) {
            case "int":
            case "java.lang.Integer":
              dout.writeInt(((Integer) v).intValue());
              break;
            case "java.lang.Long":
              dout.writeLong(((Long) v).longValue());
              break;
            case "double":
            case "java.lang.Double":
              dout.writeDouble(((Double) v).doubleValue());
              break;
            case "byte":
            case "java.lang.Byte":
              dout.writeByte(((Byte) v).byteValue());
              break;
            case "short":
            case "java.lang.Short":
              dout.writeShort(((Short) v).shortValue());
              break;
            case "float":
            case "java.lang.Float":
              dout.writeFloat(((Float) v).floatValue());
              break;
            case "boolean":
            case "java.lang.Boolean":
              dout.writeBoolean(((Boolean) v).booleanValue());
              break;
            case "char":
            case "java.lang.Character":
              dout.writeChar(((Character) v).charValue());
              break;
            }
            list.add(name + ":" + typeName);
          } else if (isString(typeName)) {
            dout.writeUTF((String) v);
            list.add(name + ":java.lang.String");
          } else if (typeName.equals("array")) {
            dout.writeUTF(serializeArray(v));
            list.add(name+":array:"+componentTypeName);
          } else if (typeName.equals("enum")) {
            @SuppressWarnings("rawtypes")
            String enumName = ((Enum) v).name();
            list.add(name+":enum:"+actualTypeName+":"+enumName);
          } else if (typeName.equals("collection")) {
            dout.writeUTF(serializeCollection(v));
            list.add(name+":collection:"+actualTypeName);
          } else if (typeName.equals("map")) {
            dout.writeUTF(serializeMap(v, actualTypeName));
            list.add(name+":map:"+actualTypeName);
          } else if (typeName.equals("bitset")) {
            dout.writeUTF(serializeBitSet((BitSet) v));
            list.add(name+":java.util.BitSet");
          } else if (typeName.equals("attributesname")) {
            dout.writeUTF(serializeAttributesName((Attributes.Name) v));
            list.add(name+":java.util.jar.Attributes.Name");
          } else {
            dout.writeUTF(serialize(v));
            list.add(name + ":" + typeName);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }

        byte[] oBytes = null;

        try {
          dout.flush();
          bout.flush();
          oBytes = bout.toByteArray();
        } catch (IOException e) {
          e.printStackTrace();
        }

        obstring = toHexString(oBytes);
      }
    }

    sb.append(oclassName + "^" + obstring);
    for (int i = 0; i < list.size(); i++) sb.append("^" + list.get(i));
    String out = sb.toString();

    return out;
  }
  
  public static Object deserialize(String s) {
    if (Objects.isNull(s)) throw new IllegalArgumentException("deserialize: s is null");
    if (s.length() == 0)  throw new IllegalArgumentException("deserialize: s is empty");
    if (s.matches("Null")) return null;

    String[] r = s.split("\\^");

    if (r[0].equals("java.lang.String")) return deserializeString(s);
    if (isPrimitiveOrBoxed(r[0])) return deserializePrimitiveOrBoxed(s);
    if (r[0].equals("array")) return deserializeArray(s);
    if (r[0].equals("enum")) return deserializeEnum(s);
    if (r[0].equals("collection")) return deserializeCollection(s);
    if (r[0].equals("map")) return deserializeMap(s);
    if (r[0].replaceAll("@.*", "").equals("java.util.BitSet")) return deserializeBitSet(s);
    if (r[0].equals("java.util.jar.Attributes.Name")) return deserializeAttributesName(s);
  
    Class<?> c = null;
    try {
      c = Class.forName(r[0]);
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException("deserialize: could not get a class "
          + "object from the first component of r, " + r[0]);
    }

    if (!hasNoArgConstructor(c)) 
      throw new IllegalArgumentException(
          "deserialize: the class " + c.getName() 
          + " serialized in s does not have a no-arg constructor");

    Object o = getInstance(c);
    if (Objects.isNull(o)) 
      throw new IllegalArgumentException("deserialize: the class " + c.getName()
          + " serialized in s cannot be instantiated with a no arg constructor");

    String hexString = "";
    
    if (r.length > 1) {
      hexString = r[1];
    } else return o;

    // build list of serialized fields to regenerate
    List<String> savedFieldInfo = new ArrayList<>();
    for (int i = 2; i < r.length; i++) savedFieldInfo.add(r[i]);

    Object[] w = (Object[]) getFields(c, "des");
    @SuppressWarnings("unchecked")
    LinkedHashMap<String, Field> fmap = (LinkedHashMap<String, Field>) w[0];
    @SuppressWarnings("unchecked")
    LinkedHashMap<String, String> cmap = (LinkedHashMap<String, String>) w[1];
    byte[] data = toByteArray(hexString);
    ByteArrayInputStream bin = new ByteArrayInputStream(data);
    DataInputStream din = new DataInputStream(bin);
 
    String[] d = null;

    for (int i = 0; i < savedFieldInfo.size(); i++) {
      d = savedFieldInfo.get(i).split(":");
      if (d.length < 2) {
        throw new DataFormatException("deserialize: field data with less than 2 components: "
            + "d=" + Arrays.toString(d));
      }

      String fname = d[0];
      String typeName = null;
      String componentTypeName = null;
      String enumName = null;
      Class<?> fType = null;
      String fTypeName = null;
      String fComponentTypeName = null;
      Field f = null;
      boolean isNull = false;
      boolean isPrimitive = false;
      boolean isArray = false;
      boolean isEnum = false;
      boolean isCollection = false;
      boolean isMap = false;
      boolean isBitSet = false;
      boolean isAttributesName = false; 
      boolean isOther = false;
      boolean noCurrentField = false;
      boolean fIsPrimitive = false;
      boolean fIsArray = false;
      boolean skip = false;
      
      if (d[d.length - 1].equals("Null")) {
        isNull = true;
      }

      if (isPrimitive(d[1])) {
        isPrimitive = true;
        if (!(d.length == 2 || d.length == 3)) {
          System.out.println("deserialize: primitive or boxed field "+fname
              + " metaData with " +d.length + " components, should be 2 or 3: " 
              + Arrays.toString(d) + "\nskipping field "+fname);
          skip = true;
        }
        typeName = d[1];
      } else if (isString(d[1])) {
          if (!(d.length == 2|| d.length == 3)) {
            System.out.println("deserialize: string field "+fname
                + " metaData with " +d.length + " components, should be 2 or 3: " 
                + Arrays.toString(d) + "\nskipping field "+fname);
            skip = true;
          }
          typeName = d[1]; 
      } else if (d[1].equals("array")) {
        isArray = true;
        typeName = "array";
        if (!(d.length == 3 || d.length == 4)) {
          System.out.println("deserialize: array field "+fname+" metaData with "
              +d.length + " components, should be 3 or 4: " + Arrays.toString(d)
              + "\nskipping field "+fname);
          skip = true;
        }
        componentTypeName = d[2];
      } else if (d[1].equals("enum")) {
        isEnum = true;
        if (!(d.length == 4 || d.length == 5)) {
          System.out.println("deserialize: enum field "+fname+" metaData" 
                + "with "+d.length+"components, should be 4 or 5: \n  " + Arrays.toString(d)
                + "\nskipping field "+fname);
          skip = true;
        }
        typeName = d[2];
        enumName = d[3];
      } else if (d[1].equals("collection")) {
        isCollection = true;
        if (!(d.length == 3 || d.length == 4)) {
          System.out.println("deserialize: collection field "+fname+" metaData" 
                + "with "+d.length+"components, should be 3 or 4: \n  " + Arrays.toString(d)
                + "\nskipping field "+fname);
          skip = true;
        }
        typeName = d[2];
      } else if (d[1].equals("map")) {
        isMap = true;
        if (!(d.length == 3 || d.length == 4)) {
          System.out.println("deserialize: map field "+fname+" metaData" 
                + "with "+d.length+"components, should be 3 or 4: \n  " + Arrays.toString(d)
                + "\nskipping field "+fname);
          skip = true;
        }
        typeName = d[2];
      } else if (d[1].equals("java.util.BitSet")) {
        isBitSet = true;
        if (!(d.length == 2 || d.length == 3)) {
          System.out.println("deserialize: java.util.BitSet field "+fname+" metaData" 
                + "with "+d.length+"components, should be 2 or 3: \n  " + Arrays.toString(d)
                + "\nskipping field "+fname);
          skip = true;
        }
        typeName = "java.util.BitSet";
      } else if (d[1].equals("java.util.jar.Attributes.Name")) {
        isAttributesName = true;
        if (!(d.length == 2|| d.length == 3)) {
          System.out.println("deserialize: java.util.jar.Attributes.Name field "
              +fname+" metaData with "+d.length+"components, should be 2 or 3: \n  " 
              + Arrays.toString(d) + "\nskipping field "+fname);
          skip = true;
        }
        typeName = "java.util.jar.Attributes.Name";
      } else if (d.length == 2|| d.length == 3) {
        typeName = d[1];
        isOther = true;
      } else if (d.length >  3) {
        System.out.println("deserialize: unrecognized metadata with more than 3 "
            + "components for field " + fname +": \n  " + Arrays.toString(d)
            + "\nskipping field " + fname);
            skip = true;
      }
 
      if (fmap.containsKey(fname)) {
        f = fmap.get(fname);
      } else {
        System.out.println("deserialize: serialized field "+fname
            + " is not currently a field of " + c.getName());
        noCurrentField = true;
        skip = true;
      }
         
      if (!skip) {
        f.setAccessible(true);      
        fType = f.getType();
        fTypeName = f.getType().getName();        
        if (isPrimitive(fType)) {
          fIsPrimitive = true;
        } else if (fType.isArray()) {
          fComponentTypeName = f.getType().getComponentType().getName();
          fIsArray = true;
        }
      }

      // for current field compare serialized metaData with that from scan
      // if there are discrepancies, skip serialization of the field since its
      // containing class may have been altered
            
      if (!skip) {
        if (isArray && fIsArray) {
          if (isPrimitive(componentTypeName) || isPrimitive(fComponentTypeName)) {
            if (! componentTypeName.equals(fComponentTypeName)) {
              System.out.println("deserialize: array field "+fname+" componentType was "
                  + componentTypeName+" during serialization but now its "+fComponentTypeName);
              skip = true;
            }
          } else if (!(isPrimitive(componentTypeName) || isPrimitive(fComponentTypeName))) {
            if (!(componentTypeName.equals(fComponentTypeName) 
                || getInterfaces(componentTypeName).contains(fComponentTypeName))) {
              System.out.println("deserialize: array field "+fname+" componentType was "
                  + componentTypeName+" during serialization but now its "+fComponentTypeName);
              skip = true;
            }
          }
        } else if (isPrimitive(typeName) || isPrimitive(fTypeName))  {
          if (! typeName.equals(fTypeName)) {
            System.out.println("deserialize: field "+fname+" was a "
                + typeName+" during serialization but now its a "+fTypeName);
            skip = true;
          } else if (! (isPrimitive || fIsPrimitive)) {
            if (!(typeName.equals(fTypeName) 
                || getInterfaces(typeName).contains(fTypeName))) {
              System.out.println("deserialize: field "+fname+" was a "
                  + typeName+" during serialization but now its a "+fTypeName);
              skip = true;
            } 
          }
        }
      }

      if (skip) {
        if (noCurrentField) {
          System.out.println("deserialize: skipping setting of field " + fname); 
        } else if (cmap.get(fname).equals(c.getName())) {
          System.out.println("deserialize: skipping setting of field " + fname 
              + " in "+ c + " object");
        } else {
          System.out.println("deserialize: skipping setting of superclass " 
              + cmap.get(fname) + " field " + fname + " in " + c + " object");
        }
      }

      try {
        if (isNull) {
          if (!skip) f.set(o, null);
        } else if (isArray) {
          if (skip) {
            din.readUTF();
          } else {
            String utf = din.readUTF();
            f.set(o, deserializeArray(utf));
          }
        } else if (isEnum) {
          if (!skip) f.set(o, getEnumConstant(typeName, enumName));
        } else if (isCollection) {
          if (skip) {
            din.readUTF();
          } else {
            String utf = din.readUTF();
            f.set(o, deserializeCollection(utf));
          }
        } else if (isMap) {
          if (skip) {
            din.readUTF();
          } else {
            f.set(o, deserializeMap(din.readUTF()));
          }
        } else if (isBitSet) {
          if (skip) {
            din.readUTF();
          } else {
            f.set(o, deserializeBitSet(din.readUTF()));
          }
        } else if (isAttributesName) {
          if (skip) {
            din.readUTF();
          } else {           
            f.set(o, deserializeAttributesName(din.readUTF()));
          }  
        } else if (isOther) {
          if (skip) {
            din.readUTF();
          } else {
            f.set(o, deserialize(din.readUTF()));
          }
        } else {
          switch (typeName) {
            case "int":
              if (skip) {
                din.readInt();
              } else {
                f.setInt(o, din.readInt());
              }
              break;
            case "long":
              if (skip) {
                din.readLong();
              } else {
                f.setLong(o, din.readLong());
              }
              break;
            case "double":
              if (skip) {
                din.readDouble();
              } else {
                f.setDouble(o, din.readDouble());
              }
              break;
            case "byte":
              if (skip) {
                din.readByte();
              } else {
                f.setByte(o, din.readByte());
              }
              break;
            case "short":
              if (skip) {
                din.readShort();
              } else {
                f.setShort(o, din.readShort());
              }
              break;
            case "float":
              if (skip) {
                din.readFloat();
              } else {
                f.setFloat(o, din.readFloat());
              }
              break;
            case "boolean":
              if (skip) {
                din.readBoolean();
              } else {
                f.setBoolean(o, din.readBoolean());
              }
              break;
            case "char":
              if (skip) {
                din.readChar();
              } else {
                f.setChar(o, din.readChar());
              }
              break;
            case "java.lang.Integer":
              if (skip) {
                din.readInt();
              } else {
                f.set(o, new Integer(din.readInt()));
              }
              break;
            case "java.lang.Long":
              if (skip) {
                din.readLong();
              } else {
                f.set(o, new Long(din.readLong()));
              }
              break;
            case "java.lang.Double":
              if (skip) {
                din.readDouble();
              } else {
                f.set(o, new Double(din.readDouble()));
              }
              break;
            case "java.lang.Byte":
              if (skip) {
                din.readByte();
              } else {
                f.set(o, new Byte(din.readByte()));
              }
              break;
            case "java.lang.Short":
              if (skip) {
                din.readShort();
              } else {
                f.set(o, new Short(din.readShort()));
              }
              break;
            case "java.lang.Float":
              if (skip) {
                din.readFloat();
              } else {
                f.set(o, new Float(din.readFloat()));
              }
              break;
            case "java.lang.Boolean":
              if (skip) {
                din.readBoolean();
              } else {
                f.set(o, new Boolean(din.readBoolean()));
              }
              break;
            case "java.lang.Character":
              if (skip) {
                din.readChar();
              } else {
                f.set(o, new Character(din.readChar()));
              }
              break;
            case "java.lang.String":
              if (skip) {
                din.readUTF();
              } else {
                f.set(o, din.readUTF());
              }
              break;
          }
        }
      } catch (IllegalArgumentException | IllegalAccessException | IOException e) {
        String eClassName = e.getClass().getName();
        if (cmap.get(fname).equals(c.getName())) {
          System.out.println("deserialize: " + eClassName + " on deserializing field "
              + fname + " in " + c + " object");
        } else {
          System.out.println("deserialize: " + eClassName + " on deserializing superclass "
              + cmap.get(fname) + " field " + fname + " in " + c + " object");
        }
        e.printStackTrace();
      }
    }     
    return o;
  }
  
  public static String serializeNull(Object...n) {
    return ("Null");
  }
  
  public static String serializePrimitiveOrBoxed(Object v, boolean primitive) {
    // primitive is a directive for serialized output type
    // if v was primitive before being objectified it must have 
    // had a non null value and have one after objectivication
    if (Objects.isNull(v)) {
      if (!primitive) {
        return serializeNull();
      } else {
        throw new IllegalArgumentException(
            "serializePrimitiveOrBoxed: v is null but primitive is true");
      }
    }

    Class<?> type = v.getClass();
    if (!isBoxed(type)) 
      throw new IllegalArgumentException(
          "serializePrimitiveOrBoxed: v is not a boxed object");

    ByteArrayOutputStream bout = new ByteArrayOutputStream(100);
    DataOutputStream dout = new DataOutputStream(bout);

    String typeName = type.getName();

    try {
      switch (typeName) {
      case "java.lang.Integer":
        dout.writeInt((Integer) v);
        if (primitive) typeName = "int";
        break;
      case "java.lang.Long":
        dout.writeLong((Long) v);
        if (primitive) typeName = "long";
        break;
      case "java.lang.Double":
        dout.writeDouble((Double) v);
        if (primitive) typeName = "double";
        break;
      case "java.lang.Byte":
        dout.writeByte((Byte) v);
        if (primitive) typeName = "byte";
        break;
      case "java.lang.Short":
        dout.writeShort((Short) v);
        if (primitive) typeName = "short";
        break;
      case "java.lang.Float":
        dout.writeFloat((Float) v);
        if (primitive) typeName = "int";
        break;
      case "java.lang.Boolean":
        dout.writeBoolean((Boolean) v);
        if (primitive) typeName = "boolean";
        break;
      case "java.lang.Character":
        dout.writeChar((Character) v);
        if (primitive) typeName = "char";
        break;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    byte[] oBytes = null;

    try {
      dout.flush();
      bout.flush();
      oBytes = bout.toByteArray();
    } catch (IOException e) {
      e.printStackTrace();
    }

    String obstring = toHexString(oBytes);
    String out = typeName + "^" + obstring;
    return out;
  }

  public static Object deserializePrimitiveOrBoxed(String s) {
    if (Objects.isNull(s)) 
      throw new IllegalArgumentException("deserializePrimitiveOrBoxed: s is null");
    
    if (s.length() == 0)
      throw new IllegalArgumentException("deserializePrimitiveOrBoxed: s is empty");

    String[] r = s.split("\\^");
    int rlength = r.length;
    if (rlength == 1) {
      if (s.equals("Null")) return null;
      throw new IllegalArgumentException("deserializePrimitiveOrBoxed: s has one "
          + "component but it's not \"Null\"");
    }

    if (rlength != 2) 
      throw new IllegalArgumentException("deserializePrimitiveOrBoxed: s has " + rlength 
          + " components but should have 1 or 2");

    if (r[1].equals("Null")) return null;

    Class<?> type = null;
    switch (r[0]) {
    case "int":
      type = int.class;
      break;
    case "long":
      type = long.class;
      break;
    case "double":
      type = double.class;
      break;
    case "byte":
      type = byte.class;
      break;
    case "short":
      type = short.class;
      break;
    case "float":
      type = float.class;
      break;
    case "boolean":
      type = boolean.class;
      break;
    case "char":
      type = char.class;
      break;
    default:
      try {
        type = Class.forName(r[0]);
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
        throw new DataContentException("deserializePrimitiveOrBoxed: attempt to "
            + "instantiate class " + r[0] + " in s caused a ClassNotFoundException");
      }
      if (!isBoxed(type))
        throw new DataContentException("deserializePrimitiveOrBoxed: " + r[0]
            + " in s isn't a primitive or boxed type");
    }

    String typeName = type.getName();

    byte[] data = toByteArray(r[1]);
    ByteArrayInputStream bin = new ByteArrayInputStream(data);
    DataInputStream din = new DataInputStream(bin);

    try {
      if (din.available() > 0) {
        switch (typeName) {
        case "int":
          return din.readInt();
        case "long":
          return din.readLong();
        case "double":
          return din.readDouble();
        case "byte":
          return din.readByte();
        case "short":
          return din.readShort();
        case "float":
          return din.readFloat();
        case "boolean":
          return din.readBoolean();
        case "char":
          return din.readChar();
        case "java.lang.Integer":
          return new Integer(din.readInt());
        case "java.lang.Long":
          return new Long(din.readLong());
        case "java.lang.Double":
          return new Double(din.readDouble());
        case "java.lang.Byte":
          return new Byte(din.readByte());
        case "java.lang.Short":
          return new Short(din.readShort());
        case "java.lang.Float":
          return new Float(din.readFloat());
        case "java.lang.Boolean":
          return new Boolean(din.readBoolean());
        case "java.lang.Character":
          return new Character(din.readChar());
        }
      } else {
        System.out.println("deserializePrimitiveOrBoxed: din is empty");
      }
    } catch (IOException e) {
      e.printStackTrace();    }

    return null;
  }

  public static String serializeString(String v) {
    if (Objects.isNull(v)) return serializeNull();
    
    Class<?> type = v.getClass();
    if (!isString(type)) 
      throw new IllegalArgumentException("serializeString: v is not a string");

    ByteArrayOutputStream bout = new ByteArrayOutputStream(100);
    DataOutputStream dout = new DataOutputStream(bout);

    try {
      dout.writeUTF(v);
    } catch (IOException e) {
      e.printStackTrace();
    }

    byte[] oBytes = null;

    try {
      dout.flush();
      bout.flush();
      oBytes = bout.toByteArray();
    } catch (IOException e) {
      e.printStackTrace();
    }

    String obstring = toHexString(oBytes);
    String out = ("java.lang.String^" + obstring);

    return out;
  }

  public static Object deserializeString(String s) {
    if (Objects.isNull(s)) throw new IllegalArgumentException("deserializeString: s is null");
    if (s.length() == 0) throw new IllegalArgumentException("deserializeString: s is empty");

    String[] r = s.split("\\^");
    int rlength = r.length;
    if (rlength == 1) {
      if (s.equals("Null")) return null;
      throw new IllegalArgumentException("deserializeString: String s has one "
          + "component but isn't \"Null\"");
    }
    if (rlength != 2) 
      throw new IllegalArgumentException("deserializeString: String s has " + rlength 
          + " components but should have 1 or 2");
    if (!r[0].matches("java.lang.String")) 
      throw new DataContentException(
          "deserializeString: s doesn't begin with \"java.lang.String\"");
    if (r[1].equals("Null")) return null;

    byte[] data = toByteArray(r[1]);
    ByteArrayInputStream bin = new ByteArrayInputStream(data);
    DataInputStream din = new DataInputStream(bin);
    String x = null;

    try {
      x = din.readUTF();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return x;
  }
  
  public static String serializeArray(Object v) {
    if (Objects.isNull(v)) return serializeNull();

    Class<?> type = v.getClass();
    if (!type.isArray()) 
      throw new IllegalArgumentException("serializeArray: v is not an array");

    Class<?> componentType = type.getComponentType();
    String componentTypeName = componentType.getName();

    ByteArrayOutputStream bout = new ByteArrayOutputStream(100);
    DataOutputStream dout = new DataOutputStream(bout);

    int length = Array.getLength(v);

    try {
      dout.writeInt(length);
      String ser = null;
      Object o = null;
      if (length > 0) {
        // an element in an array of primitive type cannot be null and has
        // a consistent value set by default when not already set deliberately
        // e.g. 0 for int[], false for boolean[], (char)0 for char[], etc.
        if (componentType.isPrimitive()) {
          switch (componentTypeName) {
          case "int":
            for (int i = 0; i < length; i++) {
              Integer d = ((int[]) v)[i];
              dout.writeInt(d.intValue());
            }
            break;
          case "long":
            dout.writeInt(length);
            for (int i = 0; i < length; i++) {
              Long d = ((long[]) v)[i];
              dout.writeLong(d.longValue());
            }
            break;
          case "double":
            for (int i = 0; i < length; i++) {
              Double d = ((double[]) v)[i];
              dout.writeDouble(d.doubleValue());
            }
            break;
          case "byte":
            for (int i = 0; i < length; i++) {
              Byte d = ((byte[]) v)[i];
              dout.writeByte(d.byteValue());
            }
            break;
          case "short":
            for (int i = 0; i < length; i++) {
              Short d = ((short[]) v)[i];
              dout.writeShort(d.shortValue());
            }
            break;
          case "float":
            for (int i = 0; i < length; i++) {
              Float d = ((float[]) v)[i];
              dout.writeFloat(d.floatValue());
            }
            break;
          case "boolean":
            for (int i = 0; i < length; i++) {
              Boolean d = ((boolean[]) v)[i];
              dout.writeBoolean(d.booleanValue());
            }
            break;
          case "char":
            for (int i = 0; i < length; i++) {
              Character d = ((char[]) v)[i];
              dout.writeChar(d.charValue());
            }
            break;
          }
        } else if (isBoxed(componentType)) {
          for (int i = 0; i < length; i++) {
            o = Array.get(v, i);
            ser = Objects.isNull(o) ? serializeNull() : serializePrimitiveOrBoxed(o, false);
            dout.writeUTF(ser);
          }
        } else if (isString(componentType)) {
          for (int i = 0; i < length; i++) {
            o = Array.get(v, i);
            ser = Objects.isNull(o) ? serializeNull() : serializeString((String) o);
            dout.writeUTF(ser);
          }
        } else { 
          boolean foundNonNull = false;
          boolean ok2Serialize = true;
          for (int i = 0; i < length; i++) {
            o = Array.get(v, i);
            if (Objects.nonNull(o)) {
              foundNonNull = true;
              ok2Serialize = isSerializable(o);
              if (!ok2Serialize) break;
            }
          }
          if (foundNonNull && !ok2Serialize) 
            throw new DataContentException("writeArray: can't serialize v's since its "
                + " componentType, " + componentType +", is not serializable");
          
          // this may serialize a possibly non serializable componentType 
          // just because all its values are presently null
          if ((foundNonNull && ok2Serialize) || !foundNonNull) {
            for (int i = 0; i < length; i++) {
              o = Array.get(v, i);
              ser = Objects.isNull(o) ? serializeNull() : serialize(o);
              dout.writeUTF(ser);
            }
          } 
        }
      }
    } catch (IOException e) {
      e.printStackTrace();    }

    byte[] oBytes = null;

    try {
      dout.flush();
      bout.flush();
      oBytes = bout.toByteArray();
    } catch (IOException e) {
      e.printStackTrace();
    }

    String obstring = toHexString(oBytes);
    String out = "array^" + componentTypeName + "^" + obstring;
    
    return out;
  }

  public static Object deserializeArray(String s) {
    if (Objects.isNull(s)) 
      throw new IllegalArgumentException("deserializeArray: s is null");    
    if (s.length() == 0)
      throw new IllegalArgumentException("deserializeArray: s is empty");    
    if (s.equals("Null")) return null;
    
    String[] r = s.split("\\^");
    int rlength = r.length;
    String c = r[0];
    if (!c.equals("array")) throw new IllegalArgumentException("deserializeArray: "
        + "string s is not marked as a serialized array");
    if (rlength != 3) throw new IllegalArgumentException("deserializeArray: "
        + "string s has " + rlength + " components but should have 3");
    if (r[1].length() == 0) 
      throw new DataContentException("deserializeArray: string s has no "
          + "componentType information");
    if (r[2].length() == 0) throw new DataContentException("deserializeArray: string s "
        + "has no component data");

    String componentTypeName = r[1];
    Class<?> componentType = null;

    switch (componentTypeName) {
      case "int":     componentType = int.class;     break;
      case "long":    componentType = long.class;    break;
      case "double":  componentType = double.class;  break;
      case "byte":    componentType = byte.class;    break;
      case "short":   componentType = short.class;   break;
      case "float":   componentType = float.class;   break;
      case "boolean": componentType = boolean.class; break;
      case "char":    componentType = char.class;    break;
      default:
        try {
          componentType = Class.forName(componentTypeName);
        } catch (ClassNotFoundException e) {
          System.out.println("deserializeArray: cannot instantiate class for componentTypeName "
              + componentTypeName);  e.printStackTrace();
        }
    }

    byte[] data = toByteArray(r[2]);
    ByteArrayInputStream bin = new ByteArrayInputStream(data);
    DataInputStream din = new DataInputStream(bin);
    int length = 0;
    Object a = null;

    try {
      if (din.available() > 0) {
        length = din.readInt();
      } else {
        throw new ArraySerializationDataFormatException(
            "deserializeArray: serialized data missing length for array");
      }
      
      a = Array.newInstance(componentType, length);
      if (length > 0) {
        if (componentType.isPrimitive()) {
          switch (componentTypeName) {
          case "int":
            for (int j = 0; j < length; j++) 
              Array.set(a, j, din.readInt());
            break;
          case "long":
            for (int j = 0; j < length; j++) 
              Array.set(a, j, din.readLong());
            break;
          case "double":
            for (int j = 0; j < length; j++) 
              Array.set(a, j, din.readDouble());
            break;
          case "byte":
            for (int j = 0; j < length; j++) 
              Array.set(a, j, din.readByte());
            break;
          case "short":
            for (int j = 0; j < length; j++) 
              Array.set(a, j, din.readShort());
            break;
          case "float":
            for (int j = 0; j < length; j++) 
              Array.set(a, j, din.readFloat());
            break;
          case "boolean":
            for (int j = 0; j < length; j++) 
              Array.set(a, j, din.readBoolean());
            break;
          case "char":
            for (int j = 0; j < length; j++) 
              Array.set(a, j, din.readChar());
            break;
          }
        } else if (isBoxed(componentType)) {
          for (int j = 0; j < length; j++)
            Array.set(a, j, deserializePrimitiveOrBoxed(din.readUTF()));
        } else {
          for (int j = 0; j < length; j++) 
            Array.set(a, j, deserialize(din.readUTF()));
        }
      }
    } catch (ArraySerializationDataFormatException | IOException e) {
      System.out.println("deserializeArray: failed read on " + e.getClass().getName());
      e.printStackTrace();
    }
    return a;
  }
  
  @SuppressWarnings({ "rawtypes" })
  public static String serializeEnum(Object v) {
    if (Objects.isNull(v)) return serializeNull();
    
    Class<?> type = v.getClass();
    
    if (! type.isEnum())
      throw new IllegalArgumentException("serializeEnum: v is not an Enum");
    
    String typeName = type.getName();
    
    String enumName = ((Enum) v).name();
    String out = "enum^" + typeName+"^"+enumName;

    return out;
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static Object deserializeEnum(String s) {
    if (Objects.isNull(s)) throw new IllegalArgumentException("deserializeEnum: s is null");
    if (s.length() == 0) throw new IllegalArgumentException("deserializeEnum: s is empty");
    if (s.equals("Null")) return null;
    
    String[] r = s.split("\\^");
    int rlength = r.length;
    String c = r[0];
    if (!c.equals("enum")) 
      throw new IllegalArgumentException(
          "deserializeEnum: string s is not marked as a serialized enum");
    if (rlength != 3)
      throw new IllegalArgumentException(
          "deserializeEnum: String s has " + rlength + " components but should have 3");
    if (r[1].length() == 0) 
      throw new DataContentException("deserializeEnum: string s has no typeName information");
    if (r[2].length() == 0)
      throw new DataContentException(
          "deserializeEnum: string s has no enumName information");

    String typeName = r[1];
    String enumName = r[2];
    
   Class<? extends Enum> enumClass = null;
    
    try {
      enumClass = (Class<? extends Enum>) Class.forName(typeName);
    } catch (ClassNotFoundException e1) {
      System.out.println("deserializeEnum: ClassNotFoundException for name "+typeName);
      e1.printStackTrace();
    }
    
    return Enum.valueOf(enumClass, enumName);
  }
  
  @SuppressWarnings({ "rawtypes" })
  public static String serializeCollection(Object v) {
    if (Objects.isNull(v)) return serializeNull();
    
    Class<?> type = v.getClass();
    
    if (! isCollection(type))
      throw new IllegalArgumentException("serializeCollection: v is not a collection");   
    if (! isSupportedCollection(type))
      throw new IllegalArgumentException(
          "serializeCollection: v's type, "+type+", is not a supported collection");
    if (! isSerializableCollection(v))
      throw new IllegalArgumentException(
          "serializeCollection: v's type, "+type+", is not a serializable collection");
    
    String typeName = type.getName();
    
    Collection a = (Collection) v;
    int length = a.size();
    
    Object[] x = new Object[length];
    int j = 0;
    Iterator it = a.iterator();
    
    while(it.hasNext()) {
      x[j] = it.next();
      j++;
    }
    
    String ar = serializeArray(x);
    return "collection^"+typeName+"@"+ar;
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static Object deserializeCollection(String s) {
    if (Objects.isNull(s))
      throw new IllegalArgumentException("deserializeCollection: s is null");   
    if (s.length() == 0) 
      throw new IllegalArgumentException("deserializeCollection: s is empty");    
    if (s.equals("Null")) return null;
    
    String[] r = s.split("\\^");
    int rlength = r.length;
    String c = r[0];
    if (!c.equals("collection")) 
      throw new IllegalArgumentException(
          "deserializeCollection: string s is not marked as a serialized collection");
    if (rlength != 4) 
      throw new IllegalArgumentException( "deserializeCollection: String s has " 
          + rlength + " components but should have 4");
    
    String typeName = r[1].replaceFirst("@.*$", "");
    String array = s.replaceFirst("^[^@]*@", "");   
    Object[] a = (Object[]) deserializeArray(array);
    String enumTypeName = null;
    
    if (typeName.equals("java.util.RegularEnumSet")
        || typeName.equals("java.util.JumboEnumSet") 
        || typeName.equals("java.util.EnumSet")) {
      Set<String> enumTypeNames = new HashSet<>();    
      for (int i = 0; i < a.length; i++) {
        if (Objects.nonNull(a[i])) {
          enumTypeNames.add(a[i].getClass().getName());
        }
      }    
      if (enumTypeNames.size() == 0) {
        enumTypeName = "java.util.Enum";
      } else if (enumTypeNames.size() == 1) {
        enumTypeName = enumTypeNames.iterator().next();
      } else {
        enumTypeName = "java.util.Enum";
      }
    }
    
    switch (typeName) {
      case "java.util.concurrent.ArrayBlockingQueue":
        ArrayBlockingQueue abq = new ArrayBlockingQueue(a.length);
        for (Object o : a)
          try {
            abq.put(o);
          } catch (InterruptedException e1) {
            e1.printStackTrace();
          }
        return abq;
      case "java.util.Stack":
        Stack st = new Stack();
        for (Object o: a) st.push(o);
        return st;
      case "java.util.concurrent.SynchronousQueue":
        SynchronousQueue sq = new SynchronousQueue();
        for (Object o : a)
          try {
            sq.put(o);
          } catch (InterruptedException e1) {
            e1.printStackTrace();
          }
        return sq;
      case "java.util.RegularEnumSet":
      case "java.util.JumboEnumSet":
      case "java.util.EnumSet":
        Class<? extends Enum> enumType = null;
        try {
          enumType = 
              (Class<? extends Enum>) Class.forName(enumTypeName);
        } catch (ClassNotFoundException e) {
          System.out.println("deserializeCollection: cannot find class extends "
              + "enum for "+enumTypeName); e.printStackTrace();
        }
        EnumSet enumSet = EnumSet.noneOf(enumType);  
        for (Object o : a)
          enumSet.add((Enum) getEnumConstant(enumTypeName, ((Enum) o).name()));
        return enumSet;
      default:
        Class<?> claz = null;
        try {
          claz = Class.forName(typeName);
        } catch (ClassNotFoundException e) {
          System.out.println("deserializeCollection: class instantiation for "
              +typeName +" failed"); e.printStackTrace();
          return null;
        }
        Constructor con = null;
        try {
          con = claz.getConstructor(Collection.class);
        } catch (NoSuchMethodException | SecurityException e) {
          System.out.println("deserializeCollection: constructor instatiation for "
              + typeName  +" failed "); e.printStackTrace();
          return null;
        }   
        try {
          return con.newInstance(Arrays.asList(a));
        } catch (InstantiationException | IllegalAccessException 
               | IllegalArgumentException | InvocationTargetException e) {
          System.out.println("deserializeCollection: new instance creation for "
              + typeName + "failed"); e.printStackTrace();
          return null;
        }
    }
  }
 
  @SuppressWarnings({ "rawtypes" })
  public static String serializeMap(Object v, String...className) {
    if (Objects.isNull(v))  return serializeNull();

    Class<?> type = null;
   
    if (className.length > 0) {
      try {
        type = Class.forName(className[0]);
      } catch (ClassNotFoundException e) {
        type = v.getClass().getSuperclass();
      }
    } else {
      type = v.getClass(); 
    }
    
    String typeName = type.getName();

    if (! isMap(type)) throw new IllegalArgumentException("serializeMap: v is not a map");
    
    if (! isSupportedMap(type))
      throw new IllegalArgumentException(
          "serializeMap: v's type, "+type+", is not a supported map");
    
    if (! isSerializableMap(v))
      throw new IllegalArgumentException(
          "serializeMap: v's type, "+type+", is not a serializable map");
 
    Map a = (Map) v;
    int length = a.size(); 

    String ar1 = "";
    String ar2 = "";
    int j;
    
    switch (typeName) {
    case "java.util.jar.Attributes":  
      // keys have type java.util.jar.Attributes$Name and
      // can be stringified with toString; values are strings
      String[] atKeys = new String[length];
      String[] atValues = new String[length];
      j = 0;
      for (Object k : a.keySet()) {
        atKeys[j] = k.toString(); 
        atValues[j] = (String) a.get(k);
        j++;
      }
      ar1 = serializeArray(atKeys);
      ar2 = serializeArray(atValues);
      break;
    default:
      Object[] keys = new Object[length];
      Object[] values = new Object[length];
      j = 0;
      for (Object k : a.keySet()) {
        keys[j] = k; 
        values[j] = a.get(k);
        j++;
      }

      ar1 = serializeArray(keys);
      ar2 = serializeArray(values);
    }

    String out = "map^"+typeName+"@"+ar1+"@"+ar2;
    
    return out;
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static Map deserializeMap(String s) {
    if (Objects.isNull(s))
      throw new IllegalArgumentException("deserializeMap: s is null");
    if (s.length() == 0)
      throw new IllegalArgumentException("deserializeMap: s is empty");  
    if (s.equals("Null")) return null;
    
    String[] r = s.split("\\^");
    int rlength = r.length;
    
    if (! r[0].equals("map"))
      throw new IllegalArgumentException(
          "deserializeMap: string s is not marked as a serialized map but as a "+r[0]);
    
    if (rlength != 6)
      throw new IllegalArgumentException( "deserializeMap: String s has " 
          + rlength + " components but should have 6");

    String typeName = r[1].replaceFirst("@.*$", "");
    
    if (typeName.length() == 0)
      throw new IllegalArgumentException( "deserializeMap: typeName field in s is empty");
    
    String[] arrays = s.replaceFirst("^[^@]*@", "").split("@");
    int alength = arrays.length;
//    for (int i = 0; i < arrays.length; i++) System.out.println(arrays[i]);
    
    if (alength != 2) 
      throw new IllegalArgumentException("deserializeMap: there are " + alength
          + " serialized arrays embedded in s, but there should be 2");
    
    boolean empty1stArrayString = false;
    boolean empty2ndArrayString = false;
    
    if (arrays[0].length() == 0) empty1stArrayString = true;
    if (arrays[1].length() == 0) empty2ndArrayString = true;
    
    if (empty1stArrayString && empty2ndArrayString) {
      throw new IllegalArgumentException(
          "deserializeMap: both array strings embedded in s are empty");
    } else if (empty1stArrayString) {
      throw new IllegalArgumentException(
          "deserializeMap: the first array string embedded in s is empty");
    } else if (empty2ndArrayString) {
      throw new IllegalArgumentException(
          "deserializeMap: the second array string embedded in s is empty");
    }
    
    boolean isSerializedArray1st = true;
    boolean isSerializedArray2nd = true;
    
    if (! isSerializedArray(arrays[0])) isSerializedArray1st = false;
    if (! isSerializedArray(arrays[1])) isSerializedArray2nd = false;
    
    if (! (isSerializedArray1st || isSerializedArray2nd)) {
      throw new IllegalArgumentException(
        "deserializeMap: neither array string embedded in s is a serialized array string");
    } else if (! isSerializedArray1st) {
      throw new IllegalArgumentException(
        "deserializeMap: the first array string embedded in s is not a serialized array string");
    } else if (! isSerializedArray2nd) {
      throw new IllegalArgumentException(
        "deserializeMap: the second array string embedded in s is not a serialized array string");
    }
 
    Object[] keys = null;
    Object[] values = null;
    
    switch (typeName) {
      case "java.util.jar.Attributes":
        String[] atKeys = (String[]) deserializeArray(arrays[0]);    
        String[] atValues = (String[]) deserializeArray(arrays[1]);
        keys = new Object[atKeys.length];
        values = new Object[atValues.length];
        for (int i = 0; i < atKeys.length; i++) {
          keys[i] = new Attributes.Name(atKeys[i]);
        }
        for (int i = 0; i < atValues.length; i++) {
          values[i] = atValues[i];
        }
        break;
      default:
        keys = (Object[]) deserializeArray(arrays[0]);
        values = (Object[]) deserializeArray(arrays[1]);
    }
    
    int length = 0;
    
    if (keys.length != values.length) {
      throw new DataFormatException("deserializeMap: the deserialized keys and values "
          + "arrays have different lengths ("+ keys.length + " and " + values.length
              + ") for a " + typeName);  
    } else length = keys.length;

    Class<?> claz = null;
    Map map = null;
    
    switch (typeName) {
      case "java.util.EnumMap":
        Map<Enum, Object> hmap = new HashMap<>();
        for (int i = 0; i < length; i++)
          hmap.put((Enum) keys[i], values[i]);
        return new EnumMap(hmap);
      default:
        try {
          claz = Class.forName(typeName);
        } catch (ClassNotFoundException e) {
          System.out.println("deserializeMap: class instantiation for "+typeName+ " failed");
          e.printStackTrace();
          return null;
        }
        try {
          map = (Map) claz.newInstance();
        } catch (InstantiationException | IllegalAccessException 
               | IllegalArgumentException  e) {
          System.out.println("deserializeMap: new instance creation for " + typeName
              + " failed"); e.printStackTrace();
          return null;
        }
        for (int i = 0; i < length; i++) 
          map.put(keys[i], values[i]);
        return map;
    }
  }
  
  public static String serializeBitSet(BitSet v) {
    if (Objects.isNull(v)) return serializeNull();
    
    Class<?> type = v.getClass();
    if (!isBitSet(type))
      throw new IllegalArgumentException("serializeBitSet: v is not a BitSet");
    
    byte[] bytes = ((BitSet) v).toByteArray();
    
    String a = serializeArray(bytes);
    
    return "java.util.BitSet@"+a;
  }
 
  public static BitSet deserializeBitSet(String s) {
    if (Objects.isNull(s))
      throw new IllegalArgumentException("deserializeBitSet: s is null"); 
    if (s.length() == 0)
      throw new IllegalArgumentException("deserializeBitSet: s is empty");    
    if (s.equals("Null")) return null;
    
    String[] r = s.split("\\@");
    int rlength = r.length;
    
    if (! r[0].equals("java.util.BitSet"))
      throw new IllegalArgumentException("deserializeBitSet: string s is not "
          + "marked as a serialized bitset but as a "+r[0]);
    
    if (rlength != 2)
      throw new IllegalArgumentException("deserializeBitSet: String s has " 
          + rlength + " components but should have 2");

    if (r[1].length() == 0) 
      throw new DataContentException(
          "deserializeBitSet: string s has no array information");
    
    String array = r[1];
  
    BitSet bitSet = new BitSet();
    byte[] bytes = (byte[]) deserializeArray(array);
    int size = (bytes.length * 8) - 7;
    for (int i = 0; i <= size; i++)
      if ((bytes[i/8] & (1<<(i%8))) != 0)
        bitSet.set(i);
    
    return bitSet;
  }
  
  public static String serializeAttributesName(Attributes.Name v) {
    if (Objects.isNull(v)) return serializeNull();
    
    String vString = v.toString();
    
    ByteArrayOutputStream bout = new ByteArrayOutputStream(100);
    DataOutputStream dout = new DataOutputStream(bout);

    try {
      dout.writeUTF(vString);
    } catch (IOException e) {
      e.printStackTrace();    
    }

    byte[] oBytes = null;

    try {
      dout.flush();
      bout.flush();
      oBytes = bout.toByteArray();
    } catch (IOException e) {
      e.printStackTrace();
    }

    String obstring = toHexString(oBytes);
    String out = ("java.util.jar.Attributes.Name^" + obstring);

    return out;
  }
 
  public static Attributes.Name deserializeAttributesName(String s) {
    if (Objects.isNull(s))
      throw new IllegalArgumentException("deserializeAttributesName: s is null");
    if (s.length() == 0)
      throw new IllegalArgumentException("deserializeAttributesName: s is empty");

    String[] r = s.split("\\^");
    int rlength = r.length;
    if (rlength == 1)
      if (s.equals("Null")) {
        return null;
      } else {
        throw new IllegalArgumentException(
            "deserializeAttributesName: String s has one component but isn't \"Null\"");
      }
    if (rlength != 2)
      throw new IllegalArgumentException("deserializeAttributesName: String s has " + rlength 
          + " components but should have 1 or 2");
    if (!r[0].matches("java.util.jar.Attributes.Name"))
      throw new DataContentException(
          "deserializeString: s doesn't begin with \"java.util.jar.Attributes.Name\"");
    if (r[1].equals("Null"))
      return null;

    byte[] data = toByteArray(r[1]);
    ByteArrayInputStream bin = new ByteArrayInputStream(data);
    DataInputStream din = new DataInputStream(bin);
    String x = null;

    try {
      x = din.readUTF();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return new Attributes.Name(x);
  }
  
  public static void copyString2File(String s, String pathName) {
    Path p = Paths.get(pathName);
    if (Files.exists(p)) {
      System.out.println("overwriting "+pathName);
    } else {
      try {
        Files.createFile(p);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    try {
      Files.write(p, s.getBytes(UTF_8));
    } catch (IOException e) {
      e.printStackTrace();    } 
  }
  
  public static String copyFile2String(String pathName) {
    Path p = Paths.get(pathName);
    if (! Files.exists(p)) {
      System.out.println(pathName+" does not exist");
      return "";
    }
    
    String output = null;
    try {
      output = new String(Files.readAllBytes(p), UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
      return  "";
    }
    
    return Objects.isNull(output) ? "" : output;
  }
  
  public static String compress(String data) {
    while (data.length() % 2 == 0 && data.matches("\\p{XDigit}+"))
      data = new String(toByteArray(data));
    ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length());
    try (GZIPOutputStream gzip = new GZIPOutputStream(bos)) {
      gzip.write(data.getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return toHexString(bos.toByteArray());
  }
  
  public static String decompress(String compressed) {
    // Test if compressed is a lexicalXSDBase64Binary String
    // and if so parse it to a byte[] or use String.getBytes().
    // Then test the byte array for the GZIP_MAGIC header and
    // return compressed as is if it has it, otherwise decompress
    // it by running the byte[] through a GZIPInputStream layered
    // on a ByteArrayInputStream initialized with that byte[].
    // Layer a BufferedReader on an InputStreamReader over the
    // GZIPInputStream to allow specification of the input  
    // encoding and for use of readLine() which can read an  
    // entire serialized string.
    
    byte[] ba = null;
    
    // if compressed is a hex string parse it to a byte[]
    if (compressed.length() % 2 == 0 && compressed.matches("\\p{XDigit}+")) {
      ba = toByteArray(compressed);
    } else { // use String.getBytes()
      ba = compressed.getBytes(UTF_8);
    }
    
    // test for the GZIP_MAGIC header 
    if (! isGzip(ba)) return compressed;
    
    // otherwise decompress it with GZIPInputStream
    ByteArrayInputStream bis = new ByteArrayInputStream(ba);
    StringBuilder sb = new StringBuilder();
    String line = null;
    try (GZIPInputStream gis = new GZIPInputStream(bis); 
        BufferedReader br = new BufferedReader(new InputStreamReader(gis, "UTF-8"))) {
      while((line = br.readLine()) != null) sb.append(line);
    } catch (IOException e ) {
      e.printStackTrace();
    }  
    
    return sb.toString();  
  }
  
  public static void gzipString2File(String s, String pathName) {
    Path p = Paths.get(pathName);
    if (Files.exists(p)) {
      System.out.println("gzipString2File: overwriting "+pathName);
    } else {
      try {
        Files.createFile(p);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    String gzipped = compress(s);

    try {
      Files.write(p, gzipped.getBytes(UTF_8));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static String gunzipFile2String(String pathName) {
    Path p = Paths.get(pathName);
    if (! Files.exists(p)) {
      System.out.println("gunzipFile2String: " + pathName+" does not exist");
      return "";
    }

    String output = null;
    try {
      output = new String(Files.readAllBytes(p), UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    if (Objects.nonNull(output)) output = decompress(output);

    return Objects.isNull(output) ? "" : output;
  }
  
  public static boolean isGzip(byte[] bytes) {
    // https://www.javacodegeeks.com/2015/01/working-with-gzip-and-compressed-data.html
    return bytes[0] == (byte) GZIP_MAGIC 
        && bytes[1] == (byte) (GZIP_MAGIC >>> 8);
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static Enum getEnumConstant(String typeName, String enumName) {
    if (Objects.isNull(typeName))
      throw new IllegalArgumentException("getEnumConstant: typeName is null");
    if (typeName.length() == 0)
      throw new IllegalArgumentException("getEnumConstant: typeName is empty");
    if (Objects.isNull(enumName))
      throw new IllegalArgumentException("getEnumConstant: enumName is null");
    if (enumName.length() == 0) 
      throw new IllegalArgumentException("getEnumConstant: enumName is empty");
        
   Class<? extends Enum> enumClass = null;
    
    try {
      enumClass = (Class<? extends Enum>) Class.forName(typeName);
    } catch (ClassNotFoundException e1) {
      e1.printStackTrace();
      return null;
    }
    
    Enum nemu = Enum.valueOf(enumClass, enumName);
    
    return nemu;
    
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static Class<? extends Enum> getEnumSetElementType(Object o) {
    if (Objects.isNull(o))
      throw new IllegalArgumentException("getEnumSetElementType: o is null");
    
    Class<?> c = o.getClass();
    String cname = c.getName();
        
    if (! (cname.equals("java.util.EnumSet")
        || cname.equals("java.util.RegularEnumSet") 
        || cname.equals("java.util.JumboEnumSet"))) { 
      throw new IllegalArgumentException("getEnumSetElementType: o is not an "
          + "EnumSet, RegularEnumSet or JumboEnumSet");
    }
    
    Class<? extends Enum> type = null;
    try {
      Field f = EnumSet.class.getDeclaredField("elementType");
      f.setAccessible(true);
      type = (Class<? extends Enum>) f.get(o);
    } catch (NoSuchFieldException | SecurityException 
        | IllegalArgumentException | IllegalAccessException e) {
      e.printStackTrace();
    }
    
    return Objects.nonNull(type) ? type : null;
  }
  
  public static Object getFields(Class<?> c, String s) {
    // method              requires
    // =================== ======================================================================
    // serialize:          List<Field> flist //excludes static & super private
    // deserialize:        LinkedHashMap<String, Field> fmap //excludes static & super private
    //                     LinkedHashMap<String, String> cmap //excludes static & super private
    // readFields:         List<Field> rlist //includes static & super private
    //                     Map<Field,Class<?>> rmap //includes static & super private
    // isSerializable:     LinkedHashMap<Field, Class<?>> imap //excludes static & super private
    // isSerialiableClass: LinkedHashMap<Field, Class<?>> imap //excludes static & super private
      
      if (Objects.isNull(c))
        throw new IllegalArgumentException("getFields: deserialize: c is null");      
      if (Objects.isNull(s))
        throw new IllegalArgumentException("getFields: deserialize: s is null");
      if (s.length() == 0)
        throw new IllegalArgumentException("getFields: deserialize: s is empty");
      
      if (! (s.matches("des.*") || s.matches("ser.*")  || s.matches("get.*")
          || s.matches("read.*") || s.matches("is.*")))
        throw new IllegalArgumentException(
            "getFields: : s doesn't match des.*, ser.*, read.* or is.*)");
          
      if (c.isArray())
        throw new IllegalArgumentException(
            "getFields: c is an Array class which isn't allowed");

      String name = null;
      List<Field> flist = new ArrayList<>();  // 4 serialize
      LinkedHashMap<String, Field> fmap = new LinkedHashMap<>();  // 4 deserialize
      LinkedHashMap<String, String> cmap = new LinkedHashMap<>(); // 4 deserialize
      List<Field> rlist = new ArrayList<>(); // 4 readFields
      Map<Field,Class<?>> rmap = new HashMap<>(); // 4 readFields
      LinkedHashMap<Field, Class<?>> imap = new LinkedHashMap<>(); // 4 isSerializable

      boolean hasStatic = false;
      boolean hasPrivate = false;
      
      // for flist, fmap and cmap get non-static fields declared in c=o.getClass()
      // for rlist get all fields declared in c
      Field[] fields = c.getDeclaredFields();
      for (Field f : fields) {
        hasStatic = false;
        if (hasStatic(f.getModifiers())) hasStatic = true;
        f.setAccessible(true);
        name = f.getName();
        if (! fmap.containsKey(name)) {
          rlist.add(f);
          rmap.put(f, c);
          if (! hasStatic) {
            flist.add(f);
            fmap.put(name, f);
            cmap.put(name, c.getName());
            imap.put(f, c);
          }   
        }
      }
      
      // for flist, fmap and cmap get non-static, non-private, non-overridden fields 
      //   declared in superclasses of c=o.getClass() in ascending order up to but 
      //   not including java.lang.Object
      // similarly for rlist but include all fields declared in all superclasses
      if (c != java.lang.Object.class) {
        Class<?> sclass = c;
        while ((sclass = sclass.getSuperclass()) != java.lang.Object.class) {
          fields = sclass.getDeclaredFields();
          for (Field f : fields) {
            hasStatic = false;
            if (hasStatic(f.getModifiers())) hasStatic = true;
            if (hasPrivate(f.getModifiers())) hasPrivate = true;
            f.setAccessible(true);
            name = f.getName();
            if (!fmap.containsKey(name)) {
              rlist.add(f);
              rmap.put(f, sclass);
              if (! (hasStatic || hasPrivate)) {
                flist.add(f);
                fmap.put(name, f);
                cmap.put(name, sclass.getName());
                imap.put(f, c);
              }
            }
          }
        }
      }
      
      if (s.matches("ser.*"))  return flist;
      if (s.matches("des.*"))  return new Object[]{fmap, cmap};
      if (s.matches("read.*")) return new Object[]{rlist, rmap};  
      if (s.matches("is.*")) return imap;
      
      return null;
    }

  public static Object getInstance(Class<?> c) {
    if (Objects.isNull(c)) throw new IllegalArgumentException("c is null");
    try {
      Constructor<?> con1 = c.getConstructor();
      Object inst1 = con1.newInstance();
      return inst1;
    } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
        | IllegalArgumentException | InvocationTargetException e1) {
      try {
        Constructor<?> con2 = c.getDeclaredConstructor();
        Object inst2 = con2.newInstance();
        return inst2;
      } catch (NoSuchMethodException | SecurityException | InstantiationException 
          | IllegalAccessException | IllegalArgumentException | InvocationTargetException e2) {
        e2.printStackTrace();
      }
    }
    return null;
  }
  
  public static List<String> getInterfaces(Class<?> c) {
    if (Objects.isNull(c))
      throw new IllegalArgumentException("getInterfaces(Class<?>): c is null");

    Set<String> z = new HashSet<>();
    String name = null;

    while (c != null) {
      Class<?>[] itfs = c.getInterfaces();
      for (Class<?> x : itfs) {
        name = x.getName();
        // exclude marker and irrelevant interfaces
        if (! (name.equals("java.io.Serializable")
            || name.equals("java.lang.Cloneable")
            || name.equals("java.lang.Comparable") // not a marker interface
            || name.equals("java.util.RandomAccess")
            || name.equals("java.util.EventListener")
            || name.equals("java.rmi.Remote")
            || name.equals("javax.security.auth.callback.Callback")
            || name.equals("javax.security.auth.login.Configuration.Parameters")
            || name.equals("java.util.concurrent.CompletableFuture.AsynchronousCompletionTask")  
            )) {
          z.add(name);
        }
      }
      c = c.getSuperclass();
    }

    return new ArrayList<String>(z);
  }
  
  public static List<String> getInterfaces(String s) {
    if (Objects.isNull(s)) 
      throw new IllegalArgumentException("getInterfaces(String): s is null");
    if (s.length() == 0)
      throw new IllegalArgumentException("getInterfaces(String): s is empty");

    Class<?> c = null;
    try {
      c = Class.forName(s);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    
    if (Objects.nonNull(c)) {
      return getInterfaces(c);
    } else {
      return new ArrayList<String>();
    }
  }
  
  public static String getSerializedClassName(String s) {
    // This method is for providing guidance for casting the result of 
    // deserialization. For arrays "className[]" may be returned where 
    // className is that of its components if known otherwise only "[]" 
    // will be returned. The overall integrity of the serialization 
    // string is not assessed and that's best done by attempting to 
    // deserialize it with deserialize();

    if (Objects.isNull(s)) throw new IllegalArgumentException("s is null");
    if (s.length() == 0) throw new IllegalArgumentException("s is empty");

    String[] r = s.split("\\^");
    int rlength = r.length;
    if (rlength == 1)
      return s; // should be Null
    if (r[0].equals("array")) {
      if (r.length < 2) {
        return "[]";
      } else {
        return "" + r[1] + "[]";
      }
    } else if (r[0].equals("enum") || r[0].equals("collection")) { 
      if (r.length < 2) {
        return r[0];
      } else if (r.length < 3) {
        return r[1];
      } else {
        return r[1] +"<" + r[2] + ">";
      }
    } else if (r[0].equals("map")) {
      if (r.length < 2) {
        return r[0];
      } else if (r.length < 3) {
        return r[1];
      } else if (r.length < 4) {
        return r[1] +"<" + r[2] + ", ?>";
      } else {
        return r[1] +"<" + r[2] + ", " + r[3] + ">";
      }
    } else {
      return r[0];
    }
  }
 
  public static boolean hasNoArgConstructor(Class<?> c) {
    if (Objects.isNull(c))
      throw new IllegalArgumentException("hasNoArgConstructor: c is null");
    try {
      return Objects.nonNull(c.getDeclaredConstructor());
    } catch (NoSuchMethodException | SecurityException e) {
      try {
        return Objects.nonNull(c.getConstructor());
      } catch (NoSuchMethodException | SecurityException e1) {
        e1.printStackTrace();
      }
    }
    return false;
  }

  public static boolean hasSerializableAnnotation(Class<?> c) {
    if (Objects.isNull(c)) throw new IllegalArgumentException("c is null");
    if (c.isAnnotationPresent(Serializable.class)) return true;
    return false;
  }

  public static boolean isAttributesName(Class<?> c) {
    if (Objects.isNull(c)) throw new IllegalArgumentException("isAttributesName: c is null");    
    if (c == java.util.jar.Attributes.Name.class) return true;    
    return false;
  }
  
  public static boolean isBitSet(Class<?> c) {
    if (Objects.isNull(c)) throw new IllegalArgumentException("isMap: c is null");    
    if (c.getName().equals("java.util.BitSet")) return true;
    return false;
  }
  
  public static boolean isBoxed(Class<?> c) {
    if (Objects.isNull(c)) throw new IllegalArgumentException("isBoxed: c is null");    
    String typeName = c.getName();
    return isBoxed(typeName);
  }
  
  public static boolean isBoxed(String s) {
    if (Objects.isNull(s)) throw new IllegalArgumentException("isBoxed: s is null");
    if (s.length() == 0) throw new IllegalArgumentException("isBoxed: s is empty");
    return s.matches("|java.lang.Integer|java.lang.Long|java.lang.Double"
        + "|java.lang.Byte|java.lang.Short|java.lang.Float" 
        + "|java.lang.Boolean|java.lang.Character") ? true : false;
  }
  
  public static boolean isCollection(Class<?> c) {
    if (Objects.isNull(c)) throw new IllegalArgumentException("isCollection: c is null");    
    return getInterfaces(c).contains("java.util.Collection") ? true : false;
  }
  
  public static boolean isEnum(Class<?> c) {
    if (Objects.isNull(c)) throw new IllegalArgumentException("isEnum: c is null");
    return c.isEnum();
  }
  
  public static boolean isMap(Class<?> c) {
    if (Objects.isNull(c)) throw new IllegalArgumentException("isMap: c is null");    
    return c.getName().equals("java.util.Map") || getInterfaces(c).contains("java.util.Map")
        ? true : false;
  }

  public static boolean isPrimitive(Class<?> c) {
    if (Objects.isNull(c)) throw new IllegalArgumentException("isPrimitive: c is null");
    String typeName = c.getName();
    return isPrimitive(typeName);
  }

  public static boolean isPrimitive(String s) {
    if (Objects.isNull(s)) throw new IllegalArgumentException("isPrimitive: s is null");
    if (s.length() == 0) throw new IllegalArgumentException("isPrimitive: s is empty");
    return s.matches("int|long|double|byte|short|float|boolean|char") ? true : false;
  }
  
  public static boolean isPrimitiveOrBoxed(Class<?> c) {
    if (Objects.isNull(c)) 
      throw new IllegalArgumentException("isPrimitiveOrBoxed: c is null");
    String typeName = c.getName();
    return isPrimitiveOrBoxed(typeName);
  }

  public static boolean isPrimitiveOrBoxed(String s) {
    if (Objects.isNull(s))
      throw new IllegalArgumentException("isPrimitiveOrBoxed: s is null");
    if (s.length() == 0)
      throw new IllegalArgumentException("isPrimitiveOrBoxed: s is empty");
    return s.matches("int|long|double|byte|short|float|boolean|char" 
        + "|java.lang.Integer|java.lang.Long|java.lang.Double"
        + "|java.lang.Byte|java.lang.Short|java.lang.Float" 
        + "|java.lang.Boolean|java.lang.Character") ? true : false;
  }

  public static boolean isSerializable(Object o) {
    // Determine if an object is serializable by criteria:
    // If useAnnotation is true and its class is annotated 
    // with @Serializable then it's assumed to be 
    // serializable. If useAnnotation is false then it's 
    // actually serializable if it's:
    //   1. primitive
    // or its class is a:
    //   2. primitive wrapper
    //   3. Enum
    //   4. array with serializable components 
    //   5. a supported collection or map with serializable
    //      components
    //   6. a specially supported class 
    // or if its class has:
    //   7. a no arg constructor and all its non-static fields 
    //      and all fields of its superclasses, that are not 
    //      overridden, static or private, are serializable.
    //
    // Supported generally means up to equality between the 
    // original and serialized objects. Some classes are 
    // essentially supportable, such as IdentityHashMap, if 
    // ordering of components is disregarded. Some other classes
    // are intrinsically not fully supportable , but without 
    // actually affecting the quality of serdes for them, such 
    // as concurrent classes that always create a unique lock
    // on instantiation. Objects of some classes that are
    // unsupported may be incidentally supported because they are
    // null or their unsupported fields or components are null.
    //
    // All collections are supported except for:
    //   java.beans.beancontext.BeanContextSupport
    //   java.beans.beancontext.BeanContextServicesSupport
    //   java.util.concurrent.ConcurrentHashMap.KeySetView
    //
    // All maps are supported except for:
    //   java.security.AuthProvider
    //   java.util.IdentityHashMap
    //   javax.print.attribute.standard.PrinterStateReasons
    //   java.util.Properties
    //   java.security.Provider
    //   java.awt.RenderingHints
    //   javax.script.SimpleBindings
    //   javax.management.openmbean.TabularDataSupport
    //   javax.swing.UIDefaults
    //
    // Specially supported classes are:
    //   java.util.BitSet.class
    //   java.util.jar.Attributes.Name.class
    //
    // Most unsupported collections, maps and other classes
    // requiring integration could be supported.
    
    if (Objects.isNull(o))
      throw new IllegalArgumentException("isSerializable(Object): o is null");
    
    Class<?> c = o.getClass();
    
    if (useAnnotation) return hasSerializableAnnotation(c);
    if (isPrimitiveOrBoxed(c)) return true;
    if (isString(c)) return true;
    if (isSerializableArray(o)) return true;
    if (c.isEnum()) return true;
    if (isSerializableCollection(o)) return true;
    if (isSerializableMap(o)) return true;
    if (isSupportedClass(c)) return true;
    if (!hasNoArgConstructor(c)) return false;
 
    @SuppressWarnings("unchecked")
    LinkedHashMap<Field, Class<?>> fmap = 
        (LinkedHashMap<Field, Class<?>>) getFields(c, "is");
    LinkedHashMap<String, String> failed = new LinkedHashMap<>();
    Class<?> type = null;
    String name = null;
    boolean ok = true;
    boolean nonSerializableSuperClass = false;
    boolean isSerializable = false;
    Object v = null;

    // build map of non-serializable field names to class names
    for (Field f : fmap.keySet()) {
      if (hasPrivate(f.getModifiers())) f.setAccessible(true);
      name = f.getName();
      type = f.getType();
      try {
        v = f.get(o);
      } catch (IllegalArgumentException | IllegalAccessException e) {
        e.printStackTrace();
      }
      if (Objects.nonNull(v)) {
        isSerializable = isSerializable(v);
        if (! isSerializable) {
          failed.put(name, fmap.get(f).getName());
          ok = false;
          if (fmap.get(f).getName() != c.getName()) {
            nonSerializableSuperClass = true;
          }
        }
      } else {
        // this isn't conclusive but ok for now since v is null
        isSerializable = isSerializableClass(type);
        if (! isSerializable) {
          failed.put(name, fmap.get(f).getName());
          ok = false;
          if (fmap.get(f).getName() != c.getName()) {
            nonSerializableSuperClass = true;
          }
        }
      }
    }

    if (ok) {
      return true;
    } else {
      if (nonSerializableSuperClass) {
        System.out.println("isSerializable: the following fields in " + c.getName() 
            + " and its superclasses are not serializable)");
      } else {
        System.out.println("isSerializable: the following fields in " + c.getName() 
            + " are not serializable)");
      }
      for (String n : failed.keySet()) {
        System.out.println("isSerializable: "+failed.get(n) + "." + n);
      }
      return false;
    }
  }

  public static boolean isSerializableArray(Object o) {
    if (Objects.isNull(o))
      throw new IllegalArgumentException("isSerializableArray: o is null");

    Class<?> c = o.getClass();
        
    if (!c.isArray()) return false;
    if (useAnnotation) return hasSerializableAnnotation(c);
    
    int length = Array.getLength(o);
    Object v = null;
    boolean foundNonNull = false;
    boolean ok2Serialize = true;
    
    for (int i = 0; i < length; i++) {
      v = Array.get(o,i);
      if (Objects.nonNull(v)) {
        foundNonNull = true;
        ok2Serialize = isSerializable(v);
        if (!ok2Serialize) break;
      }
    }
    
    if ((foundNonNull && ok2Serialize) || !foundNonNull)
      return true;

    return false; 
  }
  
  public static boolean isSerializableClass(Class<?> c) {
    // A negative outcome is conclusive but not a positive one for
    // cases of collections with all null elements and maps with all
    // null keys or all null values, however those cases are
    // incidentally serializable because null is.
    if (Objects.isNull(c))
      throw new IllegalArgumentException("isSerializableClass: c is null");    
    if (useAnnotation) return hasSerializableAnnotation(c);
    if (isPrimitiveOrBoxed(c)) return true;
    if (isString(c)) return true;
    if (c.isArray()) return isSerializableClass(c.getComponentType());
    if (c.isEnum()) return true;
    if (isSupportedClass(c)) return true;
    if (!hasNoArgConstructor(c)) return false;

    @SuppressWarnings("unchecked")
    LinkedHashMap<Field, Class<?>> fmap = 
        (LinkedHashMap<Field, Class<?>>) getFields(c, "is");
    LinkedHashMap<String, String> failed = new LinkedHashMap<>();
    Class<?> type = null;
    String name = null;
    boolean isSerializable = true; // default outcome
    boolean nonSerializableSuperClass = false;

    // test each field for serializability and put
    // failures in the failed map for report
    for (Field f : fmap.keySet()) {
      if (hasPrivate(f.getModifiers()))
        f.setAccessible(true);
      name = f.getName();
      type = f.getType();
      isSerializable = isSerializableClass(type);
      if (! isSerializable) {
        failed.put(name, fmap.get(f).getName());
        if (fmap.get(f).getName() != c.getName()) {
          nonSerializableSuperClass = true;
        }
      }
    }

    if (isSerializable) {
      return true;
    } else {
      if (nonSerializableSuperClass) {
        System.out.println("isSerializableClass: the following fields in " + c.getName() 
            + " and its superclasses are not serializable)");
      } else {
        System.out.println("isSerializableClass: the following fields in " + c.getName() 
            + " are not serializable)");
      }
      for (String n : failed.keySet()) {
        System.out.println("isSerializableClass: "+failed.get(n) + "." + n);
      }
      return false;
    }
  }
  
  public static boolean isSerializableCollection(Object o) {
    if (Objects.isNull(o))
      throw new IllegalArgumentException("isSerializableCollection: o is null");

    Class<?> c = o.getClass();

    if (!isSupportedCollection(c)) return false;
    if (useAnnotation) return hasSerializableAnnotation(c);

    @SuppressWarnings({ "unchecked", "rawtypes" })
    Object[] a = ((Collection) o).toArray(new Object[0]);
    return isSerializableArray(a);
  }

  public static boolean isSerializableMap(Object o) {
    if (Objects.isNull(o))
      throw new IllegalArgumentException("isSerializableMap: o is null");

    Class<?> c = o.getClass();

    if (!isSupportedMap(c)) return false;
    if (useAnnotation) return hasSerializableAnnotation(c);

    @SuppressWarnings("rawtypes")
    Map a = (Map) o;
    Object[] keys = new Object[a.size()];
    Object[] values = new Object[a.size()];
    int i = 0;
    for (Object k : a.keySet()) {
      keys[i] = k; values[i] = a.get(k);
      i++;
    }

    boolean isSerializableArray = true;

    if (!isSerializableArray(keys)) {
      System.out.println("isSerializableMap: keys array is not serializable");
      isSerializableArray = false;
    }

    if (!isSerializableArray(values)) {
      System.out.println("isSerializableMap: values array is not serializable");
      isSerializableArray = false;
    }

    return isSerializableArray;
  }

  public static boolean isSerializedArray(String s) {
    if (Objects.isNull(s))
      throw new IllegalArgumentException("isSerializedArray: s is null");    
    if (s.length() == 0)
      throw new IllegalArgumentException("isSerializedArray: s is empty");    
    if (s.equals("Null")) return true;
    
    String regex = "array\\^(\\p{javaJavaIdentifierStart}"
        + "\\p{javaJavaIdentifierPart}*\\.)*\\p{javaJavaIdentifierStart}"
        + "\\p{javaJavaIdentifierPart}*\\^\\p{XDigit}+";
    
    if (! (s.matches(regex))) return false;
    
    Object a = null;
    
    try {
      a = deserializeArray(s);
    } catch (Exception e) {
      return false;
    }
    
    if (Objects.isNull(a) || !a.getClass().isArray()) return false;
    return true;
  }
  
  public static boolean isString(Class<?> c) {
    if (Objects.isNull(c)) 
      throw new IllegalArgumentException("isString: c is null");
    return c == java.lang.String.class;
  }
  
  public static boolean isString(String s) {
    if (Objects.isNull(s)) 
      throw new IllegalArgumentException("isString: s is null");
    if (s.length() == 0) 
      throw new IllegalArgumentException("isString: s is empty");
    return s.equals("java.lang.String");
  }
  
  public static boolean isSupportedClass(Class<?> c) {
    if (Objects.isNull(c))
      throw new IllegalArgumentException("isSupportedClass: c is null");
    
    Set<Class<?>> supported = new HashSet<>();
    
    Class<?> anClass = null;
   
    try {
      anClass = Class.forName("java.util.jar.Attributes$Name");
    } catch (ClassNotFoundException e) {}
    
    if (Objects.nonNull(anClass)) {
      supported.add(anClass);
    }
    
    supported.add(java.util.jar.Attributes.Name.class);
    supported.add(java.util.BitSet.class);
 
    
    return supported.contains(c) ? true : false;
  }
  
  public static boolean isSupportedCollection(Class<?> c) {
    if (Objects.isNull(c))
      throw new IllegalArgumentException("isSupportedCollection: c is null");    
    if (! isCollection(c)) return false;
    
    Set<String> notSupported = new HashSet<>();
    notSupported.add("java.beans.beancontext.BeanContextServicesSupport");
    notSupported.add("java.beans.beancontext.BeanContextSupport");
    notSupported.add("java.util.concurrent.ConcurrentHashMap.KeySetView");
    
    return notSupported.contains(c.getName()) ? false : true;
  }
  
  public static boolean isSupportedMap(Class<?> c) {
    if (Objects.isNull(c))
      throw new IllegalArgumentException("isSupportedMap: c is null");    
    if (! isMap(c)) return false;
    
    Set<String> notSupported = new HashSet<>();
    notSupported.add("java.security.AuthProvider");
    notSupported.add("java.util.IdentityHashMap");
    notSupported.add("javax.print.attribute.standard.PrinterStateReasons");
    notSupported.add("java.util.Properties");
    notSupported.add("java.security.Provider");
    notSupported.add("java.awt.RenderingHints");
    notSupported.add("javax.script.SimpleBindings");
    notSupported.add("javax.management.openmbean.TabularDataSupport");
    notSupported.add("javax.swing.UIDefaults");
    
    return  notSupported.contains(c.getName()) ? false : true;
  }
 
  public static void readFields(Object o) {
    if (Objects.isNull(o)) throw new IllegalArgumentException("o is null");
    System.out.println("(format: containingClassName.name, value, type, modifiers)");

    Object[] w = (Object[]) getFields(o.getClass(), "read");
    @SuppressWarnings("unchecked")
    List<Field> flist = (List<Field>) w[0];
    @SuppressWarnings("unchecked")
    Map<Field,Class<?>> fmap = (Map<Field,Class<?>>) w[1];
    
    for (Field f : flist) {
      String name = f.getName();
      String containingClassName = fmap.get(f).getName();
      Class<?> type = null;
      Object v = null;
      try {
        v = f.get(o);
      } catch (IllegalArgumentException | IllegalAccessException e1) {
        e1.printStackTrace();
      }
      type = f.getType();
      String listModifiers = listModifiers(f.getModifiers());
      if (type.isArray()) {
        System.out.print(containingClassName+"."+name+", "
            + type.getComponentType().getName());
        switch (type.getComponentType().getName()) {
        case "int":
          System.out.print(Arrays.toString((int[]) v));
          break;
        case "long":
          System.out.print(Arrays.toString((long[]) v));
          break;
        case "double":
          System.out.print(Arrays.toString((double[]) v));
          break;
        case "byte":
          System.out.print(Arrays.toString((byte[]) v));
          break;
        case "short":
          System.out.print(Arrays.toString((short[]) v));
          break;
        case "float":
          System.out.print(Arrays.toString((float[]) v));
          break;
        case "boolean":
          System.out.print(Arrays.toString((boolean[]) v));
          break;
        case "char":
          System.out.print(Arrays.toString((char[]) v));
          break;
        default:
          System.out.print(Arrays.toString((Object[]) v));
          break;
        }
        System.out.println(", " + listModifiers);
      } else {
        System.out.println(containingClassName+"."+name + ", " + v + ", " 
            + type.getName() + ", " + listModifiers);
      }
    }
  }
  
  public static String toHexString(byte[] array) {
    if (Objects.isNull(array))
      throw new IllegalArgumentException("toHexString: array is null");
    return DatatypeConverter.printHexBinary(array);
  }

  public static byte[] toByteArray(String s) {
    if (Objects.isNull(s))
      throw new IllegalArgumentException("toByteArray: s is null");
    if (s.length() == 0) 
      return new byte[0];    
    if (!(s.length() % 2 == 0 && s.matches("\\p{XDigit}+"))) {
      System.out.println("toByteArray: s is not a hex (lexicalXSDBase64Binary) String");
      return new byte[0]; 
    } 
    return DatatypeConverter.parseHexBinary(s);
  }
  
  @SuppressWarnings("unchecked")
  public static void main(String[] args) {
    
    // rely on @Serializable to determine serializability
    useAnnotation = true;
    
    // Annotated is annotated with @Serializable
    Annotated annotated = new Annotated();
    assert annotated.equals(serdes(annotated));
    
//     // NotAnnotated is not annotated with @Serializable
//     NotAnnotated notAnnotated = new NotAnnotated();
//     NotAnnotated notAnnotatedSerdes = (NotAnnotated)serdes(notAnnotated);
//     // produces IllegalArgumentException: serialize: serialize: cannot serialize object
    
    // rely on isSerializable() to determine serializability
    // by real time inspection without @Serializable
    useAnnotation = false;
    
    // if all tests pass the output will be "all tests passed"
    
    assert null == serdes(null);
    
    NotAnnotated notAnnotated2 = new NotAnnotated();
    assert(notAnnotated2.equals(serdes(notAnnotated2)));
    
    Integer one = new Integer(1);
    assert 1 == (int) serdes(one);   
    assert one.equals(serdes(one));
    
    assert "hello".equals(serdes("hello"));
    
    // Numbers is an Enum
    assert (Numbers.FIVE) == serdes(Numbers.FIVE);
    
    int[] inta = new int[]{1,2,3};
    assert Arrays.equals(inta, (int[]) serdes(inta));
    
    Integer[] integera = new Integer[]{1,2,3};
    assert Arrays.equals(integera, (Integer[]) serdes(integera));
    
    Attributes.Name an = new Attributes.Name("hello");
    assert an.equals(serdes(an));
    
    ArrayList<Integer> list = new ArrayList<>(Arrays.asList(1,2,3));
    assert list.equals(serdes(list));
    
    LinkedList<Float> ll = new LinkedList<>(Arrays.asList(1.1F, 2.2F, 3.3F));
    assert ll.equals(serdes(ll));
    
    ArrayBlockingQueue<Integer> abq = new ArrayBlockingQueue<>(3);
    try {
      abq.put(1); abq.put(2); abq.put(3);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    // separately created ArrayBlockingQueue instances that exist at 
    // the same time have different lock objects, however abq and 
    // serdes(abq) are essentially identical
    assert Arrays.equals(abq.toArray(),
        ((ArrayBlockingQueue<Integer>) serdes(abq)).toArray());
    
    // it's impossible to put elements into a SynchronousQueue for storage
    // must have a remove operation already initiated by another thread
    SynchronousQueue<Integer> sq = new SynchronousQueue<>();
    // this shows deserialization created a new SynchronousQueue instance
    assert Arrays.equals(sq.toArray(),
        ((SynchronousQueue<Integer>) serdes(sq)).toArray());
    
    HashSet<Double> set = new HashSet<>(Arrays.asList(1.1, 2.2, 3.3));
    assert set.equals(serdes(set));
    
    EnumSet<Numbers> enumSet = EnumSet.allOf(Numbers.class);
    assert enumSet.equals(serdes(enumSet));
    
    TreeSet<Short> ts = new TreeSet<>(Arrays.asList((short) 1, (short) 2, (short) 3));
    assert ts.equals(serdes(ts));
    
    ConcurrentSkipListSet<Byte> cset = 
        new ConcurrentSkipListSet<>(Arrays.asList((byte) 1, (byte) 7, (byte) 15));
    assert cset.equals(serdes(cset));
    
    Stack<Integer> stack = new Stack<>();
    stack.push(1); stack.push(2); stack.push(3);
    assert stack.equals(serdes(stack));
    
    Vector<Integer> v = new Vector<>(Arrays.asList(1, 2, 3));
    assert v.equals(serdes(v));
 
    HashMap<String,Integer> map = new HashMap<>();
    map.put("one", 1); map.put("two", 2); map.put("three", 3); 
    assert map.equals(serdes(map));
    
    Attributes attributes = new Attributes();
    Attributes.Name atOne = new Attributes.Name("one");
    Attributes.Name atTwo = new Attributes.Name("two");
    Attributes.Name atThree = new Attributes.Name("three");
    attributes.put(atOne, atOne.toString());
    attributes.put(atTwo, atTwo.toString());
    attributes.put(atThree, atThree.toString());
    assert attributes.equals(serdes(attributes));
    
    ConcurrentHashMap<String, Integer> chm = new ConcurrentHashMap<>();
    chm.put("one", 1); chm.put("two", 2); chm.put("three", 3); 
    assert chm.equals(serdes(chm));
    
    ConcurrentSkipListMap<String, Integer> cslm = new ConcurrentSkipListMap<>();
    cslm.put("one", 1); cslm.put("two", 2); cslm.put("three", 3); 
    assert cslm.equals(serdes(cslm));
    
    EnumMap <Numbers, Integer> em = new EnumMap<Numbers, Integer>(Numbers.class);
    em.put(Numbers.ONE, 1); em.put(Numbers.TWO, 2); em.put(Numbers.THREE, 3);
    assert em.equals(serdes(em));
    
    Hashtable<String,Integer> table = new Hashtable<>();
    table.put("one", 1); table.put("two", 2); table.put("three", 3); 
    assert table.equals(serdes(table));
    
    LinkedHashMap<String,Integer> lhm = new LinkedHashMap<>();
    lhm.put("one", 1); lhm.put("two", 2); lhm.put("three", 3); 
    assert lhm.equals(serdes(lhm));
    
    TreeMap<String,Integer> tmap = new TreeMap<>();
    tmap.put("one", 1); tmap.put("two", 2); tmap.put("three", 3); 
    assert tmap.equals(serdes(tmap));
    
    WeakHashMap<String,Integer> wmap = new WeakHashMap<>();
    wmap.put("one", 1); wmap.put("two", 2); wmap.put("three", 3); 
    assert wmap.equals(serdes(wmap));
    
    BitSet bset = new BitSet(); bset.set(1); bset.set(101); bset.set(1001);
    assert bset.equals(serdes(bset));
    
    // Empty is a class with no fields
    Empty empty = new Empty();
    assert empty.equals(serdes(empty));
    
    // A is a class with one field
    A a = new A(); a.setA(null);
    assert a.equals(serdes(a));
    a.setA("a string");
    assert a.equals(serdes(a));
    
    // B is a class with primitive, array, Set, List, Map and other object fields
    B b = new B();
    assert b.equals(serdes(b));
    //System.out.println("b="+b);
    //  b=B(z=1, d=2.2, bool=true, s=what, pstr=protected string, ia=[1, 2, 3], 
    //      sa=[one, two, three], ca=[a, null, c], set1=[1.1, 2.2, 3.3], 
    //      list1=[true, false, true], map1={one=1, two=2, three=3}, 
    //      a=A [a=hello a], c=C(c=c string, x=x string))
    
    // demos of saving and recovering a serialization string to and from a file
    
//    copyString2File(ser(b), "ser2/B.ser");
//    // ser2/B.ser contains 3,295 bytes
//    B bserdes = (B) des(copyFile2String("ser2/B.ser"));
//    assert b.equals(bserdes);
    
//    gzipString2File(ser(b), "ser2/B.ser.gz");
//    // ser2/B.ser.gz contains 1,252 bytes
//    B bserdes2 = (B) des(gunzipFile2String("ser2/B.ser.gz"));
//    assert b.equals(bserdes2);
     
    System.out.println("all tests passed");
  
  }

}

