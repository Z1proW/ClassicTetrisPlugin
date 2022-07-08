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

	private static final World WORLD = Bukkit.createWorld(new WorldCreator("classictetris"));

	@Override
	public void onCommand(Player p, String[] args)
	{
		Location backupLocation = p.getLocation();
		p.teleport(WORLD.getSpawnLocation());
		new BoardPanel(p.getLocation().add(0, 40, 0), new Game(0));
	}

	@Override
	public String getPermission()
	{
		return "tetris.user";
	}

}
