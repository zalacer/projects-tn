package ejs;

import static java.lang.System.identityHashCode;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Base64.getDecoder;
import static java.util.Base64.getEncoder;
import static java.util.zip.GZIPInputStream.GZIP_MAGIC;
import static utils.ModifierUtils.hasPrivate;
import static utils.ModifierUtils.hasStatic;
import static utils.ModifierUtils.listModifiers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import annotations.Serializable;
import annotations.Transient;
import exceptions.ArraySerializationDataFormatException;
import exceptions.DataContentException;
import exceptions.DataFormatException;
import io.ByteArrayInputStream;
import io.ByteArrayOutputStream;
import io.DataInputStream;
import io.DataOutputStream;

public class Serialization {

  public Serialization(){}
  
  // useAnnotation's value toggles use of @Serializable for determining
  // serializability as implemented in isSerializable() and other methods
  // but these methods are no longer used automatically prior to 
  // serialization since they cause interference with global maps and
  // add overhead. This means that @Serializable is normally disregarded 
  // and the outcome of a serialization attempt shows if a class is actually 
  // serializable or not. However, the serializability test methods mentioned 
  // above may be used prior to serialization if there are any doubts 
  // regarding the serializability of a class.
  private boolean useAnnotation = false;

  public final boolean getUseAnnotation() {
    return useAnnotation;
  }

  public final boolean isUseAnnotation() {
    return useAnnotation;
  }

  public final void setUseAnnotation(boolean useAnnotation) {
    this.useAnnotation = useAnnotation;
  }

  private IdentityHashMap<Object, Integer> imap = null;
  private HashMap<Integer, Object> rimap = null;
  private HashMap<Integer, Integer> rmap = null;
  private IdentityHashMap<Object, HashMap<String, Integer>> read = null;
  private Integer oc = null;

  public final String ser(Object o) {
    imap = new IdentityHashMap<>();
    rmap = new HashMap<>();
    read = null;
    oc = -1;
    return serialize(o);
  }
  
  public final Object des(String s) {
    imap = new IdentityHashMap<>();
    rimap = new HashMap<>();
    // count is number of occurences of %Y in s
    int count = (s.length() - s.replaceAll("%Y", "").length())/2;
    String[] q = s.split("%Y");
    if (q.length == 1) { 
      rmap = new HashMap<>();
    } else if (q.length == 2) { // arrays, collections and sometimes maps
      if (count == 2) { // s has a trailing "%Y"
        rmap = createRmap(q[1].split("&")[0]);
      } else {
        rmap = createRmap(q[1]);
      }
    } else if (q.length == 3) { // maps
      String sp1 = q[1].split("&")[0];
      String sp2 = q[2];
      if (sp1.length() == 0 && sp2.length() == 0) {
        rmap = new HashMap<>();
      } else if (sp1.length() != 0 && sp2.length() == 0) {
        rmap = createRmap(sp1);
      } else if (sp1.length() == 0 && sp2.length() != 0) {
        rmap = createRmap(sp2);
      } else { 
        rmap = createRmap(sp1+","+sp2);
      }
    } else {
      throw new IllegalArgumentException("des: input serialization string has invalid"
        +" format with more than 3 components when split with %Y");
    
    }
    read = new IdentityHashMap<>();
    oc = -1;
    return deserialize(s);
  }

  public final Object serdes(Object o) {
    return des(ser(o));
  }

