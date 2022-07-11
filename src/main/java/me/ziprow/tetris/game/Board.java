package me.ziprow.tetris.game;

import me.ziprow.tetris.BoardPanel;

import java.util.*;

public class Board
{

	private final int width;
	private final int height;
	private final byte[][] board;

	private final BoardPanel panel;

	Board(int width, int height, BoardPanel panel)
	{
		this.width = width;
		this.height = height;
		this.panel = panel;
		board = new byte[height][width];
	}

	public boolean isInside(int x, int y)
	{
		return 0 <= x && x < width
			&& 0 <= y && y < height;
	}

	private boolean isEmpty(int x, int y)
	{
		return y < 0 || isInside(x, y) && board[y][x] == 0;
	}

	public boolean isBlocked(Tetrimino t)
	{
		for(int y = 0; y < t.getShape().length; y++)
			for(int x = 0; x < t.getShape()[y].length; x++)
				if(t.getShape()[y][x] != 0)
					if(!isEmpty(t.getXOffset() + x, t.getYOffset() + y))
						return true;
		return false;
	}

	public boolean isPlacable(Tetrimino t)
	{
		t.move(0, 1);
		if(isBlocked(t))
		{
			t.move(0, -1);
			return true;
		}
		return false;
	}

	public void place(Tetrimino t)
	{
		for(int y = 0; y < t.getShape().length; y++)
			for(int x = 0; x < t.getShape()[y].length; x++)
				if(t.getShape()[y][x] != 0)
					board[t.getYOffset() + y][t.getXOffset() + x] = t.getShape()[y][x];
	}

	private boolean isFull(int line)
	{
		for(int x = 0; x < width; x++)
			if(board[line][x] == 0)
				return false;
		return true;
	}

	private void moveDown(int line, int nbLines)
	{
		for(int x = 0; x < width; x++)
		{
			board[line + nbLines][x] = board[line][x];
			board[line][x] = 0;
		}
	}

	public int clearFullLines()
	{
		int cleared = 0;
		List<Integer> linesToClear = new ArrayList<>();
		List<Integer> linesToMoveDown = new ArrayList<>();
		Map<Integer, Integer> downMap = new HashMap<>();

		for(int y = height - 1; y >= 0; y--)
		{
			if(isFull(y))
			{
				linesToClear.add(y);
				cleared++;
			}
			else if(cleared > 0)
			{
				linesToMoveDown.add(y);
				downMap.put(y, cleared);
			}
		}

		if(cleared > 0)
		{
			for(int x = width/2; x >= 0; x--)
			{
				for(int y : linesToClear)
				{
					board[y][x] = 0;
					board[y][width-1 - x] = 0;
					panel.draw();
				}
				try {Thread.sleep(400/width);} catch(InterruptedException e) {throw new RuntimeException(e);}
			}

			linesToMoveDown.forEach(y -> moveDown(y, downMap.get(y)));
		}

		return cleared;
	}

	public void clear()
	{
		for(int y = 0; y < height; y++)
			for(int x = 0; x < width; x++)
				board[y][x] = 0;
	}

	public void close()
	{
		new Timer().schedule(new TimerTask()
		{
			int y = 0;
			@Override
			public void run()
			{
				for(int x = 0; x < width; x++)
					board[y][x] = (byte)(y % 3 + 1);

				panel.draw();
				y++;
				if(y >= height) cancel();
			}
		}, 0, 80);
	}

	public int get(int x, int y)
	{
		return board[y][x];
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public String toString()
	{
		StringBuilder s = new StringBuilder();
		for(int y = 0; y < height; y++)
		{
			for(int x = 0; x < width; x++)
				s.append(board[y][x]).append(" ");
			s.append("\n");
		}
		return s.toString();
	}

}
