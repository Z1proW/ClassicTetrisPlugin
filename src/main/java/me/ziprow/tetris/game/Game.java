package me.ziprow.tetris.game;

import me.ziprow.tetris.Tetris;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

import static org.bukkit.DyeColor.*;

public class Game implements Listener
{

//	private static final double TICK_TIME = 20; // 1000/60 = 16.67ms delay between ticks with 60fps in the original game

	GameState state = GameState.INITIALIZING;
	private final Board board;
	private final NextBox nextBox;
	private Tetrimino current;
	private final int startLevel;
	private int level, linesClearedLevel;
	private long score, linesClearedTotal;
	private final Player player;
	private final Location backupLoc;
	private final PlayerInventory backupInv;
	private final World world;
	private final Location boardLocation;
	private long softDrop;

	@SuppressWarnings("deprecation")
	public Game(Player player, int startLevel)
	{
		Bukkit.getPluginManager().registerEvents(this, Tetris.get());

		this.player = player;
		backupLoc = player.getLocation();
		backupInv = player.getInventory();

		player.getInventory().clear();
		player.getInventory().setItem(0, new ItemStack(Material.TRIPWIRE_HOOK));
		player.getInventory().setHeldItemSlot(0);

		world = createWorld(player);
		player.teleport(new Location(world, .5, 10, .5));

		boardLocation = new Location(world, 5, 23, 16);

		board = new Board(10, 20, this);

		for(int y = 0; y < board.getHeight(); y++)
			for(int x = 0; x < board.getWidth(); x++)
			{
				Block block = world.getBlockAt(boardLocation.getBlockX() - x, boardLocation.getBlockY() - y, boardLocation.getBlockZ());
				block.setType(Material.WOOL);
				block.setData(BLACK.getWoolData());
			}

		nextBox = new NextBox();
		this.startLevel = startLevel;
		level = startLevel;
		softDrop = 0;

		updateScoreboard();

		start();
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
		world.setStorm(false);
		world.setThundering(false);
		world.setWeatherDuration(Integer.MAX_VALUE);
		world.setTime(1000);

		return world;
	}

	private void updateScoreboard()
	{
		Utils.showScoreBoard(player,
				"Classic Tetris",
				"Level: " + level,
				"Lines: " + linesClearedTotal,
				"Score: " + score);
	}

	public void drawBoard()
	{
		if(board == null) return;

		for(int y = 0; y < board.getHeight(); y++)
			for(int x = 0; x < board.getWidth(); x++)
				drawBlock(board.get(x, y), x, y);
	}

