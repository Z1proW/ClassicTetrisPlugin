package me.ziprow.tetris.game;

import java.util.Random;

public class NextBox
{

	private final Random random = new Random();
	private Tetrimino next;

	NextBox()
	{
		this.next = random();
	}

	private Tetrimino random()
	{
		return Tetrimino.values()[random.nextInt(Tetrimino.values().length)];
	}

	public Tetrimino getAndUpdate()
	{
		Tetrimino t = next;
		next = random();

		if(next == t)
			next = random();

		return t;
	}

	public Tetrimino getNext()
	{
		return next;
	}

	public void reset()
	{
		next = random();
	}

}
