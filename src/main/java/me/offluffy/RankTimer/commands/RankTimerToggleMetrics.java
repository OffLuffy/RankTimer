package me.offluffy.RankTimer.commands;

import me.offluffy.RankTimer.RankTimer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RankTimerToggleMetrics implements CommandExecutor {
	private RankTimer sp;
	public RankTimerToggleMetrics(RankTimer plugin) { sp = plugin; }
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.hasPermission("ranktimer.togglemetrics")) {
			boolean metricsEnabled = sp.toggleMetrics();
			sender.sendMessage(ChatColor.AQUA + sp.getDescription().getName() + " metrics has been " + (metricsEnabled ? "enabled" : "disabled"));
		} else {
			sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
		}
		return true;
	}
}
