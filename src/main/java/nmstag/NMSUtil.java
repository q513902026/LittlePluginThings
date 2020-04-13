package nmstag;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;

public interface NMSUtil {
     interface ItemStackNMS {
        boolean hasKey(final ItemStack p0, final String p1);

        Object getTag(final ItemStack p0);

        String getStringTag(final ItemStack p0, final String p1);

        int getIntTag(final ItemStack p0, final String p1);

        long getLongTag(final ItemStack p0, final String p1);

        boolean getBooleanTag(final ItemStack p0, final String p1);

        ItemStack setStringTag(final ItemStack p0, final String p1, final String p2);

        ItemStack setIntTag(final ItemStack p0, final String p1, final int p2);

        ItemStack setLongTag(final ItemStack p0, final String p1, final long p2);

        ItemStack setBooleanTag(final ItemStack p0, final String p1, final boolean p2);
    }
    interface PlayerNMS {
        boolean hasKey(final Player p0, final String p1);

        Object getTag(final Player p0);

        String getStringTag(final Player p0, final String p1);

        int getIntTag(final Player p0, final String p1);

        long getLongTag(final Player p0, final String p1);

        boolean getBooleanTag(final Player p0, final String p1);

        Player setStringTag(final Player p0, final String p1, final String p2);

        Player setIntTag(final Player p0, final String p1, final int p2);

        Player setLongTag(final Player p0, final String p1, final long p2);

        Player setBooleanTag(final Player p0, final String p1, final boolean p2);
        void saveData(Object nbttag,final Player p0);
    }
}
