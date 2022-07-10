package me.ziprow.tetris;

import me.ziprow.tetris.commands.MainCommand;
import me.ziprow.tetris.commands.TabComplete;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class Tetris extends JavaPlugin
{

	public static final String MAIN_COLOR = "&c";
	public static final String PREFIX = Utils.color("&8[" + MAIN_COLOR + "Tetris&8] &r");

	private static Tetris main;
	private static final Map<String, String> PHRASES = new HashMap<>();

	private static final Listener[] LISTENERS = new Listener[] {};

	@Override
	public void onEnable()
	{
		main = this;

		saveDefaultConfig();
		loadLang();

		registerCommands();
		registerEvents();

		saveResource("Classic Tetris.zip", true);
	}

	private void loadLang()
	{
		saveResource("english.yml", true);

		String lang = getConfig().getString("language") + ".yml";
		YamlConfiguration langConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), lang));

		for(String phrase : langConfig.getKeys(false))
			PHRASES.put(phrase, langConfig.getString(phrase));
	}

	private void registerCommands()
	{
		PluginCommand command = getCommand("tetris");
		assert command != null;
		command.setExecutor(new MainCommand());
		command.setTabCompleter(new TabComplete());
	}

	private void registerEvents()
	{
		for(Listener listener : LISTENERS)
			Bukkit.getPluginManager().registerEvents(listener, this);
	}

	public static void reload()
	{
		main.reloadConfig();
		main.saveDefaultConfig();
		main.loadLang();

		Bukkit.getLogger().info(ChatColor.stripColor(PREFIX) + "Settings Reloaded");
	}

	public static Tetris get()
	{
		return main;
	}

	public static String getPhrase(String key)
	{
		return PHRASES.get(key);
	}

}
