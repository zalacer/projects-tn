package ch06.generics;

import static utils.StringUtils.repeatChar;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

// 25. Write a method public static String genericDeclaration(Method m) that returns the 
// declaration of the method m listing the type parameters with their bounds and the types 
// of the method //parameters, including their type arguments if they are generic types.

public class Ch0625MethodReflection {

  public static String genericDeclaration(Method m) {
    StringBuilder sb = new StringBuilder();
    String classMethod = m.getDeclaringClass().getCanonicalName() + "." + m.getName();

    sb.append(classMethod + "\n");
    sb.append(repeatChar('=', classMethod.length()) + "\n");

    TypeVariable<Method>[] typeParams = m.getTypeParameters();
    sb.append("TypeParameters:");
    if (typeParams.length == 0) {
      sb.append(" none\n");
    } else {
      sb.append(" " + typeParams.length + "\n");
    }
    for (TypeVariable<Method> tvm : typeParams) {
      sb.append("  name: " + tvm.getName() + "\n");
      Type[] bounds = tvm.getBounds();
      for (int b = 0; b < bounds.length; b++) {
        if (bounds[b] instanceof ParameterizedType) {
          ParameterizedType pt = (ParameterizedType) bounds[b];
          sb.append("    bound[" + b + "]: " + pt + "\n");
          sb.append("      typeName: " + pt.getRawType().getTypeName() + "\n");
          Type[] typeArguments = pt.getActualTypeArguments();
          for (int t = 0; t < typeArguments.length; t++) {
            sb.append("      typeArguments[" + t + "].TypeName: " + typeArguments[t].getTypeName() + "\n");
            if (typeArguments[0] instanceof WildcardType) {
              WildcardType wt = (WildcardType) typeArguments[0];
              Type[] lower = wt.getLowerBounds();
              for (int l = 0; l < lower.length; l++)
                sb.append("        lowerBound[" + l + "]: " + lower[l].getTypeName() + "\n");
              Type[] upper = wt.getUpperBounds();
              for (int u = 0; u < upper.length; u++)
                sb.append("        upperBound[" + u + "]: " + upper[u].getTypeName() + "\n");
            }
          }
        }
      }
    }

    Type[] genParamTypes = m.getGenericParameterTypes();
    sb.append("GenericParameterTypes:");
    if (genParamTypes.length == 0) {
      sb.append(" none\n");
    } else {
      sb.append(" " + genParamTypes.length + "\n");
    }
    for (Type type : genParamTypes) {
      sb.append("  name: " + type.getTypeName() + "\n");
    }

    Parameter[] params = m.getParameters();
    sb.append("Parameters:");
    if (params.length == 0) {
      sb.append(" none\n");
    } else {
      sb.append(" " + params.length + "\n");
    }
    for (Parameter p : params) {
      sb.append("  name: " + p.getName() + "\n");
      sb.append("    type: " + p.getType() + "\n");
      ParameterizedType pt2 = null;
      try {
        pt2 = (ParameterizedType) p.getParameterizedType();
      } catch (Exception e) {
      }
      if (pt2 != null) {
        sb.append("    ParameterizedType: " + pt2 + "\n");
        sb.append("      typeName: " + pt2.getRawType().getTypeName() + "\n");
        Type[] actualTypeArguments = pt2.getActualTypeArguments();
        for (int t = 0; t < actualTypeArguments.length; t++) {
          sb.append("      actualTypeArguments[" + t + "]: " + actualTypeArguments[t].getTypeName() + "\n");
          if (actualTypeArguments[0] instanceof WildcardType) {
            WildcardType wt = (WildcardType) actualTypeArguments[0];
            Type[] lower = wt.getLowerBounds();
            for (int l = 0; l < lower.length; l++)
              sb.append("        lowerBound[" + l + "]: " + lower[l].getTypeName() + "\n");
            Type[] upper = wt.getUpperBounds();
            for (int u = 0; u < upper.length; u++)
              sb.append("        upperBound[" + u + "]: " + upper[u].getTypeName() + "\n");
          }
        }
      }
    }
    return sb.toString();
  }

  public static void main(String[] args) throws NoSuchMethodException, SecurityException {

    Method sort =  Collections.class.getMethod("sort", List.class);
    System.out.println(genericDeclaration(sort));
    //  java.util.Collections.sort
    //  ==========================
    //  TypeParameters: 1
    //    name: T
    //      bound[0]: java.lang.Comparable<? super T>
    //        typeName: java.lang.Comparable
    //        typeArguments[0].TypeName: ? super T
    //          lowerBound[0]: T
    //          upperBound[0]: java.lang.Object
    //  GenericParameterTypes: 1
    //    name: java.util.List<T>
    //  Parameters: 1
    //    name: arg0
    //      type: interface java.util.List
    //      ParameterizedType: java.util.List<T>
    //        typeName: java.util.List
    //        actualTypeArguments[0]: T

    Method put =  HashMap.class.getMethod("put", Object.class, Object.class);
    System.out.println("\n"+genericDeclaration(put));
    //  java.util.HashMap.put
    //  =====================
    //  TypeParameters: none
    //  GenericParameterTypes: 2
    //    name: K
    //    name: V
    //  Parameters: 2
    //    name: arg0
    //      type: class java.lang.Object
    //    name: arg1
    //      type: class java.lang.Object

    Method repeat1 = Ch0618to21ArrayTyping.class.getMethod("repeat1", int.class, Object.class);
    System.out.println("\n"+genericDeclaration(repeat1));
    //  ch06.generics.Ch0618to21ArrayTyping.repeat1
    //  ===========================================
    //  TypeParameters: 1
    //    name: T
    //  GenericParameterTypes: 2
    //    name: int
    //    name: T
    //  Parameters: 2
    //    name: n
    //      type: int
    //    name: obj
    //      type: class java.lang.Object


  }
}

