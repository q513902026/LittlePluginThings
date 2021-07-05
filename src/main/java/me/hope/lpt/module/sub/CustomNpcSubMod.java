package me.hope.lpt.module.sub;

import com.google.common.collect.Lists;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.List;
import me.hope.lpt.LittlePluginThings;
import me.hope.lpt.core.ISubMod;
import me.hope.lpt.module.sub.content.CustomNpcSubContent;
import me.hope.lpt.util.ClassUtil;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public enum CustomNpcSubMod implements ISubMod {
  INSTANCE;
  
  private boolean isLoader = false;
  
  private List<ItemStack> EMPTY = Lists.newArrayList();
  
  private MethodHandle createMailHandler;
  
  private MethodHandle getIItemStackHandler;
  
  private MethodHandle getContainerHandler;
  
  private MethodHandle setSlotHandler;
  
  private MethodHandle setTextHandler;
  
  private MethodHandle sendMailHandler;
  
  private MethodHandle getIEntityHandler;
  
  private Object NPC_API;
  
  private Object getNpcAPI() {
    return this.NPC_API;
  }
  
  public void init() {
    try {
      ClassUtil.forName("net.minecraftforge.fml.common.Loader");
    } catch (Exception e) {
      this.isLoader = false;
      return;
    } 
    LittlePluginThings.getInstance().getLogger().info("[CustomNpcSubMod]:检测到FML,开始查询 自定义NPC MOD");
    load();
  }
  
  private void load() {
    MethodHandles.Lookup lookup = MethodHandles.lookup();
    try {
      LittlePluginThings.getInstance().getLogger().info("[CustomNpcSubMod]: 正在检测需要的类");
      Class<?> NPC_APICls = ClassUtil.forName("noppes.npcs.api.NpcAPI");
      Class<?> EntityCls = ClassUtil.forName("net.minecraft.entity.Entity");
      Class<?> IEntityCls = ClassUtil.forName("noppes.npcs.api.entity.IEntity");
      Class<?> IPlayerMailCls = ClassUtil.forName("noppes.npcs.api.entity.data.IPlayerMail");
      Class<?> IItemStackCls = ClassUtil.forName("noppes.npcs.api.item.IItemStack");
      Class<?> ItemStackCls = ClassUtil.forName("net.minecraft.item.ItemStack");
      Class<?> IPlayerCls = ClassUtil.forName("noppes.npcs.api.entity.IPlayer");
      Class<?> IContainer = ClassUtil.forName("noppes.npcs.api.IContainer");
      LittlePluginThings.getInstance().getLogger().info("[CustomNpcSubMod]: 正在获取需要的方法");
      MethodType Instance = MethodType.methodType(NPC_APICls);
      MethodHandle instanceHandler = lookup.findStatic(NPC_APICls, CustomNpcSubContent.MethodName.Instance.getValue(), Instance);
      MethodType getIEntity = MethodType.methodType(IEntityCls, EntityCls);
      this.getIEntityHandler = lookup.findVirtual(NPC_APICls, "getIEntity", getIEntity);
      MethodType createMail = MethodType.methodType(IPlayerMailCls, String.class, new Class[] { String.class });
      this.createMailHandler = lookup.findVirtual(NPC_APICls, CustomNpcSubContent.MethodName.createMail.getValue(), createMail);
      MethodType getIItemStack = MethodType.methodType(IItemStackCls, ItemStackCls);
      this.getIItemStackHandler = lookup.findVirtual(NPC_APICls, CustomNpcSubContent.MethodName.getIItemStack.getValue(), getIItemStack);
      MethodType getContainer = MethodType.methodType(IContainer);
      this.getContainerHandler = lookup.findVirtual(IPlayerMailCls, CustomNpcSubContent.MethodName.getContainer.getValue(), getContainer);
      MethodType setText = MethodType.methodType(void.class, String[].class);
      this.setTextHandler = lookup.findVirtual(IPlayerMailCls, CustomNpcSubContent.MethodName.setText.getValue(), setText);
      MethodType setSlot = MethodType.methodType(void.class, int.class, new Class[] { IItemStackCls });
      this.setSlotHandler = lookup.findVirtual(IContainer, CustomNpcSubContent.MethodName.setSlot.getValue(), setSlot);
      MethodType sendMail = MethodType.methodType(void.class, IPlayerMailCls);
      this.sendMailHandler = lookup.findVirtual(IPlayerCls, CustomNpcSubContent.MethodName.sendMail.getValue(), sendMail);
      LittlePluginThings.getInstance().getLogger().info("[CustomNpcSubMod]: 正在获取 NPC API 的实例");
      this.NPC_API = instanceHandler.invoke();
      LittlePluginThings.getInstance().getLogger().info("[CustomNpcSubMod]: 获取完成");
    } catch (Throwable e) {
      e.printStackTrace();
    } 
    this.isLoader = true;
  }
  
  public void sendSkinMail(String sourcePlayer, World world, Player destPlayer, String title, String[] texts, List<ItemStack> attached) {
    if (!isLoader())
      return; 
    List<List<ItemStack>> attachedList = Lists.partition(attached, 4);
    int size = attachedList.size();
    for (int i = 0; i < size; i++) {
      sendMail0(i, sourcePlayer + (i + 1), destPlayer, String.format("%s[%s/%s]", new Object[] { title, Integer.valueOf(i + 1), Integer.valueOf(size) }), texts, attachedList.get(i));
      if (i + 1 == size)
        destPlayer.sendMessage("您有新的未读邮件了！"); 
    } 
  }
  
  public void sendMail(String sourcePlayer, Player destPlayer, String title, String[] texts) {
    sendMail(sourcePlayer, destPlayer, title, texts, this.EMPTY);
  }
  
  public void sendMail(String sourcePlayer, Player destPlayer, String title, String[] texts, List<ItemStack> attached) {
    if (attached.size() > 0) {
      List<List<ItemStack>> attachedList = Lists.partition(attached, 4);
      int size = attachedList.size();
      for (int i = 0; i < size; i++) {
        sendMail0(i, String.format("%s %s", new Object[] { sourcePlayer, Integer.valueOf(i + 1) }), destPlayer, title, texts, attachedList.get(i));
      } 
    } else {
      sendMail0(0, sourcePlayer, destPlayer, title, texts, attached);
    } 
  }
  
  public void sendMail0(int index, String sourcePlayer, Player destPlayer, String title, String[] texts, List<ItemStack> attached) {
    Object API = getNpcAPI();
    try {
      Object IPlayerMail = this.createMailHandler.invoke(API, sourcePlayer, title);
      Object IContainer = this.getContainerHandler.invoke(IPlayerMail);
      for (int i = 0; i < attached.size(); i++)
        this.setSlotHandler.invoke(IContainer, i, getIItemStack(API, attached.get(i))); 
      this.setTextHandler.invoke(IPlayerMail, texts);
      Object IPlayer_Dest = this.getIEntityHandler.invoke(API, ((CraftPlayer)destPlayer).getHandle());
      if (IPlayer_Dest == null) {
        WardrobeSubModMender.INSTANCE.sendItemToPlayer(destPlayer, attached);
        return;
      } 
      try {
        assert IPlayerMail != null;
        Field timeFiled = IPlayerMail.getClass().getDeclaredField("time");
        timeFiled.set(IPlayerMail, Long.valueOf(System.currentTimeMillis() - 1000L * attached.size() * (index + 1)));
      } catch (NoSuchFieldException|IllegalAccessException e) {
        e.printStackTrace();
      } 
      this.sendMailHandler.invoke(IPlayer_Dest, IPlayerMail);
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    } 
  }
  
  private Object getIItemStack(Object API, ItemStack itemStack) throws Throwable {
    return this.getIItemStackHandler.invoke(API, CraftItemStack.asNMSCopy(itemStack));
  }
  
  public boolean isLoader() {
    return this.isLoader;
  }
}
