package com.testprojtime.explosionsimulator.events;

import com.testprojtime.explosionsimulator.ExplodeUtils;
import com.testprojtime.explosionsimulator.messages.DatabaseManager;
import com.testprojtime.explosionsimulator.nms.NmsEntitySpawner;
import com.testprojtime.explosionsimulator.nms.NmsManager;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class InteractEvent implements Listener {
    private final JavaPlugin plugin;
    private final DatabaseManager dbm = DatabaseManager.getInstance();
    private boolean tntThrownFlag = false;
    public InteractEvent(JavaPlugin plugin) {this.plugin = plugin;}

    // Interact Event (Right Mouse Click->Throw TNT)
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        //allowed blocks
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (e.getItem() == null) return; if (e.getItem().getType() != Material.TNT) return; if (tntThrownFlag) {NmsManager.sendActionBar(e.getPlayer(),ChatColor.WHITE+"tnt is reloading"); return;}
            Player p = e.getPlayer();
            //throw tnt code
            World world = p.getWorld();
            Item dropped = world.dropItem(e.getPlayer().getLocation(), new ItemStack(Material.TNT));
            dropped.setPickupDelay(Integer.MAX_VALUE);
            Vector velocity = e.getPlayer().getLocation().getDirection().multiply(1.75f).add(new Vector(0,0.15,0));
            dropped.setVelocity(velocity);
            tntThrownFlag = true;
            int radius = NmsManager.getTntLevel(((CraftPlayer)p).getHandle().getItemInMainHand());

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                ExplodeUtils.triggerCustomExplosion(p,dropped.getLocation(),radius,plugin,dbm);
                dropped.remove();
                tntThrownFlag = false;
            }, 60);
        }
    }
}