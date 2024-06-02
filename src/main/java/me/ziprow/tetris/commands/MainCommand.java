package me.ziprow.tetris.commands;

import me.ziprow.tetris.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class MainCommand implements CommandExecutor
{

	static final Map<String, SubCommand> SUB_COMMANDS = new HashMap<>()
	{{
		put("help", new HelpCommand());
		put("play", new PlayCommand());
	}};

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(!(sender instanceof Player p))
		{
			sender.sendMessage("You can only execute this command as a Player !");
			return true;
		}

		String subCommand = args.length > 0 ? args[0].toLowerCase() : "help";

		if(SUB_COMMANDS.containsKey(subCommand))
		{
			SubCommand sub = SUB_COMMANDS.get(subCommand);

			if(p.hasPermission(sub.getPermission()))
				sub.onCommand(p, args);
			else Utils.warn(p, "You do not have proper permissions !");
		}
		else Utils.warn(p, "That is not a command !");

		return true;
	}

}
