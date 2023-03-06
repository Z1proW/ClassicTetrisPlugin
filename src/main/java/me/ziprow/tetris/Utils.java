package me.ziprow.tetris;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public final class Utils
{

	public static final String LINE_SEPARATOR = StringUtils.repeat('-', 12);
	private static final ChatColor INFORM_COLOR = ChatColor.YELLOW;
	private static final ChatColor WARN_COLOR = ChatColor.RED;

	public static String color(String s)
	{
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	public static void sendMessage(Player p, String... msgs)
	{
		for(String msg : msgs)
			p.sendMessage(Utils.color(msg));
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
