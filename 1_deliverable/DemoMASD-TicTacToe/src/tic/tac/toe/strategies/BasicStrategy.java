package tic.tac.toe.strategies;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import tic.tac.toe.logic.Board;
import tic.tac.toe.logic.Cell;
import tic.tac.toe.logic.Game;

public class BasicStrategy implements Strategy {

	private final Random rng;
	
	public BasicStrategy() {
		rng = new Random(42L);
	}
	
	public BasicStrategy(long seed) {
		rng = new Random(seed);
	}
	
	@Override
	public Cell onNewTurn(Game game) {
		char player = game.getNextPlayer();
		char opponent = player == 'X'? 'O' : 'X';
		Board board = game.getBoard();
		List<Cell> available = board.getEmptyCells();
		List<Cell> winning = new LinkedList<>();
		Cell defensive = null;
		if (available.isEmpty()) {
			return null;
		}
		for (Cell cell : available) {
			if (board.isWinningMove(player, cell)) {
				winning.add(cell);
			}
			if (board.isWinningMove(opponent, cell)) {
				defensive = cell;
			}
		}
		if (!winning.isEmpty()) {
			return winning.get(rng.nextInt(winning.size()));
		}
		if (defensive != null) {
			return defensive;
		}
		return available.get(rng.nextInt(available.size()));
	}

}
