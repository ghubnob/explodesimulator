package com.testprojtime.explosionsimulator;

import com.testprojtime.explosionsimulator.events.*;
import com.testprojtime.explosionsimulator.messages.DatabaseManager;
import com.testprojtime.explosionsimulator.nms.NmsManager;
import com.testprojtime.explosionsimulator.nms.NmsPacketListener;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.Item;
import net.minecraft.server.v1_12_R1.ItemStack;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;

public final class ExplosionSimulator extends JavaPlugin {

    private DatabaseManager dbm1;

    @Override
    public void onEnable() {
        //Database Manager
        DatabaseManager dbm = DatabaseManager.getInstance(this);
        try { dbm.connect();
        } catch (SQLException | IOException e) {
            getLogger().info(ChatColor.RED+"SQL connecting error (onEnable): " + e); }
        dbm.initTable();
        dbm.loadAllData();

        NmsPacketListener.register(this, dbm);

        //events
        getServer().getPluginManager().registerEvents(new InteractEvent(this), this);
        getServer().getPluginManager().registerEvents(new DamageEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoined(this), this);
        getServer().getPluginManager().registerEvents(new GuiInvManager(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("ping")) {
            Player bp = (Player)sender;
            EntityPlayer p = ((CraftPlayer)bp).getHandle();
            NmsManager.sendMsg(bp, ChatColor.AQUA+"Ping: "+p.ping);
        }
        if (command.getName().equalsIgnoreCase("upgrade")) {
            Player p = (Player) sender;
            GuiInvManager.openBuyGui(p,dbm1);
        }
        if (command.getName().equalsIgnoreCase("givemetnt")) {
            Player p = (Player) sender;
            ItemStack tnt = new ItemStack(Item.getById(46));
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInt("level",1);
            org.bukkit.inventory.ItemStack bktTnt = CraftItemStack.asBukkitCopy(tnt);
            ItemMeta meta = bktTnt.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD+"TNT");
            meta.setLore(Collections.singletonList(ChatColor.GRAY+"Your exploder"));
            bktTnt.setItemMeta(meta);
            p.getInventory().addItem(bktTnt);
        }
        return false;
    }
}
