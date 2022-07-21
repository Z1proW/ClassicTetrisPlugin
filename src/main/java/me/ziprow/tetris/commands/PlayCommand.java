package me.ziprow.tetris.commands;

import me.ziprow.tetris.game.Game;
import org.bukkit.entity.Player;

public class PlayCommand implements SubCommand
{

	@Override
	public void onCommand(Player p, String[] args)
	{
		int startLevel = args.length > 1 ? Integer.parseInt(args[1]) : 0;
		new Game(p, startLevel);
	}

	@Override
	public String getPermission()
	{
		return "tetris.user";
	}

}
