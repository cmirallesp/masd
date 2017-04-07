package tic.tac.toe.logic;

/**
 * Exception used to manage errors in the Tic Tac Toe application.
 * 
 * @author sprkrd
 *
 */
public class TicTacToeException extends Exception {

	private static final long serialVersionUID = 6064674460584616957L;

	/**
	 * Behaves just like the superclass' constructor.
	 */
	public TicTacToeException() {
		super();
	}

	/**
	 * Behaves just like the superclass' constructor.
	 */
	public TicTacToeException(String msg, Throwable ex) {
		super(msg, ex);
	}

	/**
	 * Behaves just like the superclass' constructor.
	 */
	public TicTacToeException(String msg) {
		super(msg);
	}

	/**
	 * Behaves just like the superclass' constructor.
	 */
	public TicTacToeException(Throwable ex) {
		super(ex);
	}
	
}
