package me.ziprow.tetris.commands;

import me.ziprow.tetris.BoardPanel;
import org.bukkit.entity.Player;

public class PlayCommand implements SubCommand
{

	@Override
	public void onCommand(Player p, String[] args)
	{
		int startLevel = args.length > 1 ? Integer.parseInt(args[1]) : 0;
		new BoardPanel(p, startLevel);
	}

	@Override
	public String getPermission()
	{
		return "tetris.user";
	}

}