  @SuppressWarnings("unchecked")
  //  @SafeVarargs
  private final String serialize(Object o) {
    if (Objects.isNull(o))
      return serializeNull();
    
    ArrayList<String> list = new ArrayList<>();

    Class<?> oclass = o.getClass();
    String oclassName = oclass.getName();

    if (!(useAnnotation && hasSerializableAnnotation(oclass) || !useAnnotation))
      throw new IllegalArgumentException("serialize: cannot serialize o");

    if (isLambda(oclass))
      throw new IllegalArgumentException("serialize: lambda classes aren't supported");
    
    if (isInner(oclass))
      throw new IllegalArgumentException("serialize: inner classes aren't supported");
    
    if (oclass.isArray()) return serializeArray(o);
    if (isString(oclass)) return serializeString((String) o);
    if (isBoxed(oclass)) return serializePrimitiveOrBoxed(o, false);
    if (oclass.isEnum()) return serializeEnum(o);
    if (isCollection(oclass)) return serializeCollection(o);
    if (isMap(oclass)) return serializeMap(o, oclassName);
    if (isBitSet(oclass)) return serializeBitSet((BitSet) o);
    if (isAttributesName(oclass)) return serializeAttributesName((Attributes.Name) o);

    imap.put(o, ++oc);

    String obstring = "";
    ByteArrayOutputStream bout = new ByteArrayOutputStream(1000);
    DataOutputStream dout = new DataOutputStream(bout);
    StringBuilder sb = new StringBuilder();
    String name = null;
    Class<?> type = null;
    String typeName = null;
    String actualTypeName = null;
    Class<?> componentType = null;
    String componentTypeName = null;
    boolean isTransient = false;
    Object v = null;

    List<Field> flist = (List<Field>) getFields(oclass, "ser");

    if (flist.size() > 0) {
      for (Field f : flist) {
        if (hasPrivate(f.getModifiers()))
          f.setAccessible(true);
        if (f.isAnnotationPresent(Transient.class)) {
          isTransient = true;
        } else isTransient = false;
        name = f.getName();
        type = f.getType();

        try {
          v = f.get(o);
        } catch (IllegalArgumentException | IllegalAccessException e) {
          System.out.println("serialize: cannot get value for field " + name + "- skipping this field");
          e.printStackTrace();
          continue;
        }

        // This is to avoid interface types such as List and Map while 
        // preserving primitive types and to change the type of a map to 
        // its superclass for occurrences as a field value. Also supports
        // explicit non-support of fields of lambda and inner class typese.
        if (Objects.nonNull(v) && !isPrimitive(type)) {
          if (isMap(type)) {
            type = v.getClass().getSuperclass();
          } else if (!(isLambda(type) || isInner(type))) {
            type = v.getClass();
          } else if (isLambda(type)) {
            System.out.println("skipping field "+name+" because it's a lambda type");
            continue;
          } else if (isInner(type)) {
            System.out.println("skipping field "+name+" because its an inner class type");
            continue;
          }
        }

        typeName = type.getName();

        if (isEnum(type)) {
          actualTypeName = typeName;
          typeName = "enum";
        } else if (type.isArray()) {
          typeName = "array";
          componentType = type.getComponentType();
          componentTypeName = componentType.getName();
        } else if (isCollection(type)) {
          if (Objects.nonNull(v) && isSupportedCollection(type)) {
            actualTypeName = typeName;
            typeName = "collection";
          } else {
            System.out.println("serialize:" + oclassName + "." + name + " of type " 
                +typeName+" is an unsupported collection and won't be serialized");
            continue;
          }
        } else if (isMap(type)) {
          if (Objects.nonNull(v) && isSupportedMap(type)) {
            actualTypeName = typeName;
            typeName = "map";
          } else {
            System.out.println("serialize:" + oclassName + "." + name + " of type" 
                +typeName+ " is an unsupported map and won't be serialized");
            continue;
          }
        } else if (isBitSet(type)) {
          actualTypeName = typeName;
          typeName = "bitset";
        } else if (isAttributesName(oclass)) {
          actualTypeName = typeName;
          typeName = "attributesname";
        }

        if (!type.isPrimitive() && isTransient) v = null;
          
        // for null objects only list is updated and no data is written to dout
        if (Objects.isNull(v)) {
          if (typeName.equals("enum")) {
            list.add(name + ":enum:" + actualTypeName + ":Null");
          } else if (typeName.equals("array")) {
            list.add(name + ":array:" + componentTypeName + ":Null");
          } else if (typeName.equals("collection")) {
            list.add(name + ":collection:" + actualTypeName + ":Null");
          } else if (typeName.equals("map")) {
            list.add(name + ":map:" + actualTypeName + ":Null");
          } else if (typeName.equals("bitset")) {
            list.add(name + "java.util.BitSet:Null");
          } else if (typeName.equals("attributesname")) {
            list.add(name + "java.util.jar.Attributes.Name:Null");
          } else {
            list.add(name + ":" + typeName + ":Null");
          }
          continue;
        }
        
        if (typeName.equals("enum")) {
          @SuppressWarnings("rawtypes")
          String enumName = ((Enum) v).name();
          list.add(name + ":enum:" + actualTypeName + ":" + enumName);
          continue;
        }
 
        try {
          if (type.isPrimitive()) {
            switch (typeName) {
              case "int":
                if (isTransient) {
                  dout.writeInt(0);
                } else {
                  dout.writeInt(((Integer) v).intValue());
                }
                list.add(name + ":" + typeName);
                continue;
              case "long":
                if (isTransient) {
                  dout.writeLong(0);
                } else {
                  dout.writeLong(((Long) v).longValue());
                }
                list.add(name + ":" + typeName);
                continue;
              case "double":
                if (isTransient) {
                  dout.writeDouble(0);
                } else {                                                                       
                  dout.writeDouble(((Double) v).doubleValue());
                }
                list.add(name + ":" + typeName);
                continue;
              case "byte":
                if (isTransient) {
                  dout.writeByte(0);
                } else {
                  dout.writeByte(((Byte) v).byteValue());
                }
                list.add(name + ":" + typeName);
                continue;
              case "short":
                if (isTransient) {
                  dout.writeShort(0);
                } else {
                  dout.writeShort(((Short) v).shortValue());
                }
                list.add(name + ":" + typeName);
                continue;
              case "float":
                if (isTransient) {
                  dout.writeFloat(0);
                } else {
                  dout.writeFloat(((Float) v).floatValue());
                }
                list.add(name + ":" + typeName);
                continue;
              case "boolean":
                if (isTransient) {
                  dout.writeBoolean(false);
                } else {
                  dout.writeBoolean(((Boolean) v).booleanValue());
                }
                list.add(name + ":" + typeName);
                continue;
              case "char":
                if (isTransient) {
                  dout.writeChar('\0');
                } else {
                  dout.writeChar(((Character) v).charValue());
                }
                list.add(name + ":" + typeName);
                continue;
            }
          } else if (typeName.equals("java.lang.Integer")) {          
            if (imap.containsKey(v)) {
              rmap.put(++oc, imap.get(v));
            } else {
              dout.writeInt(((Integer) v).intValue());
              imap.put(v, ++oc);
            }
            list.add(name + ":" + typeName);
            continue;
          } else if (typeName.equals("java.lang.Long")) {
            if (imap.containsKey(v)) {
              rmap.put(++oc, imap.get(v));
            } else {
              dout.writeLong(((Long) v).longValue());
              imap.put(v, ++oc);
            }
            list.add(name + ":" + typeName);
            continue;
          } else if (typeName.equals("java.lang.Double")) {
            if (imap.containsKey(v)) {
              rmap.put(++oc, imap.get(v));
            } else {
              dout.writeDouble(((Double) v).doubleValue());
              imap.put(v, ++oc);
            }
            list.add(name + ":" + typeName);
            continue;
          } else if (typeName.equals("java.lang.Byte")) {
            if (imap.containsKey(v)) {
              rmap.put(++oc, imap.get(v));
            } else {
              dout.writeByte(((Byte) v).byteValue());
              imap.put(v, ++oc);
            }
            list.add(name + ":" + typeName);
            continue;
          } else if (typeName.equals("java.lang.Short")) {
            if (imap.containsKey(v)) {
              rmap.put(++oc, imap.get(v));
            } else {
              dout.writeShort(((Short) v).shortValue());
              imap.put(v, ++oc);
            }
            list.add(name + ":" + typeName);
            continue;
          } else if (typeName.equals("java.lang.Float")) {
            if (imap.containsKey(v)) {
              rmap.put(++oc, imap.get(v));
            } else {
              dout.writeFloat(((Float) v).floatValue());
              imap.put(v, ++oc);
            }
            list.add(name + ":" + typeName);
            continue;
          } else if (typeName.equals("java.lang.Boolean")) {
            if (imap.containsKey(v)) {
              rmap.put(++oc, imap.get(v));
            } else {
              dout.writeBoolean(((Boolean) v).booleanValue());
              imap.put(v, ++oc);
            }
            list.add(name + ":" + typeName);
            continue;  
          } else if (typeName.equals("java.lang.Character")) {
            if (imap.containsKey(v)) {
              rmap.put(++oc, imap.get(v));
            } else {
              dout.writeChar(((Character) v).charValue());
              imap.put(v, ++oc);
            }
            list.add(name + ":" + typeName);
            continue;  
          } else if (typeName.equals("java.lang.String")) {
            if (imap.containsKey(v)) {
              rmap.put(++oc, imap.get(v));
            } else {
              writeString((String) v, dout);
              imap.put(v, ++oc);
            }
            list.add(name + ":" + typeName);
            continue;  
          } else if (typeName.equals("array")) {
            if (imap.containsKey(v)) {
              rmap.put(++oc, imap.get(v));
            } else {
              writeString(serializeArray(v), dout);
            }
            list.add(name + ":array:" + componentTypeName);
            continue;  
          } else if (typeName.equals("collection")) {
            if (imap.containsKey(v)) {
              rmap.put(++oc, imap.get(v));
            } else {
              writeString(serializeCollection(v), dout);
            }
            list.add(name + ":collection:" + actualTypeName);
            continue;             
          } else if (typeName.equals("map")) {
            if (imap.containsKey(v)) {
              rmap.put(++oc, imap.get(v));
            } else {
              writeString(serializeMap(v, actualTypeName), dout);
            }
            list.add(name + ":map:" + actualTypeName);
            continue;             
          } else if (typeName.equals("bitset")) {
            if (imap.containsKey(v)) {
              rmap.put(++oc, imap.get(v));
            } else {
              writeString(serializeBitSet((BitSet) v), dout);
            }
            list.add(name + ":java.util.BitSet");
            continue;            
          } else if (typeName.equals("attributesname")) {
            if (imap.containsKey(v)) {
              rmap.put(++oc, imap.get(v));
            } else {
              writeString(serializeAttributesName((Attributes.Name) v), dout);
            }
            list.add(name + ":java.util.jar.Attributes.Name");
            continue;            
          } else {
            if (imap.containsKey(v)) {
              rmap.put(++oc, imap.get(v));
            } else {
              writeString(serialize(v), dout);
            }
            list.add(name + ":" + typeName);
            continue;
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      byte[] oBytes = null;

      try {
        dout.flush();
        bout.flush();
        oBytes = bout.toByteArray();
        dout.close();
      } catch (IOException e) {
        e.printStackTrace();
      }

      obstring = toB64String(oBytes);
    }

    sb.append(oclassName + "^" + obstring);
    for (int j = 0; j < list.size(); j++)
      sb.append("^" + list.get(j));
    sb.append("%Y"+rmapString());
    String out = sb.toString();
   
    return out;
  }

  @SuppressWarnings("unchecked")
  //  @SafeVarargs
  private final Object deserialize(String s) {
    if (Objects.isNull(s))
      throw new IllegalArgumentException("deserialize: s is null");
    if (s.length() == 0)
      throw new IllegalArgumentException("deserialize: s is empty");
    if (s.matches("Null"))
      return null;

    String[] q = s.split("%Y");
    String[] r = q[0].split("\\^");
    
    if (isPrimitiveOrBoxed(r[0])) return deserializePrimitiveOrBoxed(s);
    if (r[0].equals("java.lang.String")) return deserializeString(s);
    if (r[0].equals("array")) return deserializeArray(s);
    if (r[0].equals("enum")) return deserializeEnum(s);
    if (r[0].equals("collection")) return deserializeCollection(s);
    if (r[0].equals("map")) return deserializeMap(s);
    if (r[0].replaceAll("&.*", "").equals("java.util.BitSet")) return deserializeBitSet(s);
    if (r[0].equals("java.util.jar.Attributes.Name")) return deserializeAttributesName(s);
    
    oc++;
    rimap = invert(imap);
    if (rmap.containsKey(oc) && rimap.containsKey(rmap.get(oc)))
      return rimap.get(rmap.get(oc));
    
    boolean isOnlyInterface = false;

    Class<?> c = null;
    try {
      c = Class.forName(r[0]);
      if (c.isInterface() && getInterfaces(c).size() == 0)
        isOnlyInterface = true;
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException(
          "deserialize: could not get a class " 
              + "object from the first component of r=" + r[0]);
    }

    if (!isOnlyInterface)
      if (!hasNoArgConstructor(c))
        throw new IllegalArgumentException(
            "deserialize: the class " + c.getName() 
            + " serialized in s does not have a no-arg constructor");

    Object o = null;
    if (!isOnlyInterface)
      o = getInstance(c);
    if (Objects.isNull(o))
      throw new IllegalArgumentException("deserialize: the class " + c.getName()
          + " serialized in s cannot be instantiated with a no arg constructor");
    
    imap.put(o, oc);

    String hexString = "";
    if (r.length > 1) {
      hexString = r[1];
    } else
      return o;

    List<String> savedFieldInfo = new ArrayList<>();
    for (int i = 2; i < r.length; i++)
      savedFieldInfo.add(r[i]);

    Object[] w = (Object[]) getFields(c, "des");
    LinkedHashMap<String, Field> fmap = (LinkedHashMap<String, Field>) w[0];
    LinkedHashMap<String, String> cmap = (LinkedHashMap<String, String>) w[1];
    byte[] data = toByteArray(hexString);
    ByteArrayInputStream bin = new ByteArrayInputStream(data);
    DataInputStream din = new DataInputStream(bin);

    String[] d = null;

    for (int i = 0; i < savedFieldInfo.size(); i++) {
      d = savedFieldInfo.get(i).split(":");
      if (d.length < 2)
        throw new DataFormatException("deserialize: field data with less than 2 components: "
            + "d=" + Arrays.toString(d));

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
      boolean noCurrentField = false;
      boolean fIsPrimitive = false;
      boolean fIsArray = false;
      boolean skip = false;

      if (d[d.length - 1].equals("Null")) {
        isNull = true;
      }

      if (isPrimitive(d[1])) {
        isPrimitive = true;
        if (!(d.length == 2)) {
          System.out.println("deserialize: primitive " + fname + 
              " metaData with " + d.length + " components, should be 2: " 
              + Arrays.toString(d) + "\nskipping field " + fname);
          skip = true;
        }
        typeName = d[1];
      } else if (d[1].equals("array")) {
        isArray = true;
        typeName = "array";
        if (!(d.length == 3 || d.length == 4)) {
          System.out.println("deserialize: array field " + fname + " metaData with " 
              + d.length + " components, should be 3 or 4: " + Arrays.toString(d) 
              + "\nskipping field " + fname);
          skip = true;
        }
        componentTypeName = d[2];
      } else if (d[1].equals("enum")) {
        isEnum = true;
        if (!(d.length == 4 || d.length == 5)) {
          System.out.println("deserialize: enum field " + fname + " metaData" + "with " 
              + d.length + "components, should be 4 or 5: \n  " + Arrays.toString(d) 
              + "\nskipping field " + fname);
          skip = true;
        }
        typeName = d[2];
        enumName = d[3];
      } else if (d[1].equals("collection")) {
        isCollection = true;
        if (!(d.length == 3 || d.length == 4)) {
          System.out.println("deserialize: collection field " + fname + " metaData" 
              + "with " + d.length + "components, should be 3 or 4: \n  " 
              + Arrays.toString(d) + "\nskipping field " + fname);
          skip = true;
        }
        typeName = d[2];
      } else if (d[1].equals("map")) {
        isMap = true;
        if (!(d.length == 3 || d.length == 4)) {
          System.out.println("deserialize: map field " + fname + " metaData" + "with " 
              + d.length + "components, should be 3 or 4: \n  " + Arrays.toString(d) 
              + "\nskipping field " + fname);
          skip = true;
        }
        typeName = d[2];
      } else if (d[1].equals("java.util.BitSet")) {
        isBitSet = true;
        if (!(d.length == 2 || d.length == 3)) {
          System.out.println("deserialize: java.util.BitSet field " + fname + " metaData" 
              + "with " + d.length + "components, should be 2 or 3: \n  " 
              + Arrays.toString(d) + "\nskipping field " + fname);
          skip = true;
        }
        typeName = "java.util.BitSet";
      } else if (d[1].equals("java.util.jar.Attributes.Name")) {
        isAttributesName = true;
        if (!(d.length == 2 || d.length == 3)) {
          System.out.println("deserialize: java.util.jar.Attributes.Name field " + fname 
              + " metaData with " + d.length + "components, should be 2 or 3: \n  " 
              + Arrays.toString(d) + "\nskipping field " + fname);
          skip = true;
        }
        typeName = "java.util.jar.Attributes.Name";
      } else if (d.length == 2 || d.length == 3) {
        typeName = d[1];
//        isOther = true;
      } else if (d.length > 3) {
        System.out.println("deserialize: unrecognized metadata with more than 3 " 
            + "components for field " + fname + ": \n  " + Arrays.toString(d) 
            + "\nskipping field " + fname);
        skip = true;
      }

      if (fmap.containsKey(fname)) {
        f = fmap.get(fname);
      } else {
        System.out.println("deserialize: serialized field " + fname 
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

      // For current field compare serialized metadata with that from scan.
      // If there are discrepancies, skip serialization of the field. 

      if (!skip) {
        if (isArray && fIsArray) {
          if (isPrimitive(componentTypeName) || isPrimitive(fComponentTypeName)) {
            if (!componentTypeName.equals(fComponentTypeName)) {
              System.out.println("deserialize: array field " + fname 
                  + " componentType was " + componentTypeName
                  + " during serialization but now its " + fComponentTypeName);
              skip = true;
            }
          } else if (!(isPrimitive(componentTypeName) || isPrimitive(fComponentTypeName))) {
            if (!(componentTypeName.equals(fComponentTypeName)
                || getInterfaces(componentTypeName).contains(fComponentTypeName))) {
              System.out.println("deserialize: array field " + fname 
                  + " componentType was " + componentTypeName
                  + " during serialization but now its " + fComponentTypeName);
              skip = true;
            }
          }
        } else if (isPrimitive(typeName) || isPrimitive(fTypeName)) {
          if (!typeName.equals(fTypeName)) {
            System.out.println("deserialize: field " + fname + " was a " + typeName
                + " during serialization but now its a " + fTypeName);
            skip = true;
          } else if (!(isPrimitive || fIsPrimitive)) {
            if (!(typeName.equals(fTypeName) || getInterfaces(typeName).contains(fTypeName))) {
              System.out.println("deserialize: field " + fname + " was a " + typeName
                  + " during serialization but now its a " + fTypeName);
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
              + " in " + c + " object");
        } else {
          System.out.println("deserialize: skipping setting of superclass " 
              + cmap.get(fname) + " field " + fname + " in " + c + " object");
        }
      }
      
      // set field values
      try {
        if (isNull) {
          if (!skip)
            f.set(o, null);
          continue;
        } else if (isEnum) {
            if (!skip)
              f.set(o, getEnumConstant(typeName, enumName));
            continue;
        } else if (isPrimitive) {
          switch (typeName) {
            case "int":
              if (skip) {
                din.readInt();
              } else {
                f.setInt(o, din.readInt());
              }
              continue;
            case "long":
              if (skip) {
                din.readLong();
              } else {
                f.setLong(o, din.readLong());
              }
              continue;
            case "double":
              if (skip) {
                din.readDouble();
              } else {
                f.setDouble(o, din.readDouble());
              }
              continue;
            case "byte":
              if (skip) {
                din.readByte();
              } else {
                f.setByte(o, din.readByte());
              }
              continue;
            case "short":
              if (skip) {
                din.readShort();
              } else {
                f.setShort(o, din.readShort());
              }
              continue;
            case "float":
              if (skip) {
                din.readFloat();
              } else {
                f.setFloat(o, din.readFloat());
              }
              continue;
            case "boolean":
              if (skip) {
                din.readBoolean();
              } else {
                f.setBoolean(o, din.readBoolean());
              }
              continue;
            case "char":
              if (skip) {
                din.readChar();
              } else {
                f.setChar(o, din.readChar());
              }
              continue;
          }
        } else {
          ++oc;
          rimap = invert(imap);
          boolean hasRead = false;
          if (read.containsKey(o)) {
            if (read.get(o).containsKey(fname)) {
              if (read.get(o).get(fname) > 0)
                hasRead = true;
            } else
              read.get(o).put(fname, 0);
          } else {
            read.put(o, new HashMap<String, Integer>());
            read.get(o).put(fname, 0);
          }
          if (typeName.equals("java.lang.Integer")) {
            if (skip) {
              if (!hasRead) {
                din.readInt();
                read.get(o).put(fname, 1);
              }
            } else {
              if (rmap.containsKey(oc) && rimap.containsKey(rmap.get(oc))) {
                f.set(o, rimap.get(rmap.get(oc)));
              } else {
                Integer u = new Integer(din.readInt());
                imap.put(u, oc);
                f.set(o, u);
                read.get(o).put(fname, 1);
              }
            }
            continue;
          } else if (typeName.equals("java.lang.Long")) {
            if (skip) {
              if (!hasRead) {
                din.readLong();
                read.get(o).put(fname, 1);
              }
            } else {
              if (rmap.containsKey(oc) && rimap.containsKey(rmap.get(oc))) {
                f.set(o, rimap.get(rmap.get(oc)));
              } else {
                Long u = new Long(din.readLong());
                imap.put(u, oc);
                f.set(o, u);
                read.get(o).put(fname, 1);
              }
            }
            continue;
          } else if (typeName.equals("java.lang.Double")) {
            if (skip) {
              if (!hasRead) {
                din.readDouble();
                read.get(o).put(fname, 1);
              }
            } else {
              if (rmap.containsKey(oc) && rimap.containsKey(rmap.get(oc))) {
                f.set(o, rimap.get(rmap.get(oc)));
              } else {
                Double u = new Double(din.readDouble());
                imap.put(u, oc);
                f.set(o, u);
                read.get(o).put(fname, 1);
              }
            }
            continue;
          } else if (typeName.equals("java.lang.Byte")) {
            if (skip) {
              if (!hasRead) {
                din.readByte();
                read.get(o).put(fname, 1);
              }
            } else {
              if (rmap.containsKey(oc) && rimap.containsKey(rmap.get(oc))) {
                f.set(o, rimap.get(rmap.get(oc)));
              } else {
                Byte u = new Byte(din.readByte());
                imap.put(u, oc);
                f.set(o, u);
                read.get(o).put(fname, 1);
              }
            }
            continue;
          } else if (typeName.equals("java.lang.Short")) {
            if (skip) {
              if (!hasRead) {
                din.readShort();
                read.get(o).put(fname, 1);
              }
            } else {
              if (rmap.containsKey(oc) && rimap.containsKey(rmap.get(oc))) {
                f.set(o, rimap.get(rmap.get(oc)));
              } else {
                Short u = new Short(din.readShort());
                imap.put(u, oc);
                f.set(o, u);
                read.get(o).put(fname, 1);
              }
            }
            continue;
          } else if (typeName.equals("java.lang.Float")) {
            if (skip) {
              if (!hasRead) {
                din.readFloat();
                read.get(o).put(fname, 1);
              }
            } else {
              if (rmap.containsKey(oc) && rimap.containsKey(rmap.get(oc))) {
                f.set(o, rimap.get(rmap.get(oc)));
              } else {
                Float u = new Float(din.readFloat());
                imap.put(u, oc);
                f.set(o, u);
                read.get(o).put(fname, 1);
              }
            }
            continue;
          } else if (typeName.equals("java.lang.Boolean")) {
            if (skip) {
              if (!hasRead) {
                din.readBoolean();
                read.get(o).put(fname, 1);
              }
            } else {
              if (rmap.containsKey(oc) && rimap.containsKey(rmap.get(oc))) {
                f.set(o, rimap.get(rmap.get(oc)));
              } else {
                Boolean u = new Boolean(din.readBoolean());
                imap.put(u, oc);
                f.set(o, u);
                read.get(o).put(fname, 1);
              }
            }
            continue;
          } else if (typeName.equals("java.lang.Character")) {
            if (skip) {
              if (!hasRead) {
                din.readChar();
                read.get(o).put(fname, 1);
              }               
            } else {                
              if (rmap.containsKey(oc) && rimap.containsKey(rmap.get(oc))) {
                f.set(o, rimap.get(rmap.get(oc)));
              } else {
                Character u = new Character(din.readChar());
                imap.put(u, oc);
                f.set(o, u);
                read.get(o).put(fname, 1);
              }
            }
            continue;
          } else {
            if (skip) {
              if (!hasRead) {
                readString(din);
                read.get(o).put(fname, 1);
              }
            } else {
              if (rmap.containsKey(oc) && rimap.containsKey(rmap.get(oc))) {
                f.set(o, rimap.get(rmap.get(oc)));
              } else {
                if (typeName.equals("java.lang.String")) {
                  String u = readString(din);
                  imap.put(u, oc);
                  f.set(o, u);
                  read.get(o).put(fname, 1);
                  continue;
                } else {
                  Object u = null;
                  --oc;
                  if (isArray) {
                    u = deserializeArray(readString(din));
                  } else if (isCollection) {
                    u = deserializeCollection(readString(din));
                   } else if (isMap) {
                    u = deserializeMap(readString(din));
                   } else if (isBitSet) {
                    u = deserializeBitSet(readString(din));
                  } else if (isAttributesName) {
                    u = deserializeAttributesName(readString(din));
                   } else {
                    u = deserialize(readString(din));
                  }
                  if (Objects.nonNull(u)) {
                    f.set(o, u);
                    read.get(o).put(fname, 1);
                  }
                  continue;
                }
              }
            }
          }
        }
      } catch (IllegalArgumentException | IllegalAccessException | IOException e) {
        e.printStackTrace();
      }
    }
    
    try {
      din.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return o;
  }

  private final String serializeNull(Object... n) {
    return ("Null");
  }

  private final String serializePrimitiveOrBoxed(Object v, boolean primitive) {
    // primitive is a flag for controlling serialized output type
    if (Objects.isNull(v)) {
      if (!primitive) {
        return serializeNull();
      } else {
        throw new IllegalArgumentException("serializePrimitiveOrBoxed: v is null but primitive is true");
      }
    }

    Class<?> type = v.getClass();
    if (!isBoxed(type))
      throw new IllegalArgumentException(
          "serializePrimitiveOrBoxed: v is not a boxed object");

    ByteArrayOutputStream bout = new ByteArrayOutputStream(100);

    String typeName = type.getName();
    
    if (! primitive)  imap.put(v, ++oc);
      
    try (DataOutputStream dout = new DataOutputStream(bout)) {
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
    oBytes = bout.toByteArray();
    String obstring = toB64String(oBytes);
    String out = typeName + "^" + obstring;
    return out;
  }

  private final Object deserializePrimitiveOrBoxed(String s) {
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
        throw new DataContentException(
            "deserializePrimitiveOrBoxed: "+r[0]+" in s isn't a primitive or boxed type");
    }

    String typeName = type.getName();

    byte[] data = toByteArray(r[1]);
    ByteArrayInputStream bin = new ByteArrayInputStream(data);

    try (DataInputStream din = new DataInputStream(bin)) {
      if (din.available() > 0) {
        if (type.isPrimitive()) {
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
          }
        } else {
          oc++;
          rimap = invert(imap);
          if (rmap.containsKey(oc) && rimap.containsKey(rmap.get(oc)))
            return rimap.get(rmap.get(oc));
          switch (typeName) {
            case "java.lang.Integer":
               Integer x1 = new Integer(din.readInt());
               imap.put(x1, oc);
               return x1;
            case "java.lang.Long":
              Long x2 = new Long(din.readLong());
              imap.put(x2, oc);
              return x2;
            case "java.lang.Double":
              Double x3 = new Double(din.readDouble());
              imap.put(x3, oc);
              return x3;
            case "java.lang.Byte":
              Byte x4 = new Byte(din.readByte());
              imap.put(x4, oc);
              return x4;
            case "java.lang.Short":
              Short x5 = new Short(din.readShort());
              imap.put(x5, oc);
              return x5;
            case "java.lang.Float":
              Float x6 = new Float(din.readFloat());
              imap.put(x6, oc);
              return x6;
            case "java.lang.Boolean":
              Boolean x7 = new Boolean(din.readBoolean());
              imap.put(x7, oc);
              return x7;
            case "java.lang.Character":
              Character x8 = new Character(din.readChar());
              imap.put(x8, oc);
              return x8;
          }
        }
      } else {
        System.out.println("deserializePrimitiveOrBoxed: din is empty");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  private final String serializeString(String v) {
    if (Objects.isNull(v))
      return serializeNull();

    Class<?> type = v.getClass();
    if (!isString(type))
      throw new IllegalArgumentException("serializeString: v is not a string");

    ByteArrayOutputStream bout = new ByteArrayOutputStream(1000);
    DataOutputStream dout = new DataOutputStream(bout);

    imap.put(v, ++oc);
    writeString(v, dout);
    byte[] oBytes = null;

    try {
      dout.flush();
      bout.flush();
      oBytes = bout.toByteArray();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    
    String obstring = toB64String(oBytes);
    String out = "java.lang.String^" + obstring;
    return out;
  }

  private final Object deserializeString(String s) {
    if (Objects.isNull(s))
      throw new IllegalArgumentException("deserializeString: s is null");
    if (s.length() == 0)
      throw new IllegalArgumentException("deserializeString: s is empty");
    
    oc++;
    rimap = invert(imap);
    if (rmap.containsKey(oc) && rimap.containsKey(rmap.get(oc))) {
      return rimap.get(rmap.get(oc));
    }
    
    String[] r = s.split("\\^");
    int rlength = r.length;
    if (rlength == 1) {
      if (s.equals("Null"))
        return null;
      throw new IllegalArgumentException("deserializeString: String s has one " 
          + "component but isn't \"Null\"");
    }
    if (rlength != 2)
      throw new IllegalArgumentException("deserializeString: String s has " + rlength 
          + " components but should have 1 or 2");
    if (!r[0].matches("java.lang.String"))
      throw new DataContentException(
          "deserializeString: s doesn't begin with java.lang.String");
    if (r[1].equals("Null"))
      return null;

    byte[] data = toByteArray(r[1]);
    ByteArrayInputStream bin = new ByteArrayInputStream(data);
    DataInputStream din = new DataInputStream(bin);
    String x = readString(din);
    imap.put(x, oc);
    return x;
  }

  private final String serializeArray(Object v) {
    if (Objects.isNull(v)) return serializeNull();

    imap.put(v, ++oc);
    
    Class<?> type = v.getClass();
    if (!type.isArray())
      throw new IllegalArgumentException("serializeArray: v is not an array");

    Class<?> componentType = type.getComponentType();
    String componentTypeName = componentType.getName();

    ByteArrayOutputStream bout = new ByteArrayOutputStream(1000);

    int length = Array.getLength(v);
    
    try (DataOutputStream dout = new DataOutputStream(bout)) {
      dout.writeInt(length);
      String ser = null;
      Object o = null;
      if (length > 0) {
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
        } else if (componentType.isEnum()) {
          for (int i = 0; i < length; i++) {
            writeString(serializeEnum(Array.get(v, i)), dout);
          }    
        } else if (isBoxed(componentType)) {
          for (int i = 0; i < length; i++) {
            o = Array.get(v, i);
            if (imap.containsKey(o)) {
              rmap.put(++oc, imap.get(o));
            } else {
              ser = serializePrimitiveOrBoxed(o, false);
              writeString(ser, dout);
            }
          }
        } else if (isString(componentType)) {
          for (int i = 0; i < length; i++) {
            o = Array.get(v, i);
            if (imap.containsKey(o)) {
              rmap.put(++oc, imap.get(o));
            } else {
              ser = serializeString((String) o);
              writeString(ser, dout);
            }
          }
        } else {
          for (int i = 0; i < length; i++) {
            o = Array.get(v, i);
            if (imap.containsKey(o)) {
              rmap.put(++oc, imap.get(o));
            } else {
              ser = serialize(o);
              writeString(ser, dout);
            }
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

   byte[] oBytes = bout.toByteArray();
   String obstring = toB64String(oBytes);
   String out = "array^" + componentTypeName + "^" + obstring + "%Y"+ rmapString();
   return out;
  }

  private final Object deserializeArray(String s) {
    if (Objects.isNull(s))
      throw new IllegalArgumentException("deserializeArray: s is null");
    if (s.length() == 0)
      throw new IllegalArgumentException("deserializeArray: s is empty");
    if (s.equals("Null"))
      return null;
    
    oc++;
    rimap = invert(imap);
    if (rmap.containsKey(oc) && rimap.containsKey(rmap.get(oc))) {
      return rimap.get(rmap.get(oc));
    }

    String[] q = s.split("%Y");
    String[] r = q[0].split("\\^");
    int rlength = r.length;
    String c = r[0];
    if (!c.equals("array"))
      throw new IllegalArgumentException("deserializeArray: " 
          + "string s is not marked as a serialized array");
    if (rlength != 3)
      throw new IllegalArgumentException("deserializeArray: " + "string s has " + rlength 
          + " components but should have 3");
    if (r[1].length() == 0)
      throw new DataContentException("deserializeArray: string s has no " 
          + "componentType information");
    if (r[2].length() == 0)
      throw new DataContentException("deserializeArray: string s " 
          + "has no component data");
    
    String componentTypeName = r[1];
    Class<?> componentType = null;

    switch (componentTypeName) {
    case "int":
      componentType = int.class;
      break;
    case "long":
      componentType = long.class;
      break;
    case "double":
      componentType = double.class;
      break;
    case "byte":
      componentType = byte.class;
      break;
    case "short":
      componentType = short.class;
      break;
    case "float":
      componentType = float.class;
      break;
    case "boolean":
      componentType = boolean.class;
      break;
    case "char":
      componentType = char.class;
      break;
    default:
      try {
        componentType = Class.forName(componentTypeName);
      } catch (ClassNotFoundException e) {
        System.out.println("deserializeArray: cannot instantiate class for " 
            + "componentTypeName" + componentTypeName);
        e.printStackTrace();
      }
    }

    byte[] data = toByteArray(r[2]);
    ByteArrayInputStream bin = new ByteArrayInputStream(data);
    int length = 0;
    Object a = null;

    try (DataInputStream din = new DataInputStream(bin)) {
      if (din.available() > 0) {
        length = din.readInt();
      } else {
        throw new ArraySerializationDataFormatException(
            "deserializeArray: serialized data missing length for array");
      }

      a = Array.newInstance(componentType, length);
      if (length == 0) return a;
      
      imap.put(a, oc);
      
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
      } else if (componentType.isEnum()) {
        for (int j = 0; j < length; j++) {
          Object x = deserializeEnum(readString(din));   
          Array.set(a, j, x);
        }
      } else {
        for (int j = 0; j < length; j++) {
          ++oc;            
          rimap = invert(imap);
          if (rmap.containsKey(oc) && rimap.containsKey(rmap.get(oc))) {
            Array.set(a, j, rimap.get(rmap.get(oc)));
          } else {
            --oc;
            Object x = null;
            if (isBoxed(componentType)) {
              x = deserializePrimitiveOrBoxed(readString(din));
            } else if (isString(componentType)) {
              x = deserializeString(readString(din));
            } else {
              x = deserialize(readString(din));
            }
            Array.set(a, j, x);
          }
        }
      }
    } catch (ArraySerializationDataFormatException | IOException e) {
      System.out.println("deserializeArray: failed read on " + e.getClass().getName());
      e.printStackTrace();
    }
    
    return a;
  }

  @SuppressWarnings({ "rawtypes" })
  private final String serializeEnum(Object v) {
    if (Objects.isNull(v))
      return serializeNull();

    Class<?> type = v.getClass();

    if (!type.isEnum())
      throw new IllegalArgumentException("serializeEnum: v is not an Enum");

    String typeName = type.getName();

    String enumName = ((Enum) v).name();
    String out = "enum^" + typeName + "^" + enumName;

    return out;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private final Object deserializeEnum(String s) {
    if (Objects.isNull(s))
      throw new IllegalArgumentException("deserializeEnum: s is null");
    if (s.length() == 0)
      throw new IllegalArgumentException("deserializeEnum: s is empty");
    if (s.equals("Null"))
      return null;

    String[] r = s.split("\\^");
    int rlength = r.length;
    String c = r[0];
    if (!c.equals("enum"))
      throw new IllegalArgumentException("deserializeEnum: string s is not marked "
          + "as a serialized enum");
    if (rlength != 3)
      throw new IllegalArgumentException("deserializeEnum: String s has " + rlength 
          + " components but should have 3");
    if (r[1].length() == 0)
      throw new DataContentException("deserializeEnum: string s has no typeName information");
    if (r[2].length() == 0)
      throw new DataContentException("deserializeEnum: string s has no enumName information");

    String typeName = r[1];
    String enumName = r[2];

    Class<? extends Enum> enumClass = null;

    try {
      enumClass = (Class<? extends Enum>) Class.forName(typeName);
    } catch (ClassNotFoundException e1) {
      System.out.println("deserializeEnum: ClassNotFoundException for name " + typeName);
      e1.printStackTrace();
    }

    return Enum.valueOf(enumClass, enumName);
  }

  @SuppressWarnings({ "rawtypes" })
  private final String serializeCollection(Object v) {
    if (Objects.isNull(v)) return serializeNull();

    Class<?> type = v.getClass();

    if (!isCollection(type))
      throw new IllegalArgumentException("serializeCollection: v is not a collection");
    if (!isSupportedCollection(type))
      throw new IllegalArgumentException("serializeCollection: v's type, " + type 
          + ", is not a supported collection");

    String typeName = type.getName();

    Collection a = (Collection) v;
    int length = a.size();

    Object[] x = new Object[length];
    Iterator it = a.iterator();
    int j = 0;
    while (it.hasNext()) {
      x[j] = it.next();
      j++;
    }
    
    imap.put(v, ++oc);
    String ar = serializeArray(x);
    
    if (typeName.equals("java.util.concurrent.ArrayBlockingQueue")) {
      boolean policy = false;
      int capacity = 0;
      int done = 0;
      String name = null;
      Field[] fields = v.getClass().getDeclaredFields();
      for (Field f : fields) {
        name = f.getName();
        if (name.equals("lock")) {
          f.setAccessible(true);
          ReentrantLock rlock = null;
          try {
            rlock = (ReentrantLock) f.get(v);
          } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
          }
          policy = rlock.isFair();
          done++;
          if (done == 2) break;
        } else if (name.equals("items")) {
          f.setAccessible(true);
          Object[] array = null;
          try {
            array = (Object[]) f.get(v);
          } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
          }
          capacity = array.length;
          done++;
          if (done == 2) break;
        }
      }
      return "collection^"+typeName+"%Q"+capacity+","+policy+"&" + ar;
    } else  return "collection^" + typeName + "&" + ar;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private final Object deserializeCollection(String s) {
    if (Objects.isNull(s))
      throw new IllegalArgumentException("deserializeCollection: s is null");
    if (s.length() == 0)
      throw new IllegalArgumentException("deserializeCollection: s is empty");
    if (s.equals("Null"))
      return null;

    String[] r = s.split("\\^");
    int rlength = r.length;
    String c = r[0];
    if (!c.equals("collection"))
      throw new IllegalArgumentException("deserializeCollection: string s is not "  
          + "marked as a serialized collection");
    if (rlength != 4)
      throw new IllegalArgumentException(
          "deserializeCollection: String s has " + rlength + " components but should have 4");

    oc++;
    rimap = invert(imap);
    if (rmap.containsKey(oc) && rimap.containsKey(rmap.get(oc)))
      return rimap.get(rmap.get(oc));
    
    String typeName = null;
    if (r[1].contains("%Q")) {
      typeName = r[1].replaceFirst("%Q.*$", "");
      if (!typeName.equals("java.util.concurrent.ArrayBlockingQueue"))
        System.out.println("serialization contains %Q but typeName is "
            + typeName+" not java.util.concurrent.ArrayBlockingQueue");
    } else {
     typeName = r[1].replaceFirst("&.*$", "");
    }
    
    int capacity = 0;
    boolean policy = false;
    if (typeName.equals("java.util.concurrent.ArrayBlockingQueue")) {
      String[] data = r[1].replaceFirst("^.*%Q", "").replaceFirst("&.*$", "").split(",");
      capacity = new Integer(data[0]).intValue();
      policy = new Boolean(data[1]).booleanValue();   
    }
    
    String array = s.replaceFirst("^[^&]*&", "");
    
    // In order to handle cases of a collection which contains itself as an
    // element, serializeCollection puts the collection into imap first and 
    // then does the same with its array via serializeArray(). It's necessary 
    // to  preserve this order during deserialization. In order to accomplish 
    // this the system state is saved, then the array is deserialized in order
    // to determine the enumTypeName for the EnumSet instantiator and the 
    // state is restored. Then the output collection is instantiated without 
    // elements, the array is  deserialized again and the collection is populated 
    // and returned. 
    
    int ocsave = oc;
    IdentityHashMap<Object, Integer> imapsave = new IdentityHashMap<>(imap);
    rimap = invert(imap);
    HashMap<Integer, Object> rimapsave = new HashMap<>(rimap);
    HashMap<Integer, Integer> rmapsave = new HashMap<>(rmap);
    IdentityHashMap<Object, HashMap<String, Integer>> readsave = new IdentityHashMap<>(read);
    
    // inserting magic for collection object in imap
    Object magic = new Object();
    imap.put(magic, oc);
    
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
      if (enumTypeNames.size() == 1) {
        enumTypeName = enumTypeNames.iterator().next();
      } else {
        enumTypeName = "java.util.Enum";
      }
    }
    
    oc = ocsave;
    imap = new IdentityHashMap<>(imapsave);
    rimap = new HashMap<>(rimapsave);
    rmap = new HashMap<>(rmapsave);
    read = new IdentityHashMap<>(readsave);
    Collection coll = null;
    
    switch (typeName) {
    case "java.util.concurrent.ArrayBlockingQueue":
      coll = new ArrayBlockingQueue(capacity, policy);
      break;
    case "java.util.Stack":
      coll = new Stack();
      break;
    case "java.util.concurrent.SynchronousQueue":
      coll = new SynchronousQueue();
      break;
    case "java.util.RegularEnumSet":
    case "java.util.JumboEnumSet":
    case "java.util.EnumSet":
      Class<? extends Enum> enumType = null;
      try {
        enumType = (Class<? extends Enum>) Class.forName(enumTypeName);
      } catch (ClassNotFoundException e) {
        System.out.println("deserializeCollection: cannot find class extends " 
            + "enum for " + enumTypeName);
        e.printStackTrace();
      }
      coll = EnumSet.noneOf(enumType);
      break;
    default:
      Class<?> claz = null;
      try {
        claz = Class.forName(typeName);
      } catch (ClassNotFoundException e) {
        System.out.println("deserializeCollection: class instantiation for " 
            + typeName + " failed");
        e.printStackTrace();
        return null;
      }
      Constructor con = null;
      try {
        con = claz.getConstructor();
      } catch (NoSuchMethodException | SecurityException e) {
        System.out.println("deserializeCollection: constructor instatiation for " 
            + typeName + " failed ");
        e.printStackTrace();
        return null;
      }
      try {
        coll = (Collection) con.newInstance();
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
          | InvocationTargetException e) {
        System.out.println("deserializeCollection: new instance creation for " 
            + typeName + "failed");
        e.printStackTrace();
        return null;
      }
    }
    
    imap.put(coll, oc);
    
    a = (Object[]) deserializeArray(array);

    // populate the appropriate collection with elements from a
    switch (typeName) {
    case "java.util.concurrent.ArrayBlockingQueue":
      for (Object o : a)
        try {
          ((ArrayBlockingQueue) coll).put(o);
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        }
      return coll;
    case "java.util.Stack":
      for (Object o : a)
        ((Stack) coll).push(o);
      return coll;
    case "java.util.concurrent.SynchronousQueue":
      for (Object o : a)
        try {
          ((SynchronousQueue) coll).put(o);
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        }
      return coll;
    case "java.util.RegularEnumSet":
    case "java.util.JumboEnumSet":
    case "java.util.EnumSet":
      for (Object o : a)
        coll.add((Enum) getEnumConstant(enumTypeName, ((Enum) o).name()));
      return coll;
    default:
      for (Object o : a)
        coll.add(o);
      return coll;
    }
  }

  @SuppressWarnings({ "rawtypes" })
  private final String serializeMap(Object v, String... className) {
    if (Objects.isNull(v)) return serializeNull();
        
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

    if (!isMap(type))
      throw new IllegalArgumentException("serializeMap: v is not a map");

    if (!isSupportedMap(type))
      throw new IllegalArgumentException("serializeMap: v's type, " + type 
          + ", is not a supported map");

    imap.put(v, ++oc);

    Map a = (Map) v;
    int length = a.size();

    String ar1 = "";
    String ar2 = "";
    int j;

    switch (typeName) {
    case "java.util.jar.Attributes":
      // Keys have type java.util.jar.Attributes$Name and
      // can be stringified with toString; values are strings
      // identical to corresponding keys stringified.
      // This means that in the following method atKeys[]
      // and atValues[] will be identical, which is redundant,
      // but proceeding this way to preserve the expected 
      // serialization format for maps. Since the arrays are
      // identical, only the 1st will be deserialize while the
      // 2nd will be retrieved from rimap when its object ID (oc)
      // is found as a key in rmap.
      
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
    String out = "map^" + typeName + "&" + ar1 + "&" + ar2;
    return out;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private final Map deserializeMap(String s) {
    if (Objects.isNull(s))
      throw new IllegalArgumentException("deserializeMap: s is null");
    if (s.length() == 0)
      throw new IllegalArgumentException("deserializeMap: s is empty");
    if (s.equals("Null"))
      return null;

//    String[] r = s.split("\\^");
//    int rlength = r.length;
    
    String[] p = s.split("%Y");
    String q = p[0]+"&"+p[1].split("&")[1];
    String[] r = q.split("\\^");
    int rlength = r.length;

    if (!r[0].equals("map"))
      throw new IllegalArgumentException("deserializeMap: string s is not marked "
          + "as a serialized map but as a " + r[0]);

    if (rlength != 6)
      throw new IllegalArgumentException("deserializeMap: String s has " + rlength 
          + " components but should have 6");
    
    oc++;
    rimap = invert(imap);
    if (rmap.containsKey(oc) && rimap.containsKey(rmap.get(oc))) {
      return (Map) rimap.get(rmap.get(oc));
    }

    String typeName = r[1].replaceFirst("&.*$", "");

    String[] arrays = q.replaceFirst("^[^&]*&", "").split("&");
    int alength = arrays.length;

    if (alength != 2)
      throw new IllegalArgumentException(
          "deserializeMap: there are " + alength 
              + " serialized arrays embedded in s, but there should be 2");
    
    Map<Enum, Object> hmap = null;
    Class<?> claz = null;
    Map map = null;

    switch (typeName) {
    case "java.util.EnumMap":
      hmap = new HashMap<>();
      imap.put(hmap, ++oc);
      break;
    default:
      try {
        claz = Class.forName(typeName);
      } catch (ClassNotFoundException e) {
        System.out.println("deserializeMap: class instantiation for "
            + typeName + " failed");
        e.printStackTrace();
        return null;
      }
      try {
        map = (Map) claz.newInstance();
        imap.put(map, oc);
      } catch (InstantiationException | IllegalAccessException 
          | IllegalArgumentException e) {
        System.out.println("deserializeMap: new instance creation for " 
            + typeName + " failed");
        e.printStackTrace();
        return null;
      }
      break;
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
      // Replacing atKeys and atValues with keys and values in imap.
      // This is a side effect of the des process for Attributes only.
      int ock = 0; int ocv = 0;
      boolean gotOck = false; boolean gotOcv = false;
      if (imap.keySet().contains(atKeys)) {
        ock = imap.get(atKeys);
        gotOck = true;
      }
      if (imap.keySet().contains(atValues)) {
        ocv = imap.get(atValues);
        gotOcv = true;
      }
      if (gotOck && gotOcv) {
        imap.remove(atKeys);
        imap.remove(atValues);
        imap.put(keys, ock);
        imap.put(values, ocv);
        rimap = invert(imap); 
      } else {
        System.out.println("deserializeArray: for Attributes.Name processing "
            + "could not retrieve initial arrays from imap");
        
      }
      break;
    default:
      keys = (Object[]) deserializeArray(arrays[0]);
      values = (Object[]) deserializeArray(arrays[1]);
    }

    int length = 0;

    if (keys.length != values.length) {
      throw new DataFormatException("deserializeMap: the deserialized keys and values "
          + "arrays have different lengths (" + keys.length + " and " + values.length 
          + ") for a " + typeName);
    } else
      length = keys.length;

    switch (typeName) {
    case "java.util.EnumMap":
      for (int i = 0; i < length; i++)
        hmap.put((Enum) keys[i], values[i]);
      return new EnumMap(hmap);
    default:
      for (int i = 0; i < length; i++)
        map.put(keys[i], values[i]);
      return map;
    }
  }

  private final String serializeBitSet(BitSet v) {
    if (Objects.isNull(v)) return serializeNull();

    Class<?> type = v.getClass();
    if (!isBitSet(type))
      throw new IllegalArgumentException("serializeBitSet: v is not a BitSet");

    imap.put(v, ++oc);
    byte[] bytes = ((BitSet) v).toByteArray();
    String a = toB64String(bytes);
    return "java.util.BitSet&" + a;
  }

  private final BitSet deserializeBitSet(String s) {
    if (Objects.isNull(s))
      throw new IllegalArgumentException("deserializeBitSet: s is null");
    if (s.length() == 0)
      throw new IllegalArgumentException("deserializeBitSet: s is empty");
    if (s.equals("Null"))
      return null;

    String[] r = s.split("\\&");
    int rlength = r.length;

    if (!r[0].equals("java.util.BitSet"))
      throw new IllegalArgumentException("deserializeBitSet: string s is not " 
          + "marked as a serialized bitset but as a " + r[0]);

    if (rlength != 2)
      throw new IllegalArgumentException("deserializeBitSet: String s has " + rlength 
          + " components but should have 2");

    oc++;
    rimap = invert(imap);
    if (rmap.containsKey(oc) && rimap.containsKey(rmap.get(oc)))
      return (BitSet) rimap.get(rmap.get(oc));

    String array = r[1];

    BitSet bitSet = new BitSet();
    byte[] bytes = toByteArray(array);
    int size = (bytes.length * 8) - 7;
    for (int i = 0; i <= size; i++)
      if ((bytes[i / 8] & (1 << (i % 8))) != 0)
        bitSet.set(i);
    
    imap.put(bitSet, oc);
    return bitSet;
  }

  private final String serializeAttributesName(Attributes.Name v) {
    if (Objects.isNull(v)) return serializeNull();

    String vString = v.toString();
    imap.put(vString, ++oc);

    ByteArrayOutputStream bout = new ByteArrayOutputStream(1000);
    DataOutputStream dout = new DataOutputStream(bout);
    writeString(vString, dout);
    byte[] oBytes = null;

    try {
      dout.flush();
      bout.flush();
      oBytes = bout.toByteArray();
    } catch (IOException e) {
      e.printStackTrace();
    }

    String obstring = toB64String(oBytes);
    String out = "java.util.jar.Attributes.Name^" + obstring;

    return out;
  }

  private final Name deserializeAttributesName(String s) {
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
        throw new IllegalArgumentException("deserializeAttributesName: String s "
            + "has one component but isn't \"Null\"");
      }
    if (rlength != 2)
      throw new IllegalArgumentException("deserializeAttributesName: String s has " + rlength 
          + " components but should have 1 or 2");
    if (!r[0].matches("java.util.jar.Attributes.Name"))
      throw new DataContentException("deserializeString: s doesn't begin with "
          + "java.util.jar.Attributes.Name");
    if (r[1].equals("Null"))
      return null;
    
    oc++;
    rimap = invert(imap);
    if (rmap.containsKey(oc) && rimap.containsKey(rmap.get(oc)))
      return (Name) rimap.get(rmap.get(oc));

    byte[] data = toByteArray(r[1]);
    ByteArrayInputStream bin = new ByteArrayInputStream(data);
    DataInputStream din = new DataInputStream(bin);
    String x = null;
    x = readString(din);
    Name n = new Attributes.Name(x);
    imap.put(n, oc);
    return n;
  }

  private final void writeString(String s, DataOutputStream dout) {
    // This is to overcome the 64KB limit of DataOutputStream.writeUTF.
    byte[] b = s.getBytes();
    try {
      dout.writeInt(b.length);
      dout.write(b, 0, b.length);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private final String readString(DataInputStream din) {
    // This reads data written by writeString(String, DataOutputStream)
    int len = 0;
    byte[] b = null;
    try {
      len = din.readInt();
      b = new byte[len];
      din.readFully(b, 0, len);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new String(b);
  }

  public final static void copyString2File(String s, String pathName) {
    Path p = Paths.get(pathName);
    if (Files.exists(p)) {
      System.out.println("copyString2File: overwriting " + pathName);
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
      e.printStackTrace();
    }
  }

  public static final String copyFile2String(String pathName) {
    Path p = Paths.get(pathName);
    if (!Files.exists(p)) {
      System.out.println("copyFile2String: " + pathName + " does not exist");
      return "";
    }

    String output = null;
    try {
      output = new String(Files.readAllBytes(p), UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
      return "";
    }

    return Objects.isNull(output) ? "" : output;
  }

  public static final String gzip(String data) {
    while (data.length() % 2 == 0 && data.matches("\\p{XDigit}+"))
      data = new String(toByteArray(data));
    ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length());
    try (GZIPOutputStream gzip = new GZIPOutputStream(bos)) {
      gzip.write(data.getBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return toB64String(bos.toByteArray());
  }

  public static final String gunzip(String compressed) {
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
    if (!isGzip(ba))
      return compressed;

    // otherwise decompress it with GZIPInputStream
    ByteArrayInputStream bis = new ByteArrayInputStream(ba);
    StringBuilder sb = new StringBuilder();
    String line = null;
    try (GZIPInputStream gis = new GZIPInputStream(bis);
        BufferedReader br = new BufferedReader(new InputStreamReader(gis, "UTF-8"))) {
      while ((line = br.readLine()) != null)
        sb.append(line);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return sb.toString();
  }

  public static final void gzipString2File(String s, String pathName) {
    Path p = Paths.get(pathName);
    if (Files.exists(p)) {
      System.out.println("gzipString2File: overwriting " + pathName);
    } else {
      try {
        Files.createFile(p);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    String gzipped = gzip(s);

    try {
      Files.write(p, gzipped.getBytes(UTF_8));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static final String gunzipFile2String(String pathName) {
    Path p = Paths.get(pathName);
    if (!Files.exists(p)) {
      System.out.println("gunzipFile2String: " + pathName + " does not exist");
      return "";
    }

    String output = null;
    try {
      output = new String(Files.readAllBytes(p), UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (Objects.nonNull(output))
      output = gunzip(output);

    return Objects.isNull(output) ? "" : output;
  }

  private static final boolean isGzip(byte[] bytes) {
    // https://www.javacodegeeks.com/2015/01/working-with-gzip-and-compressed-data.html
    return bytes[0] == (byte) GZIP_MAGIC && bytes[1] == (byte) (GZIP_MAGIC >>> 8);
  }

  public static final String encrypt(String plaintext, String pwd) {
    // This does 256 bit AES encryption and requires installation of the Java 
    // Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files.
    // As of 20160325, for Java 8 they can be downloaded from 
    // http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html

    // construct password from string, salt from SecureRandom
    // and then secret key from them
    SecureRandom random = new SecureRandom();
    byte[] salt = new byte[4];
    random.nextBytes(salt);
    char[] password = pwd.toCharArray();

    SecretKeyFactory factory = null;
    try {
      factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
    } catch (NoSuchAlgorithmException e1) {
      e1.printStackTrace();
    }

    KeySpec spec = new PBEKeySpec(password, salt, 71719, 256);

    SecretKey sec = null;
    try {
      sec = factory.generateSecret(spec);
    } catch (InvalidKeySpecException e1) {
      e1.printStackTrace();
    }

    sec = new SecretKeySpec(sec.getEncoded(), "AES");

    // Encrypt a string
    Cipher cipher = null;
    try {
      cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
      e.printStackTrace();
    }
    try {
      cipher.init(Cipher.ENCRYPT_MODE, sec);
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    }
    byte[] iv = null;
    AlgorithmParameters params = cipher.getParameters();
    try {
      iv = params.getParameterSpec(IvParameterSpec.class).getIV();
    } catch (InvalidParameterSpecException e) {
      e.printStackTrace();
    }
    byte[] ciphertext = null;
    try {
      ciphertext = cipher.doFinal(plaintext.getBytes("UTF-8"));
    } catch (IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    return b64encode(iv) + "&Z" + b64encode(ciphertext) + "&X" + b64encode(sec.getEncoded());
  }

  public final static String decrypt(String s) {
    // This does 256 bit AES decryption and requires installation of the Java 
    // Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files.
    // As of 20160325, for Java 8 they can be downloaded from 
    // http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html

    String[] b = s.split("&Z");
    byte[] iv = b64decode(b[0]);
    byte[] ciphertext = b64decode(b[1]);
    byte[] encodedSecret = b64decode(b[2]);
    SecretKey secret = new SecretKeySpec(encodedSecret, "AES");

    Cipher cipher = null;
    try {
      cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
      e.printStackTrace();
    }
    try {
      cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
    } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
      e.printStackTrace();
    }
    String plaintext = null;
    try {
      plaintext = new String(cipher.doFinal(ciphertext), "UTF-8");
    } catch (UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException e) {
      e.printStackTrace();
    }
    return plaintext;
  }

  public static final void encryptString2File(String data, String pathName, String pwd) {
    String defaultSuffix = ".aes";
    String defaultKeyDir = "keys";
    String defaultKeySuffix = ".key";
    String pWithoutSuffix = replaceLast(pathName, "\\..*", "");
    String suffix = pathName.replaceFirst("^" + pWithoutSuffix, "");
    String pstr = pWithoutSuffix;
    if (suffix.length() > 0)
      pstr = pstr + suffix;
    else
      pstr = pstr + defaultSuffix;
    if (!createFile(pstr))
      return;
    Path p = Paths.get(pstr);
    String kfname = replaceLast(Paths.get(pstr).getFileName().toString(), "\\..*", "");
    String kfstr = defaultKeyDir + "/" + kfname + defaultKeySuffix;
    if (!createFile(kfstr))
      return;
    Path kf = Paths.get(kfstr);

    String[] crypt = encrypt(gzip(data), pwd).split("&X");
    if (crypt.length != 2) {
      System.out.println("encryptString2File: incorrect encrpytion output");
      return;
    }

    try {
      Files.write(p, crypt[0].getBytes(UTF_8));
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("encryptString2File: could not write to " + p);
      return;
    }

    try {
      Files.write(kf, crypt[1].getBytes(UTF_8));
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("encryptString2File: could not write to " + kf);
      return;
    }
  }

  public static final String decryptFile2String(String path, String... keyPath) {
    String defaultKeyDir = "keys";
    String defaultKeySuffix = ".key";
    Path p = Paths.get(path);
    if (!Files.exists(p)) {
      System.out.println("decryptFile2String: " + path + " does not exist");
      return "";
    }

    String keyPathName = null;
    if (Objects.nonNull(keyPath) && keyPath.length > 0) {
      keyPathName = keyPath[0];
    } else {
      String kfname = replaceLast(p.getFileName().toString(), "\\..*", "");
      keyPathName = defaultKeyDir + "/" + kfname + defaultKeySuffix;
    }
    Path kp = Paths.get(keyPathName);

    String d1 = null;
    try {
      d1 = new String(Files.readAllBytes(p), UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("decryptFile2String: cannot read " + p);
      return null;
    }

    String d2 = null;
    try {
      d2 = new String(Files.readAllBytes(kp), UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("decryptFile2String: cannot read " + kp);
      return null;
    }

    return gunzip(decrypt(d1 + "&Z" + d2));
  }

  private static final boolean createFile(String s) {
    Path p = Paths.get(s);
    Path parent = p.getParent();
    if (Files.exists(p)) {
      String newPath = p.toString() + "." + now();
      try {
        p.toFile().renameTo(Paths.get(newPath).toFile());
        System.out.println("encryptString2File: renamed existing file " + p 
            + " to " + newPath);
        if (Objects.nonNull(parent)) Files.createDirectories(parent);
        Files.createFile(p);
      } catch (IOException e) {
        e.printStackTrace();
        System.out.println("encryptString2File: could not rename existingfile " 
            + p + " to " + newPath + " and create a new file in its place");
        return false;
      }
    } else
      try {
        if (Objects.nonNull(parent)) Files.createDirectories(parent);
        Files.createFile(p);
      } catch (IOException e) {
        e.printStackTrace();
        System.out.println("encryptString2File: could not create file " + p);
        return false;
      }

    return true;
  }

  private final static String b64encode(byte[] b) {
    return getEncoder().withoutPadding().encodeToString(b);
  }
  
  private final static String toB64String(byte[] b) {
    return getEncoder().withoutPadding().encodeToString(b);
  }

  @SuppressWarnings("unused")
  private final static String b64encode(String s) {
    return getEncoder().withoutPadding().encodeToString(s.getBytes());
  }

  private final static byte[] b64decode(String s) {
    return getDecoder().decode(s);
  }
  
  private final static byte[] toByteArray(String s) {
    return getDecoder().decode(s);
  }

  @SuppressWarnings("unused")
  private final static String b64decode2String(String s) {
    return new String(getDecoder().decode(s));
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private final Enum getEnumConstant(String typeName, String enumName) {
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

    return Enum.valueOf(enumClass, enumName);
  }

  private final Object getFields(Class<?> c, String s) {
  // this method consolidates map generation for 5 other methods
  // method              requires
  // =================== =====================================================================
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

    if (!(s.matches("des.*") || s.matches("ser.*") || s.matches("get.*") 
        || s.matches("read.*") || s.matches("is.*")))
      throw new IllegalArgumentException("getFields: : s doesn't match des.*, "
          + "ser.*, read.* or is.*)");

    if (c.isArray())
      throw new IllegalArgumentException("getFields: c is an Array class "
          + "which isn't allowed");

    String name = null;
    List<Field> flist = new ArrayList<>(); // 4 serialize
    LinkedHashMap<String, Field> fmap = new LinkedHashMap<>(); // 4 deserialize
    LinkedHashMap<String, String> cmap = new LinkedHashMap<>(); // 4 deserialize
    List<Field> rlist = new ArrayList<>(); // 4 readFields
    Map<Field, Class<?>> qmap = new HashMap<>(); // 4 readFields
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
      if (!fmap.containsKey(name)) {
        rlist.add(f);
        qmap.put(f, c);
        if (!hasStatic) {
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
          if (hasPrivate(f.getModifiers()))  hasPrivate = true;
          f.setAccessible(true);
          name = f.getName();
          if (!fmap.containsKey(name)) {
            rlist.add(f);
            qmap.put(f, sclass);
            if (!(hasStatic || hasPrivate)) {
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
    if (s.matches("des.*"))  return new Object[] { fmap, cmap };
    if (s.matches("read.*")) return new Object[] { rlist, rmap };
    if (s.matches("is.*"))   return imap;

    return null;
  }

  private final Object getInstance(Class<?> c) {
    if (Objects.isNull(c)) throw new IllegalArgumentException("c is null");
    try {
      Constructor<?> con1 = c.getConstructor();
      Object inst1 = con1.newInstance();
      return inst1;
    } catch (NoSuchMethodException | SecurityException | InstantiationException 
        | IllegalAccessException | IllegalArgumentException 
        | InvocationTargetException e1) {
      try {
        Constructor<?> con2 = c.getDeclaredConstructor();
        Object inst2 = con2.newInstance();
        return inst2;
      } catch (NoSuchMethodException | SecurityException | InstantiationException 
          | IllegalAccessException | IllegalArgumentException 
          | InvocationTargetException e2) {
        e2.printStackTrace();
      }
    }
    return null;
  }

  public final List<String> getInterfaces(Class<?> c) {
    if (Objects.isNull(c))
      throw new IllegalArgumentException("getInterfaces(Class<?>): c is null");

    Set<String> z = new HashSet<>();
    String name = null;

    while (c != null) {
      Class<?>[] itfs = c.getInterfaces();
      for (Class<?> x : itfs) {
        name = x.getName();
        // exclude marker and irrelevant interfaces
        if (!(name.equals("java.io.Serializable") 
            || name.equals("java.lang.Cloneable")
            || name.equals("java.lang.Comparable") // not a marker interface
            || name.equals("java.util.RandomAccess") 
            || name.equals("java.util.EventListener")
            || name.equals("java.rmi.Remote") 
            || name.equals("javax.security.auth.callback.Callback")
            || name.equals("javax.security.auth.login.Configuration.Parameters")
            || name.equals("java.util.concurrent.CompletableFuture.AsynchronousCompletionTask"))) {
          z.add(name);
        }
      }
      c = c.getSuperclass();
    }

    return new ArrayList<String>(z);
  }

  public final List<String> getInterfaces(String s) {
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

  private final boolean hasNoArgConstructor(Class<?> c) {
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

  private final boolean hasSerializableAnnotation(Class<?> c) {
    if (Objects.isNull(c)) throw new IllegalArgumentException("c is null");
    if (c.isAnnotationPresent(Serializable.class))
      return true;
    return false;
  }

  private final boolean isAttributesName(Class<?> c) {
    if (Objects.isNull(c)) throw new IllegalArgumentException("isAttributesName: c is null");
    if (c == java.util.jar.Attributes.Name.class)
      return true;
    return false;
  }

  private final boolean isBitSet(Class<?> c) {
    if (Objects.isNull(c)) throw new IllegalArgumentException("isMap: c is null");
    if (c.getName().equals("java.util.BitSet"))
      return true;
    return false;
  }

  private final boolean isBoxed(Class<?> c) {
    if (Objects.isNull(c))
      throw new IllegalArgumentException("isBoxed: c is null");
    String typeName = c.getName();
    return isBoxed(typeName);
  }

  private final boolean isBoxed(String s) {
    if (Objects.isNull(s)) throw new IllegalArgumentException("isBoxed: s is null");
    if (s.length() == 0)
      throw new IllegalArgumentException("isBoxed: s is empty");
    return s.matches("|java.lang.Integer|java.lang.Long|java.lang.Double"
        + "|java.lang.Byte|java.lang.Short|java.lang.Float" 
        + "|java.lang.Boolean|java.lang.Character") ? true : false;
  }

  private final boolean isCollection(Class<?> c) {
    if (Objects.isNull(c)) throw new IllegalArgumentException("isCollection: c is null");
    return getInterfaces(c).contains("java.util.Collection") ? true : false;
  }

  private final boolean isEnum(Class<?> c) {
    if (Objects.isNull(c)) throw new IllegalArgumentException("isEnum: c is null");
    return c.isEnum();
  }

  private final boolean isMap(Class<?> c) {
    if (Objects.isNull(c)) throw new IllegalArgumentException("isMap: c is null");
    return c.getName().equals("java.util.Map") 
        || getInterfaces(c).contains("java.util.Map") ? true : false;
  }

  private final boolean isLambda(Class<?> c) {
    // based on xstream-1.4.9 com.thoughtworks.xstream.core.util.Types.isLambdaType
    if (Objects.isNull(c)) throw new IllegalArgumentException("isLambda: c is null");
    String regex = (".*\\$\\$Lambda\\$[0-9]+/.*");
    return c.isSynthetic() && c.getSimpleName().matches(regex);
  }

  private final boolean isInner(Class<?> c) {
    if (Objects.isNull(c)) throw new IllegalArgumentException("isInner: c is null");
    String regex = "^(\\p{javaJavaIdentifierStart}" 
        + "\\p{javaJavaIdentifierPart}*)\\.\\p{javaJavaIdentifierStart}"
        + "\\p{javaJavaIdentifierPart}*\\$\\p{javaJavaIdentifierStart}" 
        + "\\p{javaJavaIdentifierPart}*.*";
    return c.getName().matches(regex);
  }

  private final boolean isPrimitive(Class<?> c) {
    if (Objects.isNull(c)) throw new IllegalArgumentException("isPrimitive: c is null");
    String typeName = c.getName();
    return isPrimitive(typeName);
  }

  private final boolean isPrimitive(String s) {
    if (Objects.isNull(s)) throw new IllegalArgumentException("isPrimitive: s is null");
    if (s.length() == 0)
      throw new IllegalArgumentException("isPrimitive: s is empty");
    return s.matches("int|long|double|byte|short|float|boolean|char") ? true : false;
  }

  private final boolean isPrimitiveOrBoxed(Class<?> c) {
    if (Objects.isNull(c)) throw new IllegalArgumentException("isPrimitiveOrBoxed: c is null");
    String typeName = c.getName();
    return isPrimitiveOrBoxed(typeName);
  }

  private final boolean isPrimitiveOrBoxed(String s) {
    if (Objects.isNull(s)) throw new IllegalArgumentException("isPrimitiveOrBoxed: s is null");
    if (s.length() == 0) throw new IllegalArgumentException("isPrimitiveOrBoxed: s is empty");
    return s.matches("int|long|double|byte|short|float|boolean|char" 
        + "|java.lang.Integer|java.lang.Long|java.lang.Double"
        + "|java.lang.Byte|java.lang.Short|java.lang.Float" 
        + "|java.lang.Boolean|java.lang.Character") ? true : false;
  }

  @SafeVarargs
   public final boolean isSerializable(Object o, IdentityHashMap<Object, Integer>... imaps) {
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
    // and
    //   8. it's not and does not contain an inner class
    //   9. it's not and does not contain a lambda class
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
    // Classes with circular references are supported provided
    // they meet all other criteria, but its difficult to verify 
    // that with equality tests and System.identityHashCode or
    // identityHashCode can be used to verify components that
    // that should be identical and compare the structures of the
    ///identity maps of the original and serialized objects.
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
    // Many unsupported collections, maps and other classes
    // requiring integration could be supported.

    if (Objects.isNull(o)) throw new IllegalArgumentException("isSerializable(Object): o is null");

    IdentityHashMap<Object, Integer> imap = null;
    if (imaps.length == 0) {
      imap = new IdentityHashMap<>();
    } else {
      imap = imaps[0];
    }

    imap.put(o, 1);

    Class<?> c = o.getClass();

    if (useAnnotation) return hasSerializableAnnotation(c);
    if (isPrimitiveOrBoxed(c)) return true;
    if (isString(c)) return true;
    if (isSerializableArray(o)) return true;
    if (c.isEnum()) return true;
    if (isSerializableCollection(o)) return true;
    if (isSerializableMap(o)) return true;
    if (isLambda(c)) return false;
    if (isInner(c)) return false;
    if (isSupportedClass(c)) return true;
    if (!hasNoArgConstructor(c)) return false;

    @SuppressWarnings("unchecked")
    LinkedHashMap<Field, Class<?>> fmap = (LinkedHashMap<Field, Class<?>>) getFields(c, "is");
    LinkedHashMap<String, String> failed = new LinkedHashMap<>();

    Class<?> type = null;
    String name = null;
    boolean ok = true;
    boolean nonSerializableSuperClass = false;
    boolean isSerializable = false;
    Object v = null;

    // build map of non-serializable field names to class names
    for (Field f : fmap.keySet()) {
      if (hasPrivate(f.getModifiers()))
        f.setAccessible(true);
      name = f.getName();
      type = f.getType();
      try {
        v = f.get(o);
      } catch (IllegalArgumentException | IllegalAccessException e) {
        e.printStackTrace();
      }
      if (Objects.nonNull(v)) {
        if (imap.containsKey(v)) {
          continue;
        } else {
          imap.put(v, 1);
        }
        isSerializable = isSerializable(v, imap);
        if (!isSerializable) {
          failed.put(name, fmap.get(f).getName());
          ok = false;
          if (fmap.get(f).getName() != c.getName()) 
            nonSerializableSuperClass = true;
        }
      } else {
        // this isn't conclusive but ok for now since v is null
        isSerializable = isSerializableClass(type);
        if (!isSerializable) {
          failed.put(name, fmap.get(f).getName());
          ok = false;
          if (fmap.get(f).getName() != c.getName())
            nonSerializableSuperClass = true;
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
        System.out.println("isSerializable: the following fields in " 
            + c.getName() + " are not serializable)");
      }
      for (String n : failed.keySet()) {
        System.out.println("  " + failed.get(n) + "." + n);
      }
      return false;
    }
  }

  public final boolean isSerializableArray(Object o) {
    if (Objects.isNull(o)) throw new IllegalArgumentException("isSerializableArray: o is null");

    Class<?> c = o.getClass();

    if (!c.isArray())
      return false;

    int length = Array.getLength(o);
    if (length == 0)
      return true;

    if (useAnnotation)
      return hasSerializableAnnotation(c);

    Object v = null;
    boolean foundNonNull = false;
    boolean ok2Serialize = true;

    for (int i = 0; i < length; i++) {
      v = Array.get(o, i);
      if (Objects.nonNull(v)) {
        foundNonNull = true;
        ok2Serialize = isSerializable(v);
        if (!ok2Serialize)
          break;
      }
    }

    if ((foundNonNull && ok2Serialize) || !foundNonNull)
      return true;

    return false;
  }

  @SafeVarargs
  public final boolean isSerializableClass(Class<?> c, IdentityHashMap<Object, Integer>... imaps) {
    // A negative outcome is conclusive but not a positive one for
    // cases of collections with all null elements and maps with all
    // null keys or all null values, however those cases are
    // incidentally serializable because null is.
    if (Objects.isNull(c))
      throw new IllegalArgumentException("isSerializableClass: c is null");

    IdentityHashMap<Object, Integer> imap = null;
    if (imaps.length == 0) {
      imap = new IdentityHashMap<>();
    } else {
      imap = imaps[0];
    }

    if (useAnnotation) return hasSerializableAnnotation(c);
    if (isPrimitiveOrBoxed(c)) return true;
    if (isString(c)) return true;
    if (c.isArray()) return isSerializableClass(c.getComponentType());
    if (c.isEnum()) return true;
    if (isSupportedClass(c)) return true;
    if (isLambda(c)) return false;
    if (isInner(c)) return false;
    if (!hasNoArgConstructor(c)) return false;

    @SuppressWarnings("unchecked")
    LinkedHashMap<Field, Class<?>> fmap = (LinkedHashMap<Field, Class<?>>) getFields(c, "is");
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
      if (imap.containsKey(type)) {
        continue;
      } else imap.put(type, 1);
      isSerializable = isSerializableClass(type, imap);
      if (!isSerializable) {
        failed.put(name, fmap.get(f).getName());
        if (fmap.get(f).getName() != c.getName()) 
          nonSerializableSuperClass = true;
      }
    }

    if (isSerializable) {
      return true;
    } else {
      if (nonSerializableSuperClass) {
        System.out.println("isSerializableClass: the following fields in " + c.getName()
            + " and its superclasses are not serializable)");
      } else {
        System.out.println("isSerializableClass: the following fields in " 
            + c.getName() + " are not serializable)");
      }
      for (String n : failed.keySet()) {
        System.out.println("isSerializableClass: " + failed.get(n) + "." + n);
      }
      return false;
    }
  }

  private final boolean isSerializableCollection(Object o) {
    if (Objects.isNull(o))
      throw new IllegalArgumentException("isSerializableCollection: o is null");

    Class<?> c = o.getClass();

    if (!isSupportedCollection(c)) return false;
    if (useAnnotation) return hasSerializableAnnotation(c);

    @SuppressWarnings({ "unchecked", "rawtypes" })
    Object[] a = ((Collection) o).toArray(new Object[0]);
    return isSerializableArray(a);
  }

  public final boolean isSerializableMap(Object o) {
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
      keys[i] = k;
      values[i] = a.get(k);
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

  private final boolean isString(Class<?> c) {
    if (Objects.isNull(c)) throw new IllegalArgumentException("isString: c is null");
    return c == java.lang.String.class;
  }

  private final boolean isSupportedClass(Class<?> c) {
    if (Objects.isNull(c)) throw new IllegalArgumentException("isSupportedClass: c is null");

    Set<Class<?>> supported = new HashSet<>();

    Class<?> anClass = null;

    try {
      anClass = Class.forName("java.util.jar.Attributes$Name");
    } catch (ClassNotFoundException e) {
    }

    if (Objects.nonNull(anClass)) {
      supported.add(anClass);
    }

    supported.add(java.util.jar.Attributes.Name.class);
    supported.add(java.util.BitSet.class);

    return supported.contains(c) ? true : false;
  }

  private final boolean isSupportedCollection(Class<?> c) {
    if (Objects.isNull(c)) throw new IllegalArgumentException("isSupportedCollection: c is null");
    if (!isCollection(c)) return false;

    Set<String> notSupported = new HashSet<>();
    notSupported.add("java.beans.beancontext.BeanContextServicesSupport");
    notSupported.add("java.beans.beancontext.BeanContextSupport");
    notSupported.add("java.util.concurrent.ConcurrentHashMap.KeySetView");

    return notSupported.contains(c.getName()) ? false : true;
  }

  private final boolean isSupportedMap(Class<?> c) {
    if (Objects.isNull(c)) throw new IllegalArgumentException("isSupportedMap: c is null");
    if (!isMap(c)) return false;

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

    return notSupported.contains(c.getName()) ? false : true;
  }

  public static final void readFields(Object o) {
    // Prints object field information.
    if (Objects.isNull(o)) throw new IllegalArgumentException("o is null");
        
    Class<?> c = o.getClass();
    
    String name = null;
    List<Field> flist = new ArrayList<>(); // 4 readFields
    Map<Field, Class<?>> fmap = new HashMap<>(); // 4 readFields

    Field[] fields = c.getDeclaredFields();
    for (Field f : fields) {
      f.setAccessible(true);
      name = f.getName();
      if (!fmap.containsKey(name)) {
        flist.add(f);
        fmap.put(f, c);
      }
    }

    if (c != java.lang.Object.class) {
      Class<?> sclass = c;
      while ((sclass = sclass.getSuperclass()) != java.lang.Object.class) {
        fields = sclass.getDeclaredFields();
        for (Field f : fields) {
          f.setAccessible(true);
          name = f.getName();
          if (!fmap.containsKey(name)) {
            flist.add(f);
            fmap.put(f, sclass);
          }
        }
      }
    }

    System.out.println("(format: containingClassName.name, value, type, modifiers)");

    for (Field f : flist) {
      name = f.getName();
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
        System.out.print(containingClassName + "." + name + ", " + type.getComponentType().getName());
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
        System.out.println(containingClassName + "." + name + ", " + v 
            + ", " + type.getName() + ", " + listModifiers);
      }
    }
  }
  
  public final <K, V> HashMap<V, K> invert(Map<K, V> map) {
    // For duplicate values retain the first.
    return (HashMap<V, K>) map.entrySet().stream()
        .collect(Collectors.toMap(Entry::getValue, Entry::getKey, (a, b) -> a));
  }

  public final void printState() {
    // Prints information about global variables.
    Comparator<? super Entry<Object, Integer>> comp = (e1, e2) -> {
      int c2 = e1.getValue().compareTo(e2.getValue());
      int c1 = identityHashCode(e1.getKey()) - identityHashCode(e2.getKey());
      return c2 == 0 ? c1 : c2;
    };
    boolean allNull = true;
    StringBuilder sb = new StringBuilder();
    if (!(Objects.isNull(oc) || oc.intValue() == 0)) allNull = false;
    sb.append("  oc = " + oc + "\n");

    if (Objects.isNull(imap) || imap.size() == 0) {
      sb.append("  imap = " + imap + "\n");
    } else {
      int[] max = new int[]{0}; int len = 0;
      for (Object k : imap.keySet()) {
        if (Objects.isNull(k)) {
          len = ("null."+0).length();
        } else {
          len = (k.getClass().getName()+"."+identityHashCode(k)).length();
        }
        if (len > max[0]) max[0] = len;
      }
      Formatter f = new Formatter(sb, Locale.US);
      sb.append("  imap:\n");
      imap.entrySet().stream().sorted(comp).forEach(e -> {
          String name = null;
          if (Objects.isNull(e.getKey())) {
            name = "null";
          } else {
            name = e.getKey().getClass().getName();
          }
          f.format("    %1$-" 
          + max[0] + "s -> %2$d\n", name
          +"."+identityHashCode(e.getKey()), e.getValue());});
      f.close();
      allNull = false;
    }

    rimap = invert(imap);
    if (Objects.isNull(rimap) || rimap.size() == 0) {
      sb.append("  rimap = " + rimap + "\n");
    } else {
      sb.append("  rimap:\n");
      for (Integer x : rimap.keySet()) {
        sb.append("    " + x + " -> " + rimap.get(x).getClass().getName()
            + "." + identityHashCode(rimap.get(x)) + "\n");
      }
      allNull = false;
    }

    if (Objects.isNull(rmap) || rmap.size() == 0) {
      sb.append("  rmap = " + rmap + "\n");
    } else {
      sb.append("  rmap:\n");
      for (Integer x : rmap.keySet())
        sb.append("    " + x + " -> " + rmap.get(x) + "\n");
      allNull = false;
    }

    if (Objects.isNull(read) || read.size() == 0) {
      sb.append("  read = " + read);
    } else {
      HashMap<String, HashMap<String, Integer>> hread = new HashMap<>();
      for (Object q : read.keySet())
        hread.put(q.getClass().getName() + "." + identityHashCode(q), read.get(q));
      sb.append("  read:\n");
      for (String x : hread.keySet()) {
        sb.append("    " + x + " ->\n");
        for (String y : hread.get(x).keySet())
          sb.append("      " + y + " -> " + hread.get(x).get(y)+"\n");
      }
      sb = sb.deleteCharAt(sb.length() - 1);
      allNull = false;
    }

    if (allNull) {
      System.out.println("state = allNull");
    } else {
      System.out.println("state:");
      System.out.println(sb.toString());
    }
  }
  
  public final void printImap() {
    // Prints the contents of the imap global variable.
    StringBuilder sb = new StringBuilder();
    Comparator<? super Entry<Object, Integer>> comp = (e1, e2) -> {
      int c2 = e1.getValue().compareTo(e2.getValue());
      int c1 = identityHashCode(e1.getKey()) - identityHashCode(e2.getKey());
      return c2 == 0 ? c1 : c2;
    };
    if (Objects.isNull(imap) || imap.size() == 0) {
      sb.append("imap = " + imap + "\n");
    } else {
      int[] max = new int[]{0}; int len = 0;
      for (Object k : imap.keySet()) {
        len = (k.getClass().getName()+"."+identityHashCode(k)).length();
        if (len > max[0]) max[0] = len;
      }
      Formatter f = new Formatter(sb, Locale.US);
      sb.append("imap:\n");
      imap.entrySet().stream().sorted(comp).forEach(e -> f.format("%1$-" 
          + max[0] + "s -> %2$d\n", e.getKey().getClass().getName()
          +"."+identityHashCode(e.getKey()), e.getValue()));
      f.close();
    }
    System.out.println(sb.toString());
  }
  
  public final void printRmap() {
    // Prints the contents of the rmap global variable.
    StringBuilder sb = new StringBuilder();
    if (Objects.isNull(rmap) || rmap.size() == 0) {
      sb.append("rmap = " + rmap + "\n");
    } else {
      sb.append("rmap:\n");
      for (Integer x : rmap.keySet())
        sb.append("  " + x + " -> " + rmap.get(x) + "\n");
    }
    System.out.println(sb.toString());
  }
  
  public final void printRead() {
    // Prints the contents of the read global variable.
    StringBuilder sb = new StringBuilder();
    if (Objects.isNull(read) || read.size() == 0) {
      sb.append("read = " + read + "\n");
    } else {
      HashMap<String, HashMap<String, Integer>> hread = new HashMap<>();
      for (Object q : read.keySet())
        hread.put(q.getClass().getName() + "." + identityHashCode(q), read.get(q));
      sb.append("read:\n");
      for (String x : hread.keySet()) {
        sb.append("  " + x + " ->\n");
        for (String y : hread.get(x).keySet())
          sb.append("    " + y + " -> " + hread.get(x).get(y) + "\n");
      }
    }
    System.out.println(sb.toString());
  }
  
  public final void printRimap() {
    // Prints the contents of the rimap global variable.
    StringBuilder sb = new StringBuilder();
    rimap = invert(imap);
    if (Objects.isNull(rimap) || rimap.size() == 0) {
      sb.append("rimap = " + rimap + "\n");
    } else {
      sb.append("rimap:\n");
      for (Integer x : rimap.keySet()) {
        sb.append("  " + x + " -> " + rimap.get(x).getClass().getName()
            + "." + identityHashCode(rimap.get(x)) + "\n");
      }
    }
    System.out.println(sb.toString());
  }

  private final HashMap<Integer,Integer> createRmap(String s) {
    // For directly converting the content of a serialization 
    // string, in regions starting with %Y and ending with & 
    // or the end of the string, into a HashMap. This direct 
    // conversion is done to avoid polluting rmap and other 
    // global maps. This method is for use and called only by
    // des() and after it has prepared s.
    
    HashMap<Integer,Integer> hm = new HashMap<>();
    if (s.length() == 0) return hm;
    String[] sa = s.split(",");
    if (!(sa.length %2 == 0 && s.replaceAll(",", "").matches("\\d+")))
      throw new IllegalArgumentException("createRmap: argument s has invalid format");
    for (int i = 0; i < sa.length - 1; i+=2) {
      if (hm.containsKey(sa[i])) continue;
      hm.put(new Integer(sa[i]), new Integer(sa[i+1]));
    }
    return hm;
  }
  
  private final String rmapString() {
    Integer[] rmapar = new Integer[rmap.size() * 2];
    int i = 0;
    for (Integer k : rmap.keySet()) {
      rmapar[i++] = k;
      rmapar[i++] = rmap.get(k);
    }
    return Arrays.toString(rmapar).replaceAll("[\\[\\]|\\s+]", "");
  }
  
  private static final String now() {
    return new SimpleDateFormat("yyyyMMddHHmmSSS").format(new Date());
  }
  
  private static final String replaceLast(String text, String regex, String replacement) {
    return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
  }
}
