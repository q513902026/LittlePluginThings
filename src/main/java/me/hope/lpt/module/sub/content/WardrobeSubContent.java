package me.hope.lpt.module.sub.content;

public class WardrobeSubContent {
  public static final String I_ENTITY_SKIN_CAPABILITY_CLASS_NAME = "moe.plushie.armourers_workshop.api.common.capability.IEntitySkinCapability";
  
  public static final String ENTITY_CLASS_NAME = "net.minecraft.entity.Entity";
  
  public static final String ARMOURERS_WORKSHOP_API_CLASS_NAME = "moe.plushie.armourers_workshop.api.ArmourersWorkshopApi";
  
  public static final String I_SKIN_INVENTORY_CONTAINER_CLASS_NAME = "moe.plushie.armourers_workshop.api.common.ISkinInventoryContainer";
  
  public static final String WARDROBE_INVENTORY_CLASS_NAME = "moe.plushie.armourers_workshop.common.inventory.WardrobeInventory";
  
  public static final String ITEM_STACK_CLASS_NAME = "net.minecraft.item.ItemStack";
  
  public enum ForgeField {
    REMOVE_STACK_FROM_SLOT("func_70304_b"),
    GET_SIZE_INVENTORY("func_70302_i_"),
    getEntitySkinCapability,
    getSkinInventoryContainer,
    skinInventorys;
    
    private final String value;
    
    ForgeField() {
      this.value = name();
    }
    
    ForgeField(String value) {
      this.value = value;
    }
    
    public String getValue() {
      return this.value;
    }
  }
}
