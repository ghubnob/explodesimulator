package com.testprojtime.explosionsimulator.nms;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class NmsEntitySpawner {

    public static void spawnArmorStand(Location loc, String name, JavaPlugin plugin) {
        WorldServer world = ((CraftWorld) loc.getWorld()).getHandle();

        EntityArmorStand armorStand = new EntityArmorStand(world);

        armorStand.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());

        armorStand.setInvisible(true);
        armorStand.setNoGravity(true);
        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName(name);

        world.addEntity(armorStand);

        Bukkit.getScheduler().runTaskLater(plugin,()->{
            world.removeEntity(armorStand);
        },100);
    }
}
