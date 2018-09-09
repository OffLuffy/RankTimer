package me.offluffy.RankTimer;

import me.offluffy.RankTimer.commands.RankTimerReload;
import me.offluffy.RankTimer.commands.RankTimerToggle;
import me.offluffy.RankTimer.commands.RankTimerToggleMetrics;
import me.offluffy.RankTimer.utils.LoggablePlugin;
import net.milkbowl.vault.permission.Permission;
import org.apache.commons.lang.text.StrSubstitutor;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("SpellCheckingInspection")
public class RankTimer extends LoggablePlugin {

	private static Metrics metrics;
	private Permission perms;
	public boolean enabled = true;
	private int taskId = -1;

	public void onEnable() {

		if (!setupPermissions()) {
			logSevere("Could not find Vault! This is required for this plugin to function");
			logSevere("Disabling " + getPluginName() + "...");
			getServer().getPluginManager().disablePlugin(this);
		}

		saveDefaultConfig();
		reloadConfig();
		if (getConfig().getBoolean("enable-metrics", true)) { metrics = new Metrics(this); } else { metrics = null; }
		enabled = getConfig().getBoolean("enabled", true);

		getServer().getPluginCommand("ranktimertoggle").setExecutor(new RankTimerToggle(this));
		getServer().getPluginCommand("ranktimertogglemetrics").setExecutor(new RankTimerToggleMetrics(this));
		getServer().getPluginCommand("ranktimerreload").setExecutor(new RankTimerReload(this));

		setTaskEnabled(enabled);

		banner();
//		resourceId = "";
//		checkUpdate();
	}

	public void onDisable() { setTaskEnabled(false); }

	public void setTaskEnabled(boolean taskEnabled) {
		if (taskId != -1 && !taskEnabled) { getServer().getScheduler().cancelTask(taskId); }
		else if (taskEnabled && (taskId == -1 || !getServer().getScheduler().isCurrentlyRunning(taskId))) {
			taskId = getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
				if (!enabled) return;
				for (Player p : getServer().getOnlinePlayers()) {

					if (p.isOp() && getConfig().getBoolean("ignore-op", true)) continue;

					boolean skip = false;
					for (String ignoreGroup : getConfig().getStringList("ignore-groups")) {
						if (perms.playerInGroup(p, ignoreGroup)) { skip = true; break; }
					}
					if (skip) continue;

					for (String key : getConfig().getConfigurationSection("rank-up-timers").getKeys(false)) {
						String path = "rank-up-timers." + key + ".";
						String perm = getConfig().getString(path + "required-permission");
						int mins = getConfig().getInt(path + "minutes-played");
						List<String> giveRanks = getConfig().getStringList(path + "ranks-to-give");
						List<String> takeRanks = getConfig().getStringList(path + "ranks-to-remove");
						List<String> givePerms = getConfig().getStringList(path + "perms-to-give");
						List<String> takePerms = getConfig().getStringList(path + "perms-to-remove");
						String msg = trans(getConfig().getString(path + "message", ""), p);
						String cMsg = trans(getConfig().getString(path + "console-message", ""), p);
						if (!p.hasPermission(perm) && !perms.has(p, perm)) continue;
						if (p.getPlayerTime() > mins * 60 * 20) {
							logDebug("=====[ Executing " + path + " for player: " + p.getName() + " ]=====");
							for (String giveRank : giveRanks) {
								boolean b = perms.playerAddGroup(p, giveRank);
								logDebug(("Granting rank: " + giveRank + " to player: " + p.getName() + ", ") + (b ? "Success" : "Failed"));
							}
							for (String takeRank : takeRanks) {
								boolean b = perms.playerRemoveGroup(p, takeRank);
								logDebug(("Removing rank: " + takeRank + " to player: " + p.getName() + ", ") + (b ? "Success" : "Failed"));
							}
							for (String givePerm : givePerms) {
								boolean b = perms.playerAdd(p, givePerm);
								logDebug(("Granting perm: " + givePerm + " to player: " + p.getName() + ", ") + (b ? "Success" : "Failed"));
							}
							for (String takePerm : takePerms) {
								boolean b = perms.playerRemove(p, takePerm);
								logDebug(("Removing perm: " + takePerm + " to player: " + p.getName() + ", ") + (b ? "Success" : "Failed"));
							}
							p.sendMessage(msg);
							logCustom(cMsg);
							logDebug("=====[ Done executing rank-up-timer ]=====");
						}
					}
				}
			}, 600L, 600L);
		}
	}

	private String trans(String msg, Player target) {
		msg = ChatColor.translateAlternateColorCodes('&', msg);
		Map<String, String> repl = new HashMap<>();
		repl.put("USER", target.getName());
		StrSubstitutor ss = new StrSubstitutor(repl, "{", "}");
		msg = ss.replace(msg);
		return msg;
	}

	public boolean toggleMetrics() {
		getConfig().set("enable-metrics", !getConfig().getBoolean("enable-metrics", true));
		saveConfig();
		if (getConfig().getBoolean("enable-metrics", true)) { metrics = new Metrics(this); return true; }
		else { metrics = null; return false; }
	}

	private void banner() {
		logCustom(ChatColor.DARK_PURPLE + "  __ " +  ChatColor.BLUE + " ___");
		logCustom(ChatColor.DARK_PURPLE + " |__)" +  ChatColor.BLUE + "  |     " + ChatColor.DARK_PURPLE + getDescription().getName() + ChatColor.BLUE + " v" + getDescription().getVersion());
		logCustom(ChatColor.DARK_PURPLE + " | \\ "+  ChatColor.BLUE + "  |     " + ChatColor.DARK_GRAY + "Running on " + Bukkit.getVersion() + " " + Bukkit.getBukkitVersion());
		logCustom("");
	}

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null;
	}

}