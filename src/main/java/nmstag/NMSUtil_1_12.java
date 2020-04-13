package nmstag;

import me.hope.lpt.LittlePluginThings;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.ItemStack;


public class NMSUtil_1_12 implements  NMSUtil{
    public static class ItemStackNMS implements NMSUtil.ItemStackNMS{
    @Override
    public boolean hasKey(final ItemStack item, final String str) {
        final net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        return nmsItem.getTag().hasKey(str);
    }

    @Override
    public Object getTag(final ItemStack item) {
        final net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        return (nmsItem == null) ? null : nmsItem.getTag();
    }

    @Override
    public String getStringTag(final ItemStack item, final String str) {
        final net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        return nmsItem.getTag().getString(str);
    }

    @Override
    public int getIntTag(final ItemStack item, final String str) {
        final net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        return nmsItem.getTag().getInt(str);
    }

    @Override
    public long getLongTag(final ItemStack item, final String str) {
        final net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        return nmsItem.getTag().getLong(str);
    }

    @Override
    public boolean getBooleanTag(final ItemStack item, final String str) {
        final net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        return nmsItem.getTag().getBoolean(str);
    }

    @Override
    public ItemStack setStringTag(final ItemStack item, final String str1, final String str2) {
        final net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        final NBTTagCompound tag = nmsItem.getTag();
        tag.setString(str1, str2);
        nmsItem.setTag(tag);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    @Override
    public ItemStack setIntTag(final ItemStack item, final String str1, final int n) {
        final net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        final NBTTagCompound tag = nmsItem.getTag();
        tag.setInt(str1, n);
        nmsItem.setTag(tag);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    @Override
    public ItemStack setLongTag(final ItemStack item, final String str1, final long l) {
        final net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        final NBTTagCompound tag = nmsItem.getTag();
        tag.setLong(str1, l);
        nmsItem.setTag(tag);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    @Override
    public ItemStack setBooleanTag(final ItemStack item, final String str1, final boolean b) {
        final net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        final NBTTagCompound tag = nmsItem.getTag();
        tag.setBoolean(str1, b);
        nmsItem.setTag(tag);
        return CraftItemStack.asBukkitCopy(nmsItem);
    }
    }
    public static class PlayerNMS implements NMSUtil.PlayerNMS {
        private NBTTagCompound getTag(CraftPlayer player){
            NBTTagCompound nbtTagCompound = player.getHandle().save(new NBTTagCompound());
            return nbtTagCompound;
        }
        @Override
        public boolean hasKey(Player p0, String p1) {
            final net.minecraft.server.v1_12_R1.EntityPlayer nmsPlayer = ((CraftPlayer)p0).getHandle();
            return getTag((CraftPlayer) p0).hasKey(p1);
        }

        @Override
        public Object getTag(Player p0) {
            return this.getTag((CraftPlayer) p0);
        }

        @Override
        public String getStringTag(Player p0, String p1) {
            return null;
        }

        @Override
        public int getIntTag(Player p0, String p1) {
            return getTag((CraftPlayer) p0).getInt(p1);
        }

        @Override
        public long getLongTag(Player p0, String p1) {
            return 0;
        }

        @Override
        public boolean getBooleanTag(Player p0, String p1) {
            return false;
        }

        @Override
        public Player setStringTag(Player p0, String p1, String p2) {
            return null;
        }

        @Override
        public Player setIntTag(Player p0, String p1, int p2) {
            return null;
        }

        @Override
        public Player setLongTag(Player p0, String p1, long p2) {
            return null;
        }

        @Override
        public Player setBooleanTag(Player p0, String p1, boolean p2) {
            return null;
        }

        @Override
        public void saveData(Object nbt,final Player p0) {
            ((CraftPlayer)p0).getHandle().f((NBTTagCompound) nbt);
        }
    }
}
