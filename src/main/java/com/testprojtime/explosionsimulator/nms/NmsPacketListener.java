package com.testprojtime.explosionsimulator.nms;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.testprojtime.explosionsimulator.events.GuiInvManager;
import com.testprojtime.explosionsimulator.messages.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;


public class NmsPacketListener {
    private static final Map<Player, Integer> npcMap = new HashMap<>();
    /*private static DatabaseManager dbm;

    public static void setDbm(DatabaseManager dbm1) {
        dbm = dbm1;
    }*/

    public static void register(JavaPlugin plugin, DatabaseManager dbm) {
        ProtocolManager pm = ProtocolLibrary.getProtocolManager();

        pm.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                PacketContainer packet = event.getPacket();

                int entityId = packet.getIntegers().read(0);
                EnumWrappers.EntityUseAction action = packet.getEntityUseActions().read(0);

                if (action != EnumWrappers.EntityUseAction.INTERACT) {
                    return;
                }

                Integer npcId = npcMap.get(player);
                if (npcId != null && npcId == entityId) {
                    event.setCancelled(true);
                    //player.sendMessage("Sheep activated!");
                    Bukkit.getScheduler().runTask(plugin, () -> GuiInvManager.openBuyGui(player,dbm));
                }
            }
        });
    }

    public static void registerNpc(Player player, int entityId) {
        npcMap.put(player, entityId);
    }

    public static void unregisterNpc(Player player) {
        npcMap.remove(player);
    }
}