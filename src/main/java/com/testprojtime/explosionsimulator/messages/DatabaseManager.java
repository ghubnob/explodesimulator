package com.testprojtime.explosionsimulator.messages;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseManager {
    private Connection connection;
    private final File databaseFile;
    private final JavaPlugin plugin;
    private final Map<String, Integer> backpackCache = new ConcurrentHashMap<>();
    private static DatabaseManager instance;

    private DatabaseManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.databaseFile = new File(plugin.getDataFolder() + "data.db");
    }
    public static synchronized DatabaseManager getInstance(JavaPlugin plugin) {
        if (instance==null) instance = new DatabaseManager(plugin);
        return instance;
    }
    public static DatabaseManager getInstance() {
        if (instance == null) throw new IllegalStateException("Instance wasn't initialized");
        return instance;
    }

    // Connecting SQL Database
    public void connect() throws SQLException, IOException {
        try {
            if (!databaseFile.exists()) {
                databaseFile.getParentFile().mkdirs();
                databaseFile.createNewFile();
            }
        } catch (IOException e) {plugin.getLogger().info(ChatColor.RED+"[SQL] "+ChatColor.WHITE+"SQL connecting error: "+e);}

        String url = "jdbc:sqlite:"+databaseFile.getAbsolutePath();
        this.connection = DriverManager.getConnection(url);

//        plugin.getLogger().info("Conn instance hash: "+this.hashCode());
//        plugin.getLogger().info("Conn obj: "+connection);
        plugin.getLogger().info(ChatColor.AQUA+"[SQL] "+ChatColor.WHITE+"SQL connected!");
    }

    // Initializing SQL table
    public void initTable() {
        String sql = "CREATE TABLE IF NOT EXISTS backpacks (" +
                "player_uuid TEXT PRIMARY KEY, " +
                "blocks INTEGER DEFAULT 0" +
                ");";
        try (Statement st = connection.createStatement()) {
            st.execute(sql);
            plugin.getLogger().info(ChatColor.AQUA+"[SQL] "+ChatColor.WHITE+"SQL init success!");
        } catch (SQLException e) {plugin.getLogger().info(ChatColor.RED+"[SQL] "+ChatColor.WHITE+"SQL init error: " + e);}
    }

    // Load all data from sql to ConcurrentHashMap
    public void loadAllData() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Statement st = connection.createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM backpacks")) {
                while (rs.next()) {
                    String uuid = rs.getString("player_uuid");
                    int blocks = rs.getInt("blocks");
                    backpackCache.put(uuid, blocks);
                }
                plugin.getLogger().info("Data loaded from sql");
            } catch (SQLException e) {
                plugin.getLogger().info("Load data error: " + e);
            }
        });
    }

    public int getBlocks(String uuid) {
        return backpackCache.getOrDefault(uuid,0);
    }

    public void addBlocks(String playerUUID, int blocks) {
        if (connection == null) {plugin.getLogger().info(ChatColor.RED+"[SQL]"+ChatColor.WHITE+"Connection is null"); return;}
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                //check if exist
                String selectSql = "SELECT blocks FROM backpacks WHERE player_uuid = ?";
                try (PreparedStatement select = connection.prepareStatement(selectSql)) {
                    select.setString(1, playerUUID);
                    ResultSet rs = select.executeQuery();

                    if (rs.next()) {
                        int current = rs.getInt("blocks");
                        int newAmount = current + blocks;

                        //update
                        String updateSql = "UPDATE backpacks SET blocks = ? WHERE player_uuid = ?";
                        try (PreparedStatement update = connection.prepareStatement(updateSql)) {
                            update.setInt(1, newAmount);
                            update.setString(2, playerUUID);
                            update.executeUpdate();
                            backpackCache.put(playerUUID, newAmount);
                        }

                    } else {
                        //add player if not exist
                        String insertSql = "INSERT INTO backpacks (player_uuid, blocks) VALUES (?, ?)";
                        try (PreparedStatement insert = connection.prepareStatement(insertSql)) {
                            insert.setString(1, playerUUID);
                            insert.setInt(2, blocks);
                            insert.executeUpdate();
                            backpackCache.put(playerUUID, blocks);
                        }
                    }
                }
            } catch (SQLException e) {
                plugin.getLogger().info("Adding blocks error: "+e);
            }
        });
    }
}
