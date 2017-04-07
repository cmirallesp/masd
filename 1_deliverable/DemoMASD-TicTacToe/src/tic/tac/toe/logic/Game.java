package tic.tac.toe.logic;

/**
 * Class that represents a Tic Tac Toe game. It keeps track of the score per player
 * and performs validation of the moves and victory conditions.
 * 
 * @author sprkrd
 *
 */
public class Game {
	
	private final Board board;
	private boolean gameFinished;
	private char firstPlayer;
	private char nextPlayer;
	private char winner;
	private int turn;
	private int scoreX;
	private int scoreO;
	
	/**
	 * Starts a new game. This method does not reset the scores, but the board.
	 */
	private void startOver() {
		turn = 0;
		nextPlayer = firstPlayer;
		gameFinished = false;
		winner = ' ';
		board.reset();
	}
	
	/**
	 * Creates a new Tic Tac Toe game, ready for the player X to begin with their first
	 * move.
	 */
	public Game() {
		board = new Board();
		scoreX = 0;
		scoreO = 0;
		firstPlayer = 'X';
		startOver();
	}
	
	/**
	 * Gives the board of the current game.
	 * @return Board object. The board is copied (cloned) so the caller of this
	 * method can modify it for its own purposes, without aliasing.
	 */
	public Board getBoard() {
		Board ret = null;
		try { ret = (Board)board.clone();}
		catch (CloneNotSupportedException e) {/* Not gonna happen */}
		return ret;
	}
	
	public char getCellContent(int i, int j) {
		return board.getCell(i, j).getContent();
	}
	
	/**
	 * @return Player X's score.
	 */
	public int getScoreX() {
		return scoreX;
	}
	
	/**
	 * @return Player O's score
	 */
	public int getScoreO() {
		return scoreO;
	}
	
	/**
	 * @return Number of the current turn
	 */
	public int getTurn() {
		return turn;
	}
	
	/**
	 * Tells if a certain Cell is empty
	 * @param i row index of the Cell
	 * @param j column index of the Cell.
	 * @return true if the Cell is empty
	 */
	public boolean isCellEmpty(int i, int j) {
		return board.getCell(i, j).isEmpty();
	}
	
	/**
	 * @return true if the current game has finished (either because of a tie or because
	 * one of the players has won) or not.
	 */
	public boolean isGameFinished() {
		return gameFinished;
	}
	
	/**
	 * @return Winner of the game
	 * @throws TicTacToeException if the game has not finished
	 */
	public char getWinner() throws TicTacToeException {
		if (!gameFinished) {
			throw new TicTacToeException("There is no winner yet");
		}
		
		return winner;
	}
	
	/**
	 * Get the player that has to move in the next turn.
	 * @return
	 */
	public char getNextPlayer() {
		return nextPlayer;
	}
	
	/**
	 * Play, filling one of the cells of the grid.
	 * @param player The player that performs the movement.
	 * @param i The row's index.
	 * @param j The column's index
	 * @throws TicTacToeException If the game has already finished, if it is not
	 * the given player's turn or if the position is already filled.
	 */
	public void play(char player, int i, int j) throws TicTacToeException {
		if (gameFinished) {
			throw new TicTacToeException("Game has already finished! Start new game.");
		}
		
		player = Character.toUpperCase(player);
		
		if (player != nextPlayer) {
			throw new TicTacToeException("It is player " + nextPlayer +
					"'s turn, not " + player + "'s");
		}
		
		board.getCell(i, j).setContent(player);
		winner = board.getWinner();
		gameFinished = winner != ' ' || board.isFull();
		if (!gameFinished) {
			turn += 1;
			nextPlayer = player == 'X'? 'O' : 'X';
		}
		else if (winner == 'X') {
			scoreX += 1;
		}
		else if (winner == 'O') {
			scoreO += 1;
		}
	}
	
	/**
	 * Starts a new game without resetting the scores (just the board, the turn,
	 * and the player that gets to move in the first turn).
	 */
	public void startNewGame() {
		firstPlayer = firstPlayer == 'X'? 'O' : 'X';
		startOver();
	}

	/**
	 * Pretty print info about the current game, displaying the board, the score and
	 * the winner (should the game have already finished) or the player that gets to move
	 * next.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Score X: ").append(scoreX).append('\n');
		builder.append("Score O: ").append(scoreO).append('\n');
		builder.append("Turn: ").append(turn).append('\n');
		if (gameFinished) {
			builder.append("Winner: ").append(winner).append('\n');
		}
		else {
			builder.append("Next player: ").append(nextPlayer).append('\n');
		}
		builder.append(board);
		return builder.toString();
	}
	
}
