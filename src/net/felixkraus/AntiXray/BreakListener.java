package net.felixkraus.AntiXray;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by felix on 19.03.14.
 */
public class BreakListener implements Listener {

    private Plugin plugin;
    private AntiXray ax;
    private String sql;
    private PreparedStatement st;

    BreakListener(Plugin plugin){
        this.plugin = plugin;
        this.ax = (AntiXray) plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        Block bl = e.getBlock();
        int id;
        switch(bl.getType()){
            case STONE: id = 1; break;
            case GOLD_ORE: id = 14; break;
            case IRON_ORE: id = 15; break;
            case COAL_ORE: id = 16; break;
            case LAPIS_ORE: id = 21; break;
            case DIAMOND_ORE: id = 56; break;
            case REDSTONE_ORE: id = 73; break;
            case GLOWING_REDSTONE_ORE: id = 74; break;
            case EMERALD_ORE: id = 129; break;
            case ANCIENT_DEBRIS: id = 526; break;   /*

            default: id = -1; break;
        }
        int unixtimestamp = (int)(System.currentTimeMillis() / 1000L);
        String PN = e.getPlayer().getName();
        String WORLD = e.getPlayer().getWorld().getName();


        sql = "INSERT INTO ax_storage (Playername, Time, Type, World) VALUES (?, ?, ?, ?)";

        try {
            st = ax.getCon().prepareStatement(sql);
            st.setString(1,PN);
            st.setInt(2,unixtimestamp);
            st.setInt(3, id);
            st.setString(4, WORLD);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }


        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    st.execute();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        };

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, run);
    }
}
