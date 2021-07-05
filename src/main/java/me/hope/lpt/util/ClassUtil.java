package me.hope.lpt.util;

import com.google.common.collect.Maps;
import java.util.HashMap;

public class ClassUtil {
  private static HashMap<String, Class<?>> forNameCache = Maps.newHashMap();
  
  public static Class<?> forName(String classPath) throws ClassNotFoundException {
    Class<?> tempClass;
    if (forNameCache.containsKey(classPath) && (tempClass = forNameCache.get(classPath)) != null)
      return tempClass; 
    return Class.forName(classPath);
  }
  
  public static void clearCache() {
    forNameCache.clear();
  }
}
