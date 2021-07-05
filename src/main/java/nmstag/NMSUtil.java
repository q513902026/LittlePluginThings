package nmstag;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface NMSUtil {
  public static interface PlayerNMS {
    boolean hasKey(Player param1Player, String param1String);
    
    Object getTag(Player param1Player);
    
    String getStringTag(Player param1Player, String param1String);
    
    int getIntTag(Player param1Player, String param1String);
    
    long getLongTag(Player param1Player, String param1String);
    
    boolean getBooleanTag(Player param1Player, String param1String);
    
    Player setStringTag(Player param1Player, String param1String1, String param1String2);
    
    Player setIntTag(Player param1Player, String param1String, int param1Int);
    
    Player setLongTag(Player param1Player, String param1String, long param1Long);
    
    Player setBooleanTag(Player param1Player, String param1String, boolean param1Boolean);
    
    void saveData(Object param1Object, Player param1Player);
  }
  
  public static interface ItemStackNMS {
    boolean hasKey(ItemStack param1ItemStack, String param1String);
    
    Class getNMSItemStackClass();
    
    Object getTag(ItemStack param1ItemStack);
    
    String getStringTag(ItemStack param1ItemStack, String param1String);
    
    int getIntTag(ItemStack param1ItemStack, String param1String);
    
    long getLongTag(ItemStack param1ItemStack, String param1String);
    
    boolean getBooleanTag(ItemStack param1ItemStack, String param1String);
    
    ItemStack setStringTag(ItemStack param1ItemStack, String param1String1, String param1String2);
    
    ItemStack setIntTag(ItemStack param1ItemStack, String param1String, int param1Int);
    
    ItemStack setLongTag(ItemStack param1ItemStack, String param1String, long param1Long);
    
    ItemStack setBooleanTag(ItemStack param1ItemStack, String param1String, boolean param1Boolean);
  }
}
