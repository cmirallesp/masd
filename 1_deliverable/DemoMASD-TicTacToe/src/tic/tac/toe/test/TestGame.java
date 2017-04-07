package tic.tac.toe.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import tic.tac.toe.logic.Game;
import tic.tac.toe.logic.TicTacToeException;

/**
 * Test set for the Game class. It uses the JUnit testing framework.
 * 
 * @author sprkrd
 * @see Game
 *
 */
public class TestGame {
	
	private Game game;
	
	private void testPosition(char player, int i, int j) throws TicTacToeException {
		int turn = game.getTurn();
		game.play(player, i, j);
		if (game.isGameFinished()) {
			assertEquals(turn, game.getTurn());
		}
		else {
			assertEquals(turn + 1, game.getTurn());
			assertEquals(game.getNextPlayer(), player == 'X'? 'O' : 'X');
		}
		assertEquals(game.getCellContent(i, j), player);
	}
	
	private void testWinner(char winner) throws TicTacToeException {
		assertEquals(game.getWinner(), winner);
		assertTrue(game.isGameFinished());
	}
	
	private void playGame1() throws TicTacToeException {
		testPosition('X', 1, 1);
		testPosition('O', 1, 2);
		testPosition('X', 0, 2);
		testPosition('O', 2, 0);
		testPosition('X', 0, 0);
		testPosition('O', 2, 2);
		testPosition('X', 0, 1);
		testWinner('X');
	}
	
	private void playGame2() throws TicTacToeException {
		testPosition('X', 0, 0);
		testPosition('O', 1, 1);
		testPosition('X', 1, 0);
		testPosition('O', 2, 0);
		testPosition('X', 2, 2);
		testPosition('O', 0, 2);
		testWinner('O');
	}
	
	private void playGame3() throws TicTacToeException {
		testPosition('X', 0, 0);
		testPosition('O', 1, 1);
		testPosition('X', 1, 0);
		testPosition('O', 0, 2);
		testPosition('X', 2, 0);
		testWinner('X');
	}
	
	private void playGame4() throws TicTacToeException {
		testPosition('X', 2, 0);
		testPosition('O', 1, 1);
		testPosition('X', 1, 0);
		testPosition('O', 0, 0);
		testPosition('X', 0, 2);
		testPosition('O', 2, 2);
		testWinner('O');
	}
	
	private void playGame5() throws TicTacToeException {
		testPosition('X', 1, 1);
		testPosition('O', 0, 0);
		testPosition('X', 0, 2);
		testPosition('O', 2, 0);
		testPosition('X', 1, 0);
		testPosition('O', 1, 2);
		testPosition('X', 0, 1);
		testPosition('O', 2, 1);
		testPosition('X', 2, 2);
		testWinner(' ');
	}
	
	private void playGame6() throws TicTacToeException {
		testPosition('O', 0, 0);
		testPosition('X', 1, 1);
		testPosition('O', 1, 0);
		testPosition('X', 0, 2);
		testPosition('O', 2, 0);
		testWinner('O');
	}
	
	@Test
	public void testExampleGame1() throws TicTacToeException {
		game = new Game();
		playGame1();
		assertEquals(game.getScoreX(), 1);
		assertEquals(game.getScoreO(), 0);
	}
	
	@Test
	public void testExampleGame2() throws TicTacToeException {
		game = new Game();
		playGame2();
		assertEquals(game.getScoreX(), 0);
		assertEquals(game.getScoreO(), 1);
	}
	
	@Test
	public void testExampleGame3() throws TicTacToeException {
		game = new Game();
		playGame3();
		assertEquals(game.getScoreX(), 1);
		assertEquals(game.getScoreO(), 0);
	}
	
	@Test
	public void testExampleGame4() throws TicTacToeException {
		game = new Game();
		playGame4();
		assertEquals(game.getScoreX(), 0);
		assertEquals(game.getScoreO(), 1);
	}
	
	@Test
	public void testExampleGame5() throws TicTacToeException {
		game = new Game();
		playGame5();
		assertEquals(game.getScoreX(), 0);
		assertEquals(game.getScoreO(), 0);
	}
	
	@Test
	public void testSeveralGames() throws TicTacToeException {
		game = new Game();
		playGame1();
		game.startNewGame();
		playGame6();
		game.startNewGame();
		playGame1();
		assertEquals(game.getScoreX(), 2);
		assertEquals(game.getScoreO(), 1);
	}
	
	@Test(expected = TicTacToeException.class)
	public void testMoveAfterEndgame() throws TicTacToeException {
		game = new Game();
		playGame1();
		game.play('O', 2, 1);
	}
	
	@Test(expected = TicTacToeException.class)
	public void testWrongPlayer() throws TicTacToeException {
		game = new Game();
		game.play('X', 0, 0);
		game.play('X', 1, 1);
	}
	
	@Test(expected = TicTacToeException.class)
	public void testMoveIntoNonEmptyPosition() throws TicTacToeException {
		game = new Game();
		game.play('X', 0, 0);
		game.play('O', 1, 1);
		game.play('X', 1, 1);
	
	}

}
