package com.testprojtime.explosionsimulator.events;

import com.testprojtime.explosionsimulator.messages.DatabaseManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class CExplodeEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player p;
    private final Block b;
    private final List<Block> blockList;
    private final float yield;
    private boolean cancelled;
    public CExplodeEvent(Player p, Block b, List<Block> blockList, float yield) {
        this.p=p; this.b=b; this.blockList=blockList; this.yield=yield;
    }

    public Block getBlock() {
        return b;
    }
    public Player getPlayer() {
        return p;
    }
    public List<Block> getBlockList() {
        return blockList;
    }
    public float getYield() {
        return yield;
    }


    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
