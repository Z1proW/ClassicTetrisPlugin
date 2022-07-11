package me.ziprow.tetris.game;

import me.ziprow.tetris.BoardPanel;

import java.util.Timer;
import java.util.TimerTask;

public class Game
{

	private static final double TICK_TIME = 16.66; // 1000/60 = 16.67ms delay between ticks with 60fps

	GameState state = GameState.INITIALIZING;
	private final Board board;
	private final NextBox nextBox;
	private final BoardPanel panel;
	private Tetrimino current;
	private final int startLevel;
	private int level, linesCleared;
	private long score;

	public Game(BoardPanel panel, int startLevel)
	{
		this.panel = panel;
		board = new Board(10, 20, panel);
		nextBox = new NextBox();
		this.startLevel = startLevel;
		level = startLevel;
		start();
	}

	public void start()
	{
		if(state == GameState.INITIALIZING)
		{
			state = GameState.PLAYING;
			updateCurrent();
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
	}

	private void startGravity()
	{
		new Timer().schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				if(state == GameState.PLAYING)
				{
					panel.draw();
					moveDown();
				}
			}
		}, (long)(100*TICK_TIME), (long)(getGravityDelay()*TICK_TIME));
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
		}
	}

	public void rotateCounterClockwise()
	{
		if(state == GameState.PLAYING && current != null)
		{
			current.rotateCounterClockwise();
			if(board.isBlocked(current))
				current.rotateClockwise();
		}
	}

	public void moveLeft()
	{
		if(state == GameState.PLAYING && current != null)
		{
			current.move(-1, 0);
			if(board.isBlocked(current))
				current.move(1, 0);
		}
	}

	public void moveRight()
	{
		if(state == GameState.PLAYING && current != null)
		{
			current.move(1, 0);
			if(board.isBlocked(current))
				current.move(-1, 0);
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

				new Timer().schedule(new TimerTask()
				{
					@Override
					public void run()
					{
						if(current != null && board.isPlacable(current))
							placeCurrent();
					}
				}, (long)(10*TICK_TIME));
			}
		}
	}

	private void placeCurrent()
	{
		board.place(current);

		current = null;

		int cleared = board.clearFullLines();

		linesCleared += cleared;

		checkTransition();

		score += switch(cleared)
				{
					default -> 0;
					case 1 ->   40L;
					case 2 ->  100L;
					case 3 ->  300L;
					case 4 -> 1200L;
				}*(level+1);

		updateCurrent();
	}

	private void checkTransition()
	{
		if(startLevel == level)
		{
			if(linesCleared >= startLevel * 10L + 10 || linesCleared >= Math.max(100, startLevel * 10 - 50))
				transition();
		}
		else if(linesCleared >= 10)
			transition();
	}

	private void transition()
	{
		if(state == GameState.PLAYING)
		{
			level++;
			linesCleared = 0;
		}
	}

	public void pause()
	{
		if(state == GameState.PLAYING)
			state = GameState.PAUSED;
		else if(state == GameState.PAUSED)
			state = GameState.PLAYING;
	}

	public void gameOver()
	{
		state = GameState.GAME_OVER;
		try {Thread.sleep(1000);}
		catch(InterruptedException e) {throw new RuntimeException(e);}
		current = null;
		board.close();
	}

	public void reset()
	{
		state = GameState.INITIALIZING;
		board.clear();
		nextBox.reset();
		current = null;
		level = startLevel;
		linesCleared = 0;
		score = 0;
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

	public int getLinesCleared()
	{
		return linesCleared;
	}

}
