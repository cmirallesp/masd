package tic.tac.toe.strategies;

import tic.tac.toe.logic.Cell;
import tic.tac.toe.logic.Game;

/**
 * Common interface for all the Strategies (game AIs).
 * 
 * @author sprkrd
 *
 */
public interface Strategy {
	
	/**
	 * Basically, every Strategy has to implement the NewTurn event.
	 * 
	 * @param game current game. The strategy must assume that it has to plan for
	 * the movement of the next player.
	 * 
	 * @return Move choice.
	 */
	public Cell onNewTurn(Game game);
	
}
