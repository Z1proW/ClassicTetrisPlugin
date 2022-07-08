package me.ziprow.tetris.commands;

import org.bukkit.entity.Player;

interface SubCommand
{

	void onCommand(Player p, String[] args);

	String getPermission();

}
