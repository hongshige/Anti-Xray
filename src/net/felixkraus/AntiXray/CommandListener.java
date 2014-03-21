package net.felixkraus.AntiXray;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

/**
 * Created by felix on 21.03.14.
 */
public class CommandListener {
    private Plugin plugin;
    private AntiXray ax;


    private String name;

    public CommandListener(Plugin plugin) {
        this.plugin = plugin;
        this.ax = (AntiXray) plugin;
    }


    public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
        if(label.equalsIgnoreCase("xlu")){
            if(args.length!=2) return false;
            if(!isInteger(args[1])){
                sender.sendMessage("Please give a valid integer for the time span.");
                return false;
            }
            else {
                PreparedStatement st = null;
                int sec = (int)(System.currentTimeMillis() / 1000L) - Integer.parseInt(args[1])*86400;
                name = args[0];
                final String sql = "SELECT Type FROM ax_storage WHERE Playername = ? AND Time >= ?";
                try {
                    st = ax.getCon().prepareStatement(sql);
                    st.setString(1, args[0]);
                    st.setInt(2, sec);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                final PreparedStatement finalSt = st;
                Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ResultSet rs = finalSt.executeQuery();
                            int Diamonds = 0, Iron = 0, Gold = 0, Redstone = 0, Coal = 0, Emerald = 0, Stone = 0, Lapis = 0, gesamt = 0;
                            while(rs.next()){
                                switch(rs.getInt("Type")){
                                    case 1 : Stone++; break;
                                    case 14 : Gold++; break;
                                    case 15 : Iron ++; break;
                                    case 16 : Coal++; break;
                                    case 21 : Lapis++; break;
                                    case 56 : Diamonds++; break;
                                    case 73 : Redstone++; break;
                                    case 74 : Redstone++; break;
                                    case 129 : Emerald++; break;
                                }
                                gesamt++;
                            }
                            sendMessage(Stone, Diamonds, Gold, Iron, Coal, Lapis, Emerald, Redstone, gesamt);

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                    }

                    private void sendMessage(int Stone, int Diamonds, int Gold, int Iron, int Coal, int Lapis, int Emerald, int Redstone, int GesamtAbgebaut){
                        int summe = Stone+Diamonds+Gold+Iron+Coal+Lapis+Emerald+Redstone;
                        sender.sendMessage(ChatColor.DARK_PURPLE+"--------------------------------");
                        sender.sendMessage(ChatColor.YELLOW+"            Anti-Xray");
                        sender.sendMessage(ChatColor.YELLOW+"--------------------------------");
                        sender.sendMessage(ChatColor.BLUE+"Blocks destroyed by "+ name+":");
                        sender.sendMessage(ChatColor.YELLOW+"--------------------------------");
                        sender.sendMessage("Total: "+GesamtAbgebaut);
                        sender.sendMessage("Total (Below listed materials): " + summe);
                        sender.sendMessage(messageString(ChatColor.GREEN, "Emeralds", Emerald, summe));
                        sender.sendMessage(messageString(ChatColor.AQUA, "Diamonds", Diamonds, summe));
                        sender.sendMessage(messageString(ChatColor.GOLD, "Gold", Gold, summe));
                        sender.sendMessage(messageString(ChatColor.DARK_GRAY, "Iron", Iron, summe));
                        sender.sendMessage(messageString(ChatColor.RED, "Redstone", Redstone, summe));
                        sender.sendMessage(messageString(ChatColor.DARK_BLUE, "Lapis Lazuli", Lapis, summe));
                        sender.sendMessage(messageString(ChatColor.BLACK, "Coal", Coal, summe));
                        sender.sendMessage(messageString(ChatColor.GRAY, "Stone", Stone, summe));
                        sender.sendMessage(ChatColor.DARK_PURPLE+"--------------------------------");


                    }


                };
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, run);
                return true;
            }
        }
        return false;
    }





    private String messageString(ChatColor color, String Name, int Anzahl, int summe){
        return color+Name+": "+Anzahl+" "+" | "+new DecimalFormat("#0.00").format((double)Anzahl/(double)summe*100)+"%";
    }


    private static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c <= '/' || c >= ':') {
                return false;
            }
        }
        return true;
    }
}
