package tic.tac.toe.frontend;

import tic.tac.toe.logic.Game;

public class TicTacToeCLI implements FrontEnd {

	private final Game game;
	
	public TicTacToeCLI(Game game) {
		this.game = game;
	}
	
	@Override
	public void scheduleUpdate() {
		System.out.println(game);
	}

}
