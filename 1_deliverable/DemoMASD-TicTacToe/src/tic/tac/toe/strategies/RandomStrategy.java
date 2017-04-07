package tic.tac.toe.strategies;

import java.util.List;
import java.util.Random;

import tic.tac.toe.logic.Board;
import tic.tac.toe.logic.Cell;
import tic.tac.toe.logic.Game;

public class RandomStrategy implements Strategy {

	private final Random rng;
	
	public RandomStrategy() {
		rng = new Random(42L);
	}
	
	public RandomStrategy(long seed) {
		rng = new Random(seed);
	}
	
	@Override
	public Cell onNewTurn(Game game) {
		Board board = game.getBoard();
		List<Cell> available = board.getEmptyCells();
		if (available.isEmpty()) {
			return null;
		}
		int choice = rng.nextInt(available.size());
		return available.get(choice);
	}

}
