package tic.tac.toe.backend;

import java.util.HashMap;
import java.util.Map;

import tic.tac.toe.frontend.FrontEnd;
import tic.tac.toe.frontend.TicTacToeCLI;
import tic.tac.toe.frontend.TicTacToeGUI;
import tic.tac.toe.logic.Cell;
import tic.tac.toe.logic.Game;
import tic.tac.toe.logic.TicTacToeException;
import tic.tac.toe.strategies.BasicStrategy;
import tic.tac.toe.strategies.Minimax;
import tic.tac.toe.strategies.RandomStrategy;
import tic.tac.toe.strategies.Strategy;

/**
 * Standalone (no 2APL) main class.
 * 
 * @author sprkrd
 *
 */
public class Standalone extends Thread {
	
	private final Game game;
	private final Strategy player1;
	private final Strategy player2;
	private int timeBetweenTurns;
	private int timeAfterGame;
	private FrontEnd frontEnd;
	private volatile boolean interrupted;
	
	/**
	 * Parse integer or, in case of error, provide default.
	 * 
	 * @param valueStr Unparsed string
	 * @param defaultValue Default value to use in case of error
	 * @return parsed int
	 */
	public static int parseInt(String valueStr, int defaultValue) {
		int value = defaultValue;
		try {
			value = Integer.parseInt(valueStr);
		}
		catch (NumberFormatException ex) {
			System.err.println("No parsable integer (" + valueStr + ")");
		}
		return value;
	}
	
	/**
	 * Parse strategy
	 * 
	 * @param valueStr strategy name
	 * @param seed seed (all the strategies use a rng)
	 * @return parsed strategy (default RandomStrategy in case of error).
	 */
	public static Strategy parseStrategy(String valueStr, int seed) {
		Strategy value = null;
		if (valueStr != null) {
			switch (valueStr) {
				case "Random": value = new RandomStrategy(seed); break;
				case "Basic": value = new BasicStrategy(seed); break;
				case "Minimax": value = new Minimax(seed); break;
				default:
					System.err.println("Unknown strategy (" + valueStr + ")");
					value = new RandomStrategy(seed);
			}
		}
		return value;
	}
	
	/**
	 * Create Standalone object with parsed options.
	 * 
	 * @param options options read from the program arguments
	 */
	public Standalone(Map<String, String> options) {
		this.game = new Game();
		String player1Str = options.getOrDefault("--player1", "Random");
		String player2Str = options.getOrDefault("--player2", "Random");
		String seed1Str = options.getOrDefault("--seed1", "42");
		String seed2Str = options.getOrDefault("--seed2", "73");
		String timeAfterGameStr = options.getOrDefault("--time-after-game", "2000");
		String timeBetweenTurnsStr = options.getOrDefault("--time-between-turns", "2000");
		this.player1 = parseStrategy(player1Str, parseInt(seed1Str, 42));
		this.player2 = parseStrategy(player2Str, parseInt(seed2Str, 73));
		this.timeAfterGame = parseInt(timeAfterGameStr, 2000);
		this.timeBetweenTurns = parseInt(timeBetweenTurnsStr, 2000);
		boolean gui = options.getOrDefault("--front-end", "gui").equals("gui");
		frontEnd = gui? new TicTacToeGUI(game) : new TicTacToeCLI(game);
		interrupted = false;
	}
	
	/**
	 * Quite self-explanatory function name ;)
	 * 
	 * Keeps asking both strategies for a movement until the game ends.
	 */
	public void executeGame() {
		Strategy strategy1 = game.getNextPlayer() == 'X'? this.player1 : this.player2;
		Strategy strategy2 = game.getNextPlayer() == 'X'? this.player2 : this.player1;
		boolean ok = true;
		frontEnd.scheduleUpdate();
		while (!game.isGameFinished() && ok) {
			try {
				sleep(timeBetweenTurns);
				Cell cell = strategy1.onNewTurn(game);
				game.play(game.getNextPlayer(), cell.getRow(), cell.getColumn());
				Strategy tmp = strategy2;
				strategy2 = strategy1;
				strategy1 = tmp;
				frontEnd.scheduleUpdate();
			}
			catch (TicTacToeException ex) {
				ex.printStackTrace();
				ok = false;
			} catch (InterruptedException e) {
				ok = false;
				interrupted = true;
			}
		}
	}
	
	/**
	 * Executes games forever, waiting some time between one game and the following one.
	 */
	@Override
	public void run() {
		while (!interrupted) {
			executeGame();
			try {
				sleep(timeAfterGame);
			} catch (InterruptedException e) {
				interrupted = true;
			}
			game.startNewGame();
		}
	}
	
	/**
	 * Parses options from command line.
	 * 
	 * @param args command line arguments. The available options are: --player1, --player2 (Random, Basic,
	 * Minimax), --seed1, --seed2 (integer number), --time-after-game,
	 * --time-between-turns (integer number, ms), --front-end (gui or cli)
	 * @return Map option -> value
	 */
	public static Map<String, String> parseOptions(String[] args) {
		Map<String, String> options = new HashMap<>();
		if (args.length % 2 != 0) {
			System.err.println("Wrong number of arguments (" + args.length + ")");
		}
		
		for (int i = 0; i < args.length; i += 2) {
			String key = args[i];
			String value = args[i+1];
			options.put(key, value);
		}
		return options;
	}
	
	public static void main(String[] args) {
		Map<String, String> options = parseOptions(args);
		Standalone standalone = new Standalone(options);
		standalone.start();
	}
	
}
