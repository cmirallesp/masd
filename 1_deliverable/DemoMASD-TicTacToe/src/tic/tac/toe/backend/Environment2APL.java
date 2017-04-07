package tic.tac.toe.backend;
import java.util.HashMap;
import java.util.LinkedList;

import apapl.Environment;
import apapl.ExternalActionFailedException;
import apapl.data.APLFunction;
import apapl.data.APLIdent;
import apapl.data.APLList;
import apapl.data.APLNum;
import apapl.data.Term;
import tic.tac.toe.frontend.FrontEnd;
import tic.tac.toe.frontend.TicTacToeGUI;
import tic.tac.toe.logic.Game;
import tic.tac.toe.logic.TicTacToeException;

/**
 * Tic Tac Toe environment for the 2APL demonstration.
 *
 */
public class Environment2APL extends Environment {

	private final boolean log = true;
	
	private final Game game;
	private final FrontEnd frontEnd;
	private final HashMap<String, Character> playerId;
	private int acquiredPIds;
	
	
	private void log(String str) {
		if (log) {
			System.out.println("env> " + str);
		}
	}
	
	private void sendNewTurnEvent() {
		if (game.isGameFinished()) {
			game.startNewGame();
		}
		APLIdent[] cells = new APLIdent[9];
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				char content = Character.toLowerCase(game.getCellContent(i, j));
				cells[i*3 + j] = new APLIdent(String.valueOf(content));
			}
		}
		APLList board = new APLList(cells);
		APLIdent turn = new APLIdent(String.valueOf(Character.toLowerCase(game.getNextPlayer())));
		APLFunction event = new APLFunction("newTurn", board, turn);
		
		this.log("sendNewTurnEvent(long): sending turn event");
		
		// tell both agents
		this.notifyAgents(event);
	}
	
	private void scheduleNewTurnEvent(long delay) {
		new Thread() {
			public void run() {
				try {this.sleep(delay);}
				catch (InterruptedException e) {/*Nothing to do*/}
				sendNewTurnEvent();
			}
		}.start();
	}
	
	public Environment2APL() {
		super();
		game = new Game();
		frontEnd = new TicTacToeGUI(game);
		playerId = new HashMap<>();
		acquiredPIds = 0;
	}
	
	/**
	 * This method is automatically called whenever an agent enters the MAS.
	 * @param agName the name of the agent that just registered
	 */
	@Override
	protected void addAgent(String agName) {
		if (playerId.size() < 2) {
			this.log(String.format("addAgent(String): registering new agent (%s) into the environment", agName));
			playerId.put(agName, playerId.size() == 0? 'O' : 'X');
		} else {
			this.log("addAgent(String): WARNING!! More than two agents being added.");
		}
	}
	
	public Term acquirePlayerId(String agName) throws ExternalActionFailedException {
		Character pIdChar = playerId.get(agName);
		if (pIdChar == null) {
			throw new ExternalActionFailedException(
					String.format("Agent %s does not have a player id", agName));
		}
		APLIdent pId = new APLIdent(String.valueOf(Character.toLowerCase(playerId.get(agName))));
		this.log(String.format("sendPlayerId(String, char): sending agent %s its player id (%s)",
				agName, pId.toString()));
		++acquiredPIds;
		if (acquiredPIds == 2) {
			this.log("sendPlayerId(String, char): all pIds assigned. Starting game...");
			this.scheduleNewTurnEvent(2000);
		}
		return pId;
	}
	
	public Term play(String agName, APLNum idx) throws ExternalActionFailedException {
		int i = idx.toInt() / 3;
		int j = idx.toInt() % 3;
		try {
			this.log(String.format("play(String, APLNum): Agent %s fills position (%d, %d).", agName, i, j));
			game.play(playerId.get(agName), i, j);
		} catch (TicTacToeException e) {
			this.log("play(String, APLNum): ERROR!!" + e.getMessage());
			throw new ExternalActionFailedException(e.getMessage());
		}
		frontEnd.scheduleUpdate();
		this.scheduleNewTurnEvent(2000);
		return null;
	}

	/**
     * We do not use this method, but we need it so that the JAR file that we will create can point
     * to this class as the main class. This is only possible if the class contains  main method.
     * @param args arguments
     */
	public static void main(String [] args) {
	}
}