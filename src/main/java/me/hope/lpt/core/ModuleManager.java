package me.hope.lpt.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.hope.lpt.JarFileHelper;
import me.hope.lpt.LittlePluginThings;
import me.hope.lpt.api.ConfigInstance;
import me.hope.lpt.api.Plugin;
import me.hope.lpt.api.RegisterCommand;
import org.bukkit.command.CommandExecutor;

public class ModuleManager {
  protected static Map<String, CommandExecutor> commands = Maps.newHashMap();
  
  private static ModuleManager instance = new ModuleManager();
  
  public Set<String> modulesList = Sets.newHashSet();
  
  private static Map<Class, Object> moduleInstance = Maps.newHashMap();
  
  public void onInit() {
    for (Class<?> clazz : (Iterable<Class<?>>)JarFileHelper.getClassName("me.hope.lpt")) {
      if (isPlugin(clazz)) {
        Plugin pl = clazz.<Plugin>getAnnotation(Plugin.class);
        this.modulesList.add(clazz.getName());
      } 
    } 
  }
  
  public void onConfigInject(Collection<BaseModule> modules) throws IllegalAccessException {
    for (BaseModule baseModule : modules) {
      for (Field field : getConfigField(baseModule.getClass()))
        field.set(baseModule, baseModule.<BaseModule>setConfig(field.getName()).getConfig()); 
    } 
  }
  
  private boolean isPlugin(Class clazz) {
    if (clazz.isAnnotationPresent((Class)Plugin.class) && clazz.getSuperclass().getName().equals(BaseModule.class.getName()))
      return true; 
    return false;
  }
  
  private String[] isRegisterCommand(Object obj) {
    if (obj.getClass().isAnnotationPresent((Class)RegisterCommand.class)) {
      RegisterCommand rc = obj.getClass().<RegisterCommand>getAnnotation(RegisterCommand.class);
      return new String[] { rc.value(), rc.parentPlugin() };
    } 
    return null;
  }
  
  @Nullable
  private List<Field> getConfigField(Class clazz) {
    List<Field> fields = Lists.newArrayList();
    for (Field field : clazz.getFields()) {
      if (field.isAnnotationPresent((Class)ConfigInstance.class))
        fields.add(field); 
    } 
    return fields;
  }
  
  public static <T extends CommandExecutor> T getCommandExecutor(String name) {
    return (T)commands.get(name);
  }
  
  public static <T extends CommandExecutor> T registerCommand(String name, T command) {
    CommandExecutor commandExecutor = commands.put(name, (CommandExecutor)command);
    LittlePluginThings.getInstance().getLogger().info("[LittlePluginThings - CommandMap] : 命令" + name + "注册完成.");
    return (T)commandExecutor;
  }
  
  public static Map<String, CommandExecutor> getCommandMap() {
    return commands;
  }
  
  public static void registerModule(@Nonnull Object module) {
    moduleInstance.put(module.getClass(), module);
  }
  
  @Nullable
  public static <T> T getModule(T object) {
    if (moduleInstance.containsKey(object.getClass()))
      return (T)moduleInstance.get(object.getClass()); 
    return null;
  }
  
  public static ModuleManager getInstance() {
    return instance;
  }
}
