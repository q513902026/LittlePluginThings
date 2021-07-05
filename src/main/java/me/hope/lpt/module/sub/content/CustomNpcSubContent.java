package me.hope.lpt.module.sub.content;

public class CustomNpcSubContent {
  public static final String NPC_API_CLASS_NAME = "noppes.npcs.api.NpcAPI";
  
  public static final String I_PLAYER_MAIL_CLASS_NAME = "noppes.npcs.api.entity.data.IPlayerMail";
  
  public static final String I_WORLD_CLASS_NAME = "noppes.npcs.api.IWorld";
  
  public static final String WORLD_SERVER_CLASS_NAME = "net.minecraft.world.WorldServer";
  
  public static final String I_ITEM_STACK_CLASS_NAME = "noppes.npcs.api.item.IItemStack";
  
  public static final String ITEM_STACK_CLASS_NAME = "net.minecraft.item.ItemStack";
  
  public static final String I_CONTAINER_CLASS_NAME = "noppes.npcs.api.IContainer";
  
  public static final String I_PLAYER_CLASS_NAME = "noppes.npcs.api.entity.IPlayer";
  
  public enum MethodName {
    Instance, createMail, getIWorld, getIItemStack, getPlayer, getContainer, setText, setSlot, sendMail;
    
    private final String value;
    
    MethodName() {
      this.value = name();
    }
    
    public String getValue() {
      return this.value;
    }
  }
}
