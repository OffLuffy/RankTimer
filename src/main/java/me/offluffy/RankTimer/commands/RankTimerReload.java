package me.offluffy.RankTimer.commands;

import me.offluffy.RankTimer.RankTimer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RankTimerReload implements CommandExecutor {
	private RankTimer sp;
	public RankTimerReload(RankTimer plugin) { sp = plugin; }
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.hasPermission("ranktimer.reload")) {
			sp.reloadConfig();
			sender.sendMessage(ChatColor.AQUA + sp.getDescription().getName() + " has been reloaded");
		} else {
			sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
		}
		return true;
	}
}
