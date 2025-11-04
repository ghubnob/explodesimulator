package com.testprojtime.explosionsimulator;

import com.testprojtime.explosionsimulator.events.CExplodeEvent;
import com.testprojtime.explosionsimulator.messages.DatabaseManager;
import com.testprojtime.explosionsimulator.nms.NmsManager;
import net.minecraft.server.v1_12_R1.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class ExplodeUtils {
    public static void triggerCustomExplosion(Player p, Location loc, int radius, JavaPlugin plugin, DatabaseManager dbm) {
        World world = loc.getWorld();
        final Set<Material> ALLOWED_BLOCKS = EnumSet.of(
                Material.CONCRETE,
                Material.QUARTZ_BLOCK,
                Material.BONE_BLOCK,
                Material.STAINED_GLASS
        );

        //blocks to explode
        List<Block> blocks = getExplodedBlocks(radius, loc, ALLOWED_BLOCKS);

        //creating and calling an event
        CExplodeEvent event = new CExplodeEvent(
                p, world.getBlockAt(loc), blocks, 0.3f
        );
        Bukkit.getPluginManager().callEvent(event);

        //save to database
        dbm.addBlocks(p.getUniqueId().toString(), event.getBlockList().size());

        //explode blocks
        explodeBlocks(loc, event, plugin);
    }

    // method for getting blocks to explode
    private static List<Block> getExplodedBlocks(int radius, Location loc, Set<Material> ALLOWED_BLOCKS) {
        World world = loc.getWorld();
        List<Block> blocks = new ArrayList<>();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distanceSquared = x*x + y*y + z*z;
                    if (distanceSquared <= radius*radius) {
                        Block b = world.getBlockAt(loc.clone().add(x, y, z));
                        if (b.getType().isSolid() && ALLOWED_BLOCKS.contains(b.getType())) blocks.add(b);
                    }
                }
            }
        }
        return blocks;
    }

    // method for exploding blocks
    private static void explodeBlocks(Location loc, CExplodeEvent event, JavaPlugin plugin) {
        World world = loc.getWorld();
        if (!event.isCancelled()) {
            for (Block b : event.getBlockList()) {
                Material oldType = b.getType();
                b.setType(Material.AIR);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    b.setType(oldType);
                }, 200);
            }
        }
        world.createExplosion(loc, 0f);
    }
}
