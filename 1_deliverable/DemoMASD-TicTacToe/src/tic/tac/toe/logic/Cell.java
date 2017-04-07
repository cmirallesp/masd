package tic.tac.toe.logic;

/**
 * A single Cell of a Tic Tac Toe board
 * @author sprkrd
 *
 */
public class Cell implements Cloneable {
	private int i;
	private int j;
	private char content;
	
	/**
	 * Creates an empty cell
	 * @param i Row index
	 * @param j Column index
	 */
	public Cell(int i, int j) {
		this.i = i;
		this.j = j;
		content = ' ';
	}
	
	/**
	 * Get the contents of the cell.
	 * @return 'X' or 'O' if a move has been made; or ' ' if the cell is empty
	 */
	public char getContent() {
		return content;
	}
	
	/**
	 * Get the index of this Cell's column
	 * @return index (0-based)
	 */
	public int getColumn() {
		return j;
	}
	
	/**
	 * Get the index of this Cell's row
	 * @return index (0-based)
	 */
	public int getRow() {
		return i;
	}
	
	/**
	 * Tells whether the Cell is empty (' ')
	 * @return getContents() == ' '
	 */
	public boolean isEmpty() {
		return getContent() == ' ';
	}
	
	/**
	 * Tells if the Cell is in the major diagonal.
	 * @return true if the cell is in the major diagonal of the board.
	 */
	public boolean isInMajorDiagonal() {
		return i == j;
	}
	
	/**
	 * Tells if the Cell is in the minor diagonal.
	 * @return true if the cell is in the minor diagonal of the board.
	 */
	public boolean isInMinorDiagonal() {
		return i == (2 - j);
	}
	
	/**
	 * Sets the content of this Cell to ' '.
	 */
	public void reset() {
		content = ' ';
	}
	
	/**
	 * Fills the Cell.
	 * @param The player that makes the move.
	 * @throws TicTacToeException if the given argument is different from
	 * 'X' or 'O'.
	 */
	public void setContent(char player) throws TicTacToeException {
		player = Character.toUpperCase(player);
		if (player != 'X' && player != 'O') {
			throw new TicTacToeException("Non-valid character (" +
					player + "). Only X and O are accepted.");
		}
		if (content != ' ') {
			throw new TicTacToeException("Position (" + i + ", " + j +
					") is not empty");
		}
		content = player;
	}

	/**
	 * Gives a copy of this Cell.
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		// No need to do anything else... All the members of this class have
		// primitive types-
		return super.clone();
	}

	/**
	 * The hash is generated according to the Cell's row and columns indices and
	 * its content.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + content;
		result = prime * result + i;
		result = prime * result + j;
		return result;
	}

	/**
	 * The equals method is overridden so two Cells are considered equal if they have
	 * the same row and column indices and the same content.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cell other = (Cell) obj;
		if (content != other.content)
			return false;
		if (i != other.i)
			return false;
		return j == other.j;
	}

	/**
	 * String with cell indices and content.
	 */
	@Override
	public String toString() {
		return "(" + i + ", " + j + ", '" + content + "')";
	}
	
	
	
	
}
