package me.hope.lpt.module.sub;

import com.google.common.collect.Lists;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import me.hope.lpt.LittlePluginThings;
import me.hope.lpt.core.ISubMod;
import me.hope.lpt.module.sub.content.WardrobeSubContent;
import me.hope.lpt.util.ClassUtil;
import net.minecraft.server.v1_12_R1.ItemStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public enum WardrobeSubModMender implements ISubMod {
  INSTANCE;
  
  private boolean isLoader = false;
  
  private List<Object> skinTypes = Lists.newArrayList();
  
  private MethodHandle getEntitySkinCapabilityHandler;
  
  private MethodHandle getSkinInventoryContainerHandler;
  
  private MethodHandle removeStackFromSlotHandler;
  
  private MethodHandle getSizeInventoryHandler;
  
  private MethodHandle getPlayerWardrobeCapabilityHandler;
  
  private MethodHandle setUnlockedSlotsForSkinTypeHandler;
  
  private MethodHandle syncToPlayerHandler;
  
  private MethodHandle syncToAllTrackingHandler;
  
  private static ItemStack toItemStack(Object obj) {
    try {
      return (ItemStack)CraftItemStack.asCraftMirror((ItemStack)obj);
    } catch (Exception e) {
      LittlePluginThings.getInstance().getLogger().info("[WardrobeSubModMender] 转换物品失败" + obj);
      return new ItemStack(Material.AIR);
    } 
  }
  
  public void init() {
    try {
      ClassUtil.forName("net.minecraftforge.fml.common.Loader");
    } catch (Exception e) {
      this.isLoader = false;
      return;
    } 
    LittlePluginThings.getInstance().getLogger().info("[WardrobeSubModMender]:检测到FML,开始查询 时装工坊 MOD");
    load();
  }
  
  public boolean isLoader() {
    return this.isLoader;
  }
  
  private void load() {
    MethodHandles.Lookup lookup = MethodHandles.lookup();
    try {
      LittlePluginThings.getInstance().getLogger().info("[WardrobeSubModMender]: 正在获取需要的类");
      Class<?> IEntitySkinCapabilityCls = ClassUtil.forName("moe.plushie.armourers_workshop.api.common.capability.IEntitySkinCapability");
      Class<?> EntityCls = ClassUtil.forName("net.minecraft.entity.Entity");
      Class<?> IPlayerWardrobeCapCls = ClassUtil.forName("moe.plushie.armourers_workshop.api.common.capability.IPlayerWardrobeCap");
      Class<?> EntityPlayerCls = ClassUtil.forName("net.minecraft.entity.player.EntityPlayer");
      Class<?> EntityPlayerMPCls = ClassUtil.forName("net.minecraft.entity.player.EntityPlayerMP");
      Class<?> ISkinTypeCls = ClassUtil.forName("moe.plushie.armourers_workshop.api.common.skin.type.ISkinType");
      Class<?> ArmourersWorkshopApiCls = ClassUtil.forName("moe.plushie.armourers_workshop.api.ArmourersWorkshopApi");
      Class<?> ISkinInventoryContainerCls = ClassUtil.forName("moe.plushie.armourers_workshop.api.common.ISkinInventoryContainer");
      Class<?> WardrobeInventoryCls = ClassUtil.forName("moe.plushie.armourers_workshop.common.inventory.WardrobeInventory");
      Class<?> ItemStackCls = ClassUtil.forName("net.minecraft.item.ItemStack");
      LittlePluginThings.getInstance().getLogger().info("[WardrobeSubModMender]: 正在获取需要的方法");
      MethodType getEntitySkinCapabilityType = MethodType.methodType(IEntitySkinCapabilityCls, EntityCls);
      this.getEntitySkinCapabilityHandler = lookup.findStatic(ArmourersWorkshopApiCls, WardrobeSubContent.ForgeField.getEntitySkinCapability.getValue(), getEntitySkinCapabilityType);
      MethodType getPlayerWardrobeCapability = MethodType.methodType(IPlayerWardrobeCapCls, EntityPlayerCls);
      this.getPlayerWardrobeCapabilityHandler = lookup.findStatic(ArmourersWorkshopApiCls, "getPlayerWardrobeCapability", getPlayerWardrobeCapability);
      MethodType setUnlockedSlotsForSkinType = MethodType.methodType(void.class, ISkinTypeCls, new Class[] { int.class });
      this.setUnlockedSlotsForSkinTypeHandler = lookup.findVirtual(IPlayerWardrobeCapCls, "setUnlockedSlotsForSkinType", setUnlockedSlotsForSkinType);
      MethodType syncToPlayer = MethodType.methodType(void.class, EntityPlayerMPCls);
      this.syncToPlayerHandler = lookup.findVirtual(IPlayerWardrobeCapCls, "syncToPlayer", syncToPlayer);
      MethodType syncToAllTracking = MethodType.methodType(void.class);
      this.syncToAllTrackingHandler = lookup.findVirtual(IPlayerWardrobeCapCls, "syncToAllTracking", syncToAllTracking);
      MethodType getSkinInventoryContainer = MethodType.methodType(ISkinInventoryContainerCls);
      this.getSkinInventoryContainerHandler = lookup.findVirtual(IEntitySkinCapabilityCls, WardrobeSubContent.ForgeField.getSkinInventoryContainer.getValue(), getSkinInventoryContainer);
      MethodType removeStackFromSlot = MethodType.methodType(ItemStackCls, int.class);
      this.removeStackFromSlotHandler = lookup.findVirtual(WardrobeInventoryCls, WardrobeSubContent.ForgeField.REMOVE_STACK_FROM_SLOT.getValue(), removeStackFromSlot);
      MethodType getSizeInventory = MethodType.methodType(int.class);
      this.getSizeInventoryHandler = lookup.findVirtual(WardrobeInventoryCls, WardrobeSubContent.ForgeField.GET_SIZE_INVENTORY.getValue(), getSizeInventory);
      initValidSkinType();
      LittlePluginThings.getInstance().getLogger().info("[WardrobeSubModMender]: 获取完成");
    } catch (ClassNotFoundException|NoSuchMethodException|IllegalAccessException|NoSuchFieldException e) {
      this.isLoader = false;
      e.printStackTrace();
    } 
    this.isLoader = true;
    LittlePluginThings.getInstance().getLogger().info("[WardrobeSubModMender]: 初始化完成");
  }
  
  private void initValidSkinType() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
    Class<?> SkinTypeRegistryCls = ClassUtil.forName("moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry");
    this.skinTypes.add(SkinTypeRegistryCls.getDeclaredField("skinHead").get((Object)null));
    this.skinTypes.add(SkinTypeRegistryCls.getDeclaredField("skinChest").get((Object)null));
    this.skinTypes.add(SkinTypeRegistryCls.getDeclaredField("skinLegs").get((Object)null));
    this.skinTypes.add(SkinTypeRegistryCls.getDeclaredField("skinFeet").get((Object)null));
    this.skinTypes.add(SkinTypeRegistryCls.getDeclaredField("skinWings").get((Object)null));
    this.skinTypes.add(SkinTypeRegistryCls.getDeclaredField("skinOutfit").get((Object)null));
  }
  
  public void setAllUnlockCount(Player p, int count) {
    try {
      Object IPlayerWardrobeCapability = this.getPlayerWardrobeCapabilityHandler.invoke(((CraftPlayer)p).getHandle());
      for (Object skinType : this.skinTypes)
        this.setUnlockedSlotsForSkinTypeHandler.invoke(IPlayerWardrobeCapability, skinType, count); 
      this.syncToPlayerHandler.invoke(IPlayerWardrobeCapability, ((CraftPlayer)p).getHandle());
      this.syncToAllTrackingHandler.invoke(IPlayerWardrobeCapability);
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    } 
  }
  
  public List<ItemStack> getSkinInventoryContainerItemStack(HumanEntity humanEntity) {
    List<ItemStack> itemStack = Lists.newArrayList();
    if (!isLoader())
      return itemStack; 
    try {
      Object ISkinCapability = this.getEntitySkinCapabilityHandler.invoke(((CraftPlayer)humanEntity).getHandle());
      Object ISkinInventoryContainer = this.getSkinInventoryContainerHandler.invoke(ISkinCapability);
      Field skinInventorysField = ISkinInventoryContainer.getClass().getDeclaredField(WardrobeSubContent.ForgeField.skinInventorys.getValue());
      skinInventorysField.setAccessible(true);
      HashMap<?, ?> skinInventory = (HashMap<?, ?>)skinInventorysField.get(ISkinInventoryContainer);
      return getSkinList(skinInventory);
    } catch (Throwable throwable) {
      throwable.printStackTrace();
      return itemStack;
    } 
  }
  
  private List<ItemStack> getSkinList(HashMap<?, ?> skinInventory) throws Throwable {
    List<ItemStack> itemStackList = Lists.newArrayList();
    Object[] skinTypes = skinInventory.keySet().toArray();
    for (int i = 0; i < skinInventory.size(); i++) {
      List<ItemStack> subItem = getInventoryItemStack(skinInventory.get(skinTypes[i]));
      itemStackList.addAll(subItem);
      subItem.clear();
    } 
    return itemStackList;
  }
  
  private List<ItemStack> getInventoryItemStack(Object wardrobeInv) throws Throwable {
    List<ItemStack> itemStackList = Lists.newArrayList();
    int size = this.getSizeInventoryHandler.invoke(wardrobeInv);
    for (int i = 0; i < size; i++) {
      ItemStack itemStack = toItemStack(this.removeStackFromSlotHandler.invoke(wardrobeInv, i));
      if (itemStack.getType() != Material.AIR)
        itemStackList.add(itemStack); 
    } 
    return itemStackList;
  }
  
  public void sendToPlayer(Player p, List<ItemStack> skinItemStackList) {
    if (CustomNpcSubMod.INSTANCE.isLoader()) {
      CustomNpcSubMod.INSTANCE.sendSkinMail("萌澄果的小猫", p.getWorld(), p, "时装返还", new String[] { "这是一封装有你时装的邮件\n由于我们改动了时装槽,你身上穿戴的所有时装都被放入邮件中\n           --萌澄果" }, skinItemStackList);
    } else {
      sendItemToPlayer(p, skinItemStackList);
    } 
  }
  
  public void sendItemToPlayer(Player p, List<ItemStack> skinItemStackList) {
    skinItemStackList.forEach(itemStack -> {
          if (p.getInventory().firstEmpty() != -1) {
            p.getInventory().addItem(new ItemStack[] { itemStack });
          } else {
            Location loc = p.getLocation().clone();
            p.getWorld().dropItem(loc, itemStack);
          } 
        });
  }
}
