package me.ziprow.tetris.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TabComplete implements TabCompleter
{

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
	{
		if(!(sender instanceof Player)) return null;

		Player player = (Player)sender;
		List<String> arguments = new ArrayList<>();

		if(command.getName().equals("tetris"))
		{
			if(args.length == 1)
			{
				for(Map.Entry<String, SubCommand> subCommand : MainCommand.SUB_COMMANDS.entrySet())
					if(player.hasPermission(subCommand.getValue().getPermission()))
						arguments.add(subCommand.getKey());

				Iterator<String> iter = arguments.iterator();

				while(iter.hasNext())
				{
					String str = iter.next().toLowerCase();

					if(!str.contains(args[0].toLowerCase()))
						iter.remove();
				}
			}
		}
		return arguments;
	}

}