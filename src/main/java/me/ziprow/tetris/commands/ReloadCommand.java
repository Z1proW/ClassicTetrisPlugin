package me.ziprow.tetris.commands;

import me.ziprow.tetris.Tetris;
import me.ziprow.tetris.Utils;
import org.bukkit.entity.Player;

public class ReloadCommand implements SubCommand
{

	@Override
	public void onCommand(Player p, String[] args)
	{
		Tetris.reload();
		Utils.sendMessage(p, Tetris.PREFIX + "Settings Reloaded");
	}

	@Override
	public String getPermission()
	{
		return "tetris.admin";
	}

}
