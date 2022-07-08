package me.ziprow.tetris;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public final class Utils
{

	public static final String LINE_SEPARATOR = StringUtils.repeat('-', 12);
	private static final ChatColor INFORM_COLOR = ChatColor.YELLOW;
	private static final ChatColor WARN_COLOR = ChatColor.RED;

	public static String color(String s)
	{
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	public static <K, V> Map<K, V> map(Object... inputs)
	{
		if((inputs.length & 1) != 0) // + null check
			throw new InternalError("length is odd");

		Map<K, V> map = new HashMap<>();

		for(int i = 0; i < inputs.length; i += 2)
		{
			@SuppressWarnings("unchecked")
			K k = Objects.requireNonNull((K)inputs[i]);
			@SuppressWarnings("unchecked")
			V v = Objects.requireNonNull((V)inputs[i+1]);
			map.put(k, v);
		}

		return map;
	}

	/* Player */

	public static void sendMessage(Player p, String msg)
	{
		p.sendMessage(Utils.color(msg));
	}

	public static void sendMessage(Player p, String... msgs)
	{
		for(String msg : msgs)
			sendMessage(p, msg);
	}

	public static void inform(Player p, String... msgs)
	{
		sendMessage(p, "", Tetris.PREFIX);
		for(String msg : msgs)
			sendMessage(p, INFORM_COLOR + msg);
	}

	public static void warn(Player p, String... msgs)
	{
		playSound(p, Sound.BAT_DEATH);
		sendMessage(p, "", Tetris.PREFIX);
		for(String msg : msgs)
			sendMessage(p, WARN_COLOR + "\u26A0 " + msg.toUpperCase() + " !");
	}

	public static void playSound(Player p, Sound sound)
	{
		p.playSound(p.getLocation(), sound, 1f, 1f);
	}

	public static void clear(Player p)
	{
		PlayerInventory inv = p.getInventory();
		inv.clear();
		inv.setHelmet(null);
		inv.setChestplate(null);
		inv.setLeggings(null);
		inv.setBoots(null);
		p.setFoodLevel(20);
		p.setTotalExperience(0);
		for(PotionEffect effect : p.getActivePotionEffects())
			p.removePotionEffect(effect.getType());
	}

	public static void showScoreBoard(Player p, String title, String... lines)
	{
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective obj = board.registerNewObjective("scoreboard", "dummy");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		obj.setDisplayName(Utils.color(title));

		int i = lines.length - 1;
		for(String line : lines)
		{
			Score score = obj.getScore(Utils.color(line));
			score.setScore(i);
			i--;
		}

		p.setScoreboard(board);
	}

}
