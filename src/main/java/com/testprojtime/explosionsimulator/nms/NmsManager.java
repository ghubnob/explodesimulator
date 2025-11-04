package com.testprojtime.explosionsimulator.nms;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.util.Objects;

public class NmsManager {

    public static void sendActionBar(Player player, String message) {
        IChatBaseComponent text = createText(message,true);
        PacketPlayOutChat packet = new PacketPlayOutChat(text, ChatMessageType.GAME_INFO);
        EntityPlayer nmsPlayer = Objects.requireNonNull(getPlayer(player));
        nmsPlayer.playerConnection.sendPacket(packet);
    }

    public static void sendMsg(Player player, String msg) {
        IChatBaseComponent text = createText(msg,true);
        PacketPlayOutChat packet = new PacketPlayOutChat(text, ChatMessageType.CHAT);
        EntityPlayer nmsPlayer = Objects.requireNonNull(getPlayer(player));
        nmsPlayer.playerConnection.sendPacket(packet);
    }

    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        PlayerConnection connection = Objects.requireNonNull(getPlayer(player)).playerConnection;

        IChatBaseComponent titleComp = createText(title,false);
        IChatBaseComponent subComp = createText(subtitle,true);

        connection.sendPacket(new PacketPlayOutTitle(fadeIn, stay, fadeOut));
        connection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleComp));
        connection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subComp));
    }

    public static void sendSpawnEntitySheep(Player player) {
        PlayerConnection connection = Objects.requireNonNull(getPlayer(player)).playerConnection;

        EntitySheep sheep = new EntitySheep(Objects.requireNonNull(getPlayer(player)).world);
        sheep.setNoGravity(true);
        sheep.setInvisible(false);
        sheep.setLocation(-64.5f, 6.f, -64.5f, 0f, 0f);
        sheep.setCustomName(ChatColor.DARK_RED+"Sheep!");
        sheep.setCustomNameVisible(true);

        connection.sendPacket(new PacketPlayOutSpawnEntityLiving(sheep));

        connection.sendPacket(new PacketPlayOutEntityMetadata(sheep.getId(),sheep.getDataWatcher(),true));

        NmsPacketListener.registerNpc(player,sheep.getId());
    }

    @Deprecated
    public static org.bukkit.inventory.ItemStack setNBTTag(Player p, org.bukkit.inventory.ItemStack bktItem, String key, int value) {
        if (bktItem == null) return bktItem;

        ItemStack nmsItem = CraftItemStack.asNMSCopy(bktItem);
        if (nmsItem.isEmpty()) return bktItem;

        NBTTagCompound tag = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
        assert tag != null;
        tag.setInt(key, value);
        nmsItem.setTag(tag);

        replaceTnt(p, bktItem, CraftItemStack.asBukkitCopy(nmsItem));

        return CraftItemStack.asBukkitCopy(nmsItem);
    }
    @Deprecated
    public static int getNBTTag(org.bukkit.inventory.ItemStack bukkitItem, String key) {
        if (bukkitItem == null) return 0;

        ItemStack nmsItem = CraftItemStack.asNMSCopy(bukkitItem);
        if (nmsItem.isEmpty() || !nmsItem.hasTag()) return 0;

        NBTTagCompound tag = nmsItem.getTag();
        if (tag == null || !tag.hasKey(key)) return 0;

        return tag.getInt(key);
    }

    public static IChatBaseComponent createText(String message, boolean bold) {
        return IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + message + "\", \"bold\":"+bold+"}");
    }
    public static EntityPlayer getPlayer(Player player) {
        if (!player.isOnline()) return null;
        return ((CraftPlayer)player).getHandle();
    }
    public static ItemStack findTNT(Player p) {
        EntityPlayer pl = ((CraftPlayer)p).getHandle();
        for (int i = 0; i < pl.inventory.getSize(); i++) {
            ItemStack item = pl.inventory.getItem(i);
            if (!item.isEmpty()) {
                if (item.getItem() == Item.getById(46)) {
                    return item;
                }
            }
        }
        return null;
    }
    public static void replaceTnt(Player player, org.bukkit.inventory.ItemStack oldTnt, org.bukkit.inventory.ItemStack newTnt) {
        EntityPlayer pl = ((CraftPlayer)player).getHandle();

        ItemStack oldNmsTnt = CraftItemStack.asNMSCopy(oldTnt);
        ItemStack newNmsTnt = CraftItemStack.asNMSCopy(newTnt);

        for (int i = 0; i < pl.inventory.getSize(); i++) {
            ItemStack inventoryItem = pl.inventory.getItem(i);

            if (!inventoryItem.isEmpty() &&
                    inventoryItem.getItem() == Item.getById(46) &&
                    itemsAreEqual(inventoryItem, oldNmsTnt)) {

                pl.inventory.setItem(i, newNmsTnt);
                break;
            }
        }
        pl.updateInventory(pl.activeContainer);
    }

    public static boolean itemsAreEqual(ItemStack item1, ItemStack item2) {
        if (item1.getItem() != item2.getItem()) return false;

        if (item1.hasTag() != item2.hasTag()) return false;
        if (item1.hasTag() && !item1.getTag().equals(item2.getTag())) return false;

        return true;
    }

    public static void setTNTLevel(Player player, int level) {
        EntityPlayer pl = ((CraftPlayer)player).getHandle();

        for (int i = 0; i < pl.inventory.getSize(); i++) {
            ItemStack item = pl.inventory.getItem(i);
            if (!item.isEmpty() && item.getItem() == Item.getById(46)) {
                NBTTagCompound tag = item.hasTag() ? item.getTag() : new NBTTagCompound();
                assert tag != null;
                tag.setInt("level", level);
                item.setTag(tag);

                pl.playerConnection.sendPacket(new PacketPlayOutSetSlot(
                        pl.activeContainer.windowId, i, item
                ));

                player.sendMessage("TNT level set to: " + level);
                break;
            }
        }
        pl.updateInventory(pl.activeContainer);
    }
    public static int getTntLevel(ItemStack tnt) {
        if (tnt.getItem() != Item.getById(46)) return 0;
        if (!tnt.hasTag() || tnt.getTag()==null) return 0;
        return tnt.getTag().getInt("level");
    }
}