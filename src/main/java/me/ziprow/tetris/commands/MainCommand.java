package me.ziprow.tetris.commands;

import me.ziprow.tetris.Tetris;
import me.ziprow.tetris.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class MainCommand implements CommandExecutor
{

	static final Map<String, SubCommand> SUB_COMMANDS = new HashMap<String, SubCommand>()
	{{
		put("help", new HelpCommand());
		put("reload", new ReloadCommand());
		put("play", new PlayCommand());
	}};

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(sender instanceof Player)
		{
			Player p = (Player)sender;

			String subCommand = args.length > 0 ? args[0].toLowerCase() : "";

			if(SUB_COMMANDS.containsKey(subCommand))
			{
				SubCommand sub = SUB_COMMANDS.get(subCommand);

				if(p.hasPermission(sub.getPermission()))
					sub.onCommand(p, args);
				else Utils.warn(p, Tetris.getPhrase("no-permission-message"));
			}
			else Utils.warn(p, Tetris.getPhrase("not-a-command-message"));
		}
		return true;
	}

}
