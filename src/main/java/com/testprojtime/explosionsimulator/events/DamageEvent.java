package com.testprojtime.explosionsimulator.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageEvent implements Listener {
    @EventHandler
    public void onPlayerDamange(EntityDamageEvent e){
        e.setCancelled(false);
    }
}
