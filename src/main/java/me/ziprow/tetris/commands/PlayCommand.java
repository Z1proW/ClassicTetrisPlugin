package me.ziprow.tetris.commands;

import me.ziprow.tetris.BoardPanel;
import me.ziprow.tetris.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

public class PlayCommand implements SubCommand
{

	@Override
	public void onCommand(Player p, String[] args)
	{
		new BoardPanel(p, 0);
	}

	@Override
	public String getPermission()
	{
		return "tetris.user";
	}

}
