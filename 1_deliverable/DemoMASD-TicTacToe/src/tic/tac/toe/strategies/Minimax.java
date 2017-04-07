package tic.tac.toe.strategies;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import tic.tac.toe.logic.Board;
import tic.tac.toe.logic.Cell;
import tic.tac.toe.logic.Game;
import tic.tac.toe.logic.TicTacToeException;

/**
 * Minimax algorithm (perfect player). Nonetheless, it uses a random number generator
 * for choosing between the most advantageous moves.
 * @author sprkrd
 *
 */
public class Minimax implements Strategy {

	private final Random rng;
	private char player;
	private char opponent;
	
	/**
	 * We can save some computation by choosing among the corners and the center at the beginning
	 * of the game, since we know these are safe moves.
	 * 
	 * @return A random corner, or the center of the board.
	 */
	public static List<Cell> giveStartingMovements() {
		// We already know that the corners and the center are safe moves
		// at the beginning of the game
		List<Cell> bestMoves = new LinkedList<>();
		bestMoves.add(new Cell(0, 0));
		bestMoves.add(new Cell(0, 2));
		bestMoves.add(new Cell(1, 1));
		bestMoves.add(new Cell(2, 0));
		bestMoves.add(new Cell(2, 2));
		return bestMoves;
	}
	
	/**
	 * Recursive implementation of minimax. 
	 * @param board Current state
	 * @param maximizing Whether it is the turn of the maximizing player (i.e. me) or the
	 * minimizing player (i.e. opponent).
	 * @return value (utility) of the best move: +1 for victories, 0 for ties and -1 for defeats.
	 */
	public int minimax(Board board, boolean maximizing) {
		char winner = board.getWinner();
		if (winner != ' ') {
			return winner == player? 1 : -1;
		}
		if (board.isFull()) {
			return 0;
		}
		int bestValue = maximizing? Integer.MIN_VALUE : Integer.MAX_VALUE;
		List<Cell> availableMoves = board.getEmptyCells();
		for (Cell cell : availableMoves) {
			try {cell.setContent(maximizing? player : opponent);}
			catch (TicTacToeException ex) {/*Should not happen*/}
			int value = minimax(board, !maximizing);
			if (maximizing && value > bestValue) {
				bestValue = value;
			}
			else if (!maximizing && value < bestValue) {
				bestValue = value;
			}
			cell.reset();
		}
		return bestValue;
	}
	
	/**
	 * Top minimax procedure. It provides a list with the most advantageous moves
	 * (i.e. those that lead to a state with the greatest utility).
	 * @param game Current game object
	 * @return List of movements.
	 */
	public List<Cell> minimax_top(Game game) {
		Board board = game.getBoard();
		char player = game.getNextPlayer();
		List<Cell> bestMoves = new LinkedList<>();
		List<Cell> availableMoves = board.getEmptyCells();
		int bestValue = Integer.MIN_VALUE;
		for (Cell cell : availableMoves) {
			try {cell.setContent(player);}
			catch (TicTacToeException ex) {/*Should not happen*/}
			int value = minimax(board, false);
			cell.reset();
			if (value > bestValue) {
				bestValue = value;
				bestMoves.clear();
				bestMoves.add(cell);
			}
			if (value == bestValue) {
				bestMoves.add(cell);
			}
		}
		return bestMoves;
	}
	
	/**
	 * Creates Minimax object. RNG with default seed.
	 */
	public Minimax() {
		rng = new Random(42L);
	}
	
	/**
	 * Creates Minimax object with custom seed
	 * @param seed Custom seed.
	 */
	public Minimax(long seed) {
		rng = new Random(seed);
	}
	
	/**
	 * Overrided onNewTurn element to call the Minimax algorithm.
	 */
	@Override
	public Cell onNewTurn(Game game) {
		player = game.getNextPlayer();
		opponent = player == 'X'? 'O' : 'X';
		List<Cell> bestMoves = game.getTurn() == 0? giveStartingMovements() : minimax_top(game);
		if (bestMoves.isEmpty()) {
			return null;
		}
		return bestMoves.get(rng.nextInt(bestMoves.size()));
	}

}