	public void drawCurrent()
	{
		if(current == null) return;

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

	@SuppressWarnings("deprecation")
	private void drawBlock(int id, int x, int y)
	{
		world.getBlockAt(boardLocation.getBlockX() - x, boardLocation.getBlockY() - y,
				boardLocation.getBlockZ()).setData(makeColor(id));
	}

	@SuppressWarnings("deprecation")
	private byte makeColor(int id)
	{
		return (switch(id)
				{
					default -> BLACK;
					case 1 -> WHITE;
					case 2 -> switch(level % 10)
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
					case 3 -> switch(level % 10)
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

	public void start()
	{
		if(state == GameState.INITIALIZING)
		{
			state = GameState.PLAYING;
			updateCurrent();
			drawBoard();
			drawCurrent();
			startGravity();
		}
	}

	private void updateCurrent()
	{
		current = nextBox.getAndUpdate();
		current.reset();
		current.move(board.getWidth()/2, 0);
		if(board.isBlocked(current))
			gameOver();
		drawCurrent();
	}

	private void startGravity()
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				if(state == GameState.PLAYING)
				{
					drawBoard();
					drawCurrent();
					moveDown();
				}
				else cancel();
			}
		}.runTaskTimer(Tetris.get(), 100, getGravityDelay());
	}

	private long getGravityDelay()
	{
		return switch(level)
		{
			case 0 -> 48;
			case 1 -> 43;
			case 2 -> 38;
			case 3 -> 33;
			case 4 -> 28;
			case 5 -> 23;
			case 6 -> 18;
			case 7 -> 13;
			case 8 -> 8;
			case 9 -> 6;
			default ->
			{
				if(10 <= level && level < 13)
					yield 5;
				else if(13 <= level && level < 16)
					yield 4;
				else if(16 <= level && level < 19)
					yield 3;
				else if(19 <= level && level < 29)
					yield 2;
				else yield 1;
			}
		};
	}

	public void rotateClockwise()
	{
		if(state == GameState.PLAYING && current != null)
		{
			current.rotateClockwise();
			if(board.isBlocked(current))
				current.rotateCounterClockwise();
			else
			{
				playSound(GameSound.ROTATE);
				drawBoard();
				drawCurrent();
			}
		}
	}

	public void rotateCounterClockwise()
	{
		if(state == GameState.PLAYING && current != null)
		{
			current.rotateCounterClockwise();
			if(board.isBlocked(current))
				current.rotateClockwise();
			else
			{
				playSound(GameSound.ROTATE);
				drawBoard();
				drawCurrent();
			}
		}
	}

	public void moveLeft()
	{
		if(state == GameState.PLAYING && current != null)
		{
			current.move(-1, 0);
			if(board.isBlocked(current))
				current.move(1, 0);
			else
			{
				playSound(GameSound.MOVE);
				drawBoard();
				drawCurrent();
			}
		}
	}

	public void moveRight()
	{
		if(state == GameState.PLAYING && current != null)
		{
			current.move(1, 0);
			if(board.isBlocked(current))
				current.move(-1, 0);
			else
			{
				playSound(GameSound.MOVE);
				drawBoard();
				drawCurrent();
			}
		}
	}

	public void moveDown()
	{
		if(state == GameState.PLAYING && current != null)
		{
			current.move(0, 1);

			if(board.isBlocked(current))
			{
				current.move(0, -1);

				new BukkitRunnable() {
					@Override
					public void run()
					{
						if(current != null && board.isPlacable(current))
							placeCurrent();
					}
				}.runTaskLater(Tetris.get(), 10);
			}
			else
			{
				drawBoard();
				drawCurrent();
			}
		}
	}

	private void placeCurrent()
	{
		board.place(current);

		drawBoard();

		current = null;

		score += softDrop;
		updateScoreboard();

		int cleared = board.clearFullLines();

		linesClearedLevel += cleared;
		linesClearedTotal += cleared;
		updateScoreboard();

		checkTransition();

		score += switch(cleared)
				{
					default -> 0;
					case 1 ->   40L;
					case 2 ->  100L;
					case 3 ->  300L;
					case 4 -> 1200L;
				}*(level+1);
		updateScoreboard();

		switch(cleared)
		{
			case 1, 2, 3 -> playSound(GameSound.LINE_CLEAR);
			case 4 -> playSound(GameSound.TETRIS);
		}

		updateCurrent();
	}

	private void checkTransition()
	{
		if(startLevel == level)
		{
			if(linesClearedLevel >= startLevel * 10L + 10 || linesClearedLevel >= Math.max(100, startLevel * 10 - 50))
				transition();
		}
		else if(linesClearedLevel >= 10)
			transition();
	}

	private void transition()
	{
		if(state == GameState.PLAYING)
		{
			playSound(GameSound.LEVEL_UP);
			level++;
			updateScoreboard();
			linesClearedLevel = 0;
		}
	}

	public void gameOver()
	{
		state = GameState.GAME_OVER;
		try {Thread.sleep(1000);}
		catch(InterruptedException e) {throw new RuntimeException(e);}
		current = null;
		playSound(GameSound.GAME_OVER);
		board.close();
		// tell player to sneak to exit
		player.sendMessage(ChatColor.RED + "Game Over! Sneak to exit.");
	}

	public void reset()
	{
		state = GameState.INITIALIZING;
		board.clear();
		nextBox.reset();
		current = null;
		level = startLevel;
		linesClearedLevel = 0;
		score = 0;
		updateScoreboard();
		start();
	}

	public GameState getState()
	{
		return state;
	}

	public Board getBoard()
	{
		return board;
	}

	public Tetrimino getCurrent()
	{
		return current;
	}

	public Tetrimino getNext()
	{
		return nextBox.getNext();
	}

	public int getLevel()
	{
		return level;
	}

	public long getScore()
	{
		return score;
	}

	public long getLinesClearedLevel()
	{
		return linesClearedTotal;
	}

	@EventHandler
	public void onClick(PlayerInteractEvent e)
	{
		if(state == GameState.PLAYING && e.getPlayer() == player)
		{
			switch(e.getAction())
			{
				case RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK -> rotateClockwise();
				case LEFT_CLICK_AIR, LEFT_CLICK_BLOCK -> rotateCounterClockwise();
			}
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e)
	{
		if(state == GameState.PLAYING && e.getPlayer() == player)
		{
			double dx = e.getTo().getX() - e.getFrom().getX();
			double dz = e.getTo().getZ() - e.getFrom().getZ();

			if(dx > 0)
				moveLeft();
			if(dx < 0)
				moveRight();
//			if(dz > 0)
//				rotateClockwise();
			if(dz < 0)
				moveDown();

			player.teleport(new Location(world, .5, 10, .5));
		}
	}

	@EventHandler
	public void onSneak(PlayerToggleSneakEvent e)
	{
		System.out.println("onSneak");
		if(state == GameState.GAME_OVER && e.getPlayer() == player && e.isSneaking())
		{
			player.teleport(backupLoc);
			player.getInventory().clear();
			player.getInventory().setContents(backupInv.getContents());
			Bukkit.unloadWorld(world, false);
			new File(Bukkit.getWorldContainer(), world.getName()).delete();
			HandlerList.unregisterAll(this);
		}
	}

}
