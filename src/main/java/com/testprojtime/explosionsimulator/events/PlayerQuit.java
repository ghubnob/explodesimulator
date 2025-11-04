package com.testprojtime.explosionsimulator.events;

import com.testprojtime.explosionsimulator.nms.NmsPacketListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {
    @EventHandler
    public static void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        NmsPacketListener.unregisterNpc(p);
    }
}
