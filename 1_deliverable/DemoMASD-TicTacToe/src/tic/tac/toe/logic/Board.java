package tic.tac.toe.logic;

import java.util.LinkedList;
import java.util.List;

/**
 * The class represents a simple Tic Tac Toe board.
 * @author sprkrd
 *
 */
public class Board implements Cloneable {
	
	private Cell[][] board;
	
	/**
	 * Creates an empty Tic Tac Toc board.
	 */
	public Board() {
		board = new Cell[3][3];
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				board[i][j] = new Cell(i, j);
			}
		}
	}
	
	/**
	 * Counts the number of moves of a certain player in a given diagonal (either the
	 * major diagonal or the minor one).
	 * @param player The method counts the number of moves of this player ('X' or 'O').
	 * @param major true to count in the major diagonal. false otherwise.
	 * @return The number (integer) of moves of the given player in either the major
	 * or the minor diagonal of the board.
	 * @see countHorizontal
	 * @see countVertical
	 */
	public int countDiagonal(char player, boolean major) {
		int count = 0;
		for (int i = 0; i < 3; ++i) {
			int j = major? i : (2 - i);
			if (board[i][j].getContent() == player) {
				count += 1;
			}
		}
		return count;
	}
	
	/**
	 * Counts the number of moves of a given player in one of the rows of the board.
	 * @param player The method counts the number of moves of this player ('X' or 'O').
	 * @param i the index of the row
	 * @return The number (integer) of moves of the given player in one of the rows of
	 * the board.
	 * @see countVertical
	 * @see countDiagonal
	 */
	public int countHorizontal(char player, int i) {
		int count = 0;
		for (int j = 0; j < 3; ++j) {
			if (board[i][j].getContent() == player) {
				count += 1;
			}
		}
		return count;
	}
	
	/**
	 * Counts the number of moves of a given player in one of the columns of the board.
	 * @param player The method counts the number of moves of this player ('X' or 'O').
	 * @param j the index of the column
	 * @return The number (integer) of moves of the given player in one of the columns of
	 * the board.
	 * @see countHorizontal
	 * @see countDiagonal
	 */
	public int countVertical(char player, int j) {
		int count = 0;
		for (int i = 0; i < 3; ++i) {
			if (board[i][j].getContent() == player) {
				count += 1;
			}
		}
		return count;
	}
	
	/**
	 * Gives a cell of the board
	 * @param i row index
	 * @param j column index
	 * @return The requested cell.
	 */
	public Cell getCell(int i, int j) {
		return board[i][j];
	}
	
	/**
	 * Gives a all the available (empty) cells.
	 * @return All the available cells as a list
	 */
	public List<Cell> getEmptyCells() {
		List<Cell> emptyCells = new LinkedList<Cell>();
		for (Cell[] row : board) {
			for (Cell cell : row) {
				if (cell.isEmpty()) {
					emptyCells.add(cell);
				}
			}
		}
		return emptyCells;
	}
	
	/**
	 * Gives the winner of the current game (if there is one)
	 * @return A player ('O', 'X') that has made three moves in either a row or a
	 * column, or an empty space (' ') if there is no such player.
	 */
	public char getWinner() {
		// Check the rows
		for (int i = 0; i < 3; ++i) {
			if (!board[i][0].isEmpty()) {
				char player = board[i][0].getContent();
				if (countHorizontal(player, i) == 3) {
					return player;
				}
			}
		}
		// Check the columns
		for (int j = 0; j < 3; ++j) {
			if (!board[0][j].isEmpty()) {
				char player = board[0][j].getContent();
				if (countVertical(player, j) == 3) {
					return player;
				}
			}
		}
		// Check the diagonals
		if (!board[1][1].isEmpty()) {
			char player = board[1][1].getContent();
			if (countDiagonal(player, true) == 3) {
				return player;
			}
			if (countDiagonal(player, false) == 3) {
				return player;
			}
		}
		return ' ';
	}
	
	/**
	 * Tells whether the board is completely filled.
	 * @return true if there is no more room for another move, false otherwise.
	 */
	public boolean isFull() {
		for (Cell[] row : board) {
			for (Cell cell : row) {
				if (cell.isEmpty()) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Tells whether a certain move leads to immediate victory or not.
	 * @param player The player that makes the move
	 * @param cell The cell where the player moves.
	 * @return true or false depending upon whether the movement leads to immediate victory.
	 */
	public boolean isWinningMove(char player, Cell cell) {
		if (!cell.isEmpty()) {
			return false;
		}
		
		if (countHorizontal(player, cell.getRow()) == 2) {
			return true;
		}
		
		if (countVertical(player, cell.getColumn()) == 2) {
			return true;
		}
		
		if (cell.isInMajorDiagonal() && countDiagonal(player, true) == 2) {
			return true;
		}
		
		if (cell.isInMinorDiagonal() && countDiagonal(player, false) == 2) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Set to ' ' all the cells of the board.
	 */
	public void reset() {
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				board[i][j].reset();
			}
		}
	}

	/**
	 * No-aliased copy of this board (deep copy).
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		Board clone = (Board)super.clone();
		clone.board = new Cell[3][3];
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				clone.board[i][j] = (Cell)board[i][j].clone();
			}
		}
		return clone;
	}
	
	/**
	 * Pretty print board.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(board[0][0].getContent()).append('|');
		builder.append(board[0][1].getContent()).append('|');
		builder.append(board[0][2].getContent());
		for (int i = 1; i < 3; ++i) {
			builder.append("\n-----\n").append(board[i][0].getContent());
			for (int j = 1; j < 3; ++j) {
				builder.append('|').append(board[i][j].getContent());
			}
		}
		return builder.toString();
	}
	
}
