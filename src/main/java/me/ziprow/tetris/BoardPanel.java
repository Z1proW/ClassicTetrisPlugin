package me.ziprow.tetris;

import me.ziprow.tetris.game.Board;
import me.ziprow.tetris.game.Game;
import me.ziprow.tetris.game.Tetrimino;
import org.bukkit.Location;
import org.bukkit.Material;

import static org.bukkit.Material.*;

public class BoardPanel
{

	private final Location location;
	private final Game game;
	private final Board board;

	public BoardPanel(Location location, Game game)
	{
		this.location = location;
		this.game = game;
		this.board = game.getBoard();
		draw();
	}

	public void draw()
	{
		for(int y = 0; y < board.getHeight(); y++)
			for(int x = 0; x < board.getWidth(); x++)
			{
				location.clone().add(x, y, 0).getBlock().setType(makeColor(board.get(x, y)));
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
					location.clone().add(x + current.getXOffset(), y + current.getYOffset(), 0).getBlock().setType(makeColor(b));
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
