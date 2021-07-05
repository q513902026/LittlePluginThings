package me.hope.lpt;

import com.google.common.collect.Sets;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarFileHelper {
  public static Set<Class<?>> getClassName(String packageName) {
    return getClassName(packageName, true);
  }
  
  public static Set<Class<?>> getClassName(String packageName, boolean childPackage) {
    Set<Class<?>> fileNames = null;
    ClassLoader loader = LittlePluginThings.class.getClassLoader();
    String packagePath = packageName.replace(".", "/");
    URL url = loader.getResource(packagePath);
    if (url != null) {
      String type = url.getProtocol();
      if (type.equals("file")) {
        fileNames = getClassNameByFile(url.getPath(), null, childPackage);
      } else if (type.equals("jar")) {
        fileNames = getClassNameByJar(url.getPath(), childPackage);
      } 
    } else {
      fileNames = getClassNameByJars(((URLClassLoader)loader).getURLs(), packagePath, childPackage);
    } 
    return fileNames;
  }
  
  private static Set<Class<?>> getClassNameByFile(String filePath, Set<Class<?>> className, boolean childPackage) {
    if (className == null)
      className = Sets.newLinkedHashSet(); 
    Set<Class<?>> myClassName = Sets.newLinkedHashSet(className);
    File file = new File(filePath);
    File[] childFiles = file.listFiles();
    for (File childFile : childFiles) {
      if (childFile.isDirectory()) {
        if (childPackage)
          myClassName.addAll(getClassNameByFile(childFile.getPath(), myClassName, childPackage)); 
      } else {
        String childFilePath = childFile.getPath();
        if (childFilePath.endsWith(".class")) {
          childFilePath = childFilePath.substring(childFilePath.indexOf("\\classes") + 9, childFilePath.lastIndexOf("."));
          childFilePath = childFilePath.replace("\\", ".");
          try {
            myClassName.add(Thread.currentThread().getContextClassLoader().loadClass(childFilePath));
          } catch (ClassNotFoundException e) {
            e.printStackTrace();
          } 
        } 
      } 
    } 
    return myClassName;
  }
  
  private static Set<Class<?>> getClassNameByJar(String jarPath, boolean childPackage) {
    Set<Class<?>> myClassName = Sets.newLinkedHashSet();
    String[] jarInfo = jarPath.split("!");
    String jarFilePath = jarInfo[0].substring(jarInfo[0].indexOf("/"));
    String packagePath = jarInfo[1].substring(1);
    try {
      JarFile jarFile = new JarFile(jarFilePath);
      Enumeration<JarEntry> entrys = jarFile.entries();
      while (entrys.hasMoreElements()) {
        JarEntry jarEntry = entrys.nextElement();
        String entryName = jarEntry.getName();
        if (entryName.endsWith(".class")) {
          String myPackagePath;
          if (childPackage) {
            if (entryName.startsWith(packagePath)) {
              entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
              myClassName.add(Thread.currentThread().getContextClassLoader().loadClass(entryName));
            } 
            continue;
          } 
          int index = entryName.lastIndexOf("/");
          if (index != -1) {
            myPackagePath = entryName.substring(0, index);
          } else {
            myPackagePath = entryName;
          } 
          if (myPackagePath.equals(packagePath)) {
            entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
            myClassName.add(Thread.currentThread().getContextClassLoader().loadClass(entryName));
          } 
        } 
      } 
    } catch (Exception e) {
      e.printStackTrace();
    } 
    return myClassName;
  }
  
  private static Set<Class<?>> getClassNameByJars(URL[] urls, String packagePath, boolean childPackage) {
    Set<Class<?>> myClassName = Sets.newLinkedHashSet();
    if (urls != null)
      for (int i = 0; i < urls.length; i++) {
        URL url = urls[i];
        String urlPath = url.getPath();
        if (!urlPath.endsWith("classes/")) {
          String jarPath = urlPath + "!/" + packagePath;
          myClassName.addAll(getClassNameByJar(jarPath, childPackage));
        } 
      }  
    return myClassName;
  }
}
