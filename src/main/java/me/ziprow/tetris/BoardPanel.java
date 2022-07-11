package me.ziprow.tetris;

import me.ziprow.tetris.game.Board;
import me.ziprow.tetris.game.Game;
import me.ziprow.tetris.game.Tetrimino;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import static org.bukkit.DyeColor.*;

public class BoardPanel
{

	private final Location backupLoc;
	private final World world;
	private final Location boardLocation;
	private final Game game;
	private final Board board;

	public BoardPanel(Player p, int startLevel)
	{
		backupLoc = p.getLocation();

		world = createWorld(p);
		p.teleport(new Location(world, .5, 10, .5));

		boardLocation = new Location(world, 5, 23, 16);

		this.game = new Game(this, startLevel);
		this.board = game.getBoard();

		for(int y = 0; y < board.getHeight(); y++)
			for(int x = 0; x < board.getWidth(); x++)
			{
				Block block = world.getBlockAt(boardLocation.getBlockX() - x, boardLocation.getBlockY() - y, boardLocation.getBlockZ());
				block.setType(Material.WOOL);
				block.setData(BLACK.getWoolData());
			}

		Bukkit.getScheduler().runTaskTimer(Tetris.get(), this::draw, 0, 2);
	}

	private World createWorld(Player p)
	{
		WorldCreator wc = new WorldCreator("tetris" + p.getUniqueId());
		wc.generateStructures(false);
		wc.type(WorldType.FLAT);
		wc.generatorSettings("2;0;1;");
		wc.createWorld();

		World world = Bukkit.getWorld("tetris" + p.getUniqueId());

		for(BlockFace face : new BlockFace[]
				{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.DOWN})
			world.getBlockAt(0, 10, 0).getRelative(face).setType(Material.BARRIER);
		world.getBlockAt(0, 12, 0).setType(Material.BARRIER);
		world.setGameRuleValue("doDaylightCycle", "false");
		world.setGameRuleValue("doMobSpawning", "false");
		world.setGameRuleValue("doFireTick", "false");
		world.setGameRuleValue("doWeatherCycle", "false");
		world.setTime(1000);

		return world;
	}

	public void draw()
	{
		for(int y = 0; y < board.getHeight(); y++)
			for(int x = 0; x < board.getWidth(); x++)
			{
				drawBlock(board.get(x, y), x, y);
			}

		Tetrimino current = game.getCurrent();

		if(current == null)
			return;

		for(int y = 0; y < current.getShape().length; y++)
			for(int x = 0; x < current.getShape()[y].length; x++)
			{
				byte b = current.getShape()[y][x];
				if(b != 0)
				{
					drawBlock(b, x + current.getXOffset(), y + current.getYOffset());
				}
			}
	}

	private void drawBlock(int id, int x, int y)
	{
		world.getBlockAt(boardLocation.getBlockX() - x, boardLocation.getBlockY() - y,
				boardLocation.getBlockZ()).setData(makeColor(id));
	}

	private byte makeColor(int id)
	{
		return (switch(id)
				{
					default -> BLACK;
					case 1 -> WHITE;
					case 2 -> switch(game.getLevel() % 10)
							{
								default -> LIGHT_BLUE;
								case 1 -> LIME;
								case 2 -> PINK;
								case 3 -> GREEN;
								case 4 -> GREEN;
								case 5 -> CYAN;
								case 6 -> SILVER;
								case 7 -> RED;
								case 8 -> RED;
								case 9 -> ORANGE;
							};
					case 3 -> switch(game.getLevel() % 10)
							{
								default -> BLUE;
								case 1 -> GREEN;
								case 2 -> MAGENTA;
								case 3 -> BLUE;
								case 4 -> MAGENTA;
								case 5 -> LIME;
								case 6 -> RED;
								case 7 -> PURPLE;
								case 8 -> BLUE;
								case 9 -> ORANGE;
							};
				}).getWoolData();
	}

}
