package net.felixkraus.AntiXray;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

import java.io.IOException;
import java.sql.*;

/**
 * Created by felix on 19.03.14.
 */
public class AntiXray extends JavaPlugin {

    private Connection con;
    private Statement st;
    private String dbUrl;
    private String sql;
    private CommandListener cmdListener;

    @Override
    public void onEnable(){

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        if(!getConfig().isSet("useMetrics")){
            getConfig().set("useMetrics", true);
            saveConfig();
        }
        if(getConfig().getBoolean("useMetrics")){
            try {
                MetricsLite metrics = new MetricsLite(this);
                metrics.start();
            } catch (IOException e) {
            }
        }
        if(!openConnection()){
            System.err.println("[Anti-Xray] [Error] Can't connect to database!");
            getServer().getPluginManager().disablePlugin(this);

        }
        else{
            prepareStatement();
            createTable();
            deleteOldData();
            new BreakListener(this);
            cmdListener = new CommandListener(this);
        }
    }

    private void deleteOldData() {
        int keepData = getConfig().getInt("keepData");
        if(keepData != -1){
            int time = (int)(System.currentTimeMillis() / 1000L) - keepData * 24 * 60 * 60;
            String timeSt = String.valueOf(time);
            sql = "DELETE FROM ax_storage WHERE Time < "+timeSt;
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    int cleaned = 0;
                    try {
                        cleaned = st.executeUpdate(sql);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    System.out.println("[Anti-Xray] Cleaned "+cleaned+" rows of data!");
                }
            };
            getServer().getScheduler().runTaskAsynchronously(this, run);
        }
    }


    private void prepareStatement() {
        try {
            st = con.createStatement();
        } catch (SQLException e) {
            System.err.println("[Anti-Xray] Can't prepare statement!");
            e.printStackTrace();
        }
    }

    private void createTable() {

        sql = "CREATE TABLE IF NOT EXISTS ax_storage (Playername VARCHAR(32), Time INT(10), Type INT(6), World VARCHAR(32));";
        try {
            st.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable(){
        try {
            if(!con.isClosed()) con.close();
        } catch (SQLException e) {
            System.err.println("[Anti-Xray] Unable to close DB-Connection!");
        }
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        return cmdListener.onCommand(sender, cmd, label, args);
    }


    private boolean openConnection() {
        if(getConfig().getString("storageType").equals("sqlite")){
            dbUrl = "jdbc:sqlite:plugins/Anti-Xray/database.db";
            try {
                String driver = "org.sqlite.JDBC";
                Class.forName(driver).newInstance();
                con = DriverManager.getConnection(dbUrl);
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("[Anti-Xray] Can't connect to database!");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        else if(getConfig().getString("storageType").equals("mysql")){
            dbUrl = "jdbc:mysql://%SERVER%:%PORT%/%DATABASE%".replace("%SERVER%", getConfig().getString("mysql.server"));
            dbUrl = dbUrl.replace("%PORT%", getConfig().getString("mysql.port"));
            dbUrl = dbUrl.replace("%DATABASE%", getConfig().getString("mysql.database"));
            try {
                String driver = "com.mysql.jdbc.Driver";
                Class.forName(driver).newInstance();
                con = DriverManager.getConnection(dbUrl, getConfig().getString("mysql.username"), getConfig().getString("mysql.password"));
                return true;
            } catch (SQLException e) {
                System.out.println("[Anti-Xray] Can't connect to database!");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("Invalid storageType!");
        }
        return false;
    }




    public Connection getCon() {
        return con;
    }
}
