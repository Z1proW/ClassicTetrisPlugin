package me.ziprow.tetris;

import me.ziprow.tetris.game.Board;
import me.ziprow.tetris.game.Game;
import me.ziprow.tetris.game.Tetrimino;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.bukkit.Material.STONE;
import static org.bukkit.Material.WOOL;

public class BoardPanel
{

	private final Location backupLoc;
	private final World world;
	private final Game game;
	private final Board board;

	public BoardPanel(Player p, int startLevel)
	{
		backupLoc = p.getLocation();
		world = createWorld(p);
		assert world != null;
		p.teleport(world.getSpawnLocation());
		this.game = new Game(startLevel);
		this.board = game.getBoard();

		Bukkit.getScheduler().runTaskTimer(Tetris.get(), this::draw, 100, 100);
	}

	private World createWorld(Player p)
	{
		try
		{
			ZipFile zipFile = new ZipFile(new File(Tetris.get().getDataFolder(), "Classic Tetris.zip"));
			Enumeration<?> enu = zipFile.entries();
			while(enu.hasMoreElements())
			{
				ZipEntry zipEntry = (ZipEntry)enu.nextElement();
				String name = zipEntry.getName();
				File file = new File(name);
				if(name.endsWith("/"))
				{
					file.mkdirs();
					continue;
				}
				File parent = file.getParentFile();
				if (parent != null)
					parent.mkdirs();
				InputStream in = zipFile.getInputStream(zipEntry);
				FileOutputStream out = new FileOutputStream(file);
				byte[] bytes = new byte[1024];
				int length;
				while((length = in.read(bytes)) >= 0)
					out.write(bytes, 0, length);
				in.close();
				out.close();
			}
			zipFile.close();
		}
		catch(IOException e) {e.printStackTrace();}

		File worldDir = new File(Bukkit.getServer().getWorldContainer(), "Classic Tetris");
		worldDir.renameTo(new File(worldDir.getName() + p.getUniqueId()));
		return Bukkit.getWorld(worldDir.getName());
	}

	public void draw()
	{
		for(int y = 0; y < board.getHeight(); y++)
			for(int x = 0; x < board.getWidth(); x++)
			{
				world.getBlockAt(5 + x, 23 - y, 16).setType(makeColor(board.get(x, y)));
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
					world.getBlockAt(5 + x + current.getXOffset(), 23 - (y + current.getYOffset()), 16).setType(makeColor(b));
				}
			}
	}

	private Material makeColor(int color)
	{
		return switch(color)
				{
					default -> STONE;
					case 1 -> WOOL;
					/*case 2 -> switch(game.getLevel() % 10)
							{
								default -> new Color(52, 167, 255);
								case 1 -> new Color(160, 255, 77, 255);
								case 2 -> new Color(255, 143, 252);
								case 3 -> new Color(0, 183, 97);
								case 4 -> new Color(27, 255, 183);
								case 5 -> new Color(69, 140, 255);
								case 6 -> DARK_GRAY;
								case 7 -> new Color(112, 0, 0);
								case 8 -> RED;
								case 9 -> ORANGE;
							};
					case 3 -> switch(game.getLevel() % 10)
							{
								default -> BLUE;
								case 1 -> new Color(0, 115, 0);
								case 2 -> new Color(164, 0, 159);
								case 3 -> new Color(0, 65, 169);
								case 4 -> new Color(152, 0, 76);
								case 5 -> new Color(27, 255, 183);
								case 6 -> RED;
								case 7 -> new Color(74, 0, 185);
								case 8 -> new Color(0, 23, 187);
								case 9 -> new Color(248, 56, 0);
							};*/
				};
	}

}
