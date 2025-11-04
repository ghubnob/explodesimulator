package com.testprojtime.explosionsimulator.events;

import com.testprojtime.explosionsimulator.messages.DatabaseManager;
import com.testprojtime.explosionsimulator.nms.NmsManager;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;

public class GuiInvManager implements Listener {
    private final JavaPlugin plugin;
    private final DatabaseManager dbm = DatabaseManager.getInstance();
    public GuiInvManager(JavaPlugin plugin)  { this.plugin = plugin; }

    @EventHandler
    public void onInvClick(InventoryClickEvent e){
        Player p = (Player)e.getWhoClicked();
        if (p == null) {plugin.getLogger().info(ChatColor.RED+"InvClickEvent player null");return;}
        // Buy Menu
        if (e.getView().getTitle().contains("Buy menu")) {
            e.setCancelled(true);
            if (e.getCurrentItem().getType() == Material.TNT) {
                e.getWhoClicked().closeInventory();
                openConfirmGui((Player)e.getWhoClicked());
            } else e.getWhoClicked().closeInventory();
        }
        // Confirm Menu
        if (e.getView().getTitle().contains("Confirm")) {
            e.setCancelled(true);
            if (e.getSlot() == 3) {
                net.minecraft.server.v1_12_R1.ItemStack tnt = NmsManager.findTNT((Player)e.getWhoClicked());
                assert tnt != null;
                assert tnt.getTag() != null;
                int tntLevel = tnt.getTag().getInt("level");
                int price;
                switch(tntLevel) {
                    case 1: {price = 70; break;}
                    case 2: {price = 300; break;}
                    case 3: {price = 2000; break;}
                    case 4: {price = 10_000; break;}
                    case 5: {price = 80_000; break;}
                    case 6: {price = 320_000; break;}
                    case 7: {price = 700_000; break;}
                    case 8: {price = 1_900_000; break;}
                    default: price = Integer.MAX_VALUE;
                }

                int currBlocks = dbm.getBlocks(p.getUniqueId().toString());
                if (currBlocks >= price) {
                    dbm.addBlocks(p.getUniqueId().toString(),-price);
                    //NmsManager.setNBTTag(p, tnt,"level",NmsManager.getNBTTag(tnt,"level")+1);
                    NBTTagCompound tag = tnt.hasTag() ? tnt.getTag() : new NBTTagCompound();
                    tag.setInt("level", tag.getInt("level")+1);
                    tnt.setTag(tag);
                } else p.sendMessage("HAVEN'T ENOUGH BLOCKS");
            } else e.getWhoClicked().closeInventory();
        }
    }

    public static void openBuyGui(Player p, DatabaseManager dbmng) {
        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.DARK_AQUA + "Buy menu");
        ItemStack nullItem = new ItemStack(Material.STAINED_GLASS_PANE,1,(short)7);
        ItemMeta nullMeta = nullItem.getItemMeta();
        nullMeta.setDisplayName("");
        nullMeta.setLore(Collections.singletonList(""));
        nullItem.setItemMeta(nullMeta);
        for (int i=0; i<9; i++) if (i!=4) inv.setItem(i,nullItem);
        ItemStack item = new ItemStack(Material.TNT);
        ItemMeta meta = item.getItemMeta();
        //pricing tnt upgrade
        int tntLevel = NmsManager.getTntLevel(NmsManager.findTNT(p));
        int price;
        switch(tntLevel) {
            case 1: {price = 70; break;}
            case 2: {price = 300; break;}
            case 3: {price = 2000; break;}
            case 4: {price = 10_000; break;}
            case 5: {price = 80_000; break;}
            case 6: {price = 320_000; break;}
            case 7: {price = 700_000; break;}
            case 8: {price = 1_900_000; break;}
            case 0: {price = 1337228; break;}
            default: price = Integer.MAX_VALUE;
        }
        meta.setLore(Arrays.asList(ChatColor.WHITE+""+ChatColor.BOLD+"Upgrade your TNT here",
                ChatColor.LIGHT_PURPLE+""+ChatColor.BOLD+"Your TNT Level:"+ tntLevel,
                ChatColor.GOLD+"You have: "+ChatColor.BOLD+dbmng.getBlocks(p.getUniqueId().toString())+"/"+price+"$"
                ));
        meta.setDisplayName(ChatColor.GREEN+"Upgrade TNT");
        item.setItemMeta(meta);
        inv.setItem(4,item);
        p.openInventory(inv);
    }
    public static void openConfirmGui(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.DARK_AQUA + "Confirm");
        ItemStack nullItem = new ItemStack(Material.STAINED_GLASS_PANE,1,(short)7);
        ItemMeta nullMeta = nullItem.getItemMeta();
        nullMeta.setDisplayName("");
        nullMeta.setLore(Collections.singletonList(""));
        nullItem.setItemMeta(nullMeta);
        for (int i=0; i<9; i++) if (i!=3&&i!=5) inv.setItem(i,nullItem);
        ItemStack item1 = new ItemStack(Material.STAINED_GLASS_PANE,1,(short)3);
        ItemStack item2 = new ItemStack(Material.STAINED_GLASS_PANE,1,(short)4);
        ItemMeta meta1 = item1.getItemMeta();
        ItemMeta meta2 = item2.getItemMeta();
        meta1.setLore(Collections.singletonList(
                ChatColor.DARK_PURPLE+""+ChatColor.BOLD+"Your TNT Level:"+ NmsManager.getTntLevel(NmsManager.findTNT(p))));
        meta2.setLore(Collections.singletonList(""));
        meta1.setDisplayName(ChatColor.GREEN+""+ChatColor.BOLD+"Yes, upgrade TNT");
        meta2.setDisplayName(ChatColor.RED+""+ChatColor.BOLD+"No, Don't upgrade TNT");
        item1.setItemMeta(meta1);
        item2.setItemMeta(meta2);
        inv.setItem(3,item1);
        inv.setItem(5,item2);
        p.openInventory(inv);
    }
}
