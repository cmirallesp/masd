package tic.tac.toe.test;

import tic.tac.toe.logic.Cell;
import tic.tac.toe.logic.Game;
import tic.tac.toe.logic.TicTacToeException;
import tic.tac.toe.strategies.BasicStrategy;
import tic.tac.toe.strategies.Minimax;
import tic.tac.toe.strategies.RandomStrategy;
import tic.tac.toe.strategies.Strategy;

/**
 * Non-supervised test. It executes some games and show the results through the CLI.
 * 
 * @author sprkrd
 *
 */
public class TestStrategies {
	private final Game game;
	
	public TestStrategies() {
		game = new Game();
	}
	
	public void playGame(Strategy strategy1, Strategy strategy2) {
		boolean ok = true;
		System.out.println(game);
		while (!game.isGameFinished() && ok) {
			try {
				Cell cell = strategy1.onNewTurn(game);
				game.play(game.getNextPlayer(), cell.getRow(), cell.getColumn());
				System.out.println("###########");
				System.out.println(game);
				Strategy tmp = strategy2;
				strategy2 = strategy1;
				strategy1 = tmp;
			}
			catch (TicTacToeException ex) {
				ex.printStackTrace();
				ok = false;
			}
		}
		game.startNewGame();
	}
	
	
	public static void main(String[] args) {
		TestStrategies test = new TestStrategies();
		RandomStrategy randomStrategy = new RandomStrategy();
		BasicStrategy basicStrategy = new BasicStrategy();
		Minimax minimax = new Minimax();
		System.out.println("#### Random vs Random ####");
		test.playGame(randomStrategy, randomStrategy);
		System.out.println("#### Basic vs Random ####");
		test.playGame(basicStrategy, randomStrategy);
		System.out.println("#### Basic vs Basic ####");
		test.playGame(basicStrategy, basicStrategy);
		System.out.println("#### Minimax vs Basic ####");
		test.playGame(minimax, basicStrategy);
		System.out.println("#### Minimax vs Minimax ####");
		test.playGame(minimax, minimax);
	}
}
