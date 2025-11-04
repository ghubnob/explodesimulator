package com.testprojtime.explosionsimulator.events;

import com.testprojtime.explosionsimulator.ScoreboardManager;
import com.testprojtime.explosionsimulator.messages.DatabaseManager;
import com.testprojtime.explosionsimulator.nms.NmsEntitySpawner;
import com.testprojtime.explosionsimulator.nms.NmsManager;
import com.testprojtime.explosionsimulator.nms.NmsPacketListener;
import net.minecraft.server.v1_12_R1.EntitySheep;
import net.minecraft.server.v1_12_R1.EnumParticle;
import net.minecraft.server.v1_12_R1.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PlayerJoined implements Listener {
    private final DatabaseManager dbm = DatabaseManager.getInstance();
    private final JavaPlugin plugin;
    public PlayerJoined(JavaPlugin plugin) {this.plugin = plugin;}
    @EventHandler
    public void onPlayerJoined(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        ScoreboardManager mng = new ScoreboardManager(dbm);
        mng.setScoreboard(e.getPlayer());

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (e.getPlayer().isOnline()) mng.setScoreboard(e.getPlayer());
        }, 20L, 20L);

        if (!e.getPlayer().isOnline()) task.cancel();
        p.setWalkSpeed(0.5f);
        p.setGameMode(GameMode.ADVENTURE);

        NmsManager.sendTitle(p, ChatColor.WHITE+"Welcome!", ChatColor.AQUA+"destroy it all", 1,30,10);
        NmsManager.sendSpawnEntitySheep(p);


        ItemStack tntItem = NmsManager.findTNT(p);
        if (!tntItem.hasTag()) NmsManager.setTNTLevel(p,1);
    }
}