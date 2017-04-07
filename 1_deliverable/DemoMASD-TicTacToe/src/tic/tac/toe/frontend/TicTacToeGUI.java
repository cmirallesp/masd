package tic.tac.toe.frontend;
import javax.swing.*;

import tic.tac.toe.logic.Game;
import tic.tac.toe.logic.TicTacToeException;

import java.awt.*;

public class TicTacToeGUI extends JFrame implements FrontEnd {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5138474412071235891L;

	public static void main(String [] args) {
		Game game = new Game();
		new TicTacToeGUI(game);
	}

	private JLabel scoreXLabel, scoreOLabel;
	private JTextField info, scoreX, scoreO;
	private JButton[][] buttons;
	private Game game;
	
//	private TicTacToeBoard board;

	public TicTacToeGUI(Game game) {
		this.game = game;
		
		// Set up the grid
		this.setSize(300,350);
	    this.setTitle("Tic-Tac-Toe");
	    
	    this.getContentPane().setLayout(new BorderLayout());
	    
	    JPanel scorePanel = new JPanel();
	    scorePanel.setLayout(new GridLayout(2, 2));
	    scoreXLabel = new JLabel("Score X");
	    scoreOLabel = new JLabel("Score O");
	    scoreX = new JTextField();
	    scoreO = new JTextField();
	    scorePanel.add(scoreXLabel);
	    scorePanel.add(scoreOLabel);
	    scorePanel.add(scoreX);
	    scorePanel.add(scoreO);
	    
		JPanel grid = new JPanel();
	    grid.setSize(300,300);
	    grid.setLayout(new GridLayout(3,3));
	    buttons = new JButton[3][3];
	    for (int i = 0; i < 3; ++i) {
	    	for (int j = 0; j < 3; ++j) {
	    		buttons[i][j] = createButton();
	    		grid.add(buttons[i][j]);
	    	}
	    }

		info = new JTextField();
		
		this.getContentPane().add(info, BorderLayout.PAGE_START);
		this.getContentPane().add(grid, BorderLayout.CENTER);
		this.getContentPane().add(scorePanel, BorderLayout.PAGE_END);
		
	    this.setVisible(true);
	    
	    scheduleUpdate();
	}

	private JButton createButton() {
		JButton btn = new JButton();
		btn.setPreferredSize(new Dimension(50, 50));
		Font f = new Font("Dialog", Font.PLAIN, 72);
		btn.setFont(f);
		return btn;
	}
	
	private void update() {
		scoreX.setText(Integer.toString(game.getScoreX()));
		scoreO.setText(Integer.toString(game.getScoreO()));
		if (game.getTurn() == 0) {
			info.setText("New game! It's " + game.getNextPlayer() + "'s turn");
		}
		else if (game.isGameFinished()) {
			char winner = ' ';
			try {winner = game.getWinner();}
			catch (TicTacToeException ex) {/* Cannot happen */}
			if (winner == ' ') {
				info.setText("It's a tie!");
			}
			else {
				info.setText(winner + " has won this one");
			}
		}
		else {
			info.setText("It's " + game.getNextPlayer() + "'s turn");
		}
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 3; ++j) {
				buttons[i][j].setText(Character.toString(game.getCellContent(i, j)));
			}
		}
	}
	
	@Override
	public void scheduleUpdate() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				update();
			}
		});
	}
	
}
