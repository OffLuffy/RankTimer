package commands;

import me.offluffy.RankTimer.RankTimer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RankTimerToggle implements CommandExecutor {
	private RankTimer sp;
	public RankTimerToggle(RankTimer plugin) { sp = plugin; }
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.hasPermission("ranktimer.toggle")) {
			sp.enabled = !sp.enabled;
			sp.setTaskEnabled(sp.enabled);
			sp.getConfig().set("enabled", sp.enabled);
			sp.saveConfig();
			sender.sendMessage(ChatColor.AQUA + sp.getDescription().getName() + " has been " + (sp.enabled ? "enabled" : "disabled"));
			RankTimer.logInfo(sender.getName() + " has " + (sp.enabled ? "enabled" : "disabled") + " " + sp.getDescription().getName());
		} else {
			sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
		}
		return true;
	}
}
