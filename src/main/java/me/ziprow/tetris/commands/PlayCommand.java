package me.ziprow.tetris.commands;

import me.ziprow.tetris.Tetris;
import me.ziprow.tetris.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayCommand implements SubCommand
{

	@Override
	public void onCommand(Player p, String[] args)
	{
		int startLevel = args.length > 1 ? Integer.parseInt(args[1]) : 0;
		Game game = new Game(p, startLevel);
		Bukkit.getPluginManager().registerEvents(game, Tetris.getPlugin(Tetris.class));
	}

	@Override
	public String getPermission()
	{
		return "tetris.user";
	}

}
