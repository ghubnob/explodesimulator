package com.testprojtime.explosionsimulator;

import com.testprojtime.explosionsimulator.messages.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

public class ScoreboardManager {
    private final DatabaseManager dbm;
    public ScoreboardManager(DatabaseManager dbm) {this.dbm = dbm;}
    public void setScoreboard(Player p) {
        org.bukkit.scoreboard.ScoreboardManager manager = Bukkit.getScoreboardManager();
        org.bukkit.scoreboard.Scoreboard board = manager.getNewScoreboard();
        Objective obj = board.registerNewObjective("stats", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD+p.getName()+" stats");
        obj.getScore(ChatColor.GRAY + "BLOCKS:").setScore(dbm.getBlocks(p.getUniqueId().toString()));

        p.setScoreboard(board);
    }
}
