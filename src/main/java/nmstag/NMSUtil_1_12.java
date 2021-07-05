package nmstag;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NMSUtil_1_12 implements NMSUtil {
  public static class ItemStackNMS implements NMSUtil.ItemStackNMS {
    public boolean hasKey(ItemStack item, String str) {
      ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
      return nmsItem.getTag().hasKey(str);
    }
    
    public Object getTag(ItemStack item) {
      ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
      return (nmsItem == null) ? null : nmsItem.getTag();
    }
    
    public String getStringTag(ItemStack item, String str) {
      ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
      return nmsItem.getTag().getString(str);
    }
    
    public int getIntTag(ItemStack item, String str) {
      ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
      return nmsItem.getTag().getInt(str);
    }
    
    public long getLongTag(ItemStack item, String str) {
      ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
      return nmsItem.getTag().getLong(str);
    }
    
    public boolean getBooleanTag(ItemStack item, String str) {
      ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
      return nmsItem.getTag().getBoolean(str);
    }
    
    public ItemStack setStringTag(ItemStack item, String str1, String str2) {
      ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
      NBTTagCompound tag = nmsItem.getTag();
      tag.setString(str1, str2);
      nmsItem.setTag(tag);
      return CraftItemStack.asBukkitCopy(nmsItem);
    }
    
    public ItemStack setIntTag(ItemStack item, String str1, int n) {
      ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
      NBTTagCompound tag = nmsItem.getTag();
      tag.setInt(str1, n);
      nmsItem.setTag(tag);
      return CraftItemStack.asBukkitCopy(nmsItem);
    }
    
    public ItemStack setLongTag(ItemStack item, String str1, long l) {
      ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
      NBTTagCompound tag = nmsItem.getTag();
      tag.setLong(str1, l);
      nmsItem.setTag(tag);
      return CraftItemStack.asBukkitCopy(nmsItem);
    }
    
    public ItemStack setBooleanTag(ItemStack item, String str1, boolean b) {
      ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
      NBTTagCompound tag = nmsItem.getTag();
      tag.setBoolean(str1, b);
      nmsItem.setTag(tag);
      return CraftItemStack.asBukkitCopy(nmsItem);
    }
    
    public Class getNMSItemStackClass() {
      return ItemStack.class;
    }
  }
  
  public static class PlayerNMS implements NMSUtil.PlayerNMS {
    private NBTTagCompound getTag(CraftPlayer player) {
      NBTTagCompound nbtTagCompound = player.getHandle().save(new NBTTagCompound());
      return nbtTagCompound;
    }
    
    public boolean hasKey(Player p0, String p1) {
      EntityPlayer nmsPlayer = ((CraftPlayer)p0).getHandle();
      return getTag((CraftPlayer)p0).hasKey(p1);
    }
    
    public Object getTag(Player p0) {
      return getTag((CraftPlayer)p0);
    }
    
    public String getStringTag(Player p0, String p1) {
      return null;
    }
    
    public int getIntTag(Player p0, String p1) {
      return getTag((CraftPlayer)p0).getInt(p1);
    }
    
    public long getLongTag(Player p0, String p1) {
      return 0L;
    }
    
    public boolean getBooleanTag(Player p0, String p1) {
      return false;
    }
    
    public Player setStringTag(Player p0, String p1, String p2) {
      return null;
    }
    
    public Player setIntTag(Player p0, String p1, int p2) {
      return null;
    }
    
    public Player setLongTag(Player p0, String p1, long p2) {
      return null;
    }
    
    public Player setBooleanTag(Player p0, String p1, boolean p2) {
      return null;
    }
    
    public void saveData(Object nbtTag, Player p0) {
      ((CraftPlayer)p0).getHandle().f((NBTTagCompound)nbtTag);
    }
  }
}
