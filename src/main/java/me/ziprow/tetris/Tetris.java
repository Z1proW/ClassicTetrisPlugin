package me.ziprow.tetris;

import me.ziprow.tetris.commands.MainCommand;
import me.ziprow.tetris.commands.TabComplete;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class Tetris extends JavaPlugin
{

	public static final String MAIN_COLOR = "&c";
	public static final String PREFIX = Utils.color("&8[" + MAIN_COLOR + "Tetris&8] &r");

	@Override
	public void onEnable()
	{
		registerCommands();
	}

	private void registerCommands()
	{
		PluginCommand command = getCommand("tetris");
		command.setExecutor(new MainCommand());
		command.setTabCompleter(new TabComplete());
	}

	public static void reload()
	{
		// TODO

		Bukkit.getLogger().info(ChatColor.stripColor(PREFIX) + "Settings Reloaded");
	}

}
