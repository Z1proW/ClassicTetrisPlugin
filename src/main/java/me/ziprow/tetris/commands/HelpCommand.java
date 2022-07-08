package me.ziprow.tetris.commands;

import me.ziprow.tetris.Tetris;
import me.ziprow.tetris.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;

class HelpCommand implements SubCommand
{

	@Override
	public void onCommand(Player p, String[] args)
	{
		Utils.sendMessage(p, Tetris.PREFIX + " Commands",
				ChatColor.DARK_PURPLE + Utils.LINE_SEPARATOR);

		for(Map.Entry<String, SubCommand> subCommand : MainCommand.SUB_COMMANDS.entrySet())
			Utils.sendMessage(p, ChatColor.DARK_PURPLE + "- " + Tetris.MAIN_COLOR + "/tetris " + ChatColor.GRAY + subCommand.getKey());

		Utils.sendMessage(p, ChatColor.DARK_PURPLE + Utils.LINE_SEPARATOR);
	}

	@Override
	public String getPermission()
	{
		return "tetris.user";
	}

}
